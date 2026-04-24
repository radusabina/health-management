import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {

  constructor(private router: Router, private authService: AuthService) {}

  get isDashboardPage(): boolean {
    return this.router.url.startsWith('/dashboard');
  }

  get isOverviewPage(): boolean {
    return this.router.url.startsWith('/overview');
  }

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
