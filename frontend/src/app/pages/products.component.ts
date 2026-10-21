import { Component, computed, inject, signal, ViewChild, ElementRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { StoreService } from '../core/store.service';
import { AuthService } from '../core/auth.service';
import { Producto, ProductoInput } from '../core/models';
import { money, imageFor, stockLabel, stockClass, normalize, date } from '../core/ui';
import { errorMessage } from '../core/auth.interceptor';
import { productErrors } from '../core/validation';
@Component({standalone:true,imports:[FormsModule],templateUrl:'./products.component.html'})
export class ProductsComponent {
 store=inject(StoreService);auth=inject(AuthService);route=inject(ActivatedRoute);
 query=signal('');filter=signal(this.route.snapshot.queryParamMap.get('filtro')||'todos');
 filtered=computed(()=>this.store.products().filter(p=>normalize(p.nombre).includes(normalize(this.query()))&&(this.filter()==='todos'||this.filter()==='bajo'&&p.stock<=p.stockMinimo||this.filter()==='cero'&&p.stock===0)));
 money=money;image=imageFor;label=stockLabel;status=stockClass;date=date;
 @ViewChild('dialog') dialog!:ElementRef<HTMLDialogElement>;
 mode=signal<'edit'|'delete'>('edit');error=signal('');notice=signal('');busy=signal(false);id:number|undefined;selected:Producto|null=null;
 model:ProductoInput=this.blank();
 issues(){return productErrors(this.model);}
 blank():ProductoInput{return {nombre:'',precio:0,stock:0,stockMinimo:0,fechaVencimiento:''};}
 edit(p?:Producto){if(!this.auth.canWrite())return;this.mode.set('edit');this.id=p?.id;this.model=p?{nombre:p.nombre,precio:p.precio,stock:p.stock,stockMinimo:p.stockMinimo,fechaVencimiento:p.fechaVencimiento}:this.blank();this.error.set('');this.dialog.nativeElement.showModal();}
 remove(p:Producto){if(!this.auth.isAdmin())return;this.selected=p;this.mode.set('delete');this.error.set('');this.dialog.nativeElement.showModal();}
 close(){if(!this.busy())this.dialog.nativeElement.close();}
 async save(){if(this.busy()||!this.auth.canWrite())return;if(this.issues().length){this.error.set(this.issues().join(' '));return;}this.busy.set(true);this.error.set('');try{await this.store.saveProduct({...this.model,nombre:this.model.nombre.trim()},this.id);this.dialog.nativeElement.close();this.notice.set(this.id?'Producto actualizado.':'Producto creado.');await this.store.refresh();}catch(e){this.error.set(errorMessage(e));}finally{this.busy.set(false);}}
 async confirmDelete(){if(!this.selected)return;this.busy.set(true);try{await this.store.removeProduct(this.selected.id);this.dialog.nativeElement.close();this.notice.set('Producto eliminado.');await this.store.refresh();}catch(e){this.error.set(errorMessage(e));}finally{this.busy.set(false);}}
}

