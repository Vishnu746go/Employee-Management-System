import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  role: string | null = null; 

  constructor(private authService: AuthService, private router: Router) {
    this.role = this.authService.getUserRole(); 
  }
  
  navigateToPage(page: string) {
    if (this.role === 'ADMIN') {
      this.router.navigate([`/admin/${page}`]);
    } else if(this.role === 'MANAGER'){
      this.router.navigate([`/manager/${page}`]);
    }else if(this.role === 'EMPLOYEE'){
      this.router.navigate([`/employee/${page}`]);
    }else{
      alert("You dont have access")
    }
  }
  
    logout() {
      this.authService.logout();
      this.router.navigate(['/login']); 
    }
}
