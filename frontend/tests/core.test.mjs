import '@angular/compiler';
import {test,beforeEach} from 'node:test';
import assert from 'node:assert/strict';
import {readFileSync} from 'node:fs';
import {Injector,runInInjectionContext} from '@angular/core';
import {HttpClient,HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {of,throwError} from 'rxjs';
import {AuthService} from '../.test-build/auth.service.js';
import {authGuard,salesGuard} from '../.test-build/guards.js';
import {authInterceptor} from '../.test-build/auth.interceptor.js';
import {StoreService} from '../.test-build/store.service.js';
import {totalCart,stockLabel,imageFor} from '../.test-build/ui.js';
let storage;
beforeEach(()=>{storage=new Map();globalThis.sessionStorage={getItem:k=>storage.get(k)||null,setItem:(k,v)=>storage.set(k,v),removeItem:k=>storage.delete(k)};});
function setup(role='ADMIN',httpExtra={}){
 const calls=[];const router={navigateByUrl:p=>{calls.push(p);return Promise.resolve(true)},createUrlTree:p=>p};
 const http={post:(url,body)=>of({accessToken:'token-'+role,roles:[role],username:body.username,expiresIn:3600}),...httpExtra};
 const injector=Injector.create({providers:[AuthService,StoreService,{provide:HttpClient,useValue:http},{provide:Router,useValue:router}]});
 return {injector,auth:injector.get(AuthService),store:injector.get(StoreService),calls};
}
test('login guarda JWT pero nunca la contraseña y logout elimina la sesión',async()=>{
 const {auth,calls}=setup();await auth.login('admin','clave-privada');assert.equal(auth.active(),true);assert.equal(auth.isAdmin(),true);
 assert.ok(!storage.get('mayen-session').includes('clave-privada'));auth.logout();assert.equal(storage.has('mayen-session'),false);assert.equal(auth.active(),false);assert.deepEqual(calls,['/login']);
});
test('roles limitan rutas y una sesión vencida no permite acceder',async()=>{
 const {auth,injector}=setup('LECTOR');await auth.login('lector','lector123');assert.equal(auth.canWrite(),false);assert.equal(auth.canSell(),false);
 assert.equal(runInInjectionContext(injector,()=>authGuard({},{})),true);assert.deepEqual(runInInjectionContext(injector,()=>salesGuard({},{})),['/inicio']);
 auth.session.set({...auth.session(),expiresAt:Date.now()-1});assert.deepEqual(runInInjectionContext(injector,()=>authGuard({},{})),['/login']);
});
test('EDITOR puede vender pero no eliminar',async()=>{const {auth}=setup('EDITOR');await auth.login('editor','editor123');assert.equal(auth.canSell(),true);assert.equal(auth.isAdmin(),false);});
test('interceptor adjunta Bearer y limpia la sesión ante 401',async()=>{
 const {auth,injector}=setup();await auth.login('admin','admin123');let sent;
 const request={url:'/api/productos',clone:options=>{sent=options;return {url:'/api/productos'};}};
 const stream=runInInjectionContext(injector,()=>authInterceptor(request,()=>throwError(()=>new HttpErrorResponse({status:401}))));
 stream.subscribe({error:()=>{}});assert.equal(sent.setHeaders.Authorization,'Bearer token-ADMIN');assert.equal(auth.active(),false);
});
test('LECTOR carga productos sin consultar ventas',async()=>{
 const calls=[];const {auth,store}=setup('LECTOR',{get:url=>{calls.push(url);return of([]);}});await auth.login('lector','lector123');await store.refresh();assert.deepEqual(calls,['/api/productos']);
});
test('una carga pendiente de otra sesión no reemplaza los datos',async()=>{
 let release;const {Subject}=await import('rxjs');const delayed=new Subject();
 const {auth,store}=setup('LECTOR',{get:()=>delayed});await auth.login('lector','lector123');const loading=store.refresh();auth.logout();delayed.next([{id:1,nombre:'privado'}]);delayed.complete();await loading;assert.deepEqual(store.products(),[]);
});
test('carrito calcula centavos y categorías reconocen acentos',()=>{
 assert.equal(totalCart([{productoId:1,cantidad:3}],[{id:1,precio:.1}]),.3);assert.equal(imageFor('Atún Florida'),'/assets/images/conserva.svg');assert.equal(stockLabel({stock:0,stockMinimo:5}),'Sin stock');
});
test('proxy de desarrollo incluye login y subrutas de la API',()=>{
 const proxy=JSON.parse(readFileSync('proxy.conf.json','utf8'));const pattern=new RegExp(Object.keys(proxy)[0]);
 for(const path of ['/api/auth/login','/api/productos/12','/api/ventas'])assert.ok(pattern.test(path));
 assert.equal(pattern.test('/assets/logo.svg'),false);assert.equal(pattern.test('/apix'),false);
});
import {salesSummary} from '../.test-build/analytics.js';
import {productErrors} from '../.test-build/validation.js';
import {errorMessage} from '../.test-build/auth.interceptor.js';
test('gráficos incluyen ceros, suman centavos y filtran fechas fuera del periodo',()=>{
 const sale=(fecha,total,id,cantidad)=>({fecha,total,detalles:[{productoId:id,nombreProducto:'Arroz',cantidad,subtotal:total}]});
 const r=salesSummary([sale('2026-10-21T08:00:00',.1,1,1),sale('2026-10-21T10:00:00',.2,1,2),sale('2026-10-14T10:00:00',90,2,99),sale('2026-10-22T00:00:00',80,2,99)],7,new Date(2026,9,21));
 assert.equal(r.days.length,7);assert.equal(r.days[0].key,'2026-10-15');assert.equal(r.days[0].total,0);assert.equal(r.days[6].total,.3);assert.equal(r.total,.3);assert.equal(r.top[0].units,3);assert.equal(r.top.length,1);
});
test('periodo de 30 días atraviesa mes y ordena productos por cantidad',()=>{
 const r=salesSummary([{fecha:'2026-10-01T15:00:00',total:8,detalles:[{productoId:1,nombreProducto:'Arroz',cantidad:1,subtotal:2},{productoId:2,nombreProducto:'Avena',cantidad:3,subtotal:6}]}],30,new Date(2026,9,21));
 assert.equal(r.days[0].key,'2026-09-22');assert.equal(r.top[0].id,2);assert.equal(r.total,8);
});
test('validación de producto rechaza campos inválidos',()=>{
 const valid={nombre:'Arroz',precio:5.5,stock:20,stockMinimo:5,fechaVencimiento:'2027-12-31'};assert.deepEqual(productErrors(valid),[]);
 for(const change of [{nombre:'   '},{precio:0},{precio:1.234},{precio:NaN},{stock:1.5},{stockMinimo:-1},{fechaVencimiento:'2027-02-30'},{fechaVencimiento:''}])assert.ok(productErrors({...valid,...change}).length);
});
test('errores HTTP explican el problema y conservan el detalle del servidor',()=>{
 for(const status of [0,400,401,403,404,409])assert.ok(errorMessage({status}).length>15);
 assert.equal(errorMessage({status:409,error:{mensaje:'Stock insuficiente'}}),'Stock insuficiente');
});
test('401 deja un aviso visible para volver a iniciar sesión',async()=>{
 const {auth,injector}=setup();await auth.login('admin','fixture');
 const request={url:'/api/productos',clone:()=>({url:'/api/productos'})};
 runInInjectionContext(injector,()=>authInterceptor(request,()=>throwError(()=>new HttpErrorResponse({status:401})))).subscribe({error:()=>{}});
 assert.equal(auth.active(),false);assert.match(auth.sessionNotice(),/Inicia sesión nuevamente/);
 await auth.login('admin','fixture');assert.equal(auth.sessionNotice(),'');
});
test('un fallo pendiente de otra sesión no contamina la sesión nueva',async()=>{
 const {Subject}=await import('rxjs');const delayed=new Subject();
 const {auth,store}=setup('LECTOR',{get:()=>delayed});await auth.login('lector','fixture');const pending=store.refresh();
 auth.logout();delayed.error(new HttpErrorResponse({status:403}));await pending;assert.equal(store.error(),'');
});
