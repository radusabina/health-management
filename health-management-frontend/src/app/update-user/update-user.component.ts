import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user/user.service';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { IUser } from '../dtos/user/IUser';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IUserUpdate } from '../dtos/user/IUserUpdate';

@Component({
  selector: 'app-update-user',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './update-user.component.html',
  styleUrl: './update-user.component.css'
})
export class UpdateUserComponent implements OnInit {
  user: IUser | null = null;
  userUpdate: IUserUpdate = {
    email: '',
    fullName: '',
    age: null,
    gender: 'OTHER',
    weightKg: null,
    heightCm: null,
  };
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  isCurrentPasswordValid = false;
  isCheckingPassword = false;

  constructor(private userService: UserService,
    private router: Router,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getAuthResponse()?.user || null;
    if (this.user) {
      this.userUpdate = this.toUpdateModel(this.user);
    }
  }

  onCurrentPasswordInput(): void {
    if (!this.currentPassword.trim()) {
      this.isCurrentPasswordValid = false;
      this.newPassword = '';
      this.confirmPassword = '';
    }
  }

  isPasswordValid(): void {
    const password = this.currentPassword.trim();
    if (!password) {
      this.isCurrentPasswordValid = false;
      this.newPassword = '';
      this.confirmPassword = '';
      return;
    }

    this.isCheckingPassword = true;
    this.userService.isPasswordValid(password).subscribe({
      next: (isValid) => {
        this.isCurrentPasswordValid = isValid;
        if (!isValid) {
          this.newPassword = '';
          this.confirmPassword = '';
        }
      },
      error: () => {
        this.isCurrentPasswordValid = false;
        this.newPassword = '';
        this.confirmPassword = '';
      },
      complete: () => {
        this.isCheckingPassword = false;
      },
    });
  }

  private toUpdateModel(user: IUser): IUserUpdate {
    return {
      email: user.email,
      fullName: user.fullName,
      age: user.age,
      gender: user.gender,
      weightKg: user.weightKg,
      heightCm: user.heightCm,
    };
  }

}
