import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth/auth.service';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  email: string = '';
  password: string = '';

  errorMessage: string | null = null;
  loading = false;

  constructor(
    private authService: AuthService,
    private router: Router,
  ) {}

  onSubmit(): void {
    this.errorMessage = null;
    this.loading = true;

    this.authService
      .login({ email: this.email, password: this.password })
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (res) => {
          this.authService.setAuth(res);
          this.router.navigate(['/']);
        },
        error: (err) => {
          this.errorMessage =
            err?.error?.message ||
            err?.error?.error ||
            'Invalid email or password';
        },
      });
  }
}
