import { Component, OnInit } from '@angular/core';
import { UserService } from '../services/user/user.service';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth/auth.service';
import { IUser } from '../dtos/user/IUser';
import { CommonModule } from '@angular/common';
import { Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { IUserUpdate } from '../dtos/user/IUserUpdate';
import { Observable } from 'rxjs';
import { GeneralGoalService } from '../services/general-goal/general-goal.service';
import { IGeneralGoal } from '../dtos/general-goal/IGeneralGoal';

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
  passwordValidationAttempted = false;
  private passwordValidationTimer: ReturnType<typeof setTimeout> | null = null;

  // delete state
  showDeleteModal = false;

  // goal state
  generalGoal: IGeneralGoal | null = null;
  goalForm = { calorieGoal: null as number | null, waterGoal: null as number | null, weightTarget: null as number | null, bottleAmountMl: null as number | null };
  goalSaveSuccess = false;
  goalSaveError = false;

  constructor(private userService: UserService,
    private router: Router,
    private authService: AuthService,
    private location: Location,
    private generalGoalService: GeneralGoalService,
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getAuthResponse()?.user || null;
    if (this.user) {
      this.userUpdate = this.toUpdateModel(this.user);
      this.loadGoal(this.user.id);
    }
  }

  private loadGoal(userId: string): void {
    this.generalGoalService.getByUserId(userId).subscribe({
      next: (goal) => {
        this.generalGoal = goal;
        this.goalForm = {
          calorieGoal: goal.calorieGoal,
          waterGoal: goal.waterGoal,
          weightTarget: goal.weightTarget,
          bottleAmountMl: goal.bottleAmountMl,
        };
      },
      error: () => {},
    });
  }

  onSaveGoal(): void {
    const userId = this.user?.id;
    if (!userId || !this.goalForm.calorieGoal || !this.goalForm.waterGoal || !this.goalForm.weightTarget || !this.goalForm.bottleAmountMl) return;

    const req = {
      userId,
      calorieGoal: this.goalForm.calorieGoal,
      waterGoal: this.goalForm.waterGoal,
      weightTarget: this.goalForm.weightTarget,
      bottleAmountMl: this.goalForm.bottleAmountMl,
    };

    const call$: Observable<unknown> = this.generalGoal
      ? this.generalGoalService.updateForUser(req)
      : this.generalGoalService.add(req);

    call$.subscribe({
      next: () => {
        this.goalSaveSuccess = true;
        this.goalSaveError = false;
        if (!this.generalGoal) {
          this.loadGoal(userId);
        }
        setTimeout(() => (this.goalSaveSuccess = false), 3000);
      },
      error: () => {
        this.goalSaveError = true;
        this.goalSaveSuccess = false;
        setTimeout(() => (this.goalSaveError = false), 3000);
      },
    });
  }

  // password validation
  onCurrentPasswordInput(): void {
    this.clearPasswordValidationTimer();
    this.passwordValidationAttempted = false;

    if (!this.currentPassword.trim()) {
      this.isCurrentPasswordValid = false;
      this.isCheckingPassword = false;
      this.newPassword = '';
      this.confirmPassword = '';
      return;
    }

    this.isCheckingPassword = false;
    this.passwordValidationTimer = setTimeout(() => {
      this.isPasswordValid();
    }, 2000);
  }

  onCurrentPasswordBlur(): void {
    this.clearPasswordValidationTimer();
    this.isPasswordValid();
  }

  isPasswordValid(): void {
    const password = this.currentPassword.trim();
    if (!password) {
      this.isCurrentPasswordValid = false;
      this.isCheckingPassword = false;
      this.passwordValidationAttempted = false;
      this.newPassword = '';
      this.confirmPassword = '';
      return;
    }

    this.isCheckingPassword = true;
    console.log('Checking password...' + password);
    this.userService.isPasswordValid(password, this.user?.id || '').subscribe({
      next: (response: any) => {
        this.passwordValidationAttempted = true;
        this.isCurrentPasswordValid = response.valid;
        if (!response.valid) {
          this.newPassword = '';
          this.confirmPassword = '';
        }
      },
      error: () => {
        this.passwordValidationAttempted = true;
        this.isCurrentPasswordValid = false;
        this.newPassword = '';
        this.confirmPassword = '';
      },
      complete: () => {
        this.isCheckingPassword = false;
      },
    });
  }

  private clearPasswordValidationTimer(): void {
    if (!this.passwordValidationTimer) return;
    clearTimeout(this.passwordValidationTimer);
    this.passwordValidationTimer = null;
  }

  // update user
  onUpdateUser(): void {
    const userId = this.user?.id;
    if (!userId) {
      console.error('Missing user id, cannot update');
      return;
    }

    if (this.newPassword && this.newPassword === this.confirmPassword) {
      this.userService.updatePassword(userId, this.newPassword).subscribe({
        next: () => {
          this.updateUserProfile(userId);
        },
        error: () => {
          console.error('Failed to update password');
        }
      });
    } else {
      this.updateUserProfile(userId);
    }
  }

  onDeleteAccount(): void {
    const userId = this.user?.id;
    if (!userId) return;

    this.userService.deleteUser(userId).subscribe({
      next: () => {
        this.authService.clearAuth();
        this.router.navigate(['/start']);
      },
      error: () => {
        this.showDeleteModal = false;
      },
    });
  }

  goBack(): void {
    if (window.history.length > 1) {
      this.location.back();
      return;
    }

    this.router.navigate(['/dashboard']);
  }

  private updateUserProfile(userId: string): void {
    this.userService.updateUser(userId, this.userUpdate).subscribe({
      next: () => {
        this.syncAuthUser();
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        console.error('Failed to update user');
      }
    });
  }

  private syncAuthUser(): void {
    if (!this.user) return;

    const updatedUser: IUser = {
      ...this.user,
      ...this.userUpdate,
    };

    this.user = updatedUser;
    this.authService.updateStoredUser(updatedUser);
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
