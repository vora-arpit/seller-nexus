import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Order, OrderService } from "../../core";
import { catchError, exhaustMap, map, of, switchMap } from "rxjs";
import * as OrderActions from "../actions/order.action";

@Injectable()
export class OrderEffects{
    constructor(
        private action$:Actions,
        // private orderService:OrderService,
    ){}

    // getOrder$ = createEffect(() => {
    //     return this.action$.pipe(
    //       ofType(OrderActions.getOrder),
    //       switchMap(() => { // Destructure filter from the action payload
    //         return this.orderService.findAll('').pipe( // Pass the filter text to the service method
    //             map(orders => 
    //                 OrderActions.getOrderSuccess({ orders })),
    //             catchError(error => of(OrderActions.getOrderFailure({ error })))
    //         );
    //       })
    //     );
    //   });
      

    // addOrder$=createEffect(()=>{
    //     return this.action$.pipe(
    //         ofType(OrderActions.addOrder),
    //         exhaustMap(action=>{
    //             return this.orderService.create(action.order).pipe(
    //                 map((order:Order)=>OrderActions.addOrderSuccess({order})),
    //                 catchError(error=>of(OrderActions.addOrderFailure({ error })))
    //             );
    //         })
    //     );
    // });

}