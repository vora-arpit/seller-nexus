import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { User } from '../models';
import { environment } from '../../../environments/environment.development';
import { FACEBOOK_AUTH_URL, GITHUB_AUTH_URL, GOOGLE_AUTH_URL } from '../constants';

@Injectable()
export class AuthService {

  private ITEM_KEY = 'currentUser';
  private rootPath = `${environment.API_BASE_URL}/auth`;
  public OAuthURLS = {
    google: GOOGLE_AUTH_URL,
    facebook: FACEBOOK_AUTH_URL,
    github: GITHUB_AUTH_URL,
  };

  private currentUserSubject: BehaviorSubject<User>;
  public currentUser$: Observable<User>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<User>(JSON.parse(localStorage.getItem(this.ITEM_KEY)));
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  public get currentUserValue() {
    return this.currentUserSubject.value;
  }

  login(email: string, password: string) {
    return this.http.post<{ token: string, user: User }>(`${this.rootPath}/login`, { email, password })
      .pipe(map(auth => {
        if (auth.token && auth.user) {
          auth.user.token = auth.token;
          localStorage.setItem(this.ITEM_KEY, JSON.stringify(auth.user));
          this.currentUserSubject.next(auth.user);
        }
        return auth.user;
      }));
  }

  signup(name: string, email: string, password: string) {
    return this.http.post<{
      success: boolean;
      message: string;
    }>(`${this.rootPath}/signup`, { name, email, password });
  }

  forgotPassword(email: string): Observable<any> {
    return this.http.post<any>(`${this.rootPath}/forgot-password`, { email });
  }

  resetPassword(token: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.rootPath}/reset-password`, { token, password });
}

  

  processOAuth2LoginResult(token: string) {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<User>(`${this.rootPath}/current`, { headers })
      .pipe(tap(user => {
        const newUser = {
          ...user, token
        }
        localStorage.setItem(this.ITEM_KEY, JSON.stringify(newUser));
        this.currentUserSubject.next(newUser);
      }));
  }

  logout() {
    localStorage.removeItem(this.ITEM_KEY);
    this.currentUserSubject.next(null);
  }

  // Method to retrieve the token
  getToken(): string | null {
    const user = JSON.parse(localStorage.getItem(this.ITEM_KEY));
    return user ? user.token : null;
  }
}
