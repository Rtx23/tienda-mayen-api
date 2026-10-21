import { Venta } from './models';

/** Agrupa por fecha local, igual que LocalDateTime de la API; no convierte a UTC. */
export function salesSummary(sales:Venta[], count:number, now=new Date()) {
 const key=(d:Date)=>`${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`;
 const days=Array.from({length:count},(_,i)=>{const d=new Date(now.getFullYear(),now.getMonth(),now.getDate()-count+1+i);return {key:key(d),label:d.toLocaleDateString('es-PE',{day:'2-digit',month:'short'}),cents:0,total:0};});
 const byDay=new Map(days.map(d=>[d.key,d]));
 const products=new Map<number,{id:number;name:string;units:number;cents:number}>();
 for(const sale of sales){const day=byDay.get(sale.fecha.slice(0,10));if(!day)continue;day.cents+=Math.round(Number(sale.total)*100);
  for(const detail of sale.detalles){const p=products.get(detail.productoId)||{id:detail.productoId,name:detail.nombreProducto,units:0,cents:0};p.units+=detail.cantidad;p.cents+=Math.round(Number(detail.subtotal)*100);products.set(p.id,p);}}
 for(const d of days)d.total=d.cents/100;
 return {days,top:[...products.values()].sort((a,b)=>b.units-a.units||a.id-b.id).slice(0,5),total:days.reduce((n,d)=>n+d.cents,0)/100};
}
