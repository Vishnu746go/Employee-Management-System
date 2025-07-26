import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ShiftService } from '../../services/shift.service';
import { ToastrService } from 'ngx-toastr';
import { AttendanceService } from '../../services/attendance.service';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
@Component({
  selector: 'app-emp-dashboard',
  templateUrl: './emp-dashboard.component.html',
  styleUrls: ['./emp-dashboard.component.css'],
  imports:[CommonModule]
})
export class EmpDashboardComponent {
    employeeName: string = 'John Doe';
    shiftData: any = null;
  
    clockedIn: boolean = false;
    clockedOut: boolean = false;
    clockInTime: string = '';
    clockOutTime: string = '';
    selectedDate: string = '';

      constructor(private userService: UserService,private attendanceService: AttendanceService, private router: Router,private shiftService: ShiftService, private toastr: ToastrService) {}
    
      ngOnInit() {
        const today = new Date().toISOString().split('T')[0]; 
        this.selectedDate = today;
        this.getShiftForDate(today);
        this.attendanceService.getTodayAttendance().subscribe({
          next: (res: any) => {
            this.clockInTime = res.clockInTime || '';
            this.clockOutTime = res.clockOutTime || '';
          },
          error: (err) => {
            console.error('Error loading today\'s attendance:', err);
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
          }
        });

        this.userService.getCurrentUser().subscribe({
          next: (res: any) => {
            this.employeeName = res.name;
          },
          error: (err) => {
            console.error('Failed to load user info:', err);
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
          }
        });
      }

      getShiftForDate(date: string) {
        this.shiftService.getShiftByDate(date).subscribe({
          next: (response) => this.shiftData = response,
          error: (err) => {
            this.shiftData = null 
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
          }
        });
      }

      goToAttendance() {
        this.router.navigate(['/employee/attendance']);
      }
}
