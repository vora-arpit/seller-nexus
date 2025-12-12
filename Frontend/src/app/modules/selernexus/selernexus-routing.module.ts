import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { JoomLoginComponent } from './joom-login/joom-login.component';
import { JoomProductsComponent } from './joom-products/joom-products.component';
import { JoomTransferComponent } from './joom-transfer/joom-transfer.component';
import { JoomTransferLogsComponent } from './joom-transfer-logs/joom-transfer-logs.component';
import { BulkTransferComponent } from './bulk-transfer/bulk-transfer.component';
import { HealthMonitorComponent } from './health-monitor/health-monitor.component';

const routes: Routes = [
  {
    path: '',
    component: JoomLoginComponent,
    data: { title: 'Joom Login' }
  },
  {
    path: 'joom-products',
    component: JoomProductsComponent,
    data: { title: 'Joom Products' }
  },
  {
    path: 'joom-transfer',
    component: JoomTransferComponent,
    data: { title: 'Joom Transfer' }
  },
  {
    path: 'joom-transfer-logs',
    component: JoomTransferLogsComponent,
    data: { title: 'Transfer Logs' }
  },
  {
    path: 'bulk-transfer',
    component: BulkTransferComponent,
    data: { title: 'Bulk Transfer' }
  },
  {
    path: 'health-monitor',
    component: HealthMonitorComponent,
    data: { title: 'Health Monitor' }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class SellerNexusRoutingModule { }
