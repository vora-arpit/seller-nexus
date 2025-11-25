import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MakeOrderComponent } from './make-order/make-order.component';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { OrderListComponent } from './order-list/order-list.component';
import { EditOrderitemComponent } from './edit-orderitem/edit-orderitem.component';
import { OrderItemListComponent } from './orderitem-list/orderitem-list.component';
import { OrderService,OrderItemService } from '../../../core';
import { RouterModule } from '@angular/router';

@NgModule({
  declarations: [
    MakeOrderComponent,
    OrderListComponent,
    EditOrderitemComponent, OrderItemListComponent
  ],
  imports: [
    CommonModule,RouterModule,FormsModule,ReactiveFormsModule
  ],
  providers: [ OrderService,OrderItemService ],
})
export class OrderModule { }

