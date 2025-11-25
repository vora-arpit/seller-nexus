// import { Component, OnInit } from '@angular/core';
// import { FormBuilder, FormGroup, Validators } from '@angular/forms';
// import { Router } from '@angular/router'; // Import Router
// import emailjs from '@emailjs/browser';

// @Component({
//   selector: 'app-forgot',
//   templateUrl: './forgot.component.html',
//   styleUrls: ['./forgot.component.scss']
// })
// export class ForgotComponent implements OnInit {
//   forgotForm: FormGroup;
//   generatedVerificationCode: number;

//   constructor(
//     private formBuilder: FormBuilder,
//     private router: Router // Inject Router
//   ) {}

//   ngOnInit() {
//     this.forgotForm = this.formBuilder.group({
//       email: ['', [Validators.required, Validators.email]],
//       verificationCode: ['', Validators.required]
//     });
//   }

//   send() {
//     emailjs.init('V9bC_cXppGZ3fW8j8');

//     // Generate a random 8-digit number
//     this.generatedVerificationCode = Math.floor(10000000 + Math.random() * 90000000);
    
//     // Prepare the message including the random number
//     const message = `Your verification code is: ${this.generatedVerificationCode}`;
    
//     let response = emailjs.send('service_hpvj6p2', 'template_qbgh8ip', {
//       to_email: this.forgotForm.value.email,
//       message: message,
//     });
    
//     alert('Verification code has been sent to your email.');
//   }

//   verify() {
//     const enteredVerificationCode = this.forgotForm.value.verificationCode;
//     if (enteredVerificationCode == this.generatedVerificationCode) {
//       // Redirect to reset password page
//       this.router.navigate(['/auth/reset']); // Navigate to '/auth/reset' path
//     } else {
//       alert('Incorrect verification code. Please try again.');
//     }
//   }
// }


import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../core';
// import { AuthService } from 'path-to-your-auth-service'; // Replace 'path-to-your-auth-service' with the actual path

@Component({
  selector: 'app-reset',
  templateUrl: './forgot.component.html',
  styleUrls: ['./forgot.component.scss']
})
export class ForgotComponent implements OnInit {
  submitted = false;
  forgotForm: FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.forgotForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]]
    });
  }
  
  get f() { return this.forgotForm.controls; }

  onSubmit() {
    this.submitted = true;

    if (this.forgotForm.invalid) {
      return;
    }
    console.log(this.forgotForm.value.email);

    this.authService.forgotPassword(this.forgotForm.value.email)
      .subscribe(
        response => {
          // Reset the form and display success message
          this.forgotForm.reset();
          this.submitted = false;
          alert('Reset password email sent successfully!');
        },
        error => {
          console.error(error); // Handle error
          alert('An error occurred while sending reset password email. Please try again.');
        }
      );
  }
}
