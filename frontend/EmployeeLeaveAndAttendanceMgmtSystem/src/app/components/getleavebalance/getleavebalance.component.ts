import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LeaveService } from '../../services/leave.service';
import { LeaveBalanceResponseDTO } from '../../dtos/leave.dto';
import { ToastrService } from 'ngx-toastr';

@Component({
  standalone: true,
  selector: 'app-getleavebalance',
  templateUrl: './getleavebalance.component.html',
  styleUrls: ['./getleavebalance.component.css'],
  imports: [CommonModule]
})
export class GetLeaveBalanceComponent implements OnInit {
  leaveBalance: LeaveBalanceResponseDTO | null = null;

  constructor(private leaveService: LeaveService, private toastr: ToastrService) {}

  ngOnInit() {
    this.leaveService.getLeaveBalance().subscribe({
      next: (response) => this.leaveBalance = response,
      error: (err) =>{ 
        console.error('Error fetching leave balance')
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }
}
