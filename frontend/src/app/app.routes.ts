import { Routes } from '@angular/router';
import { authGuard, salesGuard } from './core/guards';
import { LoginComponent } from './pages/login.component';
import { ShellComponent } from './pages/shell.component';
import { DashboardComponent } from './pages/dashboard.component';
import { ProductsComponent } from './pages/products.component';
import { SaleComponent } from './pages/sale.component';
import { HistoryComponent } from './pages/history.component';
export const routes:Routes=[
 {path:'login',component:LoginComponent},
 {path:'',component:ShellComponent,canActivate:[authGuard],children:[
  {path:'inicio',component:DashboardComponent},{path:'productos',component:ProductsComponent},
  {path:'nueva-venta',component:SaleComponent,canActivate:[salesGuard]},
  {path:'ventas',component:HistoryComponent,canActivate:[salesGuard]},
  {path:'',pathMatch:'full',redirectTo:'inicio'}]},
 {path:'**',redirectTo:'inicio'}
];

