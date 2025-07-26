import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-admin-dashboard',
  imports: [],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent {

  totalUsers: number = 0;
  totalManagers: number = 0;
  totalEmployees: number = 0;

  constructor(
    private userService: UserService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe(users => this.totalUsers = users.length);
    this.userService.getAllManagers().subscribe(managers => this.totalManagers = managers.length);
    this.userService.getAllEmployees().subscribe(emps => this.totalEmployees = emps.length);
  }  

  goToCreateUser() {
    this.router.navigate(['/admin/create-user']);
  }
  
  goToAssignEmployees() {
    this.router.navigate(['/admin/assign-emp']);
  }
  
  goToDeleteUser() {
    this.router.navigate(['/admin/delete-user']);
  }
  
}
