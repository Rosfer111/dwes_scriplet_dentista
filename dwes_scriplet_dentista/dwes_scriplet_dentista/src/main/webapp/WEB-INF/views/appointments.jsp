<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <title>Listado de citas</title>
  <c:url var="cssUrl" value="/estilos.css"/>
  <link rel="stylesheet" href="${cssUrl}">
</head>
<body>
  <h1><c:choose><c:when test="${all}">Todas las citas</c:when><c:otherwise>Próximas citas</c:otherwise></c:choose></h1>
  <c:if test="${param.success eq 'appointment'}"><p>Cita registrada.</p></c:if>
  <c:if test="${param.success eq 'deleted-appointment'}"><p>Cita anulada.</p></c:if>
  <table>
    <thead>
      <tr><th>Id cita</th><th>Fecha</th><th>Hora</th><th>Paciente</th><th>Motivo</th><th>Odontólogo</th><th>Acciones</th></tr>
    </thead>
    <tbody>
      <c:forEach var="appointment" items="${appointments}">
        <tr>
          <td><c:out value="${appointment.id}"/></td>
          <td><c:out value="${appointment.fecha}"/></td>
          <td><c:out value="${appointment.hora}"/></td>
          <td><c:out value="${appointment.paciente}"/></td>
          <td><c:out value="${appointment.motivo}"/></td>
          <td><c:out value="${appointment.odontologo}"/></td>
          <td>
            <form method="post" action="<c:url value='/app/appointments/delete'/>">
              <input type="hidden" name="id_cita" value="<c:out value='${appointment.id}'/>">
              <input type="submit" value="Anular">
            </form>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${empty appointments}">
        <tr><td colspan="7">No hay citas para mostrar.</td></tr>
      </c:if>
    </tbody>
  </table>
  <p>
    <c:choose>
      <c:when test="${all}"><a href="<c:url value='/app/appointments'/>">Ver próximas citas</a></c:when>
      <c:otherwise><a href="<c:url value='/app/appointments'><c:param name='todas' value='1'/></c:url>">Ver el histórico completo</a></c:otherwise>
    </c:choose>
    | <a href="<c:url value='/app/appointments/new'/>">Nueva cita</a>
    | <a href="<c:url value='/app/home'/>">Volver al inicio</a>
  </p>
</body>
</html>
