import { OnInit, Component } from '@angular/core';
@Component({
  selector: 'app-not-found',
  template: `
  <div class="center-content">
  <section>
    <img  src="/assets/images/newlogovertical.png" alt="Logo" height="100px" style="border-radius: 10px;">
    <!-- <h1>Site</h1> -->
    <h2 style="margin-left: -100px;">404 - The requested page was not found.</h2>
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
export class NotFoundComponent implements OnInit {
  
  ngOnInit() {

  }
}