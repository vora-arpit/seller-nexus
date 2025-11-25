import { NgModule } from '@angular/core';
import { RouterModule, Routes, PreloadAllModules } from '@angular/router';
import { NotFoundComponent } from './shared/components/not-found/not-found.component';
import { ContentLayoutComponent } from './layout/content-layout/content-layout.component';
import { AuthGuard } from './core';
import { AuthLayoutComponent } from './layout/auth-layout/auth-layout.component';
import { ForbiddenComponent } from './shared/components/forbidden/forbidde.component';

const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: '',
    component: ContentLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      {
        path: 'selernexus',
        loadChildren: () =>
          import('./modules/selernexus/selernexus.module').then(m => m.SellerNexusModule)
      },
      {
        path: 'sellers',
        data: {
          roles: ['ADMIN']
        },
        loadChildren: () =>
          import('./modules/users/users.module').then(m => m.UsersModule)
      }
    ]
  },
  {
    path: 'auth',
    component: AuthLayoutComponent,
    loadChildren: () =>
      import('./modules/auth/auth.module').then(m => m.AuthModule)
  },
  {
    path: 'forbidden',
    component: ForbiddenComponent
  },
  //when no route matches
  {
    path: '**',
    component: NotFoundComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    preloadingStrategy: PreloadAllModules
  })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
