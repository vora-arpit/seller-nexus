// import { Component, OnInit } from '@angular/core';
// import { ActivatedRoute, Router } from '@angular/router';
// import { NotificationService, Order, OrderService } from '../../../../core';


// @Component({
//   selector: 'app-order-make-container',
//   template: `
//   <ng-container *ngIf="order">
//     <app-make-order
//       [order]="order" 
//       (submitted)="submitted()"
//       (canceled)="canceled()"
//       (deleted)="deleted()"
//     ></app-make-order>
//     </ng-container>`
// })
// export class MakeOrderContainer implements OnInit {

//   public order: Order;

//   constructor(
//     private route: ActivatedRoute,
//     private router: Router,
//     private orderService: OrderService,
//     private notificationService: NotificationService
//   ) { }

//   ngOnInit() {
//     // You may initialize the order item here if needed
//     this.order = this.route.snapshot.data['order'];
//     if (!this.order)
//       this.order;
//   }

//   private home() {
//     // Adjust the navigation path as per your application routing
//     this.router.navigate(['/orderitems']);
//   }

//   canceled() {
//     // console.log('order canceled because:',reason);
//     this.home();
//   }

//   deleted() {
//     if (this.order) {
//       this.orderService.deleteOrder(this.order.id)
//         .subscribe({
//           next: () => {
//             this.notificationService.showSuccess(`Order ${this.order.id} was deleted.`);
//             this.home();
//           },
//           error: (error) => {
//             console.error('Delete error:', error);
//             this.notificationService.showError('Failed to delete order.');
//           }
//         });
//     }
//   }
  
//   submitted() {
//     if (!this.order) return;
  
//     // You may add any necessary transformations or validations here
  
//     const action = this.order.id ? 'updated' : 'created';
  
//     const request = this.order.id ?
//       this.orderService.updateOrder(this.order.id, this.order) :
//       this.orderService.create(this.order);
  
//     request.subscribe({
//       next:(updatedOrder) => {
//         this.order = updatedOrder;
//         this.notificationService.showSuccess(`Order ${updatedOrder.id} ${action}`);
//         this.home();
//       },
//       error:(error) => {
//         console.error(`${action} error:`, error);
//         this.notificationService.showError(`Failed to ${action} order.`);
//       }
//   });
//   }

// }
