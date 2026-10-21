# APF3 — Interfaz Angular de Tienda MAYEN

El frontend Angular se integra con la API REST de Spring Boot y PostgreSQL. Conserva el login y añade imágenes locales al resumen, catálogo, registro e historial de ventas.

## Checklist

| Requisito | Implementación |
|---|---|
| Angular organizado | Componentes standalone en `frontend/src/app/pages`, servicios y modelos en `core` |
| Navegación | `app.routes.ts`, `ShellComponent`, rutas protegidas |
| Formularios validados | `ProductsComponent`, `validation.ts` y cantidades del carrito |
| API real | `StoreService` y HttpClient hacia `/api` |
| CRUD visible | Catálogo y diálogos para crear, editar y eliminar según permisos |
| Errores HTTP | Mensajes para 400, 401, 403, 404 y 409; reintento de carga |
| Login con JWT | `AuthService`, interceptor y guards; autorización comprobada también en Java |
| Flujo completo | Producto creado y editado en navegador, venta registrada, detalle y stock contrastados con SQL en PostgreSQL |

## Mejoras visuales

- Fotografías de apoyo guardadas dentro del proyecto; no se necesitan servicios externos para cargarlas.
- Evolución de ventas de los últimos 7 o 30 días, con tabla accesible de importes diarios.
- Cinco productos más vendidos por cantidad, con importe asociado.
- Indicadores de inventario, existencias y ventas calculados con la API.

Los importes se suman en centavos. Los días sin operaciones aparecen en cero. Los gráficos agrupan por identificador del producto y usan fechas locales, coherentes con el backend. Son ingresos por ventas, no ganancias netas.

## Ejecución y comprobación

La compilación Angular está incluida en `src/main/resources/static`. Iniciar Spring Boot con el perfil PostgreSQL y la conexión configurada localmente, y abrir su URL en el navegador. No subir la configuración privada de conexión.

Para modificar el frontend: usar Node 24, ejecutar `npm ci`, `npm test` y `npm run publish` desde `frontend`. Reiniciar Java después de publicar. Para desarrollo: `npm start`; el proxy conecta Angular con el backend local.

Con Java 21: `./mvnw.cmd package` desde la raíz del proyecto. Las pruebas de integración Maven usan H2 aislada; la revisión del navegador se realizó además contra PostgreSQL, en un esquema separado de los datos habituales.

La revisión del navegador incluyó login incorrecto/correcto, validaciones, creación y edición de producto, cantidad superior al stock, venta, detalle, stock final, filtros del gráfico, cierre de sesión y permisos de lector y editor. La eliminación se comprobó por API sobre un producto temporal. No se confirmó una eliminación en el navegador.

Se corrigió la lectura del identificador del producto en detalles de venta: el acceso mediante getter funciona con los proxies LAZY de Hibernate. El test de integración comprueba el identificador al recuperar las ventas.

Las capturas, registros de ejecución y comprobaciones SQL locales se conservan fuera del repositorio público. No se versionan datos de negocio, tokens ni credenciales. Las credenciales sintéticas de los tests son exclusivas de sus fixtures.

## Trabajo posterior

El registro de gastos queda para una ampliación futura y no se implementa en este avance.
