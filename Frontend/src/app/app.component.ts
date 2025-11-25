import { Component } from '@angular/core';
import { Router, NavigationStart, NavigationEnd, NavigationCancel, NavigationError, Event } from '@angular/router';
import { NgxSpinnerService } from 'ngx-spinner';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {

  constructor(
    private _loadingBar: NgxSpinnerService,
    private _router: Router
  ) {
    this._router.events.subscribe((event: Event) => {
      this.navigationInterceptor(event);
    });
  }

  private navigationInterceptor(event: Event): void {
    if (event instanceof NavigationStart) {
      this._loadingBar.show();
    }
    if (event instanceof NavigationEnd) {
      this._loadingBar.hide();
    }
    if (event instanceof NavigationCancel) {
      this._loadingBar.hide();
    }
    if (event instanceof NavigationError) {
      this._loadingBar.hide();
    }
  }
}
