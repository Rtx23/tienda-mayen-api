import { ProductoInput } from './models';
export function productErrors(p:ProductoInput):string[] {
 const errors:string[]=[];
 if(!p.nombre?.trim()||p.nombre.trim().length>120)errors.push('Escribe un nombre de 1 a 120 caracteres.');
 if(typeof p.precio!=='number'||!Number.isFinite(p.precio)||p.precio<.01||p.precio>9999999999.99||Math.abs(p.precio*100-Math.round(p.precio*100))>.0001)errors.push('El precio debe ser positivo y tener como máximo dos decimales.');
 for(const [label,value] of [['El stock',p.stock],['El stock mínimo',p.stockMinimo]] as const){if(!Number.isInteger(value)||value<0||value>2147483647)errors.push(`${label} debe ser un entero entre 0 y 2147483647.`);}
 const date=p.fechaVencimiento;const parsed=new Date(date+'T12:00:00');
 if(!/^\d{4}-\d{2}-\d{2}$/.test(date)||!Number.isFinite(parsed.getTime())||parsed.getFullYear()!==Number(date.slice(0,4))||parsed.getMonth()+1!==Number(date.slice(5,7))||parsed.getDate()!==Number(date.slice(8,10)))errors.push('Selecciona una fecha de vencimiento válida.');
 return errors;
}
