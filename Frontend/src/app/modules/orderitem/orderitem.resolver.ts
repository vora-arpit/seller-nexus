import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { OrderItem, OrderItemService } from '../../core';

@Injectable({ providedIn: 'root' })
export class OrderItemResolver implements Resolve<OrderItem | null> {

  constructor(private orderItemService: OrderItemService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<OrderItem> {
    const id = route.paramMap.get('id');
    if (id !== null) {
      return this.orderItemService.getOrderItemById(BigInt(id));
    } else {
      throw new Error('OrderItem ID is null');
    }
  }

}
