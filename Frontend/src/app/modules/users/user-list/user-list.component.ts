
import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { Router } from '@angular/router';
import { User } from '../../../core';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styles: [`
    .user-pic{ height:40px; border-radius:50%}
  `]
})
export class UserListComponent implements OnInit {
  
  @Input() public users: [User] | undefined;

  @Output() public filtered: EventEmitter<string> = new EventEmitter();

  constructor(private router: Router) { }

  ngOnInit() {
  }
  onKeyUp(event: KeyboardEvent) {
    const value = (event.target as HTMLInputElement).value;
    this.filtered.emit(value);
  }

  navigate(id: any) {
    this.router.navigate([id])
  }

}