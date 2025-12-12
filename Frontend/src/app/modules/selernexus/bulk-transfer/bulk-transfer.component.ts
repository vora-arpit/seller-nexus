import { Component, OnInit } from '@angular/core';
import { TransferV2Service } from '../../../core/services/sellernexus/transfer-v2.service';
import { JoomAuthService } from '../../../core/services/sellernexus/joom-auth.service';
import { JoomProductsService } from '../../../core/services/sellernexus/joom-products.service';

@Component({
  selector: 'app-bulk-transfer',
  templateUrl: './bulk-transfer.component.html',
  styleUrls: ['./bulk-transfer.component.scss']
})
export class BulkTransferComponent implements OnInit {
  credentials: any[] = [];
  products: any[] = [];
  selectedProducts: string[] = [];
  sourceAccountId: number | null = null;
  targetAccountId: number | null = null;
  sellerId: number = 1;
  
  loading = false;
  productsFetched = false;
  transferResult: any = null;
  errorMessage = '';

  constructor(
    private transferV2Service: TransferV2Service,
    private authService: JoomAuthService,
    private productService: JoomProductsService
  ) { }

  ngOnInit(): void {
    this.loadCredentials();
  }

  loadCredentials(): void {
    this.authService.getCredentials().subscribe({
      next: (data: any) => {
        this.credentials = data || [];
        if (this.credentials.length >= 2) {
          this.sourceAccountId = this.credentials[0].id;
          this.targetAccountId = this.credentials[1].id;
          // Get sellerId from the credential
          this.sellerId = this.credentials[0].sellerId || 1;
        } else if (this.credentials.length === 1) {
          this.sourceAccountId = this.credentials[0].id;
          this.sellerId = this.credentials[0].sellerId || 1;
        }
        
        // Auto-fetch products if source account is selected
        if (this.sourceAccountId) {
          this.fetchProducts();
        }
      },
      error: (err: any) => {
        console.error('Error loading credentials:', err);
        this.errorMessage = 'Failed to load accounts';
      }
    });
  }

  fetchProducts(): void {
    if (!this.sourceAccountId) {
      this.errorMessage = 'Please select a source account';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.products = [];
    this.selectedProducts = [];
    
    this.productService.getProductsForCredential(this.sourceAccountId).subscribe({
      next: (res: any) => {
        let items: any[] = [];
        if (Array.isArray(res)) {
          items = res;
        } else if (res && Array.isArray(res.items)) {
          items = res.items;
        } else if (res && Array.isArray(res.data)) {
          items = res.data;
        } else if (res && res.result && Array.isArray(res.result)) {
          items = res.result;
        } else if (res && res.products && Array.isArray(res.products)) {
          items = res.products;
        } else {
          const arr = Object.values(res).find(v => Array.isArray(v));
          items = Array.isArray(arr) ? arr : [];
        }

        this.products = items;
        this.productsFetched = true;
        this.loading = false;

        if (items.length === 0) {
          this.errorMessage = 'No products found for this account';
        }
      },
      error: (err: any) => {
        console.error('Error fetching products:', err);
        this.errorMessage = err.error?.error || 'Failed to fetch products';
        this.loading = false;
      }
    });
  }

  toggleProduct(productId: string): void {
    const index = this.selectedProducts.indexOf(productId);
    if (index > -1) {
      this.selectedProducts.splice(index, 1);
    } else {
      if (this.selectedProducts.length < 50) {
        this.selectedProducts.push(productId);
      } else {
        this.errorMessage = 'Maximum 50 products can be selected';
      }
    }
  }

  isSelected(productId: string): boolean {
    return this.selectedProducts.includes(productId);
  }

  allSelected(): boolean {
    return this.products.length > 0 && this.selectedProducts.length === this.products.length;
  }

  someSelected(): boolean {
    return this.selectedProducts.length > 0 && this.selectedProducts.length < this.products.length;
  }

  toggleAll(): void {
    if (this.allSelected()) {
      // Deselect all
      this.selectedProducts = [];
    } else {
      // Select all (up to 50 max)
      this.selectedProducts = this.products.slice(0, 50).map(p => p.id);
      if (this.products.length > 50) {
        this.errorMessage = 'Only first 50 products selected (maximum limit)';
      }
    }
  }

  bulkTransfer(): void {
    if (!this.sourceAccountId || !this.targetAccountId) {
      this.errorMessage = 'Please select both source and target accounts';
      return;
    }

    if (this.selectedProducts.length === 0) {
      this.errorMessage = 'Please select at least one product';
      return;
    }

    if (this.selectedProducts.length < 2) {
      this.errorMessage = 'Bulk transfer requires at least 2 products';
      return;
    }

    this.loading = true;
    this.errorMessage = '';
    this.transferResult = null;

    this.transferV2Service.bulkTransfer(
      this.sellerId,
      this.sourceAccountId,
      this.targetAccountId,
      this.selectedProducts
    ).subscribe({
      next: (result) => {
        this.transferResult = result;
        this.loading = false;
        this.selectedProducts = [];
      },
      error: (err) => {
        console.error('Error during bulk transfer:', err);
        this.errorMessage = err.error?.error || 'Bulk transfer failed';
        this.loading = false;
      }
    });
  }
}
