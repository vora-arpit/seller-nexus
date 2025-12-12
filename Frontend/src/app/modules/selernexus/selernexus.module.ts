import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { JoomLoginComponent } from './joom-login/joom-login.component';
import { JoomProductsComponent } from './joom-products/joom-products.component';
import { JoomTransferComponent } from './joom-transfer/joom-transfer.component';
import { JoomTransferLogsComponent } from './joom-transfer-logs/joom-transfer-logs.component';
import { BulkTransferComponent } from './bulk-transfer/bulk-transfer.component';
import { HealthMonitorComponent } from './health-monitor/health-monitor.component';
import { SellerNexusRoutingModule } from './selernexus-routing.module';

@NgModule({
  declarations: [
    JoomLoginComponent,
    JoomProductsComponent,
    JoomTransferComponent,
    JoomTransferLogsComponent,
    BulkTransferComponent,
    HealthMonitorComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    SellerNexusRoutingModule
  ]
})
export class SellerNexusModule { }
