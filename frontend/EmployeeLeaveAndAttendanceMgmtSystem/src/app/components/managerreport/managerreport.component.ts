import { Component } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ReportService } from '../../services/report.service';
import {
  AttendanceReportRequest,
  AttendanceReportResponse,
  LeaveReportRequest,
  LeaveReportResponse,
  ShiftReportRequest,
  ShiftReportResponse
} from '../../dtos/report.dto';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-manager-report',
  imports:[ReactiveFormsModule, FormsModule, CommonModule],
  templateUrl: './managerreport.component.html',
  styleUrls: ['./managerreport.component.css']
})
export class ManagerReportComponent {
  reportForm: FormGroup;
  attendanceReport?: AttendanceReportResponse | null = null; // marking like this so that  
  leaveReport?: LeaveReportResponse | null = null;
  shiftReport?: ShiftReportResponse | null = null;
  reportsVisible: boolean = false;
  employeeList: any;

  constructor(
    private fb: FormBuilder,
    private reportService: ReportService,
    private toastr: ToastrService,
    private userService: UserService
  ) {
    this.reportForm = this.fb.group({
      userId: [null, Validators.required],
      month: ['', Validators.required]
    });    
  }

  currentPageIndex: number = 0;

  get reportPages(): string[] {
    return ['attendance', 'leave', 'shift'];
  }

  nextPage(): void {
    if (this.currentPageIndex < this.reportPages.length - 1) {
      this.currentPageIndex++;
    }
  }

  previousPage(): void {
    if (this.currentPageIndex > 0) {
      this.currentPageIndex--;
    }
  }


  ngOnInit(): void {
    this.userService.getAssignedEmployeesForManager().subscribe({
      next: (employees) => {
        this.employeeList = employees;
      },
      error: (err) => {
        console.error('Failed to load employees:', err);
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  fetchReports(): void {
    if (this.reportForm.invalid) {
      this.reportForm.markAllAsTouched();
      this.toastr.warning('Please fill all fields before submitting.', 'Validation Error');
      return;
    }
    this.reportsVisible = true;
    const { userId, month } = this.reportForm.value;
    const year = parseInt(month.split('-')[0]); 

    const currentDate = new Date();
    const selectedDate = new Date(month + '-01'); // assuming month is in 'YYYY-MM' format

    if (selectedDate > currentDate) {
      this.toastr.error('Please select a valid month. Future dates are not allowed.');
      return;
    }

    console.log(userId, month, year);
    const attendanceRequest: AttendanceReportRequest = { userId, month };
    const leaveRequest: LeaveReportRequest = { userId, month };
    const shiftRequest: ShiftReportRequest = { userId, month };

    this.reportService.getAttendanceReport(attendanceRequest).subscribe({
      next: (data) => this.attendanceReport = data,
      error: (err) => {
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });

    this.reportService.getLeaveReport(leaveRequest).subscribe({
      next: (data) => this.leaveReport = data,
      error: (err) => {
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });

    this.reportService.getShiftReport(shiftRequest).subscribe({
  next: (data) => {
    if (data && Object.keys(data).length > 0) {
      this.shiftReport = data;
    } else {
      this.shiftReport = null;
    }
  },
  error: (err) => {
    if (err.status === 404 || err.status === 204) {
      this.shiftReport = null;
    } else {
      console.error('Error fetching shift report:', err);
      const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
      this.toastr.error(errorData.message); 
    }
  }
});

  }
}
