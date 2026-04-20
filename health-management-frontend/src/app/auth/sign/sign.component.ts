import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService, IUserRegister } from '../../services/auth/auth.service';

@Component({
  selector: 'app-sign',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sign.component.html',
  styleUrl: './sign.component.css',
})
export class SignComponent {
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  fullName: string = '';
  age: number | null = null;
  gender: string = '';
  weightKg: number | null = null;
  heightCm: number | null = null;

  loading = false;
  errorMessage: string | null = null;

  constructor(
    private auth: AuthService,
    private router: Router,
  ) {}

  get passwordsDoNotMatch(): boolean {
    return this.password !== this.confirmPassword;
  }

  onSubmit(registerForm: any): void {
    this.errorMessage = null;
    if (this.loading) return;

    if (registerForm.invalid) {
      this.errorMessage = 'Please fix the highlighted fields.';
      return;
    }

    this.loading = true;

    const payload: IUserRegister = {
      email: this.email,
      password: this.password,
      fullName: this.fullName,
      age: this.age,
      gender: this.gender,
      weightKg: this.weightKg,
      heightCm: this.heightCm,
    };

    this.auth.register(payload).subscribe({
      next: () => {
        this.auth
          .login({ email: this.email, password: this.password })
          .subscribe({
            next: (res) => {
              this.auth.setAuth(res);
              this.loading = false;
              this.router.navigate(['/dashboard']);
            },
            error: (loginErr) => {
              console.error('Auto-login failed', loginErr);
              this.loading = false;
              this.errorMessage =
                'Registered but auto-login failed. Please sign in.';
              this.router.navigate(['/login']);
            },
          });
      },
      error: (err) => {
        this.loading = false;
        console.log('Registration error', err);
        this.errorMessage =
          err?.error?.message || err?.error?.error || 'Registration failed';
      },
    });
  }
}
