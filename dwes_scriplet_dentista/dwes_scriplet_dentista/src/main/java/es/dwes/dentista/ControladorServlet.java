package es.dwes.dentista;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/app/*")
public class ControladorServlet extends HttpServlet {
    private static final String VIEWS = "/WEB-INF/views/";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();
        if (path == null || "/".equals(path)) {
            path = "/home";
        }

        try {
            switch (path) {
                case "/home" -> forward(request, response, "home.jsp");
                case "/patients" -> showPatients(request, response);
                case "/appointments" -> showAppointments(request, response);
                case "/patients/new" -> forward(request, response, "patient-form.jsp");
                case "/appointments/new" -> showAppointmentForm(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException exception) {
            handleDatabaseError(request, response, exception);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();

        try {
            switch (path == null ? "" : path) {
                case "/patients/create" -> createPatient(request, response);
                case "/appointments/create" -> createAppointment(request, response);
                case "/patients/delete" -> deletePatient(request, response);
                case "/appointments/delete" -> deleteAppointment(request, response);
                default -> response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (IllegalArgumentException exception) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            request.setAttribute("error", "Los datos enviados no son válidos. Revise el formulario.");
            forward(request, response, "error.jsp");
        } catch (SQLException exception) {
            handleDatabaseError(request, response, exception);
        }
    }

    private void showPatients(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String sql = "SELECT p.id_paciente, p.nombre, p.apellidos, p.dni, p.fecha_nacimiento, "
                + "p.telefono, p.email, p.direccion, p.alergias, "
                + "(SELECT COUNT(*) FROM cita c WHERE c.id_paciente = p.id_paciente) AS num_citas "
                + "FROM paciente p ORDER BY p.apellidos, p.nombre";
        List<Map<String, Object>> patients = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                Map<String, Object> patient = new LinkedHashMap<>();
                patient.put("id", results.getInt("id_paciente"));
                patient.put("nombre", results.getString("nombre"));
                patient.put("apellidos", results.getString("apellidos"));
                patient.put("dni", results.getString("dni"));
                patient.put("fechaNacimiento", results.getString("fecha_nacimiento"));
                patient.put("telefono", results.getString("telefono"));
                patient.put("email", results.getString("email"));
                patient.put("direccion", results.getString("direccion"));
                patient.put("alergias", results.getString("alergias"));
                patient.put("numCitas", results.getInt("num_citas"));
                patients.add(patient);
            }
        }
        request.setAttribute("patients", patients);
        forward(request, response, "patients.jsp");
    }

    private void showAppointments(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        boolean all = "1".equals(request.getParameter("todas"));
        String sql = "SELECT c.id_cita, c.fecha, c.hora, c.motivo, p.nombre, p.apellidos, "
                + "o.nombre AS odontologo FROM cita c "
                + "JOIN paciente p ON p.id_paciente = c.id_paciente "
                + "JOIN odontologos o ON o.id_odontologo = c.id_odontologo "
                + (all ? "" : "WHERE c.fecha >= CURDATE() ")
                + "ORDER BY c.fecha, c.hora";
        List<Map<String, Object>> appointments = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet results = statement.executeQuery()) {
            while (results.next()) {
                Map<String, Object> appointment = new LinkedHashMap<>();
                appointment.put("id", results.getInt("id_cita"));
                appointment.put("fecha", results.getString("fecha"));
                appointment.put("hora", results.getString("hora").substring(0, 5));
                appointment.put("motivo", results.getString("motivo"));
                appointment.put("paciente", results.getString("apellidos") + ", "
                        + results.getString("nombre"));
                appointment.put("odontologo", results.getString("odontologo"));
                appointments.add(appointment);
            }
        }
        request.setAttribute("appointments", appointments);
        request.setAttribute("all", all);
        forward(request, response, "appointments.jsp");
    }

    private void showAppointmentForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Map<String, Object>> patients = new ArrayList<>();
        List<Map<String, Object>> dentists = new ArrayList<>();
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(
                         "SELECT id_paciente, nombre, apellidos FROM paciente ORDER BY apellidos, nombre");
                 ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Map<String, Object> patient = new LinkedHashMap<>();
                    patient.put("id", results.getInt("id_paciente"));
                    patient.put("nombreCompleto", results.getString("apellidos") + ", "
                            + results.getString("nombre"));
                    patients.add(patient);
                }
            }
            try (PreparedStatement statement = connection.prepareStatement(
                         "SELECT id_odontologo, nombre, especialidad FROM odontologos ORDER BY nombre");
                 ResultSet results = statement.executeQuery()) {
                while (results.next()) {
                    Map<String, Object> dentist = new LinkedHashMap<>();
                    dentist.put("id", results.getInt("id_odontologo"));
                    dentist.put("nombre", results.getString("nombre"));
                    dentist.put("especialidad", results.getString("especialidad"));
                    dentists.add(dentist);
                }
            }
        }
        request.setAttribute("patients", patients);
        request.setAttribute("dentists", dentists);
        String selectedPatient = request.getParameter("paciente");
        try {
            request.setAttribute("selectedPatient",
                    selectedPatient == null ? null : Integer.valueOf(selectedPatient));
        } catch (NumberFormatException exception) {
            request.setAttribute("selectedPatient", null);
        }
        forward(request, response, "appointment-form.jsp");
    }

    private void createPatient(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String nombre = required(request, "nombre");
        String apellidos = required(request, "apellidos");
        String dni = required(request, "dni");
        String nacimiento = request.getParameter("fecha_nacimiento");
        String sql = "INSERT INTO paciente "
                + "(nombre, apellidos, dni, fecha_nacimiento, telefono, email, direccion, alergias) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nombre);
            statement.setString(2, apellidos);
            statement.setString(3, dni);
            if (nacimiento == null || nacimiento.isBlank()) {
                statement.setNull(4, Types.DATE);
            } else {
                statement.setDate(4, Date.valueOf(nacimiento));
            }
            statement.setString(5, request.getParameter("telefono"));
            statement.setString(6, request.getParameter("email"));
            statement.setString(7, request.getParameter("direccion"));
            statement.setString(8, request.getParameter("alergias"));
            statement.executeUpdate();
        }
        response.sendRedirect(request.getContextPath() + "/app/patients?success=patient");
    }

    private void createAppointment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        Date date = Date.valueOf(required(request, "fecha"));
        String enteredTime = required(request, "hora");
        Time time = Time.valueOf(enteredTime.length() == 5 ? enteredTime + ":00" : enteredTime);
        int patientId = positiveId(request, "id_paciente");
        int dentistId = positiveId(request, "id_odontologo");
        String sql = "INSERT INTO cita (fecha, hora, id_paciente, id_odontologo, motivo) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDate(1, date);
            statement.setTime(2, time);
            statement.setInt(3, patientId);
            statement.setInt(4, dentistId);
            statement.setString(5, request.getParameter("motivo"));
            statement.executeUpdate();
        }
        response.sendRedirect(request.getContextPath() + "/app/appointments?success=appointment");
    }

    private void deletePatient(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        deleteById("DELETE FROM paciente WHERE id_paciente = ?", positiveId(request, "id_paciente"));
        response.sendRedirect(request.getContextPath() + "/app/patients?success=deleted-patient");
    }

    private void deleteAppointment(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        deleteById("DELETE FROM cita WHERE id_cita = ?", positiveId(request, "id_cita"));
        response.sendRedirect(request.getContextPath() + "/app/appointments?success=deleted-appointment");
    }

    private void deleteById(String sql, int id) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    private Connection getConnection() throws SQLException {
        String url = getServletContext().getInitParameter("db.url");
        String user = getServletContext().getInitParameter("db.user");
        String password = getServletContext().getInitParameter("db.password");
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException exception) {
            throw new SQLException("No se encontró el driver JDBC de MySQL. "
                    + "Compruebe que mysql-connector-j está incluido en el WAR.", exception);
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String required(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Falta el campo " + name);
        }
        return value.trim();
    }

    private static int positiveId(HttpServletRequest request, String name) {
        try {
            int id = Integer.parseInt(required(request, name));
            if (id > 0) {
                return id;
            }
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Identificador no válido", exception);
        }
        throw new IllegalArgumentException("Identificador no válido");
    }

    private void handleDatabaseError(HttpServletRequest request, HttpServletResponse response,
                                     SQLException exception) throws ServletException, IOException {
        getServletContext().log("Error al acceder a la base de datos de la clínica dental", exception);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        request.setAttribute("error", "No se ha podido completar la operación. "
                + "Compruebe la conexión y la configuración de la base de datos.");
        forward(request, response, "error.jsp");
    }

    private void forward(HttpServletRequest request, HttpServletResponse response, String view)
            throws ServletException, IOException {
        request.getRequestDispatcher(VIEWS + view).forward(request, response);
    }
}
