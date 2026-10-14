# Limpiar datos comerciales con Postman v2

Importar Tienda_MAYEN_Limpieza_v2.postman_collection.json y usar la colección
Tienda MAYEN - Limpieza de datos comerciales v2. Reiniciar gestion con la ruta nueva.
Revisar baseUrl (8080 o el puerto actual) y adminPassword.

Enviar 02 Eliminar todos los productos ventas y detalles - conservar usuarios.
El script hace login admin automáticamente y envía la confirmación fija al backend.
También se puede ejecutar la colección completa con Runner, una iteración.
No hay que cambiar la variable confirmarEliminacion: se retiró en v2.

El DELETE borra todos los productos, ventas y detalles de la base conectada.
Conserva TODOS los usuarios, hashes de contraseña, roles y estado, incluidos
admin, editor y lector. No reinicia IDs. Solo ADMIN puede limpiar.
La ruta es DELETE /api/admin/datos-comerciales y el borrado es transaccional.
No cargar ventas simultáneamente durante la limpieza.

Esperar 200 con detallesEliminados, ventasEliminadas, productosEliminados y
usuariosConservados=true. Si falla login, la petición se omite y Postman Console
explica el motivo. 401/403 requieren revisar autenticación o permisos. La ruta
requiere reiniciar la aplicación actualizada. Importar no ejecuta el borrado.
