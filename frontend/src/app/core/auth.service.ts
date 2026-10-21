import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { Session } from './models';
@Injectable({providedIn:'root'})
export class AuthService {
  private http=inject(HttpClient); private router=inject(Router);
  session=signal<Session|null>(this.restore());
  private restore():Session|null {
    try { const s=JSON.parse(sessionStorage.getItem('mayen-session')||'null'); return s?.accessToken && Array.isArray(s.roles) && s.expiresAt>Date.now()?s:null; } catch { return null; }
  }
  active(){return !!this.session() && this.session()!.expiresAt>Date.now();}
  has(...roles:string[]){return this.active() && this.session()!.roles.some(r=>roles.includes(r));}
  canWrite(){return this.has('EDITOR','ADMIN');} canSell(){return this.canWrite();} isAdmin(){return this.has('ADMIN');}
  async login(username:string,password:string){
    const s=await firstValueFrom(this.http.post<Session>('/api/auth/login',{username,password}));
    if(!s.accessToken || !Array.isArray(s.roles))throw new Error('No se recibió una sesión válida.');
    const session={...s,expiresAt:Date.now()+s.expiresIn*1000}; this.session.set(session);
    sessionStorage.setItem('mayen-session',JSON.stringify(session));
  }
  logout(){ this.session.set(null);sessionStorage.removeItem('mayen-session');void this.router.navigateByUrl('/login'); }
}

