import { Component, ElementRef, EventEmitter, Input, OnChanges, OnInit, Output, ViewChild,SimpleChanges } from '@angular/core';
import { Product } from '../../../core'; // Import Product from the appropriate location

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit,OnChanges {
  @Input() public products: Product[] = [];
  @Output() public filtered: EventEmitter<string> = new EventEmitter();
  @ViewChild('filterInput') filterInput!: ElementRef;

  pagedProducts: Product[] = []; // Subset of products to display per page
  currentPage = 1; // Current page number
  itemsPerPage = 5; // Number of items to display per page

  constructor() { }

  ngOnInit() {
    this.updatePagedProducts(); // Initialize pagedProducts when products are available
  }

  updatePagedProducts(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.pagedProducts = this.products.slice(startIndex, endIndex);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['products']) {
      this.updatePagedProducts();
    }
  }
  // Event handler for page change event from MatPaginator
  onPageChange(event: any): void {
    this.currentPage = event.pageIndex + 1; // Update currentPage
    this.itemsPerPage = event.pageSize; // Update itemsPerPage
    this.updatePagedProducts(); // Update pagedProducts based on new pagination settings
  }

  navigate(id: any) {
    // Implement navigation logic here
  }
}
