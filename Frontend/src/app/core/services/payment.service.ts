import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class PaymentService {

  constructor(private http: HttpClient) { }
    
  createPayment(payment: any, orderId: number): Observable<any> {
    return this.http.post(`${environment.API_BASE_URL}/payment?orderId=${orderId}`, payment);
  }

  generateInvoice(invoiceData: any): Observable<any> {
    return this.http.post(`${environment.API_BASE_URL}/invoice/generate-invoice`, invoiceData, { responseType: 'blob' });
  }
}