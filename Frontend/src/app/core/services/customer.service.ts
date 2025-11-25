import { Injectable } from "@angular/core";
import { HttpClient } from '@angular/common/http';
import { Observable, catchError, tap, throwError } from 'rxjs';
import { Customer } from '../models';
import { environment } from '../../../environments/environment.development';

@Injectable()
export class CustomerService {

  private rootPath = `${environment.API_BASE_URL}/customers`;

  constructor(private http: HttpClient) { }

  findById(id: bigint): Observable<Customer> {
    return this.http.get<Customer>(`${this.rootPath}/${id}`);
  }

  findAll(text: string): Observable<Customer[]> {
    console.log("text",text)
    return this.http.get<Customer[]>(`${this.rootPath}?filter=${text}`).pipe(
      catchError(this.handleError)
    );
  }
  

  create(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(`${this.rootPath}`, customer);
  }

  update( id: bigint,customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.rootPath}/${id}`, customer);
  }

  delete(id: bigint): Observable<void> {
    return this.http.delete<void>(`${this.rootPath}/${id}`);
  }

  printAllCustomers(): void {
    this.findAll('').subscribe(
      customers => {
        console.log('All customers:', customers);
      },
      error => {
        console.error('Error fetching customers:', error);
      }
    );
  }

  private handleError(error: any) {
    console.error("An error occurred", error); // For demo purposes only
    return throwError(error.message || "Server error");
  }
}
