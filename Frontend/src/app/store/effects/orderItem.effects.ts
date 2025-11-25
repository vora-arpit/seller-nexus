import { Injectable } from '@angular/core';
import * as OrderItemActions from '../actions/orderItem.action';
import { OrderItem, OrderItemService } from '../../core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, map, of, switchMap } from 'rxjs';

@Injectable()
export class OrderItemEffects{
    constructor(
        private actions$: Actions, 
        private orderItemService: OrderItemService
    ){}

    getOrderItems$ = createEffect(() =>
        this.actions$.pipe(
          ofType(OrderItemActions.getOrderItem), // Listen for the getOrderItem action
          switchMap((action) => { // Extract the id from the action payload
            return this.orderItemService.getOrderItemsByOrderId(action.orderId).pipe( // Call the service method
              map(orderItems => OrderItemActions.getOrderItemSuccess({ orderItem: orderItems })), // Dispatch success action with the order items
              catchError(error => of(OrderItemActions.getOrderItemFailure({ error }))) // Dispatch failure action with the error
            );
          })
        )
      );

      // addOrderItem$=createEffect(()=>
      // this.actions$.pipe(
      //   ofType(OrderItemActions.addOrderItem),
      //   switchMap((action)=>{
      //     return this.orderItemService.createOrderItem(action.orderItem).pipe(
      //       map((orderItem:OrderItem)=>OrderItemActions.addOrderItemSuccess({orderItem})),
      //       catchError(error=>of(OrderItemActions.addOrderItemFailure({error})))
      //     )
      //   })
      // ))
      
}
