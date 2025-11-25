import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Product, ProductService } from '../../../core'; // Import Product and ProductService from the appropriate location
import { Store } from '@ngrx/store';
import { getProducts } from '../../../store/actions/products.action';
import { getAllProducts } from '../../../store/selectors/products.selector';

@Component({
  selector: 'app-product-list-container',
  template: `
    <app-product-list
      [products]="products"
      (filtered)="filter($event)"
    ></app-product-list>
  `
})
export class ProductListContainer implements OnInit, OnDestroy {

  products: Product[] = [];
  filter$ = new Subject<string>();
  subscription: Subscription = new Subscription();

  constructor(
     private store: Store
  ) { }

  ngOnInit() {
    this.subscription.add(
      this.filter$.pipe(
        debounceTime(300),
        distinctUntilChanged(),
        // switchMap(text => this.productService.findAll(text))
      ).subscribe(
        text => {
          // this.products = results;
        this.store.dispatch(getProducts());
        }
      )
    )
    this.filter$.next('');

    this.store.select(getAllProducts).subscribe((product) => {
      this.products = product;
    });
  }

  filter(text: string) {
    this.filter$.next(text);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }
}
