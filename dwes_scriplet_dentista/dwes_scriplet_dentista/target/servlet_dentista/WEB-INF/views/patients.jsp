<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Listado de pacientes</title>
  <c:url var="cssUrl" value="/estilos.css"/>
  <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
  <h1>Listado de pacientes</h1>
  <c:if test="${param.success eq 'patient'}"><p>Paciente dado de alta.</p></c:if>
  <c:if test="${param.success eq 'deleted-patient'}">
    <p>Paciente eliminado. Las citas asociadas también se han eliminado (ON DELETE CASCADE).</p>
  </c:if>
  <p><a href="<c:url value='/app/patients/new'/>">Registrar paciente</a></p>
  <table>
    <thead>
      <tr>
        <th>Id</th><th>Nombre</th><th>Apellidos</th><th>DNI</th>
        <th>Fecha nacimiento</th><th>Teléfono</th><th>Email</th>
        <th>Dirección</th><th>Alergias</th><th>Citas</th><th>Acciones</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach var="patient" items="${patients}">
        <tr>
          <td><c:out value="${patient.id}"/></td>
          <td><c:out value="${patient.nombre}"/></td>
          <td><c:out value="${patient.apellidos}"/></td>
          <td><c:out value="${patient.dni}"/></td>
          <td><c:out value="${patient.fechaNacimiento}"/></td>
          <td><c:out value="${patient.telefono}"/></td>
          <td><c:out value="${patient.email}"/></td>
          <td><c:out value="${patient.direccion}"/></td>
          <td><c:out value="${patient.alergias}"/></td>
          <td><c:out value="${patient.numCitas}"/></td>
          <td>
            <c:url var="newAppointmentUrl" value="/app/appointments/new">
              <c:param name="paciente" value="${patient.id}"/>
            </c:url>
            <a href="${newAppointmentUrl}">Nueva cita</a>
            <form method="post" action="<c:url value='/app/patients/delete'/>">
              <input type="hidden" name="id_paciente" value="<c:out value='${patient.id}'/>">
              <input type="submit" value="Borrar">
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty patients}">
        <tr><td colspan="11">No hay pacientes registrados.</td></tr>
      </c:if>
    </tbody>
  </table>
  <p><a href="<c:url value='/app/home'/>">Volver al inicio</a></p>
</body>
</html>
