import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// import { MakeOrderComponent } from './make-order/make-order.component';
// import { MakeOrderContainer } from './make-order/make-order.container';
import { OrderItemResolver } from './orderitem.resolver';
import { OrderListOrderComponent } from './order-list-order/order-list-order.component';
import { MakeOrderOrderComponent } from './make-order-order/make-order-order.component';
import { OrderitemListOrderComponent } from './orderitem-list-order/orderitem-list-order.component';
import { EditOrderitemOrderComponent } from './edit-orderitem-order/edit-orderitem-order.component';
// import { OrderListComponent } from './order-list/order-list.component';
// import { AddOrderitemComponent } from './add-orderitem/add-orderitem.component';
// import { OrderItemListComponent } from './orderitem-list/orderitem-list.component';



const routes: Routes = [
  {
    path: '',
    component: OrderListOrderComponent,
    data: { title: 'Order List' }
  },
  {
    path: 'new',
    component: MakeOrderOrderComponent,
    data: { title: 'New Order' }
  },
  {
    path: ':orderId',
    component: OrderitemListOrderComponent,
    data:{title: 'List of OrderItems'}
  },
  {
    path: ':orderid/:orderItemId',
    component: EditOrderitemOrderComponent,
    data: { title: 'Edit Order Item' }
  },
  {
    path: ':orderid/orderitem/new',
    component: EditOrderitemOrderComponent,
    data: { title: 'Add Order Item' }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [OrderItemResolver]
})
export class OrderItemRoutingModule { }
