// import { Component, OnDestroy, OnInit } from '@angular/core';
// import { ActivatedRoute } from '@angular/router';
// import { Subject, Subscription } from 'rxjs';
// import { distinctUntilChanged, switchMap } from 'rxjs/operators';
// import { OrderItem, OrderItemService } from '../../../../core';

// @Component({
//   selector: 'app-order-list-container',
//   template: `
//     <app-orderItem-list
//       [orderItems]="orderItems"
//       (filtered)="filter($event)"
//     ></app-orderItem-list>
//   `
// })
// export class OrderItemListContainer implements OnInit, OnDestroy {

//   orderItems: OrderItem[] = [];
//   filter$ = new Subject<number>();
//   subscription: Subscription = new Subscription();

//   constructor(
//     private route: ActivatedRoute,
//     private orderItemService: OrderItemService
//   ) { }

//   ngOnInit() {
//     this.subscription.add(
//       this.route.paramMap.pipe(
//         switchMap(params => {
//           const orderId = Number(params.get('id')); // Extract the orderId from route parameters
//           console.log('Order ID:', orderId);
//           return this.orderItemService.getOrderItemsByOrderId(orderId);
//         })
//       ).subscribe(
//         result => { this.orderItems = result; }
//       )
//     );
//   }

//   ngOnDestroy() {
//     this.subscription.unsubscribe();
//   }

//   filter(orderId: number) {
//     this.filter$.next(orderId);
//   }
// }
