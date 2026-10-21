import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet, NavigationEnd } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AuthService } from '../core/auth.service';
import { StoreService } from '../core/store.service';
@Component({standalone:true,imports:[RouterLink,RouterLinkActive,RouterOutlet],templateUrl:'./shell.component.html'})
export class ShellComponent {
 auth=inject(AuthService);store=inject(StoreService);router=inject(Router);title=signal('Resumen');
 today=new Date().toLocaleDateString('es-PE',{day:'numeric',month:'long',year:'numeric'});
 constructor(){this.router.events.pipe(takeUntilDestroyed()).subscribe(e=>{if(e instanceof NavigationEnd)this.title.set(({'/inicio':'Resumen','/productos':'Productos','/nueva-venta':'Nueva venta','/ventas':'Historial de ventas'} as Record<string,string>)[e.urlAfterRedirects.split('?')[0]]||'Resumen');});void this.store.refresh();}
 logout(){this.store.reset();this.auth.logout();}
}

