import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OrderItemService, OrderService } from '../../core';
import { MakeOrderOrderComponent } from './make-order-order/make-order-order.component';
import { OrderListOrderComponent } from './order-list-order/order-list-order.component';
import { EditOrderitemOrderComponent } from './edit-orderitem-order/edit-orderitem-order.component';
import { OrderitemListOrderComponent } from './orderitem-list-order/orderitem-list-order.component';
import { OrderItemRoutingModule } from './orderitem-routing.module';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IconsModule } from '../../shared/icons.module';
import { MaterialModule } from '../../shared/material.module';


@NgModule({
  declarations: [
    MakeOrderOrderComponent,
    OrderListOrderComponent,
    OrderitemListOrderComponent,
    EditOrderitemOrderComponent
  ],
  imports: [
    CommonModule,OrderItemRoutingModule,RouterModule,FormsModule,ReactiveFormsModule,IconsModule,MaterialModule
  ],
  providers: [ OrderService,OrderItemService ],
})
export class OrderitemModule { }

