import { Component, OnInit } from '@angular/core';
import { Router,ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-forbidden',
  template: `
    <div class="center-content">
  <section>
    <img  src="/assets/images/newlogovertical.png" alt="Logo" height="100px" style="border-radius: 10px;">
    <!-- <h1>Site</h1> -->
    <h2 style="margin-left: -130px;">403 - You are not authorized to see this page.</h2>
    <button style="margin-left: 30px;" class="btn btn-secondory" (click)="login()">Click Here to Back</button>
    </section>
  </div>
  `,
  styles:[`.center-content {
    display: flex;
    justify-content: center;
    align-items: flex-start;
    height: 100vh;
    padding-top: 50px;
  }
  `]
})
export class ForbiddenComponent implements OnInit {
  returnUrl!: string; 
  constructor(private router:Router,private route:ActivatedRoute){}
  
  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }
  
  login(){
    this.router.navigate([this.returnUrl]);
  }
}