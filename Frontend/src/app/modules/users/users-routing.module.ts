import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserEditContainer } from './user-edit/user-edit.container';
import { UserListContainer } from './user-list/user-list.container';
import { UserResolver } from './user.resolver';

const routes: Routes = [
  {
    path: '',
    component: UserListContainer,
    data: { title: 'Seller List' }
  },
  {
    path: ':id',
    component: UserEditContainer,
    resolve: {
      user: UserResolver
    },
    data: { title: 'Edit Seller' }
  }

]

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [UserResolver]
})
export class UserRoutingModule { }
