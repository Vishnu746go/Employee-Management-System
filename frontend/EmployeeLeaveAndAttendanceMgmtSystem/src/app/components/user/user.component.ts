import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { ToastrService } from 'ngx-toastr'; 

@Component({
  standalone: true,
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class UserComponent {
  userForm: FormGroup;

  constructor(private fb: FormBuilder, private userService: UserService, private toastr: ToastrService) {
    this.userForm = this.fb.group({
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(5)]],
      userRole: ['', Validators.required]
    });
  }

  onSubmit() {
    if (this.userForm.invalid) { 
      this.userForm.markAllAsTouched(); 
      return; 
    }
    if (this.userForm.valid) {
      this.userService.createUser(this.userForm.value).subscribe({
        next: (response) => {
          console.log(response);
          this.toastr.success(response, 'Success');
          setTimeout(()=>window.location.reload(), 1500);
        },
        error: (err) => {
          const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
          this.toastr.error(errorData.message || 'An error occurred.', 'Error');
        }
      });
    }
  }
}
