import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterOutlet} from '@angular/router';
import { NavbarComponent } from '../../components/navbar/navbar.component';

@Component({
  standalone: true,
  selector: 'app-employee',
  templateUrl: './employee.component.html',
  styleUrls: ['./employee.component.css'],
  imports: [RouterOutlet, NavbarComponent]
})
export class EmployeeComponent {
  constructor(private authService: AuthService, private router: Router) {}

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
