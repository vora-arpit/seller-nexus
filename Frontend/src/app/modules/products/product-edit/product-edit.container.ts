// import { Component, OnInit } from '@angular/core';
// import { ActivatedRoute, Router } from '@angular/router';
// import { Product, ProductService, NotificationService } from '../../../core';
// import { Store } from '@ngrx/store';
// import { addProducts, deleteProducts, updateProducts } from '../../../store/actions/products.action';

// @Component({
//   selector: 'app-product-edit-container',
//   template: `
//     <ng-container *ngIf="product">
//       <app-product-edit 
//         [product]="product" 
//         (submitted)="submitted()"
//         (canceled)="canceled()"
//         (deleted)="deleted()"
//       ></app-product-edit>
//     </ng-container>`
// })
// export class ProductEditContainer implements OnInit {

//   public product?: Product; // Change to optional type

//   constructor(
//     private route: ActivatedRoute,
//     private router: Router,
//     private productService: ProductService,
//     private notificationService: NotificationService,
//     private store:Store
//   ) { }

//   ngOnInit() {
//     this.product = this.route.snapshot.data['product'];
//     console.log(this.product); // Check if product is defined
//     if (!this.product)
//       this.product = new Product();
//   }

//   private home() {
//     this.router.navigate(['/products']);
//   }

//   canceled() {
//     this.home();
//   }

 
//   deleted() {
//     if (this.product) {
//       this.store.dispatch(deleteProducts({ id: this.product.id }));
//     }
//   }



//   submitted() {
//     if (!this.product) return;
  
//     const action = this.product.id ? 'updated' : 'created';
  
//     if (this.product.id) {
//       this.store.dispatch(updateProducts({ id:this.product.id,product: this.product }));
//     } else {
//       this.store.dispatch(addProducts({ product: this.product }));
//     }
//   }
  
// }

















//  // deleted() {
//   //   if (this.product) {
//   //     this.productService.delete(this.product.id)
//   //       .subscribe(
//   //         () => {
//   //           this.notificationService.showSuccess(`Product ${this.product!.id} was deleted.`);
//   //           this.home();
//   //         },
//   //         error => {
//   //           console.error('Delete error:', error);
//   //           this.notificationService.showError('Failed to delete product.');
//   //         }
//   //       );
//   //   }
//   // }


  
//   // submitted() {
//   //   if (!this.product) return;
  
//   //   const action = this.product.id ? 'updated' : 'created';
  
//   //   const request = this.product.id ?
//   //     this.productService.update(this.product.id, this.product) :
//   //     this.productService.create(this.product);
  
//   //   request.subscribe(
//   //     updatedProduct => {
//   //       this.product = updatedProduct;
//   //       this.notificationService.showSuccess(`Product ${updatedProduct.id} ${action}`);
//   //       this.home();
//   //     },
//   //     error => {
//   //       console.error(`${action} error:`, error);
//   //       this.notificationService.showError(`Failed to ${action} product.`);
//   //     }
//   //   );
//   // }