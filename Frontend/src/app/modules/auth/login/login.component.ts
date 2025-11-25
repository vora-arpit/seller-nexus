import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { first } from 'rxjs/operators';
import { AuthService } from '../../../core/services';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginForm!: FormGroup; // Initialize as undefined
  loading = false;
  submitted = false;
  returnUrl!: string; // Initialize as undefined
  error: string | undefined; // Initialize as undefined
  OAuthURLS: any = {}; // Adjust type based on your OAuthURLS property

  constructor(
    private formBuilder: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private authenticationService: AuthService
  ) {
    // redirect to selernexus if already logged in
    if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/selernexus']);
    }
    this.OAuthURLS = authenticationService.OAuthURLS;
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      email: ['avora@cpp.edu', [Validators.required,Validators.email]],
      password: ['saq123', [Validators.required]]
    });
    // get return url from route parameters or default to '/selernexus'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/selernexus';
  }

  get f() { return this.loginForm.controls; }

  onSubmit() {
    this.submitted = true;
    this.error = undefined; // Reset error before validation
    if (this.loginForm.invalid)
      return;

    this.loading = true;
    this.authenticationService.login(this.f['email'].value, this.f['password'].value)
      .pipe(first())
      .subscribe(
        user => {
          this.router.navigate([this.returnUrl]);
        },
        error => {
          this.error = error?.message || 'An error occurred'; // Handle potential undefined error
          this.loading = false;
        }
      );
  }
}