import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LeaveService } from '../../services/leave.service';
import { LeaveRequestResponseDTO } from '../../dtos/leave.dto';
import { GetLeaveBalanceComponent } from '../getleavebalance/getleavebalance.component';
import { ToastrService } from 'ngx-toastr';

@Component({
  standalone: true,
  selector: 'app-getleaverequesthistory',
  templateUrl: './getleaverequesthistory.component.html',
  styleUrls: ['./getleaverequesthistory.component.css'],
  imports: [CommonModule, GetLeaveBalanceComponent]
})
export class GetLeaveRequestHistoryComponent implements OnInit {
  leaveRequests: LeaveRequestResponseDTO[] = [];

  constructor(private leaveService: LeaveService, private toastr: ToastrService) {}

  ngOnInit() {
    this.fetchLeaveRequests();
  }

  fetchLeaveRequests() {
    this.leaveService.getLeaveRequests().subscribe({
      next: (response) => {
        this.leaveRequests = response.slice(-5);
        this.leaveRequests = this.leaveRequests.reverse();
      },
      error: (err) => {
        console.error('Error fetching leave requests');
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }
}
