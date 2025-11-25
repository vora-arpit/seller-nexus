import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment.development';

export interface TransferLog {
  id: number;
  sellerId: number;
  productId?: number;
  sourceCredentialId?: number;
  targetCredentialId?: number;
  platformName: string;
  sourceProductExtId?: string;
  targetProductExtId?: string;
  status: string;
  message?: string;
  requestPayload?: string;
  responsePayload?: string;
  errorMessage?: string;
  startedAt: string;
  finishedAt?: string;
  durationMs?: number;
  syncedAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class JoomTransferLogsService {
  private apiUrl = `${environment.API_BASE_URL}/api/joom/transfer/logs`;

  constructor(private http: HttpClient) {}

  getLogs(): Observable<TransferLog[]> {
    return this.http.get<TransferLog[]>(this.apiUrl);
  }
}
