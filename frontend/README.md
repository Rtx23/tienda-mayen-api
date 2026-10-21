# Tienda MAYEN con Angular

Frontend Angular 21. La guía completa está en ../INTERFAZ_WEB.md.

## Desarrollo

Requiere Node 24 LTS o una versión compatible con Angular 21.

```powershell
npm ci
npm start
```

Abrir localhost:4200 y mantener gestion activo en 8080. El proxy conecta las
peticiones /api con Java. Cambiar target en proxy.conf.json si Java usa otro puerto.

## Pruebas y versión integrada

```powershell
npm test
npm run publish
```

publish copia Angular compilado a los recursos estáticos de Spring Boot. Reiniciar
gestion y abrir localhost:8080. Esta modalidad no necesita otro servidor frontend.

Los módulos de pages son componentes standalone; core contiene servicios,
interceptor, guards y modelos. Las imágenes están en public/assets/images.
No se almacenan contraseñas. Las reglas definitivas de permisos y stock están
en Java; los guards también limitan las rutas que muestra Angular.

Documentación oficial de compatibilidad:
https://angular.dev/reference/versions
