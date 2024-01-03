import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

const BASIC_URL = ["http://localhost:8080/"]

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  signup(signupRequest: any): Observable<any> {
    return this.http.post<[]>(BASIC_URL + "api/auth/signup", signupRequest)
  }

  login(loginRequest: any): Observable<any> {
    return this.http.post<[]>(BASIC_URL + "api/auth/login", loginRequest);
  }
}
