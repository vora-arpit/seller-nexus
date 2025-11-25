import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EditOrderitemComponent } from './edit-orderitem/edit-orderitem.component';
import { OrderItemListComponent } from './orderitem-list/orderitem-list.component';
import { OrderResolver } from './order.resolver';
import { MakeOrderComponent } from './make-order/make-order.component';
import { OrderListComponent } from './order-list/order-list.component';

const routes: Routes = [
  {
    path: '',
    component: OrderListComponent,
    data: { title: 'Order List' }
  },
  {
    path: 'new',
    component: MakeOrderComponent,
    data: { title: 'Add Order' }
  },
  {
    path: 'editorder/new',
    component: EditOrderitemComponent,
    data: { title: 'Edit OrderItem' }
  },
  {
    path: ':orderid',
    component: OrderItemListComponent,
    data: { title: 'Edit Order' },
  },
  {
    path: ':orderid/:orderItemId',
    component: EditOrderitemComponent,
    data: { title: 'Edit Order Item' }
  },
  {
    path: ':orderid/orderitem/new',
    component: EditOrderitemComponent,
    data: { title: 'Edit Order Item' }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [OrderResolver]
})
export class OrderRoutingModule { }
