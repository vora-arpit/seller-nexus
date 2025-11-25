import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProductListContainer } from './product-list/product-list.container';
import { ProductResolver } from './products.resolver';
import { AuthGuard } from '../../core';
import { ProductEditComponent } from './product-edit/product-edit.component';

const routes: Routes = [
  {
    path: '',
    component: ProductListContainer,
    data: { title: 'Product List' }
  },
  {
    path: 'new',
    component: ProductEditComponent,
    canActivate:[AuthGuard],
    data: { title: 'New Product', roles:['MANAGER'] }
  },
  {
    path: ':id',
    component: ProductEditComponent,
    canActivate:[AuthGuard],
    resolve: {
      product: ProductResolver
    },
    data: { title: 'Edit Product',roles:['MANAGER'] }
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [ProductResolver]
})
export class ProductRoutingModule { }
