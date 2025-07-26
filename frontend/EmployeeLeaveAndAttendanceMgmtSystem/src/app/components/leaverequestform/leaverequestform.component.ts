import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { LeaveService } from '../../services/leave.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  standalone: true,
  selector: 'app-leaverequestform',
  templateUrl: './leaverequestform.component.html',
  styleUrls: ['./leaverequestform.component.css'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class LeaveRequestFormComponent {
  leaveForm: FormGroup;

  constructor(private fb: FormBuilder, private leaveService: LeaveService, private toastr: ToastrService) {
    this.leaveForm = this.fb.group({
      leaveType: ['', Validators.required],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      reason: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  onSubmit() {
    if (this.leaveForm.invalid) { 
      this.leaveForm.markAllAsTouched();
      this.toastr.warning('All Fields are required!') 
      return; 
    }
    
    this.leaveService.createLeaveRequest(this.leaveForm.value).subscribe({
      next: () => {
        this.toastr.success('Leave request submitted successfully!', 'Success');
        setTimeout(()=>window.location.reload(), 1500);
      },
      error: (err) => {
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message || 'Error submitting leave request', 'Error'); 
      }
    });
  }
}
