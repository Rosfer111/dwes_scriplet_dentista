<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Error</title>
  <c:url var="cssUrl" value="/estilos.css"/>
  <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
  <h1 class="error">No se pudo completar la operación</h1>
  <p class="error"><c:out value="${error}"/></p>
  <p><a href="<c:url value='/app/home'/>">Volver al inicio</a></p>
</body>
</html>
