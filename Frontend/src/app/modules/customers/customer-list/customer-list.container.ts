import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subject, Subscription } from 'rxjs';
import {  debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Customer, } from '../../../core';
import { Store } from '@ngrx/store';
import { getCustomer } from '../../../store/actions/customer.action';
import { getAllCustomers } from '../../../store/selectors/customer.selector';

@Component({
  selector: 'app-customer-list-container',
  template: `
  <app-customer-list
    [customers]="customers"
    (filtered)="filter($event)"
  ></app-customer-list>
  `
})
export class CustomerListContainer implements OnInit, OnDestroy {

  customers: Customer[]=[];
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
        // switchMap(text => this.customerService.findAll(text))
      ).subscribe(
        text => {
          this.store.dispatch(getCustomer());
        }
      )
    )
    this.filter$.next('')
    
    this.store.select(getAllCustomers).subscribe(
      (customers) => this.customers = customers
    )
    

  }

  filter(text: string) {
    this.filter$.next(text);
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

}