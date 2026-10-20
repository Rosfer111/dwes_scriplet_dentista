<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Clínica Dental</title>
  <c:url var="cssUrl" value="/estilos.css"/>
  <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
  <h1>Gestión de citas de la clínica dental</h1>
  <p>Seleccione una opción:</p>
  <ul>
    <li><a href="<c:url value='/app/patients'/>">Ver listado de pacientes</a></li>
    <li><a href="<c:url value='/app/appointments'/>">Ver próximas citas</a></li>
    <li><a href="<c:url value='/app/appointments/new'/>">Nueva cita</a></li>
    <li><a href="<c:url value='/app/patients/new'/>">Nuevo paciente</a></li>
  </ul>
</body>
</html>
