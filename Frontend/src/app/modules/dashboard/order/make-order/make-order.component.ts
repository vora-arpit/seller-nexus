import { Component, OnInit } from "@angular/core";
import { Customer, Order, OrderItem, OrderService } from "../../../../core";
import { Router } from "@angular/router";

@Component({
  selector: "app-make-order",
  templateUrl: "./make-order.component.html",
  styleUrls: ['./make-order.component.scss']
})
export class MakeOrderComponent implements OnInit {
  order: Order = {
    id: null,
    createdAt: null,
    createdBy: null,
    customer: new Customer(), // Use the existing customer object
    status: '', // Initialize status with an empty string
    total: null,
    itemcount: null,
    orderItem:new OrderItem()
  };
  orderItemId:Number
  customerId: Number;
  showModal: boolean = false;
  public status = ['STARTED', 'PAID', 'SHIPPED'];

  constructor(
    private orderService: OrderService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // You can initialize any other properties or fetch data here if needed
  }

  onSubmit(): void {
    // Create a new Order object based on the existing order
    const newOrder: Order = {
      id: null,
      createdAt: null,
      createdBy: null,
      customer: this.order.customer, // Use the existing customer object
      status: this.order.status,
      total: this.order.total,
      itemcount: null,
      orderItem:this.order.orderItem
    };

    // Subscribe to the create method of the order service
    this.orderService.create(this.customerId, this.orderItemId,newOrder).subscribe(
      (result) => {
        // Handle success response
        console.log('Order added Successfully');
      },
      (error) => {
        // Handle error response
        console.error('Error while adding order:', error);
      }
    );

    // Log the original order object
    console.log(this.order);
  }

  canceled() {
    this.router.navigate(["dashboard"]);
  }

  toggleModal(): void {
    this.showModal = !this.showModal;
  }
}
