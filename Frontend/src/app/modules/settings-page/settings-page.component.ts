// import { Component, OnDestroy, OnInit } from '@angular/core';
// import { Subscription } from 'rxjs';
// import { AuthService, User } from '../../core'; // Adjust the path based on your project structure

// @Component({
//   selector: 'app-settings-page',
//   templateUrl: './settings-page.component.html',
//   styleUrls: ['./settings-page.component.css']
// })
// export class SettingsPageComponent implements OnInit, OnDestroy {
//   tokens: any;
//   authenticated: User;
//   subscriber$: Subscription;

//   constructor(private authService: AuthService) {}

//   ngOnInit(): void {
//     this.subscriber$ = this.authService.currentUser$.subscribe(user => {
//       this.authenticated = user;
//       // Assuming tokens are fetched from AuthService as well
//       this.tokens = this.authService.getTokens(); // Modify this according to your AuthService implementation
//     });
//   }

//   ngOnDestroy(): void {
//     if (this.subscriber$) {
//       this.subscriber$.unsubscribe();
//     }
//   }
// }
