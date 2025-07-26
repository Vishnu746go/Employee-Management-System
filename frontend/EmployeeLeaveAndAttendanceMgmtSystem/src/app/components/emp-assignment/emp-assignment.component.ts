import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserDTO } from '../../dtos/user.dto';
import { ToastrService } from 'ngx-toastr'; 

@Component({
  standalone: true,
  selector: 'app-emp-assignment',
  templateUrl: './emp-assignment.component.html',
  styleUrls: ['./emp-assignment.component.css'],
  imports: [CommonModule, FormsModule]
})export class EmpAssignmentComponent implements OnInit {
  managers: UserDTO[] = [];
  employees: UserDTO[] = [];
  selectedManager: number | null = null;
  selectedEmployees: number[] = [];
  assignedEmployees: UserDTO[] = []; 
  submitted = false; 
  constructor(private userService: UserService, private toastr: ToastrService) {}
  ngOnInit() {
    this.getManagers();
    this.getEmployees();
  }

  getManagers() {
    this.userService.getAllManagers().subscribe({
      next: (response: UserDTO[]) => this.managers = response,
      error: (err) => {
        console.error('Error fetching managers');
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  getEmployees() {
    this.userService.getAllEmployees().subscribe({
      next: (response: UserDTO[]) => this.employees = response,
      error: (err) => {
        console.error('Error fetching employees');
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message); 
      }
    });
  }

  onManagerChange(event: any) {
      this.selectedManager = event.target.value;
      if (this.selectedManager) {
          this.getAssignedEmployees(this.selectedManager);
      }
  }

  getAssignedEmployees(managerId: number) {
      this.userService.getAssignedEmployees(managerId).subscribe({
        next: (response: UserDTO[]) => this.assignedEmployees = response,
        error: (err) =>{ 
          console.error('Error fetching assigned employees');
          const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
          this.toastr.error(errorData.message); 
        }
      });
  }


  toggleSelection(employeeId: number, event: any) {
    if (event.target.checked) {
      this.selectedEmployees.push(employeeId);
    } else {
      this.selectedEmployees = this.selectedEmployees.filter(id => id !== employeeId);
    }
  }
  onSubmit() {
    this.submitted = true; 

    if (!this.selectedManager || this.selectedEmployees.length === 0) {
        return; 
    }

    const requestData = {
        managerId: this.selectedManager,
        employeeIds: this.selectedEmployees
    };

    this.userService.assignEmployeesToManager(requestData).subscribe({
        next: (response) => {
            this.toastr.success('Employees assigned successfully!', 'Success'); 
            console.log('API Response:', response);
            this.selectedEmployees = []; 
            this.submitted = false; 
            this.selectedManager = 0;
            this.ngOnInit(); 
        },
        error: (err) => {
            const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
            this.toastr.error(errorData.message); 
            console.error('API Error:', err);
        }
    });
}

}