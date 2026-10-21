import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { StoreService } from '../core/store.service';
import { AuthService } from '../core/auth.service';
import { Item, Producto } from '../core/models';
import { money, imageFor, normalize, totalCart } from '../core/ui';
import { errorMessage } from '../core/auth.interceptor';
@Component({standalone:true,imports:[FormsModule],templateUrl:'./sale.component.html'})
export class SaleComponent {
 store=inject(StoreService);auth=inject(AuthService);router=inject(Router);query=signal('');cart=signal<Item[]>([]);busy=signal(false);error=signal('');invalid=signal<number[]>([]);
 money=money;image=imageFor;total=computed(()=>totalCart(this.cart(),this.store.products()));
 products=computed(()=>this.store.products().filter(p=>p.stock>0&&normalize(p.nombre).includes(normalize(this.query()))));
 product(id:number){return this.store.products().find(p=>p.id===id);}
 add(p:Producto){if(this.busy())return;const item=this.cart().find(i=>i.productoId===p.id);if(!item&&this.cart().length>=100){this.error.set('Puedes incluir hasta 100 productos por venta.');return;}if((item?.cantidad||0)>=p.stock){this.error.set('No hay más unidades disponibles de este producto.');return;}this.cart.update(items=>item?items.map(i=>i.productoId===p.id?{...i,cantidad:i.cantidad+1}:i):[...items,{productoId:p.id,cantidad:1}]);this.invalid.update(ids=>ids.filter(id=>id!==p.id));this.error.set('');}
 remove(id:number){if(!this.busy()){this.cart.update(items=>items.filter(i=>i.productoId!==id));this.invalid.update(ids=>ids.filter(i=>i!==id));}}
 quantity(id:number,value:number){const n=Number(value),p=this.product(id);if(!p||!Number.isInteger(n)||n<1||n>p.stock){this.invalid.update(ids=>[...new Set([...ids,id])]);this.error.set('La cantidad debe ser positiva y no superar el stock.');return;}this.invalid.update(ids=>ids.filter(i=>i!==id));this.cart.update(items=>items.map(i=>i.productoId===id?{...i,cantidad:n}:i));this.error.set('');}
 async save(){
   if(this.busy()||!this.cart().length||this.invalid().length)return;
   if(this.cart().some(i=>!this.product(i.productoId)||i.cantidad>this.product(i.productoId)!.stock)){this.error.set('Revisa los productos y el stock disponible.');return;}
   this.busy.set(true);this.error.set('');
   try{const sale=await this.store.registerSale(this.cart());this.cart.set([]);await this.store.refresh();await this.router.navigate(['/ventas'],{queryParams:{registrada:sale.id}});}
   catch(e){this.error.set(errorMessage(e));}finally{this.busy.set(false);}
 }
}

