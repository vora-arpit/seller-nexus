// import { Component, OnDestroy, OnInit } from '@angular/core';
// import { Subscription } from 'rxjs';
// import { OrderService, Order } from '../../../../core';
// // import { Order, DashboardService } from '../../../core';

// @Component({
//   selector: 'app-order-list-container',
//   template: `
//     <app-order-list
//       [orders]="orders"
//     ></app-order-list>
//   `
// })
// export class OrderListContainer implements OnInit, OnDestroy {

//   orders: Order[] = [];
//   subscription: Subscription = new Subscription();

//   constructor(private orderService: OrderService) { }

//   ngOnInit() {
//     console.log('Order list container initialized');
//     this.subscription.add(
//       this.orderService.findAll().subscribe(
//         results => { this.orders = results; }
//       )
//     );
//   }
  
//   ngOnDestroy() {
//     console.log('Order list container destroyed');
//     this.subscription.unsubscribe();
//   }
// }
