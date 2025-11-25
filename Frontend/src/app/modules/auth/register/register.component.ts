import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../../core/services';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  loginForm!: FormGroup; // Initialize as undefined
  loading = false;
  submitted = false;
  error: string | undefined; // Initialize as undefined

  constructor(
    private formBuilder: FormBuilder,
    private router: Router,
    private route: ActivatedRoute,
    private authenticationService: AuthService
  ) {
    if (authenticationService.currentUserValue) {
      this.router.navigate(['/selernexus']);
    }
  }

  ngOnInit() {
    this.loginForm = this.formBuilder.group({
      name: ['', Validators.required],
      email: ['', [Validators.required,Validators.email]],
      password: ['', [Validators.required, Validators.pattern("^(?=.*[A-Z])(?=.*[^A-Za-z0-9])[A-Za-z\\d\\S]{8,}$")]]
    });
  }

  get f() { return this.loginForm.controls; }

  onSubmit() {
    this.submitted = true;
    this.error = undefined; // Reset error before validation
    if (this.loginForm.invalid) {
      return;
    }

    this.loading = true;
    this.authenticationService.signup(this.f['name'].value, this.f['email'].value, this.f['password'].value).
      subscribe(data => {
        this.autoLogin();
        this.loading = false;
      }, error => {
        this.loading = false;
        this.error = error?.message || 'An error occurred'; // Handle potential undefined error
      });
  }

  private autoLogin() {
    this.authenticationService.login(this.f['email'].value, this.f['password'].value)
      .subscribe(token => {
        this.router.navigate(['/selernexus']);
      }, error => {
        console.log('autologin error', error)
      });
  }

}
