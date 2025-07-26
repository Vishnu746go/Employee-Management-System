import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterOutlet } from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  standalone: true,
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
  imports: [ RouterOutlet, NavbarComponent]
})
export class AdminComponent {
  constructor(private authService: AuthService, private router: Router) {}
  

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']); 
  }
}
