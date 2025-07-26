import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';

@Component({
  standalone: true,
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  imports: [CommonModule, ReactiveFormsModule]
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage = '';


  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router, private toastr: ToastrService) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(5)]]
    });
  }


  onSubmit() {
    if (this.loginForm.valid) {
      this.authService.login(this.loginForm.value).subscribe({
        next: (response) => {
          this.authService.saveToken(response.token, response.userRole);
          console.log('Login successful!', response);

          if (response.userRole === 'ADMIN') {
            this.router.navigate(['/admin']);
          } else if (response.userRole == 'MANAGER'){
            this.router.navigate(['/manager']);
          }else if (response.userRole == 'EMPLOYEE'){
            this.router.navigate(['/employee']);
          }
          this.toastr.success('Logged in successfully', 'Success');
        },
        error: (err) => {
          this.errorMessage = 'Login failed. Please check your credentials.';
          const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
          this.toastr.error(errorData.message);  
        }
      });
    }
  }
}