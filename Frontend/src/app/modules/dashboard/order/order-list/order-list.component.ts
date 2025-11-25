import { Component, Input, OnInit } from '@angular/core';
import { Order, OrderService } from '../../../../core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss']
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  pagedOrders: Order[] = []; // Subset of orders to display per page
  currentPage = 1; // Current page number
  itemsPerPage = 10; // Number of items to display per page

  constructor(private route: ActivatedRoute, private orderService: OrderService) { }

  ngOnInit(): void {
    this.orderService.findAll().subscribe((result) => {
      this.orders = result;
      this.setPage(1); // Initialize pagedOrders when orders are available
    });
  }

  // Function to set the current page
  setPage(page: number) {
    if (page < 1 || page > this.totalPages()) {
      return;
    }
    this.currentPage = page;
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = Math.min(startIndex + this.itemsPerPage, this.orders.length);
    this.pagedOrders = this.orders.slice(startIndex, endIndex);
  }

  // Function to get the total number of pages
  totalPages(): number {
    return Math.ceil(this.orders.length / this.itemsPerPage);
  }

  // Function to navigate to the next page
  nextPage() {
    this.setPage(this.currentPage + 1);
  }

  // Function to navigate to the previous page
  prevPage() {
    this.setPage(this.currentPage - 1);
  }
}
