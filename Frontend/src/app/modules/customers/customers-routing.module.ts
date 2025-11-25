import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
// import { CustomerEditContainer } from './customer-edit/customer-edit.container';
import { CustomerResolver } from './customer.resolver';
import { CustomerListContainer } from './customer-list/customer-list.container';
import { CustomerEditComponent } from './customer-edit/customer-edit.component';

const routes: Routes = [
  {
    path: '',
    component: CustomerListContainer,
    data: { title: 'Customer List' }
  },
  {
    path: 'new',
    component: CustomerEditComponent,
    data: { title: 'New Customer' }
  },
  {
    path: ':id',
    component: CustomerEditComponent,
    resolve: {
      customer: CustomerResolver
    },
    data: { title: 'Edit Customer' }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [CustomerResolver]
})
export class CustomerRoutingModule { }
