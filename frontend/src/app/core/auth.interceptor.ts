import { inject } from '@angular/core';
import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
export const authInterceptor:HttpInterceptorFn=(request,next)=>{
  const auth=inject(AuthService); const protectedApi=request.url.startsWith('/api/') && request.url!=='/api/auth/login';
  if(protectedApi && auth.session()){
    if(!auth.active()){auth.logout();return throwError(()=>new Error('Tu sesión venció.'));}
    request=request.clone({setHeaders:{Authorization:'Bearer '+auth.session()!.accessToken}});
  }
  return next(request).pipe(catchError(error=>{
    if(protectedApi && error instanceof HttpErrorResponse && error.status===401)auth.logout();
    return throwError(()=>error);
  }));
};
export function errorMessage(e:any):string {
  return e?.error?.mensaje || e?.error?.detail || (e?.status===0?'No se pudo conectar con la tienda. Comprueba que gestion esté ejecutándose.':e?.status===400?'Revisa los datos ingresados.':e?.status===404?'No se encontró el registro. Actualiza la página.':e?.status===409?'La operación entra en conflicto con los datos actuales.':e?.status===403?'Tu rol no permite esta operación.':e?.status===401?'Usuario o contraseña incorrectos.':e?.message||'No se completó la operación.');
}

