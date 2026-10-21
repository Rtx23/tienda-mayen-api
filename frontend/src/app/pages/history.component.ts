import { SalesAnalyticsComponent } from './sales-analytics.component';
import { Component, computed, inject, signal, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { StoreService } from '../core/store.service';
import { Venta } from '../core/models';
import { money,date } from '../core/ui';
import { errorMessage } from '../core/auth.interceptor';
@Component({standalone:true,imports:[RouterLink,SalesAnalyticsComponent],templateUrl:'./history.component.html'})
export class HistoryComponent {
 store=inject(StoreService);route=inject(ActivatedRoute);money=money;date=date;selected=signal<Venta|null>(null);error=signal('');
 registered=this.route.snapshot.queryParamMap.get('registrada');total=computed(()=>this.store.sales().reduce((sum,s)=>sum+Number(s.total),0));
 units=computed(()=>this.store.sales().reduce((sum,s)=>sum+s.detalles.reduce((n,d)=>n+d.cantidad,0),0));
 @ViewChild('dialog') dialog!:ElementRef<HTMLDialogElement>;
 async details(id:number){try{this.selected.set(await this.store.sale(id));this.dialog.nativeElement.showModal();}catch(e){this.error.set(errorMessage(e));}}
 close(){this.dialog.nativeElement.close();}
}

