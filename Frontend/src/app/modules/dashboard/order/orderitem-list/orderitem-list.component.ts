import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OrderItem, OrderItemService } from '../../../../core';

@Component({
  selector: 'app-orderItem-list',
  templateUrl: './orderitem-list.component.html',
  styleUrls: ['./orderitem-list.component.scss']
})
export class OrderItemListComponent implements OnInit {

  orderItems: OrderItem[] = [];
  orderId: number;

  constructor(private route: ActivatedRoute, private orderItemService: OrderItemService) { }

  ngOnInit(): void {
    // Retrieve the order ID from the route parameters
    this.orderId = Number(this.route.snapshot.paramMap.get('orderid'));
    
    // Fetch order items based on the order ID
    this.orderItemService.getOrderItemsByOrderId(this.orderId).subscribe((result) => {
      this.orderItems = result;
    });
  }
}
