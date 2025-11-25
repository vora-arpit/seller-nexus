import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Account, Order } from '../models';
import { environment } from '../../../environments/environment.development';
import { Observable } from 'rxjs';

@Injectable()
export class DashboardService {

  private rootPath = `${environment.API_BASE_URL}/dashboard`;

  constructor(private http: HttpClient) { }

  account() {
    return this.http.get<Account>(`${this.rootPath}/account`);
  }

  lastOrders() {
    return this.http.get<Order[]>(`${this.rootPath}/last-orders`);
  }

  findById(id: bigint): Observable<Order> {
    return this.http.get<Order>(`${this.rootPath}/${id}`)
  }
}