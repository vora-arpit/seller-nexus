import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { JoomLoginComponent } from './joom-login/joom-login.component';
import { JoomProductsComponent } from './joom-products/joom-products.component';
import { JoomTransferComponent } from './joom-transfer/joom-transfer.component';
import { JoomTransferLogsComponent } from './joom-transfer-logs/joom-transfer-logs.component';
import { SellerNexusRoutingModule } from './selernexus-routing.module';

@NgModule({
  declarations: [
    JoomLoginComponent,
    JoomProductsComponent,
    JoomTransferComponent,
    JoomTransferLogsComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SellerNexusRoutingModule
  ]
})
export class SellerNexusModule { }
