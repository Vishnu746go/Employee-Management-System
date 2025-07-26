import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private apiUrl = 'http://localhost:1018/api/attendance';

  constructor(private http: HttpClient) {}

  clockIn(clockInTime: string) {
    const requestBody = { time:clockInTime };
    return this.http.post(`${this.apiUrl}/clock-in`, requestBody, { responseType: 'text' });
  }

  clockOut(clockOutTime: string) {
    const requestBody = { time:clockOutTime };
    return this.http.post(`${this.apiUrl}/clock-out`, requestBody, { responseType: 'text' });
  }

  getTodayAttendance() {
    return this.http.get(`${this.apiUrl}/today`);
  }

  getTodayAttendanceForManager(userId: number){
    return this.http.get(`${this.apiUrl}/today/${userId}`);
  }

  getAttendanceHistory(){
    return this.http.get(`${this.apiUrl}/attendance-history/`);
  }
}
