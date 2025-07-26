import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { AttendanceService } from '../../services/attendance.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-team-attendance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './team-attendance.component.html',
  styleUrls: ['./team-attendance.component.css']
})
export class TeamAttendanceComponent implements OnInit {
  teamData: {
    userId: number;
    name: string;
    status: string;
    clockIn: string;
    clockOut: string;
  }[] = [];
  currentDateTime: string = '';
  pageSize = 5;
  currentPage = 0;

  constructor(
    private userService: UserService,
    private attendanceService: AttendanceService,
    private toastr: ToastrService
  ) {
    setInterval(() => {
      this.updateDateTime();
    }, 100);
  }

  ngOnInit() {
    this.loadTeamAttendance();
  }

  
  get paginatedTeamData(): any[] {
    const start = this.currentPage * this.pageSize;
    return this.teamData.slice(start, start + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.teamData.length / this.pageSize);
  }

  
nextPage(): void {
  if (this.currentPage < this.totalPages-1) {
    this.currentPage++;
  }
}

previousPage(): void {
  if (this.currentPage > 0) {
    this.currentPage--;
  }
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

  loadTeamAttendance() {
    this.userService.getAssignedEmployeesForManager().subscribe({
      next: employees => {
        employees.forEach(user => {
          this.attendanceService.getTodayAttendanceForManager(user.id).subscribe({
            next: (res: any) => {
              this.teamData.push({
                userId: user.id,
                name: user.name,
                status: res?.status || 'ABSENT',
                clockIn: res?.clockInTime || '-',
                clockOut: res?.clockOutTime || '-'
              });
            },
            error: () => {
              this.teamData.push({
                userId: user.id,
                name: user.name,
                status: 'ABSENT',
                clockIn: '-',
                clockOut: '-'
              });
            }
          });
        });
        this.teamData.sort((a, b) => a.userId - b.userId);
      },
      error: err => {
        console.error('Failed to load employees:', err)
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }
}
