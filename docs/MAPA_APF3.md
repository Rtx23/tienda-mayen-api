# Dónde se implementa el checklist APF3

Las rutas siguientes son relativas a la raíz del proyecto. Las clases Java están bajo `src/main/java/com/tienda/gestion`.

| Requisito | Archivo o clase | Responsabilidad |
|---|---|---|
| Angular organizado | `frontend/src/app/pages` y `frontend/src/app/core` | Separar pantallas, servicios y modelos |
| Navegación | `frontend/src/app/app.routes.ts` | Rutas login, inicio, productos, nueva-venta y ventas |
| Validaciones | `frontend/src/app/core/validation.ts` | Reglas de entrada de productos |
| API REST | `frontend/src/app/core/store.service.ts` | Solicitudes HTTP de productos y ventas |
| CRUD visible | `frontend/src/app/pages/products.component.ts` | Operaciones del catálogo desde la pantalla |
| Errores HTTP | `frontend/src/app/core/ui.ts` | Mensajes para fallos de la API |
| JWT | `frontend/src/app/core/auth.service.ts` y `auth.interceptor.ts` | Mantener sesión y adjuntar Bearer a solicitudes protegidas |
| Acceso por rol | `frontend/src/app/core/guards.ts` y `config/SecurityConfig.java` | Restringir navegación y aplicar permisos en servidor |
| Persistencia | `adapter/out/persistence/ProductoJpaAdapter.java` y `VentaJpaAdapter.java` | Adaptar los puertos del dominio a JPA |
| Registro de venta | `application/service/VentaService.java` | Coordinar la operación de venta y el inventario |
| Gráficos | `frontend/src/app/core/analytics.ts` | Calcular evolución y productos más vendidos |

Flujo para explicar una venta: componente Angular → StoreService → VentaController → VentaService → adaptador JPA → PostgreSQL. El navegador no se conecta directamente a PostgreSQL.

Las restricciones visuales no sustituyen la autorización Java. Cambiar el HTML del navegador no concede permisos en la API. Ver [APF3.md](../APF3.md) para el alcance y las comprobaciones realizadas.
