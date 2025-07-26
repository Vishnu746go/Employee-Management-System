import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ReportService } from '../../services/report.service';
import { LeaveRequestResponseDTO, LeaveBalanceResponseDTO } from '../../dtos/leave.dto';
import { ShiftResponseDTO } from '../../dtos/shift.dto';
import { UserService } from '../../services/user.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-manager-history',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './manager-history.component.html',
  styleUrls: ['./manager-history.component.css']
})
export class ManagerHistoryComponent implements OnInit {
  historyForm: FormGroup;
  employeeList: any[] = [];

  attendanceHistory: any[] = [];
  leaveHistory: LeaveRequestResponseDTO[] = [];
  leaveBalance: LeaveBalanceResponseDTO | null = null;
  shiftHistory: ShiftResponseDTO[] = [];
  shiftBalance: number | null = null;

  pageSize: number = 5;
  submitted = false;

  pageIndex: Record<string, number> = {
    attendance: 0,
    leaveRequests: 0,
    shiftHistory: 0
  };

  historyFetched: Record<string, boolean> = {
    attendance: false,
    leaveRequests: false,
    leaveBalance: false,
    shiftHistory: false,
    shiftBalance: false
  };

  currentPageIndex: number = 0;

  constructor(
    private fb: FormBuilder,
    private reportService: ReportService,
    private userService: UserService,
    private toastr: ToastrService
  ) {
    this.historyForm = this.fb.group({
      userId: [null, Validators.required],
      operation: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.userService.getAssignedEmployeesForManager().subscribe({
      next: (users) => this.employeeList = users,
      error: (err) => {
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  fetchHistory(): void {
    this.submitted = true;
    if (this.historyForm.invalid){
      this.toastr.warning('Please fill all fields before submitting.', 'Validation Error');
      return;
    }

    const { userId, operation } = this.historyForm.value;
    this.clearData();

    switch (operation) {
      case 'attendance':
        this.reportService.getAttendanceHistory(userId).subscribe({
          next: (data) => {
            this.attendanceHistory = data;
            this.historyFetched['attendance'] = true;
          },
          error: (err) => {
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
            this.historyFetched['attendance'] = true;
          }
        });
        break;

      case 'leaveRequests':
        this.reportService.getLeaveHistory(userId).subscribe({
          next: (data) => {
            this.leaveHistory = data;
            this.historyFetched['leaveRequests'] = true;
          },
          error: (err) => {
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
            this.historyFetched['leaveRequests'] = true;
          }
        });
        break;

      case 'leaveBalance':
        this.reportService.getLeaveBalance(userId).subscribe({
          next: (data) => {
            this.leaveBalance = data;
            this.historyFetched['leaveBalance'] = true;
          },
          error: (err) => {
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
            this.historyFetched['leaveBalance'] = true;
          }
        });
        break;

      case 'shiftHistory':
        this.reportService.getShiftHistory(userId).subscribe({
          next: (data) => {
            this.shiftHistory = data;
            this.historyFetched['shiftHistory'] = true;
          },
          error: (err) => {
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
            this.historyFetched['shiftHistory'] = true;
          }
        });
        break;

      case 'shiftBalance':
        this.reportService.getShiftBalance(userId).subscribe({
          next: (data) => {
            this.shiftBalance = data;
            this.historyFetched['shiftBalance'] = true;
          },
          error: (err) => {
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
            this.historyFetched['shiftBalance'] = true;
          }
        });
        break;
    }
  }

  private clearData(): void {
    this.attendanceHistory = [];
    this.leaveHistory = [];
    this.leaveBalance = null;
    this.shiftHistory = [];
    this.shiftBalance = null;

    this.pageIndex = {
      attendance: 0,
      leaveRequests: 0,
      shiftHistory: 0
    };

    this.historyFetched = {
      attendance: false,
      leaveRequests: false,
      leaveBalance: false,
      shiftHistory: false,
      shiftBalance: false
    };
  }

  next(type: string): void {
    const totalItems = this.getDataLength(type);
    if ((this.pageIndex[type] + 1) * this.pageSize < totalItems) {
      this.pageIndex[type]++;
    }
  }

  previous(type: string): void {
    if (this.pageIndex[type] > 0) {
      this.pageIndex[type]--;
    }
  }

  getDataLength(type: string): number {
    switch (type) {
      case 'attendance': return this.attendanceHistory?.length || 0;
      case 'leaveRequests': return this.leaveHistory?.length || 0;
      case 'shiftHistory': return this.shiftHistory?.length || 0;
      default: return 0;
    }
  }

  getTotalPages(type: string): number {
    const total = this.getDataLength(type);
    return Math.ceil(total / this.pageSize);
  }  

  hasNext(type: string): boolean {
    const totalItems = this.getDataLength(type);
    return (this.pageIndex[type] + 1) * this.pageSize < totalItems;
  }

  hasPagination(type: string): boolean {
    return ['attendance', 'leaveRequests', 'shiftHistory'].includes(type);
  }
}
