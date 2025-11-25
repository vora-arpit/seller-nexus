import { NgModule } from '@angular/core';
import { ProductEditComponent } from './product-edit/product-edit.component';
// import { ProductEditContainer } from './product-edit/product-edit.container';
import { ProductListComponent } from './product-list/product-list.component';
import { ProductRoutingModule } from './products-routing.module';
import { SharedModule } from '../../shared/shared.module';
import { ProductListContainer } from './product-list/product-list.container';
import { ProductService } from '../../core';

@NgModule({
  declarations: [
    ProductListContainer,
    ProductListComponent,
    // ProductEditContainer,
    ProductEditComponent
  ],
  providers:[ProductService],
  imports: [
    SharedModule,
    ProductRoutingModule
  ]
})
export class ProductsModule { }
