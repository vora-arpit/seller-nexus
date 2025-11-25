import { Component, OnInit, ViewChild } from '@angular/core';
import { PageEvent, MatPaginator } from '@angular/material/paginator';
import { JoomProductsService } from '../../../core/services/sellernexus/joom-products.service';
import { JoomAuthService } from '../../../core/services/sellernexus/joom-auth.service';

@Component({
  selector: 'app-joom-products',
  templateUrl: './joom-products.component.html',
  styleUrls: ['./joom-products.component.scss']
})
export class JoomProductsComponent implements OnInit {

  products: any[] = [];
  loading = false;
  error = '';
  page = 1;
  pageSize = 50;
  hasMore = true;
  total = 0;
  credentials: any[] = [];
  selectedCredentialId: number | null = null;

  constructor(private productsService: JoomProductsService, private joomAuth: JoomAuthService) { }

  @ViewChild(MatPaginator) paginator?: MatPaginator;

  ngOnInit(): void {
    this.loadCredentials();
  }

  /**
   * Load products from backend.
   * @param append if true, append to existing products; otherwise replace
   */
  /** Load a specific page (replaces existing list) */
  loadPage(pageNumber: number) {
    this.loading = true;
    this.error = '';
    this.productsService.getProductsForCredential(this.selectedCredentialId, pageNumber, this.pageSize).subscribe({
      next: (res) => {
        let items: any[] = [];
        if (Array.isArray(res)) {
          items = res;
        } else if (res && Array.isArray(res.items)) {
          // Backend returns {items: [...]}
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
        this.page = pageNumber;
        this.hasMore = items.length === this.pageSize;

        // rough total: if more pages exist assume at least one more page
        this.total = this.hasMore ? (this.page * this.pageSize + this.pageSize) : (this.page * this.pageSize);

        this.loading = false;
        // reset paginator to current page if present
        if (this.paginator) {
          this.paginator.pageIndex = this.page - 1;
          this.paginator.length = this.total;
        }
      },
      error: (err) => {
        this.loading = false;
        this.error = 'Failed to load products.';
        console.error('Joom products error', err);
      }
    });
  }

  loadCredentials() {
    this.joomAuth.getCredentials().subscribe({
      next: (res: any[]) => {
        this.credentials = res || [];
        // Auto-select the first credential if none chosen
        if (!this.selectedCredentialId && this.credentials.length) {
          this.selectedCredentialId = this.credentials[0].id;
        }
        // Load products for the selected credential
        if (this.selectedCredentialId) {
          this.loadPage(1);
        }
      },
      error: (err) => {
        console.error('Failed to load credentials', err);
      }
    });
  }

  loadMore() {
    // kept for compatibility but using numbered paging now
    if (this.loading || !this.hasMore) { return; }
    this.loadPage(this.page + 1);
  }

  refresh() {
    this.loadPage(1);
  }

  onPage(event: PageEvent) {
    const pageIndex = event.pageIndex; // zero based
    const pageSize = event.pageSize;

    // If pageSize changed, reset to first page to avoid jumping to stale offsets
    if (pageSize !== this.pageSize) {
      this.pageSize = pageSize;
      // reset paginator UI to first page
      if (this.paginator) {
        this.paginator.pageIndex = 0;
      }
      this.loadPage(1);
      this.scrollToTop();
      return;
    }

    // Normal page change
    this.loadPage(pageIndex + 1);
    this.scrollToTop();
  }

  scrollToTop() {
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  getTitle(p: any): string {
    return p?.name || p?.title || p?.product_name || p?.productTitle || '';
  }

  getImage(p: any): string {
    if (!p) { return '/assets/images/product.png'; }

    // Prefer JOOM `mainImage.origUrl`
    const main = p?.mainImage;
    if (main) {
      if (typeof main === 'string' && main.length) { return main; }
      if (main.origUrl) { return main.origUrl; }
      if (Array.isArray(main.processed) && main.processed.length) {
        const first = main.processed[0];
        if (first?.origUrl) { return first.origUrl; }
        if (first?.url) { return first.url; }
      }
    }

    // extraImages array (objects with origUrl)
    if (Array.isArray(p?.extraImages) && p.extraImages.length) {
      const e = p.extraImages[0];
      if (e?.origUrl) { return e.origUrl; }
      if (typeof e === 'string') { return e; }
    }

    // Older shapes
    const img = p.image || p.image_url || (p.images && p.images[0]) || p.pic_url || p.picture || p.imageUrl;
    if (typeof img === 'string' && img.length) { return img; }
    if (Array.isArray(img) && img.length) { return img[0]; }

    return '/assets/images/product.png';
  }

  formatPrice(p: any): string {
    // JOOM returns a few possible price containers; prefer salesPrice then price then averageSalesPrice
    const sales = p?.salesPrice;
    const price = p?.price;
    const avg = p?.averageSalesPrice;

    const pick = sales || price || avg;
    if (!pick) {
      // try primitive fallbacks
      const prim = p?.price || p?.amount || p?.cost;
      return prim !== undefined && prim !== null ? String(prim) : '';
    }

    // pick may be object with min/max
    const min = pick?.min ?? pick?.min_price ?? pick?.from ?? pick?.low;
    const max = pick?.max ?? pick?.max_price ?? pick?.to ?? pick?.high;

    if (min !== undefined && max !== undefined) {
      // if equal show single value
      try {
        const nmin = String(min);
        const nmax = String(max);
        return nmin === nmax ? nmin : `${nmin} - ${nmax}`;
      } catch {
        return `${min} - ${max}`;
      }
    }

    // If pick is a primitive
    if (typeof pick === 'string' || typeof pick === 'number') {
      return String(pick);
    }

    // last resort: attempt to extract any numeric-looking property
    for (const k of ['amount', 'value', 'price', 'selling_price']) {
      if (pick[k] !== undefined) { return String(pick[k]); }
    }

    return '';
  }

  shortDesc(p: any, len = 140): string {
    const d = p?.description || p?.desc || p?.short_description || p?.summary || '';
    if (!d) { return ''; }
    const t = String(d);
    return t.length > len ? t.substring(0, len).trim() + '…' : t;
  }

}
