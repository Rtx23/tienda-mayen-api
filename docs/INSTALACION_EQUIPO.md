# Instalación para integrantes del equipo

1. Instalar Java 21 y PostgreSQL. La primera ejecución Maven requiere Internet.
2. Clonar o descomprimir el proyecto y abrir una terminal en la carpeta que contiene `pom.xml`.
3. Iniciar PostgreSQL. En pgAdmin, conectado a la base `postgres`, ejecutar `CREATE DATABASE tienda_mayenm;` si todavía no existe.
4. Copiar `config/postgres-local.properties.example` como `config/postgres-local.properties`. Ajustar usuario, contraseña, puerto y nombre de base a los del equipo. No compartir el archivo resultante.
5. Ejecutar:

```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=demo,postgres"
```

Abrir `http://localhost:8080/` cuando Java termine de iniciar. JPA crea o actualiza las tablas. El perfil `demo` inicializa usuarios de práctica; revisar [README_APF2.md](../README_APF2.md). Esta configuración es para laboratorio local.

Como alternativa al archivo privado, se pueden definir `DB_URL`, `DB_USER` y `DB_PASSWORD` en el entorno. Si se usa esa alternativa, no crear un archivo local con valores distintos: sus propiedades explícitas también participan en la configuración.

En Eclipse: importar como Existing Maven Projects, seleccionar JDK 21, actualizar Maven y ejecutar `GestionApplication`. En Program arguments poner `--spring.profiles.active=demo,postgres`; usar la carpeta que contiene `pom.xml` como directorio de trabajo.

Angular ya está compilado en `src/main/resources/static`. Solo para modificarlo hace falta Node 24: desde `frontend`, ejecutar `npm ci`, `npm test` y `npm run publish`. Reiniciar Java para servir la nueva compilación.

Cada integrante tendrá sus propios registros. Clonar Git no copia la base de datos de otro equipo.
