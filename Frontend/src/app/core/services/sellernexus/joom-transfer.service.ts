import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class JoomTransferService {
  private baseUrl = environment.API_BASE_URL;
  constructor(private http: HttpClient) { }

  transferOne(sourceCredentialId: number, targetCredentialId: number, sourceProductId: string) {
    const body = { sourceCredentialId, targetCredentialId, sourceProductId };
    console.log('Transfer request body:', body);
    return this.http.post(`${this.baseUrl}/api/joom/transfer`, body);
  }
}
