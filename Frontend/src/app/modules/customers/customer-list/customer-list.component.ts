import { Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import { Router } from '@angular/router';
import { Customer } from '../../../core';

@Component({
  selector: 'app-customer-list',
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.scss']
})
export class CustomerListComponent implements AfterViewInit, OnInit, OnChanges {

  @Input() public customers: Customer[] = [];

  @Output() public filtered: EventEmitter<string> = new EventEmitter();
  @ViewChild('filterInput') filterInput!: ElementRef<HTMLInputElement>;

  pagedCustomers: Customer[] = [];
  currentPage = 1;
  itemsPerPage = 5;

  constructor(private router: Router) { }

  ngAfterViewInit() {
    if (this.filterInput) {
      this.filterInput.nativeElement.addEventListener('keyup', () => {
        this.filtered.emit(this.filterInput!.nativeElement.value);
      });
    } else {
      console.error('filterInput is undefined');
    }
  }

  ngOnInit() {
    this.updatePagedCustomers();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['customers']) {
      this.updatePagedCustomers();
    }
  }

  updatePagedCustomers(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.pagedCustomers = this.customers.slice(startIndex, endIndex);
  }

  onPageChange(event: any): void {
    this.currentPage = event.pageIndex + 1; // Update currentPage (pageIndex is 0-based)
    this.itemsPerPage = event.pageSize; // Update itemsPerPage
    this.updatePagedCustomers(); // Update pagedCustomers based on new pagination settings
  }
  

  navigate(id: any) {
    this.router.navigate([id]);
  }
}
