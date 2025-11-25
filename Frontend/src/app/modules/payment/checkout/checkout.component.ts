// import { Component } from '@angular/core';
// import { loadStripe } from '@stripe/stripe-js';
// import { environment } from '../../../../environments/environment.development';
// import { PaymentService } from '../../../core';

// @Component({
//   selector: 'app-checkout',
//   templateUrl: './checkout.component.html',
//   styleUrl: './checkout.component.scss'
// })
// export class CheckoutComponent {
//   stripePromise = loadStripe(environment.stripe);
//   constructor(private paymentService:PaymentService) {}

//   async pay(): Promise<void> {
//     // here we create a payment object
//     const payment = {
//       name: 'Iphone',
//       currency: 'usd',
//       // amount on cents *10 => to be on dollar
//       amount: 99900,
//       quantity: '1',
//       cancelUrl: 'http://localhost:4200/cancel',
//       successUrl: 'http://localhost:4200/success',
//     };

//     const stripe = await this.stripePromise;

//     // this is a normal http calls for a backend api
//     this.paymentService.createPayment(payment,).subscribe((data: any) => {
//       stripe.redirectToCheckout({
//         sessionId: data.id,
//       });
//     });
//   }
// }
