// import { Component, OnDestroy, OnInit } from '@angular/core';
// import { Subject, Subscription } from 'rxjs';
// import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
// import { Order } from '../../../core'; // Import Product and ProductService from the appropriate location
// import { Store } from '@ngrx/store';
// import { getOrder } from '../../../store/actions/order.action';
// import { getAllOrders } from '../../../store/selectors/order.selector';

// @Component({
//   selector: 'app-product-list-container',
//   template: `
//     <app-order-list-order
//       [orders]="orders"
//       (filtered)="filter($event)"
//     ></app-order-list-order>
//   `
// })
// export class OrderListContainer implements OnInit, OnDestroy {

//   orders: Order[] = [];
//   filter$ = new Subject<string>();
//   subscription: Subscription = new Subscription();

//   constructor(
//      private store: Store
//   ) { }

//   ngOnInit() {
//     this.subscription.add(
//       this.filter$.pipe(
//         debounceTime(300),
//         distinctUntilChanged(),
//       ).subscribe(
//         text => {
//           // Pass the filter text to the service method
//           this.store.dispatch(getOrder());
          
//         }
//       )
//     )
//     this.filter$.next('');
  
//     this.store.select(getAllOrders).subscribe((orders) => 
//       this.orders = orders
//       // console.log(this.orders);
//     )
//     console.log(this.orders);
//   }
  

//   filter(text: string) {
//     this.filter$.next(text);
//   }

//   ngOnDestroy() {
//     this.subscription.unsubscribe();
//   }
// }
