import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { NotificationService, OrderItem, OrderItemService, PaymentService, Product } from '../../../core';
import { Router } from '@angular/router';
import { loadStripe } from '@stripe/stripe-js';
import { environment } from '../../../../environments/environment.development';

@Component({
  selector: 'app-orderitem-list-order',
  templateUrl: './orderitem-list-order.component.html',
  styleUrls: ['./orderitem-list-order.component.scss']
})
export class OrderitemListOrderComponent implements OnInit {

  orderItems: OrderItem[] = [];
  products: Product[];
  @Input() orderId: number;
  orderItemId: number;
  totalAmount: number;

  stripePromise = loadStripe(environment.stripe);
  
  constructor(
    private route: ActivatedRoute, 
    private orderItemService: OrderItemService,
    private notificationService: NotificationService,
    private router: Router,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.orderId = params['orderId'];
      this.orderItemService.getOrderItemsByOrderId(this.orderId).subscribe((result) => {
        this.orderItems = result;
        this.calculateTotal();
      });
    });
  }

  canceled(){
    this.router.navigate(["/order"]);
  }

  calculateTotal(): void {
    this.totalAmount = this.orderItems.reduce((total, orderItem) => total + (orderItem.product.price * orderItem.quantity), 0);
    console.log(this.totalAmount);
  }
  
  async pay(): Promise<void> {
    const amountInPaise = this.totalAmount * 100;

    const payment = {
      name: `Order ${this.orderId}`,
      currency: 'inr',
      amount: amountInPaise,
      quantity: '1',
      cancelUrl: 'http://localhost:4200/payment/cancel',
      successUrl: 'http://localhost:4200/payment/success',
    };

    const stripe = await this.stripePromise;

    this.paymentService.createPayment(payment, this.orderId).subscribe((data: any) => {
      stripe.redirectToCheckout({
        sessionId: data.id,
      });
    });
  }
}




// import { Component, OnInit } from '@angular/core';
// import { ActivatedRoute } from '@angular/router';
// import { NotificationService, OrderItem, OrderItemService } from '../../../core';
// import { Router } from '@angular/router';
// import { Store } from '@ngrx/store';
// import { getAllOrderItems } from '../../../store/selectors/orderItem.selector';
// import { getOrderItem } from '../../../store/actions/orderItem.action';
// import { Subscription } from 'rxjs';


// @Component({
//   selector: 'app-orderitem-list-order',
//   templateUrl: './orderitem-list-order.component.html',
//   styleUrls: ['./orderitem-list-order.component.scss']
// })
// export class OrderitemListOrderComponent implements OnInit {

//   orderItems: OrderItem[] = [];
//   orderId: number;
//   subscription: Subscription;


//   constructor(
//     private route: ActivatedRoute,
//     private store: Store,
//     private orderItemService: OrderItemService,
//     private notificationService: NotificationService,
//     private router: Router
//   ) { }

//   ngOnInit(): void {
//     // Retrieve the order ID from the route parameters
//     this.orderId = Number(this.route.snapshot.paramMap.get('orderid'));

//     this.store.dispatch(getOrderItem({ orderId: this.orderId }));

    

//     this.subscription = this.store.select(getAllOrderItems).subscribe((result) => {
//       this.orderItems = result;
//       console.log("orderitems:",this.orderItems)
//     });
//   }

//   ngOnDestroy(): void {
//     this.subscription.unsubscribe();
//   }

//   canceled(){
//     this.router.navigate(["/order"]);
//   }
// }
