# Ejecutar el proyecto desde GitHub

Requisitos: JDK 21, Maven Wrapper incluido y Postman. Para PostgreSQL, iniciar
el servidor y crear la base tienda_mayenm antes de ejecutar la aplicación.

## Conexión local

La contraseña PostgreSQL no se publica. Definir DB_PASSWORD en Environment de
la configuración Eclipse, o crear config/postgres-local.properties con:

```properties
spring.datasource.password=${DB_PASSWORD:tu_clave_local}
```

Este archivo está excluido de Git. Configurar DB_URL y DB_USER si el servidor
usa otra base o usuario. Ejecutar GestionPostgreSQL.launch con demo,postgres.
La configuración demo y sus usuarios son exclusivamente para laboratorio.

## Pruebas

```powershell
.\mvnw.cmd test
```

Las pruebas usan H2 en memoria con create-drop y no borran la base comercial.

## Postman

Usar Tienda_MAYEN_Datos para cargar catálogo y ventas, APF2 para comprobar
requisitos y Limpieza_v2 para eliminar datos comerciales con ADMIN. La limpieza
conserva todos los usuarios. Revisar baseUrl y el puerto antes de enviar.

## Archivos excluidos

No se publican data, target, logs, .env ni contraseñas locales. Los datos H2 no
se migran automáticamente a PostgreSQL. No ejecutar dos procesos en el mismo
puerto; usar otro server.port si hace falta.
