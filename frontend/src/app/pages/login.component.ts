import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../core/auth.service';
import { StoreService } from '../core/store.service';
import { errorMessage } from '../core/auth.interceptor';
@Component({standalone:true,imports:[FormsModule],templateUrl:'./login.component.html'})
export class LoginComponent {
 auth=inject(AuthService);store=inject(StoreService);router=inject(Router);
 username='';password='';visible=signal(false);busy=signal(false);error=signal('');
 constructor(){if(this.auth.active())void this.router.navigateByUrl('/inicio');}
 async login(){this.busy.set(true);this.error.set('');try{await this.auth.login(this.username.trim(),this.password);this.password='';this.store.reset();await this.router.navigateByUrl('/inicio');}catch(e){this.error.set(errorMessage(e));}finally{this.busy.set(false);}}
}

