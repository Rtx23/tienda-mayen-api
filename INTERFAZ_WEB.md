# Interfaz Angular de Tienda MAYEN

La interfaz ahora usa Angular 21 con componentes standalone, rutas protegidas,
formularios y HttpClient. El backend Java y PostgreSQL conservan sus reglas y
rutas. Se mantienen el diseño y las imágenes locales.

## Abrir en Eclipse con el puerto 8080

1. Actualizar gestion en Eclipse con F5.
2. Detener la ejecución anterior e iniciar GestionPostgreSQL con demo,postgres.
3. Abrir http://localhost:8080/ (o el server.port elegido).
4. Iniciar sesión con admin, editor o lector.

La versión Angular compilada está incluida en src/main/resources/static. Para
abrirla en 8080 no hace falta Node ni otro servidor. Se utilizan rutas con # para
poder refrescar sin añadir controladores Java para las pantallas.

## Desarrollo Angular en 4200

Usar Node 24 LTS, o una versión compatible con Angular 21. El equipo tiene Node 25;
esta entrega se compiló con el runtime Node 24 disponible. No se cambió Node global.

Desde frontend:

```powershell
npm ci
npm start
```

Abrir http://localhost:4200/. Mantener Java activo en 8080. proxy.conf.json envía
/api al backend; si Java usa 8081, cambiar target. PostgreSQL sigue en 5432.

## Compilar y publicar en Spring Boot

Desde frontend:

```powershell
npm test
npm run publish
```

publish compila y copia Angular a src/main/resources/static. Luego actualizar y
reiniciar Eclipse. Ejecutar .\mvnw.cmd package desde gestion para generar el JAR
con los archivos compilados. Maven no ejecuta npm automáticamente. Conservar
package-lock.json para instalaciones reproducibles.

## Componentes y servicios

Todos están dentro de frontend/src/app.

- pages/login.component: inicio de sesión.
- pages/shell.component: navegación, usuario, rol y cierre de sesión.
- pages/dashboard.component: inventario e indicadores calculados con la API.
- pages/products.component: búsqueda, filtros y CRUD autorizado.
- pages/sale.component: carrito, cantidades, stock y registro de venta.
- pages/history.component: historial y detalles con precios originales.
- app.routes.ts: rutas y guards de sesión y acceso a ventas.
- core/auth.service.ts: login, JWT en sessionStorage y vencimiento.
- core/auth.interceptor.ts: añade Bearer y cierra sesión ante 401.
- core/store.service.ts: llamadas HttpClient y estado de productos y ventas.
- frontend/src/styles.css y frontend/public/assets: diseño e imágenes locales.
- frontend/scripts/publicar.mjs: integración de la compilación con Spring Boot.

LECTOR consulta productos y existencias. EDITOR crea y edita productos y registra
ventas. ADMIN puede además eliminar productos no vendidos. El backend verifica
los permisos aunque se manipule el navegador. No hay botón de limpieza masiva.
No se almacenan contraseñas y logout elimina el JWT. El servidor confirma stock
actual y calcula el precio al registrar cada venta.

## Verificación

npm test comprueba los servicios reales de Angular mediante inyección de
dependencias: sesión, roles, guards, interceptor, carga de lector, aislamiento
entre sesiones y carrito. Angular build valida las plantillas con strictTemplates.
Maven verifica recursos públicos y API privada, junto con las pruebas del backend.
Estas comprobaciones no sustituyen la revisión completa en un navegador.

## Imágenes y respaldo

Las imágenes están en frontend/public/assets/images y static/assets/images.
bienvenida.png fue generada con la herramienta integrada: tienda de abarrotes
peruana con luz cálida, estantes y cítricos, sin personas ni marcas. Los SVG son
ilustraciones representativas de categorías. No se cargan imágenes externas.
La interfaz JavaScript anterior se respaldó en Entregas/Pruebas_interfaz/
respaldo_javascript, fuera del proyecto. Los módulos activos ahora son Angular.
