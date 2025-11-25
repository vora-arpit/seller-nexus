import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";
import { Customer, Order, OrderItem, OrderService } from '../../../core';

@Component({
  selector: 'app-make-order-order',
  templateUrl: './make-order-order.component.html',
  styleUrl: './make-order-order.component.scss'
})
export class MakeOrderOrderComponent implements OnInit{
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
  returnUrl!: string; 

  constructor(
    private orderService: OrderService,
    private router: Router,private route:ActivatedRoute
  ) {}

  ngOnInit(): void {

    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';  }

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
    this.router.navigate(["/order"]);
  }

  toggleModal(): void {
    this.showModal = !this.showModal;
  }

  deleteOrder(order: Order): void {
    if (order.itemcount && order.itemcount > 0) {
      console.log('Cannot delete order with associated order items');
      return; // Exit the method if there are associated order items
    }

    if (order.id) {
      if (confirm('Are you sure you want to delete this order?')) {
        this.orderService.deleteOrder(order.id).subscribe(
          () => {
            console.log('Order deleted successfully');
            // Optionally, navigate back to the order list or perform any other action
          },
          (error) => {
            console.error('Error deleting order:', error);
          }
        );
      }
    }
  }
}
