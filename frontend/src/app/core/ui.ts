import { Producto, Item, Venta } from './models';
export const money=(v:number)=>new Intl.NumberFormat('es-PE',{style:'currency',currency:'PEN'}).format(v||0);
export const normalize=(s:string)=>s.normalize('NFD').replace(/[\u0300-\u036f]/g,'').toLowerCase();
export function imageFor(name:string){
 const n=normalize(name);
 for(const [words,image] of [['arroz|azucar|avena','granos'],['leche','leche'],['aceite','aceite'],['fideo|pasta','pasta'],['atun|conserva','conserva'],['galleta','galletas'],['agua|gaseosa|bebida','bebida'],['cafe','cafe'],['detergente|limpieza','limpieza']]){
 if(words.split('|').some(w=>n.includes(w)))return '/assets/images/'+image+'.svg';
 }return '/assets/images/bolsa.svg';
}
export const stockLabel=(p:Producto)=>p.stock===0?'Sin stock':p.stock<=p.stockMinimo?'Stock bajo':'Disponible';
export const stockClass=(p:Producto)=>p.stock===0?'danger':p.stock<=p.stockMinimo?'warning':'success';
export const totalCart=(cart:Item[],products:Producto[])=>cart.reduce((s,i)=>s+Math.round((products.find(p=>p.id===i.productoId)?.precio||0)*100)*i.cantidad,0)/100;
export function date(value:string,time=false){const d=new Date(time?value:value.slice(0,10)+'T12:00:00');return d.toLocaleDateString('es-PE',{day:'2-digit',month:'short',year:'numeric',...(time?{hour:'2-digit',minute:'2-digit'}:{})});}
export function days(sales:Venta[]){const now=new Date();return Array.from({length:7},(_,i)=>{const d=new Date(now);d.setDate(d.getDate()-6+i);const key=d.getFullYear()+'-'+String(d.getMonth()+1).padStart(2,'0')+'-'+String(d.getDate()).padStart(2,'0');return {label:d.toLocaleDateString('es-PE',{weekday:'short'}),total:sales.filter(s=>s.fecha.slice(0,10)===key).reduce((n,s)=>n+Number(s.total),0)};});}

