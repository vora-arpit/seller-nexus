import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment.development';
import { AuthService } from '../auth.service';

@Injectable({
  providedIn: 'root'
})
export class JoomAuthService {

  private baseUrl = `${environment.API_BASE_URL}`;

  constructor(
    private http: HttpClient,
    private authService: AuthService   // <-- Inject AuthService here
  ) {}

  getAuthorizationUrl(clientId: string, label?: string, clientSecret?: string) {
    console.log('Joom login called');

    const currentUser = this.authService.currentUserValue;
    const userId = currentUser?.id;

    console.log("Logged in User ID →", userId);

    let url = `${this.baseUrl}/api/joom/auth/authorize?clientId=${encodeURIComponent(clientId)}&userId=${userId}`;
    if (label) {
      url += `&label=${encodeURIComponent(label)}`;
    }
    if (clientSecret) {
      url += `&clientSecret=${encodeURIComponent(clientSecret)}`;
    }

    return this.http.get(url, { responseType: 'text' });
  }

  getCredentials() {
    return this.http.get<any[]>(`${this.baseUrl}/api/joom/auth/credentials`);
  }

  deleteCredential(id: number) {
    return this.http.delete(`${this.baseUrl}/api/joom/auth/credentials/${id}`);
  }
}
