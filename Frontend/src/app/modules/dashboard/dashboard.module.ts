import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NgChartsModule } from 'ng2-charts';
import { AccountOverviewComponent } from './account-overview/account-overview.component';
import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard.routing.module';
import { LastOrdersComponent } from './last-orders/last-orders.component';
import { SharedModule } from '../../shared/shared.module';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import { ChartjsModule } from '@coreui/angular-chartjs';
import { BadgeModule, CardModule, GridModule } from '@coreui/angular';
import { OrderModule } from './order/order.module';


@NgModule({
  declarations: [ DashboardComponent,
    AccountOverviewComponent,
    LastOrdersComponent],
  imports: [
    CommonModule,SharedModule, OrderModule,
    NgChartsModule, CardModule,
    DashboardRoutingModule,NgxChartsModule,ChartjsModule
  ]
})
export class DashboardModule { }
