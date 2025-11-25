import { Component, Input, OnInit } from '@angular/core';
import { Account } from '../../../core';
import { ChartData, ChartDataset } from 'chart.js';

@Component({
  selector: 'app-account-overview',
  templateUrl: './account-overview.component.html',
  styleUrls: ['./account-overview.component.scss']
})
export class AccountOverviewComponent implements OnInit {
  @Input() public account: Account | undefined;

  public monthlySalesChartData: ChartData = {
    datasets: [],
    labels: ['Done', 'Goal']
  };
  public quarterlySalesChartData: ChartData = {
    datasets: [],
    labels: ['Done', 'Goal']
  };
  public monthlyCustomersChartData: ChartData = {
    datasets: [],
    labels: ['Done', 'Goal']
  };
  public quarterlyCustomersChartData: ChartData = {
    datasets: [],
    labels: ['Done', 'Goal']
  };

  constructor() {}

  
  ngOnInit() {
    // Initialize chart data arrays here
    this.monthlySalesChartData.datasets = this.account ? [
      { 
        data: [this.account.monthlySalesCurrent, this.account.monthlySalesCurrent * 0.5], 
        label: 'Monthly Sales',
        backgroundColor: [
          'green', // Background color for "Done"
          'color(srgb 0.64 0.02 0.02)'   // Background color for "Goal"
        ]
      }
    ] : [];
    
    this.quarterlySalesChartData.datasets = this.account ? [
      { 
        data: [this.account.quarterlySalesCurrent, this.account.quarterlySalesCurrent * 0.7], 
        label: 'Quarterly Sales',
        backgroundColor: [
          'green', // Background color for "Done"
          'color(srgb 0.64 0.02 0.02)'   // Background color for "Goal"
        ]
      }
    ] : [];
    
    this.monthlyCustomersChartData.datasets = this.account ? [
      { 
        data: [this.account.monthlyCustomersCurrent, this.account.monthlyCustomersCurrent * 3], 
        label: 'Monthly Customers',
        backgroundColor: [
          'green', // Background color for "Done"
          'color(srgb 0.64 0.02 0.02)'   // Background color for "Goal"
        ]
      }
    ] : [];
    
    this.quarterlyCustomersChartData.datasets = this.account ? [
      { 
        data: [this.account.quarterlyCustomersCurrent, this.account.quarterlyCustomersCurrent * 2], 
        label: 'Quarterly Customers',
        backgroundColor: [
          'green', // Background color for "Done"
          'color(srgb 0.64 0.02 0.02)'   // Background color for "Goal"
        ]
      }
    ] : [];
  }

}
