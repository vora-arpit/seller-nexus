import { OnInit, Component, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { Role, User } from '../../../core';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html'
})
export class UserEditComponent implements OnInit {

  @Input() user: User | undefined;
  @Input() roles: Role[] | undefined;

  @Output() addRemoveRole = new EventEmitter<Role>();
  @Output() submitted = new EventEmitter<User>();
  @Output() canceled = new EventEmitter<string>();

  // Change angForm from private to public
  public angForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder
  ) { this.angForm = this.formBuilder.group({});}

  ngOnInit() {
    this.createForm();
    this.roles = this.roles?.sort((a, b) => a.name.localeCompare(b.name))
      .map(r => ({
        ...r,
        granted: this.user?.roles.includes(r.name) ?? false
      }));
  }

  createForm() {
    this.angForm = this.formBuilder.group({
      name: [this.user?.name ?? '', Validators.required],
      imageUrl: [this.user?.imageUrl ?? '']
    });
  }

  onSubmit() {
    if (!this.angForm?.valid || !this.user)
      return;

    this.submitted.emit(this.angForm.value);
  }

}
