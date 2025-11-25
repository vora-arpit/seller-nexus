import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { AuthGuard } from '../../core';

const routes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    canActivate: [AuthGuard],
    data: { title: 'Dashboard' }
  },
  // {
  //   path: 'order',
  //   loadChildren: () => import('./order/order.routing.module').then(m => m.OrderRoutingModule),
  //   canActivate: [AuthGuard],
  //   data: { title: 'Dashboard' }
  // },
]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule { }