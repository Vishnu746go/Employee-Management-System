import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:1018/api/auth/login'; 

  constructor(private http: HttpClient) {}

  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}`, credentials);
  }

  saveToken(token: string, role: string) {
    localStorage.setItem('jwtToken', token);
    localStorage.setItem('role', role);
  }

  getToken(): string | null {
    return localStorage.getItem('jwtToken');
  }

  getUserRole(): string | null {
    return localStorage.getItem('role');
  }

  logout() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('role');
  }
}

