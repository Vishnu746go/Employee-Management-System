import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { EmailValidator } from '@angular/forms';
import { Observable } from 'rxjs';
import { UserDTO } from '../dtos/user.dto';


@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:1018/api/users'; 

  constructor(private http: HttpClient) {}

  createUser(userData: UserDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, userData, { responseType: 'text' });
  }

  deleteUserById(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/delete/${id}`,  { responseType: 'text' });
  }  

  getAllUsers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}/allUsers`);
  }  

  getAllManagers(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}/allManagers`);
  }  

  getAllEmployees(): Observable<UserDTO[]>{
    return this.http.get<UserDTO[]>(`${this.apiUrl}/allEmployees`);
  }

  getAssignedEmployees(managerId: number): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}/assigned-employees/${managerId}`);
  }

  assignEmployeesToManager(data: { managerId: number, employeeIds: number[] }): Observable<any> {
    return this.http.post(`${this.apiUrl}/assign-employees`, data, { responseType: 'text' });
  }

  getAssignedEmployeesForManager(): Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.apiUrl}/assigned-employees`);
  }

  
  getCurrentUser() {
    return this.http.get(`${this.apiUrl}/current`);
  }  
  
}
