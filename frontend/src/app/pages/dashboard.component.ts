import { SalesAnalyticsComponent } from './sales-analytics.component';
import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { StoreService } from '../core/store.service';
import { money, imageFor, stockLabel, stockClass, days, date } from '../core/ui';
@Component({standalone:true,imports:[RouterLink,SalesAnalyticsComponent],templateUrl:'./dashboard.component.html'})
export class DashboardComponent {
 auth=inject(AuthService);store=inject(StoreService);money=money;image=imageFor;label=stockLabel;status=stockClass;date=date;
 low=computed(()=>this.store.products().filter(p=>p.stock<=p.stockMinimo));zero=computed(()=>this.store.products().filter(p=>p.stock===0));
 sold=computed(()=>this.store.sales().reduce((sum,s)=>sum+Number(s.total),0));
 inventory=computed(()=>this.store.products().reduce((sum,p)=>sum+p.stock*p.precio,0));
 units=computed(()=>this.store.products().reduce((sum,p)=>sum+p.stock,0));
 chart=computed(()=>days(this.store.sales()));maximum=computed(()=>Math.max(1,...this.chart().map(d=>d.total)));
}

