import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ShiftAssignmentComponent } from '../components/shift-assignment/shift-assignment.component';
import { ShiftAssignment } from '../dtos/shift.dto';

@Injectable({
  providedIn: 'root'
})
export class ShiftService {
  private apiUrl = 'http://localhost:1018/api/shifts';

  constructor(private http: HttpClient) {}
  
  assignWeeklyShifts(requestData: ShiftAssignment): Observable<any> {
    return this.http.post(`${this.apiUrl}/assign/week`, requestData,  { responseType: 'text' });
  }
  
  getShiftByDate(date: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/user/shift?date=${date}`);
  }

  createSwapRequest(requestData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/swap-request`, requestData, { responseType: 'text' });
  }  

  getShiftSwapBalance(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/user/swap-balance`);
  }
  
  getRecentSwapHistory(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/swap-request/user`);
  }
  
  getShiftSwapRequestsForManager(status: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/swap-request/manager/${status}`);
  }
  
  updateSwapRequestStatus(requestData: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/swap-request/status`, requestData, { responseType: 'text' });
  }
  
}
