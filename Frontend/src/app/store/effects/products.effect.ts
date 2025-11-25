import { Injectable } from "@angular/core";
import { Actions, createEffect, ofType } from "@ngrx/effects";
import { Product, ProductService } from "../../core";
import { catchError, exhaustMap, map, of, switchMap } from "rxjs";
import * as ProductActions from '../actions/products.action';
import {Store} from '@ngrx/store';


@Injectable()
export class ProductEffects{
    constructor(
        private action$:Actions,
        private productService:ProductService,
        private store: Store
    ){}

    getProducts$ = createEffect(() => {
        return this.action$.pipe(
          ofType(ProductActions.getProducts), // Listen for the getProducts action
          switchMap(() => {
            return this.productService.findAll('').pipe( // Call findAll with an empty filter text
              map(products => ProductActions.getProductsSuccess({ products })), // Dispatch the getProductsSuccess action with the retrieved products
              catchError(error => of(ProductActions.getProductsFailure({ error }))) // Dispatch the getProductsFailure action if an error occurs
            );
          })
        );
      });

      addProducts$= createEffect(() => {
        return this.action$.pipe(
          ofType(ProductActions.addProducts),
          exhaustMap((action) => {
                return this.productService.create(action.product).pipe(
                    map((product: Product) => 
                        ProductActions.addProductsSuccess({ product })
                ),    
                    catchError((error) => of(ProductActions.addProductsFailure({ error })))
                );
            })
        )}
        );
    

        updateProducts$= createEffect(() => {
            return this.action$.pipe(
              ofType(ProductActions.updateProducts),
              exhaustMap((action) => {
                    return this.productService.update(BigInt(action.id),action.product).pipe(
                        map((product: Product) => 
                            ProductActions.updateProductsSuccess({ product })
                    ),    
                        catchError((error) => of(ProductActions.updateProductsFailure({ error })))
                    );
                })
            );
        })

        deleteProducts$= createEffect(() => {
            return this.action$.pipe(
              ofType(ProductActions.deleteProducts),
              exhaustMap((action) => {
                    return this.productService.delete(action.id).pipe(
                        map(() => 
                            ProductActions.deleteProductsSuccess({ id:action.id })
                    ),
                        catchError((error) => of(ProductActions.deleteProductsFailure({ error })))
                    );
                })
            );
        })
}