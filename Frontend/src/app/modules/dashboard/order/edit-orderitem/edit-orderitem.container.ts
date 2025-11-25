// import { Component, OnInit } from '@angular/core';
// import { ActivatedRoute, Router } from '@angular/router';
// import { NotificationService, OrderItem, OrderItemService } from '../../../../core';

// @Component({
//   selector: 'app-order-make-container',
//   template: `
//     <app-edit-orderitem
//       [orderitem]="orderItem" 
//       (submitted)="submitted()"
//       (canceled)="canceled()"
//       (deleted)="deleted()"
//     ></app-edit-orderitem>`
// })
// export class editOrderItemContainer implements OnInit {

//   public orderItem: OrderItem;
//   public orderItemId: bigint;

//   constructor(
//     private route: ActivatedRoute,
//     private router: Router,
//     private orderItemService: OrderItemService,
//     private notificationService: NotificationService
//   ) { }

//   ngOnInit() {
//     // You may initialize the order item here if needed
//     this.orderItemId = BigInt(this.route.snapshot.paramMap.get('orderItemId'));

//     // Fetch the order item details based on the ID
//     this.orderItemService.getOrderItemById(this.orderItemId).subscribe(
//       (result) => {
//         this.orderItem = result;
//         console.log(this.orderItem);
//       },
//       (error) => {
//         console.error('Error fetching order item:', error);
//       }
//     );
//   }

//   private home() {
//     // Adjust the navigation path as per your application routing
//     this.router.navigate(['/orderitems']);
//   }

//   canceled() {
//     this.home();
//   }

//   deleted() {
//     if (this.orderItem) {
//       this.orderItemService.deleteOrderItem(this.orderItem.id)
//         .subscribe({
//           next: () => {
//             this.notificationService.showSuccess(`OrderItem ${this.orderItem.id} was deleted from order.`);
//             this.home();
//           },
//           error: (error) => {
//             console.error('Delete error:', error);
//             this.notificationService.showError('Failed to delete orderitem.');
//           }
//         });
//     }
//   }
  
//   submitted() {
//     if (!this.orderItem) return;
  
//     // You may add any necessary transformations or validations here
  
//     const action = this.orderItem.id ? 'updated' : 'created';
  
//     const request = this.orderItem.id ?
//       this.orderItemService.updateOrderItem(this.orderItem.id, this.orderItem) :
//       this.orderItemService.createOrderItem(this.orderItem);
  
//     request.subscribe({
//       next: (updatedOrder) => {
//         this.orderItem = updatedOrder;
//         this.notificationService.showSuccess(`Order ${updatedOrder.id} ${action}`);
//         this.home();
//       },
//       error: (error) => {
//         console.error(`${action} error:`, error);
//         this.notificationService.showError(`Failed to ${action} order.`);
//       }
//     });
//   }
// }



// <form [formGroup]="angForm" (ngSubmit)="onSubmit()" novalidate class="form">
//   <div class="grid-2 mgb-small">
//     <div class="panel white col">
//       <h4>Main Info</h4>
//       <label for="price" class="mgt-small">Price <span class="text-red">*</span></label>
//       <input type="text" id="price"  formControlName="price" [(ngModel)]="orderitem.price">
//       <div *ngIf="angForm.controls['price']?.invalid && (angForm.controls['price']?.dirty || angForm.controls['price']?.touched)">
//         <div *ngIf="angForm.controls['price']?.errors?.['required']" class="input-error-msg">price is required</div>
//       </div>

//       <label for="quantity" class="mgt-small">Quantity <span class="text-red">*</span></label>
//       <input type="number" id="quantity" formControlName="quantity"  [(ngModel)]="orderitem.quantity">
//       <div *ngIf="angForm.controls['quantity']?.invalid && (angForm.controls['quantity']?.dirty || angForm.controls['quantity']?.touched)">
//         <div *ngIf="angForm.controls['quantity']?.errors?.['required']" class="input-error-msg">Price is required</div>
//       </div>

//       <label for="product" class="mgt-small">Product  <span class="text-red">*</span></label>
//       <input type="number" id="product" formControlName="product" [(ngModel)]="orderitem.product">
//       <div *ngIf="angForm.controls['product']?.invalid && (angForm.controls['product']?.dirty || angForm.controls['product']?.touched)">
//         <div *ngIf="angForm.controls['product']?.errors?.['required']" class="input-error-msg">Product id is required</div>
//       </div>
//     </div>

//     <div class="panel white col">
//       <h4>Additional Info</h4>
//     </div>
//   </div>

//   <div class="grid-2">
//     <div class="col">
//       <a *ngIf="orderitem.id" class="btn btn-secondary" (click)="confirmDelete()">Delete</a>
//     </div>
//     <div class="col text-right">
//       <a class="btn btn-secondary" (click)="canceled.emit('')">Cancel</a>
//       <button class="btn btn-primary" [disabled]="!angForm.valid">Save</button>
//     </div>
//   </div>
// </form> -->

