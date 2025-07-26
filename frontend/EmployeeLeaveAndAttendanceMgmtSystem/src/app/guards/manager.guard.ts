import { CanActivateFn } from '@angular/router';
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class ManagerGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}

  canActivate(): boolean {
    const token = this.authService.getToken();
    const role = this.authService.getUserRole();
    if (token && role === 'MANAGER') {
      return true;
    } else {
      this.router.navigate(['/login']); 
      return false;
    }
  }
}

