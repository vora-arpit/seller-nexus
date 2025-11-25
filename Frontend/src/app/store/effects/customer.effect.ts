import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Customer, CustomerService } from "../../core";
import * as CustomerActions from '../actions/customer.action';
import { catchError, exhaustMap, map, switchMap } from "rxjs/operators";
import { of } from "rxjs";

@Injectable()
export class CustomerEffects {
    constructor(
        private action$: Actions,
        private customerService: CustomerService,
    ) {}

    getCustomers$ = createEffect(() => {
        return this.action$.pipe(
            ofType(CustomerActions.getCustomer),
            switchMap(() => {
                return this.customerService.findAll('').pipe(
                    map(customers =>
                        // console.log(customers)
                        CustomerActions.getCustomerSuccess({ customers })), // Ensure customers is of type Customer[]
                    catchError(error => of(CustomerActions.getCustomerFailure({ error })))
                );
            })
        );
    });


    addCustomer$ = createEffect(() => {
        return this.action$.pipe(
            ofType(CustomerActions.addCustomer),
            exhaustMap(action => {
                return this.customerService.create(action.customer).pipe(
                    map((customer:Customer) => CustomerActions.addCustomerSuccess({ customer })),
                    catchError(error => of(CustomerActions.addCustomerFailure({ error })))
            
                );
            })
        );
    });

    updateCustomer$ = createEffect(() => {
        return this.action$.pipe(
            ofType(CustomerActions.updateCustomer),
            exhaustMap(action => {
                return this.customerService.update(BigInt(action.id),action.customer).pipe(
                    map((customer:Customer) => CustomerActions.updateCustomerSuccess({ customer })),
                    catchError(error => of(CustomerActions.updateCustomerFailure({ error })))
                );
            })
        );
    });

    deleteCustomer$ = createEffect(() => {
        return this.action$.pipe(
            ofType(CustomerActions.deleteCustomer),
            exhaustMap(action => {
                return this.customerService.delete(action.id).pipe(
                    map(() => CustomerActions.deleteCustomerSuccess({ id: action.id })),
                    catchError(error => of(CustomerActions.deleteCustomerFailure({ error })))
                );
            })
        );
    });
}
