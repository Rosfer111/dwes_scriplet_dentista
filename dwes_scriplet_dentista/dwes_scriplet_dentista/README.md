# Clínica Dental

Aplicación web Java para gestionar pacientes y citas de una clínica dental. Está
implementada con **Servlet, JSP, JSTL y JDBC** sobre MySQL, siguiendo el patrón MVC:

- **Controlador:** `ControladorServlet` recibe las peticiones, coordina las operaciones
  y selecciona la vista.
- **Modelo/datos:** el Servlet ejecuta las consultas JDBC parametrizadas y prepara los
  datos que necesitan las vistas.
- **Vistas:** JSP sin scriptlets, ubicadas en `WEB-INF/views` y renderizadas mediante
  Expression Language y JSTL.

El proyecto contiene un único archivo Servlet y centraliza en él la lógica de acceso a
datos para mantener la estructura sencilla. Las vistas no abren conexiones ni ejecutan
consultas.

## Estructura

```text
servlet_dentista/
├── pom.xml
└── src/main/
    ├── java/es/dwes/dentista/
    │   └── ControladorServlet.java
    ├── resources/
    │   └── dentista.sql
    └── webapp/
        ├── estilos.css
        └── WEB-INF/
            ├── web.xml
            └── views/
                ├── home.jsp
                ├── patients.jsp
                ├── appointments.jsp
                ├── patient-form.jsp
                ├── appointment-form.jsp
                └── error.jsp
```

## Funcionalidad

- Consultar pacientes, incluido el número de citas de cada uno.
- Consultar las próximas citas o el histórico completo.
- Dar de alta pacientes y citas.
- Anular citas y eliminar pacientes (las citas del paciente se eliminan por la clave
  foránea `ON DELETE CASCADE`).
- Crear una cita desde el listado de pacientes con ese paciente preseleccionado.

Las altas y eliminaciones usan **POST** y el patrón **Post/Redirect/Get**. Las vistas
quedan en `WEB-INF`, de modo que se accede a ellas únicamente a través del Servlet. Los
valores mostrados se escapan con JSTL y las consultas usan `PreparedStatement`.

## Base de datos

La base de datos `dentista` tiene tres tablas:

| Tabla | Clave | Contenido |
|---|---|---|
| `paciente` | `id_paciente` | Datos personales y de contacto, alergias |
| `odontologos` | `id_odontologo` | Nombre y especialidad de cada profesional |
| `cita` | `id_cita` | Fecha, hora, paciente, odontólogo y motivo |

Relaciones:

- `cita.id_paciente` → `paciente.id_paciente`, con `ON DELETE CASCADE`.
- `cita.id_odontologo` → `odontologos.id_odontologo`.

Importa el script de creación y datos de ejemplo:

```bash
mysql -u user -puser < src/main/resources/dentista.sql
```

El script crea la base de datos y genera citas pasadas y futuras relativas al día de
importación.

## Requisitos y configuración

- Java 21.
- Maven 3.8 o posterior.
- MySQL Server accesible desde el servidor web.
- Apache Tomcat **11.0.x**.

La aplicación utiliza Jakarta Servlet 6.1, Jakarta Pages (JSP) 4.0 y Jakarta Standard
Tag Library (JSTL) 3.0. Está preparada para Tomcat 11 y usa el espacio de nombres
`jakarta.*`; no es compatible directamente con Tomcat 9.

La conexión JDBC está configurada como parámetros de contexto en
`src/main/webapp/WEB-INF/web.xml`:

| Parámetro | Valor inicial |
|---|---|
| `db.url` | `jdbc:mysql://localhost:3306/dentista?...` |
| `db.user` | `user` |
| `db.password` | `user` |

Adapta estos parámetros al entorno antes de desplegar. Los valores son únicamente los
usados en el entorno docente/local y no deben reutilizarse en producción.
El driver `mysql-connector-j` se declara como dependencia Maven y se incluye dentro de
`WEB-INF/lib` en el WAR; no es necesario copiarlo manualmente a Tomcat.

## Compilar y desplegar

```bash
mvn clean package
```

El empaquetado genera `target/servlet_dentista.war`. Despliega ese WAR en Tomcat 11; el
contexto por defecto será `/servlet_dentista`, cuya página de bienvenida abre el
controlador en `/app/home`.

## Rutas del controlador

| Método | Ruta | Acción |
|---|---|---|
| GET | `/app/home` | Menú principal |
| GET | `/app/patients` | Listado de pacientes |
| GET | `/app/appointments` | Próximas citas |
| GET | `/app/appointments?todas=1` | Histórico de citas |
| GET | `/app/patients/new` | Formulario de paciente |
| GET | `/app/appointments/new` | Formulario de cita |
| POST | `/app/patients/create` | Alta de paciente |
| POST | `/app/appointments/create` | Alta de cita |
| POST | `/app/patients/delete` | Baja de paciente |
| POST | `/app/appointments/delete` | Anulación de cita |
