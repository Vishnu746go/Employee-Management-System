import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { ToastrService } from 'ngx-toastr';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-user-deletion',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-deletion.component.html',
  styleUrls: ['./user-deletion.component.css']
})
export class UserDeletionComponent implements OnInit {
  userForm!: FormGroup;
  users: any[] = [];
  currentUserEmail: string = '';

  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.userForm = this.fb.group({
      selectedUserId: [null]
    });

    this.userService.getCurrentUser().subscribe({
      next: (res: any) => {
        this.currentUserEmail = res.email;
        this.userService.getAllUsers().subscribe(users => {
          this.users = users.filter(u => u.email !== this.currentUserEmail);
        });
      },
      error: () => this.toastr.error('Failed to load users')
    });
  }

  deleteUser(): void {
    const selectedId = this.userForm.get('selectedUserId')?.value;
    if (!selectedId) {
      this.toastr.warning('Please select a user to delete');
      return;
    }

    this.userService.deleteUserById(selectedId).subscribe({
      next: (response) => {
        console.log(response);
        this.toastr.success(response, 'Success');
        this.userForm.reset();
        this.ngOnInit();
      },
      error: (err) =>{
        const errorData = typeof err.error === 'string' ? JSON.parse(err.error) : err.error;
        this.toastr.error(errorData.message || 'An error occurred.', 'Error');
      } 
    });
  }
}
