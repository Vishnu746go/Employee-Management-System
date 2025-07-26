import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AttendanceService } from '../../services/attendance.service';
import { ShiftService } from '../../services/shift.service';
import { UserService } from '../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import { LeaveService } from '../../services/leave.service';

@Component({
  selector: 'app-manager-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './manager-dashboard.component.html',
  styleUrls: ['./manager-dashboard.component.css']
})
export class ManagerDashboardComponent implements OnInit {
  employeeName: string = '';
  currentDateTime: string = '';
  assignedEmployees: any[] = [];
  attendanceMap: Map<number, any> = new Map();
  stats = {
    totalEmployees: 0,
    presentToday: 0,
    onLeaveToday: 0,
    absentToday: 0
  };

  pendingShiftRequests: any[] = [];
  pendingLeaveRequests: any[] = [];

  constructor(
    private attendanceService: AttendanceService,
    private shiftService: ShiftService,
    private userService: UserService,
    private leaveService: LeaveService,
    private toastr: ToastrService,
    private router: Router
  ) {
    setInterval(() => {
      this.updateDateTime();
    }, 1000);
  }

  ngOnInit(): void {
    this.loadCurrentUser();
    this.updateDateTime();
    this.loadAssignedEmployees();
    this.loadPendingSwapRequests();
    this.loadPendingLeaveRequests();
  }

  updateDateTime() {
    this.currentDateTime = new Date().toLocaleString('en-IN', {
      weekday: 'long',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  loadCurrentUser() {
    this.userService.getCurrentUser().subscribe({
      next: (res: any) => {
        this.employeeName = res.name;
      },
      error: (err: any) => {
        console.error('Failed to load user info:', err);
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  loadAssignedEmployees() {
    this.userService.getAssignedEmployeesForManager().subscribe({
      next: employees => {
        this.assignedEmployees = employees;
        this.stats.totalEmployees = employees.length;
        this.loadAttendanceStatus();
      },
      error: (err: any) => {
        console.error('Failed to load assigned employees:', err);
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  loadAttendanceStatus() {
    let present = 0;
    let absent = 0;
    let onLeave = 0;

    const today = new Date().toISOString().split('T')[0];

    this.assignedEmployees.forEach(user => {
      this.attendanceService.getTodayAttendanceForManager(user.id).subscribe({
        next: (res: any) => {
          if (res && res.status === 'LEAVE') {
            onLeave++;
          } else if (res && res.status === 'PRESENT') {
            present++;
          } else {
            absent++;
          }
          console.log(res);
          this.stats.presentToday = present;
          this.stats.absentToday = absent;
          this.stats.onLeaveToday = onLeave;
        },
        error: err => {
          absent++;
          this.stats.absentToday = absent;
        }
      });
    });
  }

  loadPendingSwapRequests() {
    this.shiftService.getShiftSwapRequestsForManager('PENDING').subscribe({
      next: res => this.pendingShiftRequests = res,
      error: err => {
        console.error('Failed to load pending swaps:', err)
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  loadPendingLeaveRequests(){
    this.leaveService.getLeaveRequestsForManagerByStatus('PENDING').subscribe({
      next: res => this.pendingLeaveRequests = res,
      error: err => {
        console.error('Failed to load pending swaps:', err)
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  goToAttendance() {
    this.router.navigate(['/manager/team-attendance']);
  }

  goToSwapRequests() {
    this.router.navigate(['/manager/shift-swap-request']);
  }  

  goToLeaveRequests(){
    this.router.navigate(['/manager/leave-approval']);
  }

  goToShifts(){
    this.router.navigate(['/manager/assign-shift']);
  }
}
