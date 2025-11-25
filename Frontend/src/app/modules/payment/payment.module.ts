import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { IconsModule } from '../../shared/icons.module';
import { MaterialModule } from '../../shared/material.module';
// import { CheckoutComponent } from './checkout/checkout.component';
import { PaymentSuccessComponent } from './success/success.component';
import { CancelComponent } from './cancel/cancel.component';
import { PaymentRoutingModule } from './payment-routing.module';


@NgModule({
  declarations: [
    // CheckoutComponent,
    PaymentSuccessComponent,CancelComponent
  ],
  imports: [
    CommonModule,PaymentRoutingModule,RouterModule,FormsModule,ReactiveFormsModule,IconsModule,MaterialModule
  ],
  providers: [ ],
})
export class PaymentModule { }

