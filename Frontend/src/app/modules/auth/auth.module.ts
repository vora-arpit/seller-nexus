import { NgModule } from '@angular/core';
import { AuthRoutingModule } from './auth.routing.module';
import { ForgotComponent } from './forgot/forgot.component';
import { LoginComponent } from './login/login.component';
import { OAuth2RedirectComponent } from './oauth2/oauth2-redirect.component';
import { RegisterComponent } from './register/register.component';
import { SharedModule } from '../../shared/shared.module';
import { ResetComponent } from './reset/reset.component';


@NgModule({
  declarations: [
    RegisterComponent,
    ForgotComponent,
    LoginComponent,
    OAuth2RedirectComponent,ResetComponent
  ],
  imports: [
    SharedModule, AuthRoutingModule
  ]
})
export class AuthModule { }
