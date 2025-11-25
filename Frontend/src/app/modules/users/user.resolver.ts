import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { User, UserService } from '../../core';

@Injectable()
export class UserResolver implements Resolve<Observable<User>>{

  constructor(private userService: UserService) { }

  resolve(route: ActivatedRouteSnapshot): Observable<User> {
    const id = route.paramMap.get('id');
    if (id !== null) {
      return this.userService.findById(+id);
    } else {
      // Handle the case where 'id' is null, for example, by redirecting to an error page
      // You can return an Observable of null or throw an error
      throw new Error('User ID is missing in the route parameters');
    }
  }
}
