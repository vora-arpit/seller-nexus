import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services';

@Injectable({ providedIn: "root" })
export class AuthGuard implements CanActivate {

  constructor(
    private router: Router,
    private authenticationService: AuthService
  ) { }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const currentUser = this.authenticationService.currentUserValue;
    if (!currentUser) {
      console.log('user tried to access private route and was not logged in')
      // not logged in, redirect to login page with the return url
      this.router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
      return false;
    }
    if (route.data && route.data['roles'] && !this.checkRoles(currentUser.roles, route.data['roles'])) {
      // Route requires specific roles and the user does not have them
      this.router.navigate(['/forbidden']);
      return false;
    }
    
    return true;
  }

  private checkRoles(userRoles: string[], requiredRoles: string[]): boolean {
    for (const role of requiredRoles) {
      if (!userRoles.includes(role)) {
        return false; // User is missing at least one required role
      }
    }
    return true; // User has all required roles
  }

}