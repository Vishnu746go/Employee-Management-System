import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpRequest, HttpResponse } from '@angular/common/http';
import { Observable, map, catchError, filter } from 'rxjs';
import {
  AttendanceReportRequest,
  LeaveReportRequest,
  ShiftReportRequest,
} from '../dtos/report.dto';
import{ LeaveBalanceResponseDTO } from '../dtos/leave.dto'
import { ShiftResponseDTO } from '../dtos/shift.dto';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private apiUrl = 'http://localhost:1018/api/report';
  private attendanceUrl="http://localhost:1018/api/attendance"
  private leaveUrl="http://localhost:1018/api/leaves"
  private shiftUrl="http://localhost:1018/api/shifts"

  
  constructor(private http: HttpClient) {}

  getAttendanceReport(requestData: AttendanceReportRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/attendance/`, requestData);
  }  

  getLeaveReport(request: LeaveReportRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/leave/`, request);
  
  }

  getShiftReport(request: ShiftReportRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/shift`, request);
    
  }

  getAttendanceHistory(userId: number): Observable<any> {
    return this.http.get(`${this.attendanceUrl}/attendance-history/${userId}`);
  }
  
  getLeaveHistory(userId: number): Observable<any> {
    return this.http.get(`${this.leaveUrl}/manager/history/${userId}`);
  }
  
  getLeaveBalance(userId: number): Observable<LeaveBalanceResponseDTO> {
    return this.http.get<LeaveBalanceResponseDTO>(`${this.leaveUrl}/manager/balance/${userId}`);
  }
  
  
  getShiftHistory(userId: number): Observable<ShiftResponseDTO[]> {
    return this.http.get<ShiftResponseDTO[]>(`${this.shiftUrl}/manager/history/${userId}`);
  }
  
  getShiftBalance(userId: number): Observable<any> {
    return this.http.get(`${this. shiftUrl}/swap-request/manager/balance/${userId}`);
  }
  
}
