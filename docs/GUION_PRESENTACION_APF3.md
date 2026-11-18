# Demostración del avance 3

Usar una base o esquema de práctica, con la conexión comprobada antes de presentar. La siguiente secuencia crea registros: no ejecutarla sobre datos que deban permanecer intactos.

1. Mostrar login y probar un acceso incorrecto: debe aparecer un mensaje visible. Iniciar sesión con un usuario administrador de laboratorio.
2. Navegar entre resumen, catálogo, nueva venta e historial. Explicar las rutas y los componentes con [el mapa de archivos](MAPA_APF3.md).
3. En catálogo, intentar enviar un formulario incompleto y mostrar las validaciones. Crear un producto de práctica con precio 9,20 y stock 20; anotar su ID real.
4. Editar el nombre del producto y comprobar el cambio en el listado. Crear otro producto temporal sin ventas y eliminar únicamente ese producto confirmando la operación.
5. Vender tres unidades del primer producto. El importe esperado es 27,60 y su stock debe bajar de 20 a 17 si no hubo otras operaciones.
6. Abrir historial y detalle de la venta. Verificar cantidades e importe. Mostrar los gráficos y explicar que representan ingresos, no utilidad neta.
7. Consultar en PostgreSQL los registros correspondientes a los IDs anotados. Recargar la página y comprobar que los datos siguen visibles: esa evidencia completa el flujo Angular → API → base de datos.
8. Cerrar sesión y entrar como lector. Mostrar las restricciones de consulta y explicar que los permisos también se verifican en Java.

Para evidencias, capturar pantallas del formulario validado, producto editado, detalle de venta y consulta de persistencia. No capturar tokens, contraseñas ni conexiones privadas. No afirmar que una prueba pasó hasta observar su resultado en el equipo de presentación.

Pruebas automatizadas complementarias: `npm test` desde `frontend` y `.\mvnw.cmd test` desde la raíz. La ejecución en navegador demuestra la integración que los tests aislados no prueban por sí solos.
