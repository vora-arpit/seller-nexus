

import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NotificationService, Product } from '../../../core';
import { Store } from '@ngrx/store';
import { addProducts, deleteProducts, getProducts, updateProducts } from '../../../store/actions/products.action';
import { ActivatedRoute, Router } from '@angular/router';
import { getAllProducts } from '../../../store/selectors/products.selector';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-product-edit',
  templateUrl: './product-edit.component.html',
  styleUrls: ['./product-edit.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductEditComponent implements OnInit {
  

  // @Input() public product: Product = new Product();
  // @Input() public error: string = '';

  @Output() public submitted: EventEmitter<Product> = new EventEmitter();
  // @Output() public deleted: EventEmitter<string> = new EventEmitter();
  products: Product[] = [];
  subscription: Subscription;
  productId: string | null = null;
  
  @Input() public product: Product = new Product();

  public angForm: FormGroup = this.fb.group({});

  constructor(
    private fb: FormBuilder, private route: ActivatedRoute,
    private store: Store,private router:Router,private notificationService:NotificationService
  ) { 
      this.subscription= this.store.select(getAllProducts).subscribe((product) => {
        this.products = product;
    });
}

ngOnInit() {
  this.createForm();
  this.store.dispatch(getProducts());

  this.productId = this.route.snapshot.paramMap.get('id'); // Assign productId here

  const product = this.route.snapshot.data['product'];
  if (product && product.id) {
    this.populateForm(product);
  }
}

  createForm() {
    this.angForm = this.fb.group({
      // id: 0,
      name: ['', [Validators.required]],//, Validators.pattern('^[a-zA-Z]*$')
      price: ['', [Validators.required]],//, Validators.pattern('[0-9]*')
      quantityInStock: ['', [Validators.required]]//, Validators.pattern('[0-9]*')
    });
  }
  onSubmit() {
    // console.log("Submit button clicked");
  if (this.angForm.invalid) {
    this.notificationService.showError("Form is invalid");
    return;
  }
    const formData = this.angForm.value;
    // console.log("formData:"+this.product+"/n"+this.product.id);
    if (!this.productId) {
      this.store.dispatch(addProducts({ product: formData }));
      this.notificationService.showSuccess("Product added successfully");
    } else {
      this.store.dispatch(updateProducts({ id:this.productId? this.productId:null, product: formData }));
      this.notificationService.showSuccess("Product updated successfully");
    }
    

    this.submitted.next(formData);
  }


  populateForm(product: Product) {
    this.angForm.patchValue({
      // id: product.id,
      name: product.name,
      price: product.price,
      quantityInStock: product.quantityInStock
    });
  }
  confirmDelete() {
    if (!this.productId) {
      this.notificationService.showError("Cannot delete product without id");
      return;
    }

    this.store.dispatch(deleteProducts({ id: BigInt(this.productId) }));
    this.notificationService.showSuccess("Product deleted Successfully")
  }


  canceled() {
    this.router.navigate(['/products']);
  }
}








// import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
// import { FormBuilder, FormGroup, Validators } from '@angular/forms';
// import { Product } from '../../../core';
// import { Store } from '@ngrx/store';
// import { updateProducts } from '../../../store/actions/products.action';

// @Component({
//   selector: 'app-product-edit',
//   templateUrl: './product-edit.component.html',
//   styleUrls: ['./product-edit.component.scss'],
//   changeDetection: ChangeDetectionStrategy.OnPush
// })
// export class ProductEditComponent implements OnInit {
  

//   @Input() public product: Product = new Product();
//   @Input() public error: string = '';

//   @Output() public submitted: EventEmitter<Product> = new EventEmitter();
//   @Output() public canceled: EventEmitter<string> = new EventEmitter();
//   @Output() public deleted: EventEmitter<string> = new EventEmitter();

//   public angForm: FormGroup = this.fb.group({});

//   constructor(
//     private fb: FormBuilder,private store:Store
//   ) { }

//   ngOnInit() {
//     this.createForm();
//   }

//   createForm() {
//     this.angForm = this.fb.group({
//       name: [this.product?.name || '', [Validators.required, Validators.pattern('^[a-zA-Z]*$')]],
//       price: [this.product?.price || '', [Validators.required, Validators.pattern('[0-9]*')]],
//       quantity_in_stock: [this.product?.quantityInStock || '', [Validators.required, Validators.pattern('[0-9]*')]]
//     });
//   }

//   // createForm() {
//   //     this.angForm = this.fb.group({
//   //       name: ['', [Validators.required, Validators.pattern('^[a-zA-Z]*$')]],
//   //       price: ['', [Validators.required, Validators.pattern('[0-9]*')]],
//   //       quantity_in_stock: ['',[Validators.required, Validators.pattern('[0-9]*')]]
//   //     });
//   //   }
  

//   onSubmit() {
//     if (this.angForm.invalid)
//       return;
//     this.submitted.next(this.angForm.value);
//   }


//   confirmDelete(){
//     if(confirm("Are you sure to delete?")) {
//       this.deleted.emit('');
//     }
//   }
// }
