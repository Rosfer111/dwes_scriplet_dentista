<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Nuevo paciente</title>
  <c:url var="cssUrl" value="/estilos.css"/>
  <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
  <h1>Alta de paciente</h1>
  <form method="post" action="<c:url value='/app/patients/create'/>">
    <label>Nombre <input type="text" name="nombre" maxlength="40" required></label><br>
    <label>Apellidos <input type="text" name="apellidos" maxlength="80" required></label><br>
    <label>DNI <input type="text" name="dni" maxlength="9" required></label><br>
    <label>Fecha de nacimiento <input type="date" name="fecha_nacimiento"></label><br>
    <label>Teléfono <input type="text" name="telefono" maxlength="20"></label><br>
    <label>Correo electrónico <input type="email" name="email" maxlength="80"></label><br>
    <label>Dirección <input type="text" name="direccion" maxlength="120"></label><br>
    <label>Alergias o notas <input type="text" name="alergias" maxlength="255"></label><br>
    <input type="submit" value="Guardar">
  </form>
  <p><a href="<c:url value='/app/home'/>">Volver al inicio</a></p>
</body>
</html>
