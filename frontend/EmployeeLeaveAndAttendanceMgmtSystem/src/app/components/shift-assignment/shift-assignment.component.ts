import { Component, OnInit } from '@angular/core';
import { ShiftService } from '../../services/shift.service';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { UserDTO } from '../../dtos/user.dto';

@Component({
  selector: 'app-shift-assignment',
  templateUrl: './shift-assignment.component.html',
  styleUrls: ['./shift-assignment.component.css'],
  imports: [CommonModule]
})
export class ShiftAssignmentComponent implements OnInit {
  shiftForm!: FormGroup;
  employees: UserDTO[] = [];
  shiftTypes: string[] = ['DAY', 'NIGHT'];
  selectedMondayDate!: string;
  submitted = false;

  constructor(private shiftService: ShiftService, private fb: FormBuilder, private toastr: ToastrService, private userService: UserService) {}

  ngOnInit() {
    this.shiftForm = this.fb.group({
      userId: [null, Validators.required],
      date: ['', Validators.required],
      shiftType: [null, Validators.required]
    });

    this.getAssignedEmployeesForManager();
  }

  onUserChange(event: any) {
    this.shiftForm.controls['userId'].setValue(event.target.value);
    this.shiftForm.controls['userId'].markAsTouched(); 
  }
  
  onShiftTypeChange(event: any) {
    this.shiftForm.controls['shiftType'].setValue(event.target.value);
    this.shiftForm.controls['shiftType'].markAsTouched();
  }
  
  onDateChange(event: any) {
    const selectedDate = event.target.value;
    this.selectedMondayDate = selectedDate;
    this.shiftForm.controls['date'].setValue(selectedDate); 
    this.shiftForm.controls['shiftType'].markAsTouched();
  }
  
  getAssignedEmployeesForManager() {
    this.userService.getAssignedEmployeesForManager().subscribe({
      next: (response: UserDTO[]) => this.employees = response,
      error: (err) =>{ 
        console.error('Error fetching assigned employees')
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message || "Error fetching assigned employees"); 
      }
    });
  }

  onSubmit() {
    this.submitted = true;

    if (this.shiftForm.invalid) {
      this.toastr.error('Please fill all fields before submitting.', 'Validation Error');
      return;
    }

    
    const selectedDate = new Date(this.shiftForm.value.date);
    const today = new Date();
    const dayOfWeek = selectedDate.getDay();

    if (dayOfWeek !== 1) {
      this.toastr.warning('Please select a Monday to assign weekly shifts.', 'Invalid Date');
      return;
    }

    if(selectedDate < today){
      this.toastr.warning('Selected date must not be in the past.', 'Invalid Date');
      return;
    }

    this.shiftService.assignWeeklyShifts(this.shiftForm.value).subscribe({
      next: (response) => {
        this.toastr.success('Shift assigned successfully!', 'Success');
        setTimeout(()=>window.location.reload(), 1500);
        console.log(response);
      },
      error: (err) => {
        console.error('Error assigning shift!', 'Error')
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }
}
