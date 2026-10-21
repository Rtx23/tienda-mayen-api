import { bootstrapApplication } from '@angular/platform-browser';
import { provideZonelessChangeDetection } from '@angular/core';
import { provideRouter, withHashLocation } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { authInterceptor } from './app/core/auth.interceptor';
bootstrapApplication(AppComponent,{providers:[provideZonelessChangeDetection(),provideRouter(routes,withHashLocation()),provideHttpClient(withInterceptors([authInterceptor]))]}).catch(console.error);

