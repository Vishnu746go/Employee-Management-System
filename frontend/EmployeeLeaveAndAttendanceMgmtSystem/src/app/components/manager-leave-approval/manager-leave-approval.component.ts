import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LeaveService } from '../../services/leave.service';
import { LeaveRequestResponseDTO, UpdateLeaveRequest } from '../../dtos/leave.dto';
import { ToastrService } from 'ngx-toastr';
import { UserDTO } from '../../dtos/user.dto';
import { UserService } from '../../services/user.service';

@Component({
  standalone: true,
  selector: 'app-manager-leave-approval',
  templateUrl: './manager-leave-approval.component.html',
  styleUrls: ['./manager-leave-approval.component.css'],
  imports: [CommonModule]
})
export class ManagerLeaveApprovalComponent implements OnInit {
  leaveRequests: LeaveRequestResponseDTO[] = [];
  assignedEmployees: UserDTO[] = [];

  pageSize = 5;
  currentPage = 0;

  constructor(private leaveService: LeaveService, private userService: UserService, private toastr: ToastrService) {}

  ngOnInit() {
    this.fetchAssignedEmployees();
  }

    get paginatedLeaveRequests(): LeaveRequestResponseDTO[] {
      const start = this.currentPage * this.pageSize;
      return this.leaveRequests.slice(start, start + this.pageSize);
    }

    get totalPages(): number {
      return Math.ceil(this.leaveRequests.length / this.pageSize);
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


  fetchAssignedEmployees() {
    this.userService.getAssignedEmployeesForManager().subscribe({
      next: (employees) => {
        this.assignedEmployees = employees;
        this.fetchLeaveRequests();
      },
      error: (err) => {
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  fetchLeaveRequests() {
    this.leaveService.getPendingLeaveRequests().subscribe({
      next: (response) => {
        const today = new Date();
        this.leaveRequests = response.filter(req => new Date(req.endDate) >= today).map(req => ({
          ...req,
          employeeName: this.assignedEmployees.find(emp => emp.id === req.userId)?.name
        }));
      },
      error: (err) => {
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  updateStatus(requestId: number, status: 'APPROVED' | 'REJECTED') {

    const updateRequest: UpdateLeaveRequest = {
      leaveRequestId: requestId,
      status: status
    };

    this.leaveService.updateLeaveStatus(updateRequest).subscribe({
      next: () => {
        this.leaveRequests = this.leaveRequests.filter(req => req.id !== requestId);
        this.toastr.success(`Leave request ${status.toLowerCase()} successfully!`, 'Success');
      },
      error: (err) => {
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }
}
