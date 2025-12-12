import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlatformCredentialService {
  private apiUrl = 'http://localhost:8080/api/platform-credentials';

  constructor(private http: HttpClient) { }

  getAllCredentials(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getCredentialById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  deleteCredential(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  updateCredential(id: number, credential: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, credential);
  }
}
