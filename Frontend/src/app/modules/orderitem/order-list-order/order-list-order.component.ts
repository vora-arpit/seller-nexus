import { Component, OnInit, Output} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order, OrderService } from '../../../core';

@Component({
  selector: 'app-order-list-order',
  templateUrl: './order-list-order.component.html',
  styleUrl: './order-list-order.component.scss'
})
export class OrderListOrderComponent implements OnInit{
  orders: Order[] = [];
  pagedOrders: Order[] = []; 
  currentPage = 1; 
  itemsPerPage = 5;
  @Output() orderId:number;

  constructor(private route: ActivatedRoute, private orderService: OrderService,
    private router:Router
  ) { }

  ngOnInit(): void {
    this.orderService.findAll().subscribe((result) => {
      this.orders = result;
      // console.log(this.orders); 
      this.setPage(1); 
    });
  }
  
  goToOrderItemList(orderId: number): void {
    this.router.navigate(['/order', orderId]);
    
  }

  
  setPage(page: number) {
    if (page < 1 || page > this.totalPages()) {
      return;
    }
    this.currentPage = page;
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = Math.min(startIndex + this.itemsPerPage, this.orders.length);
    this.pagedOrders = this.orders.slice(startIndex, endIndex);
  }

  
  totalPages(): number {
    return Math.ceil(this.orders.length / this.itemsPerPage);
  }


  updatePagedOrders(): void {
    const startIndex = this.currentPage * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.pagedOrders = this.orders.slice(startIndex, endIndex);
  }

  
  onPageChange(event: any): void {
    this.currentPage = event.pageIndex; 
    this.itemsPerPage = event.pageSize; 
    this.updatePagedOrders(); 
  }
}
