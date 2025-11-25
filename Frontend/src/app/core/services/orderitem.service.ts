import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { Order, OrderItem } from '../models';

@Injectable()
export class OrderItemService {

  private baseUrl = `${environment.API_BASE_URL}/orderitem`;

  constructor(private http: HttpClient) { }

  getOrderItemById(id: bigint): Observable<OrderItem> {
    return this.http.get<OrderItem>(`${this.baseUrl}/${id}`);
  }

  getOrderItemsByOrderId(id: number): Observable<OrderItem[]> {
    return this.http.get<OrderItem[]>(`${this.baseUrl}/order/${id}`);
  }

  createOrderItem(productId:number,orderId:number ,orderItem: OrderItem): Observable<OrderItem> {
    return this.http.post<OrderItem>(`${this.baseUrl}/${productId}/${orderId}`, orderItem);
  }

  updateOrderItem(id: bigint, orderItem: OrderItem): Observable<OrderItem> {
    return this.http.put<OrderItem>(`${this.baseUrl}/${id}`, orderItem);
  }

  deleteOrderItem(id: bigint): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
  findAll(text: string): Observable<OrderItem[]> {
    return this.http.get<OrderItem[]>(`${this.baseUrl}?filter=${text}`).pipe(
      tap(response => console.log('Response from findAll:', response)));
  }
  searchOrderItems(filter: string): Observable<OrderItem[]> {
    console.log('Searching order items with filter:', filter);
    const params = new HttpParams().set('filter', filter);
    return this.http.get<OrderItem[]>(`${this.baseUrl}`, { params });
  }
  
}

// class UpdateOrderItemRequest {
//   constructor(
//     public orderItem: OrderItem,
//     public orderId: Order
//   ) {}
// }
