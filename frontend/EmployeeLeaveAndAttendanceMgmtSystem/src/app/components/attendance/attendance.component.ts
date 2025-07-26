import { Component, OnInit } from '@angular/core';
import { AttendanceService } from '../../services/attendance.service';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-attendance',
  templateUrl: './attendance.component.html',
  styleUrls: ['./attendance.component.css'],
  imports: [CommonModule]
})
export class AttendanceComponent implements OnInit {
  currentTime: string = new Date().toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  clockInTime: string = '';
  clockOutTime: string = '';
  attendanceHistory: any[] = [];

  constructor(private attendanceService: AttendanceService, private toastr: ToastrService) {
    setInterval(() => {
      this.currentTime = new Date().toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
    }, 1000);
  }

  ngOnInit() {
    this.attendanceService.getTodayAttendance().subscribe({
      next: (response: any) => {
        if (response.clockInTime) {
          this.clockInTime = response.clockInTime;
        }
        if (response.clockOutTime) {
          this.clockOutTime = response.clockOutTime;
        }
      },
      error: (err) => {
        console.error('Error fetching attendance:', err);
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message);
      }
    });

    this.attendanceService.getAttendanceHistory().subscribe({
      next: (response) => {
        this.attendanceHistory = this.generateAttendanceHistory(response as any[]);
      },
      error: (err) => {
        console.error('Error fetching attendance history:', err);
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message);
      }
    });
  }

  clockIn() {

    if(this.clockInTime){
      this.toastr.warning('Clock-in is already done!', 'Warning');
    }
    if (!this.clockInTime) {
      this.clockInTime = new Date().toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', second: '2-digit' });

      this.attendanceService.clockIn(this.clockInTime).subscribe({
        next: (response) => {
          console.log('Clock-in saved:', response);
          this.toastr.success('Clock-in saved successfully', 'Success');
        },
        error: (err) => {
          console.error('Clock-in failed:', err);
          const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
          this.toastr.error(errorData.message);  
        }
      });
    }
  }

  clockOut() {
    if(!this.clockInTime){
      this.toastr.warning('Clock-in is not yet done!', 'Warning');
    }

    if(this.clockOutTime){
      this.toastr.warning('Clock-out is already done!', 'Warning');
    }
    if (this.clockInTime && !this.clockOutTime) {
      this.clockOutTime = new Date().toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', second: '2-digit' });

      this.attendanceService.clockOut(this.clockOutTime).subscribe({
        next: (response) => {
          console.log('Clock-out saved:', response);
          this.toastr.success('Clock-out saved successfully', 'Success');
        },
        error: (err) => {
          console.error('Clock-out failed:', err);
          const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
          this.toastr.error(errorData.message);
        }
      });
    }
  }

  getPastWorkingDays(): string[] {
    const dates: string[] = [];
    let dayCounter = 1;
    while (dates.length < 5) {
      const date = new Date();
      date.setDate(date.getDate() - dayCounter);
      const day = date.getDay();
      if (day !== 0 && day !== 6) {
        dates.push(date.toISOString().split('T')[0]);
      }
      dayCounter++;
    }
    return dates;
  }  

  generateAttendanceHistory(history: any[]): any[] {
    const pastDays = this.getPastWorkingDays();
    const attendanceMap = new Map(history.map(record => [record.date, record]));
  
    return pastDays.map(date => {
      return attendanceMap.has(date)
        ? attendanceMap.get(date)
        : { date, status: 'ABSENT', workHours: '0' };
    });
  }  
}
