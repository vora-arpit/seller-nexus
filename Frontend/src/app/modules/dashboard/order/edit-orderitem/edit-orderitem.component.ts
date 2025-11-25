

import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Order, OrderItem, OrderItemService, OrderService, Product } from '../../../../core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-order-item',
  templateUrl: './edit-orderitem.component.html',
  styleUrls: ['./edit-orderitem.component.scss']
})
export class EditOrderitemComponent implements OnInit {
  orderItemId: bigint;
  orderId:number;
  orderItem: OrderItem={
    id:null,
    price:1000,
    quantity:2,
    product:new Product(),
    order:null
  };
  showModal: boolean = false;
  order:Order;
  isAddMode: boolean;
  productId:number;

  constructor(
    private route: ActivatedRoute,
    private orderItemService: OrderItemService,private router:Router,private orderService:OrderService
  ) { }

  ngOnInit(): void {
    // Retrieve the order item ID from the route parameters
    const orderItemIdParam = this.route.snapshot.paramMap.get('orderItemId');
    if (orderItemIdParam) {
      this.orderItemId = BigInt(orderItemIdParam);
    
      // Fetch the order item details based on the ID
      this.orderItemService.getOrderItemById(this.orderItemId).subscribe(
        (result) => {
          this.orderItem = result;
        },
        (error) => {
          console.error('Error fetching order item:', error);
        }
      );
    }
  
    const orderIdParam = this.route.snapshot.paramMap.get('orderid');
    if (orderIdParam) {
      this.orderId = Number(orderIdParam);
      // console.log("id:" + this.orderId)
    
      // Fetch the order details based on the ID
      this.orderService.findById(this.orderId).subscribe(
        (result) => {
          // console.log("result:" + result)
          this.order = result;
        },
        (error) => {
          console.error('Error fetching order item:', error);
        }
      );
    }
  
    this.isAddMode = !this.orderItemId;
  }
  
  

  onSubmit(){
    if(this.isAddMode){
      if(this.isAddMode){
        this.AddOrderItem();
      }else{
        this.updateOrderItem();
      }
    }
  }

  updateOrderItem(): void {
    // Call the service to update the order item
    // const updateRequest = new UpdateOrderItemRequest(this.orderItem);
    
    this.orderItemService.updateOrderItem(this.orderItemId,this.orderItem).subscribe(
      (result) => {
        console.log('Order item updated successfully:');
        // Optionally, navigate back to the order item list or perform any other action
      },
      (error) => {
        console.error('Error updating order item:', error);
      }
    );
  }

  
  AddOrderItem(): void {
    const neworderItem: OrderItem={
      id:null,
      price:1000,
      quantity:2,
      product:new Product(),
      order:null
    };
    // Call the service to update the order item
    // const updateRequest = new UpdateOrderItemRequest(this.orderItem);
    
    this.orderItemService.createOrderItem(this.productId,this.orderId,neworderItem).subscribe(
      (result) => {
        console.log('Order item Added successfully:');
        // Optionally, navigate back to the order item list or perform any other action
      },
      (error) => {
        console.error('Error While Adding order item:', error);
      }
    );
  }

  canceled(){
    this.router.navigate(["/dashboard"]);
  }
  confirmDelete(){
    this.orderItemService.deleteOrderItem(this.orderItem.id);
    }

  toggleModal(): void {
    this.showModal = !this.showModal;
  }

  getKeys(obj:any):Array<string>{
    return Object.keys(obj);
  }
}
