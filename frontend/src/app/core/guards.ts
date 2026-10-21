import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
export const authGuard:CanActivateFn=()=>inject(AuthService).active() || inject(Router).createUrlTree(['/login']);
export const salesGuard:CanActivateFn=()=>inject(AuthService).canSell() || inject(Router).createUrlTree(['/inicio']);

