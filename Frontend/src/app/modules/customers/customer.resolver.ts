import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { CustomerService } from '../../core/services';
import { Customer } from '../../core';

@Injectable({ providedIn: 'root' })
export class CustomerResolver implements Resolve<Customer> {

  constructor(private customerService: CustomerService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Customer> {
    const id = route.paramMap.get('id');
    if (id !== null) {
      return this.customerService.findById(BigInt(id));
    } else {
      throw new Error('Customer ID is null');
    }
  }

}
