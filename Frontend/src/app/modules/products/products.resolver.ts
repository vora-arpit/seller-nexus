import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Resolve } from '@angular/router';
import { Observable } from 'rxjs';
import { ProductService } from '../../core/services';
import { Product } from '../../core/models';
// import { Product } from '../../core/models/product.model';

@Injectable()
export class ProductResolver implements Resolve<Product | null> {

  constructor(private productService: ProductService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Product | null> {
    const id = route.paramMap.get('id');
    if (id !== null) {
      return this.productService.findById(BigInt(id)); // Convert id to bigint
    } else {
      throw new Error('Product ID is null');
    }
  }

}
