import { Component, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { NotificationService, Role, User, UserService } from "../../../core";

@Component({
  selector: 'app-user-edit-container',
  template: `
  <div *ngIf="(roles$ | async) as roles">
    <app-user-edit
      [user]="user"
      [roles]="roles"
      (submitted)="submitted($event)"
      (canceled)="canceled($event)"
      (addRemoveRole)="addRemoveRole($event)"
    ></app-user-edit>
  </div>`
})
export class UserEditContainer implements OnInit {

  user: User | undefined;
  roles$: Observable<Role[]> | undefined;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private userService: UserService,
    private notificationService: NotificationService
  ) { }

  ngOnInit() {
    this.user = this.route.snapshot.data['user'];
    this.roles$ = this.userService.getAllRoles();
  }

  submitted(event: User) {
    if (this.user) {
      this.userService.update(this.user.id, event)
        .subscribe(
          updateUser => {
            this.notificationService.showSuccess('User updated.');
            this.home();
          }
        );
    }
  }

  addRemoveRole(role: Role) {
    console.log('addRemoveRole', role)
    if (role.granted)
      this.addRole(role);
    else
      this.removeRole(role);
  }

  addRole(role: Role) {
    if (this.user) {
      this.userService.addRole(this.user.id, role.name)
        .subscribe(
          res => {
            if (this.user) {
              this.user.roles = [...this.user.roles, role.name];
              this.notificationService.showSuccess('Role added.');
            }
          }
        );
    }
  }

  removeRole(role: Role) {
    if (this.user) {
      this.userService.removeRole(this.user.id, role.name)
        .subscribe(
          res => {
            if (this.user) {
              this.user.roles = this.user.roles?.filter(r => r !== role.name);
              this.notificationService.showSuccess('Role removed.');
            }
          }
        );
    }
  }

  canceled(event: any) {
    this.home();
  }

  home() {
    this.router.navigate(['/users']);
  }
}
