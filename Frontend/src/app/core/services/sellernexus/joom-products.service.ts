import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment.development';

@Injectable({
  providedIn: 'root'
})
export class JoomProductsService {

  private baseUrl = environment.API_BASE_URL;

  constructor(private http: HttpClient) { }

  getProducts(page: number = 1, pageSize: number = 50): Observable<any> {
    const url = `${this.baseUrl}/api/joom/products?page=${page}&pageSize=${pageSize}`;
    return this.http.get<any>(url);
  }

  getProductsForCredential(credentialId: number | null, page: number = 1, pageSize: number = 50) {
    let url = `${this.baseUrl}/api/joom/products?page=${page}&pageSize=${pageSize}`;
    if (credentialId) {
      url += `&credentialId=${credentialId}`;
    }
    return this.http.get<any>(url);
  }

}
