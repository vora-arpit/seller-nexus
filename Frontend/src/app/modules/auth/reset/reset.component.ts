import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AuthService, NotificationService } from '../../../core';

@Component({
  selector: 'app-reset',
  templateUrl: './reset.component.html',
  styleUrls: ['./reset.component.scss']
})
export class ResetComponent implements OnInit {
  submitted = false;
  resetForm: FormGroup;
  token: string; // Define a token variable to store the token from the URL

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService, 
    private route: ActivatedRoute ,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.resetForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.pattern("^(?=.*[A-Z])(?=.*[^A-Za-z0-9])[A-Za-z\\d\\S]{8,}$")]],
      password1: ['', [Validators.required, Validators.pattern("^(?=.*[A-Z])(?=.*[^A-Za-z0-9])[A-Za-z\\d\\S]{8,}$")]]
    });

    // Get the token from the URL
    this.route.queryParams.subscribe(params => {
      this.token = params['token'];
      console.log(this.token);
    });
  }
  
  get f() { return this.resetForm.controls; }

  onSubmit() {
    this.submitted = true;

    if (this.resetForm.invalid) {
      return;
    }

    // Call the resetPassword method from the AuthService with the token
    this.authService.resetPassword(this.token, this.resetForm.value.password)
      .subscribe(
        response => {
          // Reset the form and display success message
          this.resetForm.reset();
          this.submitted = true;
          this.notificationService.showSuccess('Password reset successfully!');
        },
        error => {
          // console.error(error); // Handle error
          this.notificationService.showError('Password reset successfully!');
        }
      );
  }
}
