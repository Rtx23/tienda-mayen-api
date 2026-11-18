# Diagnóstico de ejecución

| Síntoma | Qué revisar |
|---|---|
| Puerto 8080 ocupado | Detener la ejecución Java anterior o configurar otro puerto y adaptar la URL/proxy |
| Connection refused al conectar PostgreSQL | Servicio iniciado, host y puerto de la conexión |
| Password authentication failed | Usuario y contraseña de PostgreSQL de ese equipo |
| Database does not exist | Crear la base o corregir su nombre en la URL JDBC |
| Datos diferentes a los esperados | Perfil activo, base y esquema; H2 y PostgreSQL no comparten automáticamente registros |
| Postman muestra `{{baseUrl}}` sin resolver | Definir la variable `baseUrl` con la URL real y revisar el entorno seleccionado |
| 401 Unauthorized | Iniciar sesión otra vez y enviar el token JWT vigente en Authorization Bearer |
| 403 Forbidden | El usuario está autenticado pero su rol no permite esa operación |
| 400 Bad Request | Revisar campos y mensajes de validación del cuerpo de respuesta |
| 404 Not Found | Comprobar la ruta y que el ID exista en la base activa |
| 409 Conflict | Revisar el mensaje: puede haber stock insuficiente o un producto relacionado con ventas |
| No aparece un cambio de Angular | Ejecutar `npm run publish`, reiniciar Java y recargar el navegador |

H2 puede funcionar dentro de Java y PostgreSQL en su propio servicio al mismo tiempo. El perfil determina qué datasource usa esta aplicación. Dos procesos Java no pueden escuchar simultáneamente en la misma dirección y puerto 8080.

Importar una colección Postman no ejecuta sus solicitudes. Al ejecutarla, las pruebas negativas pueden esperar errores 400, 401 o 403: revisar el nombre de la solicitud y sus tests antes de concluir que falló el sistema.

Las colecciones de limpieza eliminan registros comerciales; no son una solución para errores de autenticación o conexión. Para la instalación inicial seguir [INSTALACION_EQUIPO.md](INSTALACION_EQUIPO.md).
