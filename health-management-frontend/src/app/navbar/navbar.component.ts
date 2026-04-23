import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  constructor(private router: Router, private authService: AuthService) {}

  logout(): void {
    //this.authService.logout();
    this.router.navigate(['/login']);
  }

  navigateToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  navigateToOverview(): void {
    this.router.navigate(['/overview']);
  }

  navigateToProfile(): void {
    this.router.navigate(['/profile']);
  }
}
