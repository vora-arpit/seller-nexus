import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TransferV2Service {
  private apiUrl = 'http://localhost:8080/api/v2/transfer';

  constructor(private http: HttpClient) { }

  bulkTransfer(sellerId: number, sourceCredentialId: number, targetCredentialId: number, productIds: string[]): Observable<any> {
    return this.http.post(`${this.apiUrl}/bulk`, {
      sellerId,
      sourceCredentialId,
      targetCredentialId,
      productIds
    });
  }

  getHealth(): Observable<any> {
    return this.http.get(`${this.apiUrl}/health`);
  }

  resetCircuitBreaker(serviceName: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/circuit-breaker/${serviceName}/reset`, {});
  }
}
