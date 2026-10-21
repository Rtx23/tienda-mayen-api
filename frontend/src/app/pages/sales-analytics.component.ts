import { Component, computed, inject, signal } from '@angular/core';
import { StoreService } from '../core/store.service';
import { salesSummary } from '../core/analytics';
import { money, imageFor } from '../core/ui';

@Component({selector:'app-sales-analytics',standalone:true,template:`
<section class="analytics" aria-label="Análisis de ventas">
 <div class="analytics-heading"><div><span class="eyebrow">RESULTADOS DE TU TIENDA</span><h2>Las ventas, en perspectiva</h2></div><div class="filter-group" aria-label="Periodo de análisis">@for(n of [7,30];track n){<button [class.active]="period()===n" [attr.aria-pressed]="period()===n" (click)="period.set(n)">Últimos {{n}} días</button>}</div></div>
 <div class="analytics-grid"><article class="panel trend-panel"><div class="panel-head"><h2>Evolución diaria</h2><strong>{{money(summary().total)}}</strong></div>
 <p class="chart-caption">Importe vendido · {{summary().days[0].label}} al {{summary().days[period()-1].label}}</p>
 <svg class="trend-chart" viewBox="0 0 620 190" role="img" [attr.aria-label]="'Ventas de los últimos '+period()+' días: '+money(summary().total)">
 <defs><linearGradient id="sales-fill" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stop-color="#7cac91" stop-opacity=".5"/><stop offset="100%" stop-color="#7cac91" stop-opacity=".03"/></linearGradient></defs>
 @for(y of [20,65,110,155];track y){<line x1="20" x2="600" [attr.y1]="y" [attr.y2]="y" stroke="#e9eee8"/>}
 <path [attr.d]="area()" fill="url(#sales-fill)"/><polyline [attr.points]="points()" fill="none" stroke="#24634b" stroke-width="3" stroke-linejoin="round"/>
 @for(d of summary().days;track d.key;let i=$index){<circle [attr.cx]="20+i*580/(period()-1)" [attr.cy]="155-d.total/maximum()*130" r="3" fill="#24634b"><title>{{d.label}}: {{money(d.total)}}</title></circle>}
 </svg><details class="chart-data"><summary>Ver importes diarios</summary><table><thead><tr><th>FECHA</th><th>TOTAL</th></tr></thead><tbody>@for(d of summary().days;track d.key){<tr><td>{{d.label}}</td><td>{{money(d.total)}}</td></tr>}</tbody></table></details>
 @if(!summary().top.length){<p class="chart-caption">No hay ventas en este periodo.</p>}</article>
 <article class="panel"><div class="panel-head"><h2>Productos más vendidos</h2><span class="section-tag">Por unidades</span></div>
 @for(p of summary().top;track p.id){<div class="ranking-row"><img [src]="image(p.name)" alt=""><div><strong>{{p.name}}</strong><small>{{p.units}} unidades · {{money(p.cents/100)}}</small><div class="ranking-track"><span [style.width.%]="p.units/summary().top[0].units*100"></span></div></div></div>}
 @empty{<div class="empty"><img src="/assets/images/bolsa.svg" alt=""><strong>Pronto verás tus favoritos</strong>Registra ventas para comparar productos.</div>}</article></div>
</section>`})
export class SalesAnalyticsComponent {
 store=inject(StoreService);period=signal(7);money=money;image=imageFor;
 summary=computed(()=>salesSummary(this.store.sales(),this.period()));
 maximum=computed(()=>Math.max(1,...this.summary().days.map(d=>d.total)));
 points=computed(()=>this.summary().days.map((d,i)=>`${20+i*580/(this.period()-1)},${155-d.total/this.maximum()*130}`).join(' '));
 area=computed(()=>`M20,155 L${this.points().replaceAll(' ',' L')} L600,155 Z`);
}
