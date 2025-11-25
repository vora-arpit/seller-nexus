import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable,throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment.development';
import { Order } from '../models';

@Injectable()
export class OrderService {

  private baseUrl = `${environment.API_BASE_URL}/order`;

  constructor(private http: HttpClient) { }

  findById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/${id}`)
  }
  create(CustomerId:Number,orderItemId:Number,order: Order): Observable<Order> {
    return this.http.patch<Order>(`${this.baseUrl}/${CustomerId}/${orderItemId}`,  order )
      .pipe(
        catchError(this.handleError)
      );
  }
  updateOrder(id: BigInt, order: Order): Observable<Order> {
    return this.http.patch<Order>(`${this.baseUrl}/${id}`, order);
  }

  deleteOrder(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
  findAll() {
    return this.http.get<Order[]>(`${this.baseUrl}/orders`);
  }
  // searchOrder(filter: string): Observable<Order[]> {
  //   console.log('Searching order items with filter:', filter);
  //   const params = new HttpParams().set('filter', filter);
  //   return this.http.get<Order[]>(`${this.baseUrl}`, { params });
  // }
  private handleError(error: any): Observable<never> {
    console.error('An error occurred:', error);
    return throwError('Something went wrong; please try again later.');
  }
  
}
