import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom, forkJoin, of } from 'rxjs';
import { AuthService } from './auth.service';
import { errorMessage } from './auth.interceptor';
import { Producto, ProductoInput, Venta, Item } from './models';
@Injectable({providedIn:'root'})
export class StoreService {
  private http=inject(HttpClient); private auth=inject(AuthService);
  products=signal<Producto[]>([]); sales=signal<Venta[]>([]);loading=signal(false);error=signal('');
  reset(){this.products.set([]);this.sales.set([]);this.error.set('');}
  async refresh(){
    const token=this.auth.session()?.accessToken;this.loading.set(true);this.error.set('');
    try{
      const result=await firstValueFrom(forkJoin({products:this.http.get<Producto[]>('/api/productos'),sales:this.auth.canSell()?this.http.get<Venta[]>('/api/ventas'):of([] as Venta[])}));
      if(token===this.auth.session()?.accessToken){this.products.set(result.products);this.sales.set(result.sales.sort((a,b)=>b.id-a.id));}
    }catch(e){this.error.set(errorMessage(e));}finally{this.loading.set(false);}
  }
  saveProduct(body:ProductoInput,id?:number){return firstValueFrom(id?this.http.put<Producto>('/api/productos/'+id,body):this.http.post<Producto>('/api/productos',body));}
  removeProduct(id:number){return firstValueFrom(this.http.delete('/api/productos/'+id));}
  registerSale(items:Item[]){return firstValueFrom(this.http.post<Venta>('/api/ventas',{items}));}
  sale(id:number){return firstValueFrom(this.http.get<Venta>('/api/ventas/'+id));}
}

