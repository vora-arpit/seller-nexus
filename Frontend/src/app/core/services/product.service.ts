import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from "@angular/core";
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Product } from '../models';
import { environment } from '../../../environments/environment.development';

@Injectable()
export class ProductService {

  private rootPath = `${environment.API_BASE_URL}/products`;

  constructor(private http: HttpClient) { }

  findById(id: bigint): Observable<Product> {
    return this.http.get<Product>(`${this.rootPath}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  findAll(text: string): Observable<Product[]> {
    
    return this.http.get<Product[]>(`${this.rootPath}?filter=${text}`).pipe(
      catchError(this.handleError)
    );
  }

  create(product: Product): Observable<Product> {
    return this.http.post<Product>(`${this.rootPath}`, product)
      .pipe(
        catchError(this.handleError)
      );
  }

  update(id: bigint, product: Product): Observable<Product> {
    return this.http.patch<Product>(`${this.rootPath}/${id}`, product)
      .pipe(
        catchError(this.handleError)
      );
  }

  delete(id: bigint): Observable<void> {
    return this.http.delete<void>(`${this.rootPath}/${id}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: any) {
    console.error("An error occurred", error); // For demo purposes only
    return throwError(error.message || "Server error");
  }
}
