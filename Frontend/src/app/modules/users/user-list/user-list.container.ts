import { OnInit, Component, OnDestroy } from '@angular/core';
import { Observable, Subject, Subscription } from 'rxjs';
import { distinctUntilChanged, switchMap } from 'rxjs/operators';
import { User, UserService } from '../../../core';

@Component({
  selector: 'app-user-list-container',
  template: `<app-user-list
  [users]="users"
  (filtered)="filter($event)"
  ></app-user-list>`
})
export class UserListContainer implements OnInit, OnDestroy {

  public users$: Observable<[User]> | undefined;

  users: [User] | undefined;
  filter$ = new Subject<string>();
  subscription: Subscription = new Subscription();

  constructor(
    private userService: UserService
  ) { }

  ngOnInit() {
    this.subscription.add(
      this.filter$.pipe(
        //debounceTime(300),
        distinctUntilChanged(),
        switchMap(text => this.userService.findAll(text))
      ).subscribe(
        results => this.users = results
      )
    )
    this.filter$.next('')
  }

  filter(text: string) {
    this.filter$.next(text);
  }

  ngOnDestroy(){
    this.subscription.unsubscribe();
  }
}