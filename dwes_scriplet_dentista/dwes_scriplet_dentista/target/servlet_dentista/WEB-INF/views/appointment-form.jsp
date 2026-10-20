<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Nueva cita</title>
  <c:url var="cssUrl" value="/estilos.css"/>
  <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
  <h1>Alta de cita</h1>
  <c:if test="${empty patients}"><p class="nota">Registre primero un paciente para poder crear una cita.</p></c:if>
  <c:if test="${empty dentists}"><p class="nota">No hay odontólogos registrados.</p></c:if>
  <form method="post" action="<c:url value='/app/appointments/create'/>">
    <label>Fecha <input type="date" name="fecha" required></label><br>
    <label>Hora <input type="time" name="hora" required></label><br>
    <label>Paciente
      <select name="id_paciente" required>
        <option value="">-- Seleccione un paciente --</option>
        <c:forEach var="patient" items="${patients}">
          <c:choose>
            <c:when test="${patient.id == selectedPatient}">
              <option value="<c:out value='${patient.id}'/>" selected><c:out value="${patient.nombreCompleto}"/></option>
            </c:when>
            <c:otherwise>
              <option value="<c:out value='${patient.id}'/>"><c:out value="${patient.nombreCompleto}"/></option>
            </c:otherwise>
          </c:choose>
        </c:forEach>
      </select>
    </label><br>
    <label>Odontólogo
      <select name="id_odontologo" required>
        <option value="">-- Seleccione un odontólogo --</option>
        <c:forEach var="dentist" items="${dentists}">
          <option value="<c:out value='${dentist.id}'/>">
            <c:out value="${dentist.nombre}"/> (<c:out value="${dentist.especialidad}"/>)
          </option>
        </c:forEach>
      </select>
    </label><br>
    <label>Motivo <input type="text" name="motivo" maxlength="200"></label><br>
    <input type="submit" value="Guardar">
  </form>
  <p><a href="<c:url value='/app/home'/>">Volver al inicio</a></p>
</body>
</html>
