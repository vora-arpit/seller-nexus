import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { Order, OrderService } from '../../../core';


@Injectable({ providedIn: 'root' })
export class OrderResolver implements Resolve<Order> {

  constructor(private orderService: OrderService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Order> {
    const id = route.paramMap.get('id');
    if (id !== null) {
      return this.orderService.findById(BigInt(+id));
    } else {
      throw new Error('OrderItem ID is null');
    }
  }

}
