import { NgModule,OnInit, isDevMode } from '@angular/core';
import { BrowserModule, provideClientHydration } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { NgChartsModule } from 'ng2-charts';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CoreModule, OrderItemService, OrderService, ProductService } from './core';
import { MatIconModule } from '@angular/material/icon';
import { SharedModule } from './shared/shared.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
// import { SlimLoadingBarModule } from 'ng2-slim-loading-bar';
import { ReverseStr } from './shared/pipes/reverse.pipe';
import { AuthLayoutComponent } from './layout/auth-layout/auth-layout.component';
import { ContentLayoutComponent } from './layout/content-layout/content-layout.component';
import { NgxSpinnerModule } from 'ngx-spinner';
import { NgxChartsModule } from '@swimlane/ngx-charts';
import {StoreModule} from '@ngrx/store';
import {EffectsModule} from '@ngrx/effects';
import { allEffects, allReducers } from './store/states/app.state';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';

@NgModule({
  declarations: [
    AppComponent,
    ReverseStr,
    AuthLayoutComponent,
    ContentLayoutComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule, ReactiveFormsModule,
    HttpClientModule,
    NgChartsModule,
    CoreModule,
    SharedModule,
    NgxChartsModule,
    BrowserAnimationsModule,NgxSpinnerModule.forRoot(),
    MatIconModule,StoreModule.forRoot(allReducers),EffectsModule.forRoot(allEffects), StoreDevtoolsModule.instrument({ maxAge: 25, logOnly: !isDevMode() }),
  ],
  providers: [
    provideClientHydration(),ProductService,OrderService,OrderItemService

  ],
  bootstrap: [AppComponent]
})
export class AppModule{}
