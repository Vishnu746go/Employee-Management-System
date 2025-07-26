import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map, switchMap, catchError } from 'rxjs/operators';
import { CreateLeaveRequest, LeaveRequestResponseDTO, LeaveBalanceResponseDTO, UpdateLeaveRequest } from '../dtos/leave.dto';

@Injectable({
  providedIn: 'root'
})
export class LeaveService {
  private apiUrl = 'http://localhost:1018/api/leaves';

  constructor(private http: HttpClient) {}

  createLeaveRequest(request: CreateLeaveRequest): Observable<string> {
    return this.http.post(`${this.apiUrl}/create-request`, request, { responseType: 'text' });
  }

  getLeaveRequests(): Observable<LeaveRequestResponseDTO[]> {
    return this.http.get<LeaveRequestResponseDTO[]>(`${this.apiUrl}/requests`);
  }

  getLeaveBalance(): Observable<LeaveBalanceResponseDTO> {
    return this.http.get<LeaveBalanceResponseDTO>(`${this.apiUrl}/balance`);
  }

  getPendingLeaveRequests(): Observable<LeaveRequestResponseDTO[]> {
    return this.http.get<LeaveRequestResponseDTO[]>(`${this.apiUrl}/manager/history`);
  }

  updateLeaveStatus(updateRequest: UpdateLeaveRequest): Observable<string> {
    return this.http.put(`${this.apiUrl}/manager/update-leave`, updateRequest, { responseType: 'text' });
  }

  getLeaveRequestsForManagerByStatus(status: string){
    return this.http.get<any[]>(`${this.apiUrl}/manager/${status}`);
  }
  
}
