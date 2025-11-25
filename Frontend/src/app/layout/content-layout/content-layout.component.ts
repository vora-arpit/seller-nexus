import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { User, AuthService } from '../../core';
// import { SYSTEM_ROUTES } from '../../../layout/system-layout/system.routes';

@Component({
  selector: 'app-content-layout',
  templateUrl: './content-layout.component.html',
  styleUrls: ['./content-layout.component.scss']
})
export class ContentLayoutComponent implements OnInit {
  routes: any[] = []; // Initialize routes as an empty array
  public user?: User; // Initialize user as null

  constructor(
    private authenticationService: AuthService,
    private router: Router
  ) { }

  ngOnInit() {
    // this.routes = SYSTEM_ROUTES; // Assign SYSTEM_ROUTES to routes array

    this.authenticationService.currentUser$
      .subscribe(user => {
        if (user) { // Check if user is truthy
          this.user = user; // Assign user value from observable
        } else {
          this.router.navigate(['/auth/login']);
        }
      });
  }
  
  doLogout() {
    this.authenticationService.logout();
  }
}
