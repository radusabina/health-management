import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth/auth.service';
import { IUser, UserService } from '../services/user/user.service';
import { MealService } from '../services/meal/meal.service';
import { GeneralGoalService } from '../services/general-goal/general-goal.service';
import { AddGeneralGoalComponent } from '../add-general-goal/add-general-goal.component';
import { IMeal } from '../dtos/meal/IMeal';
import { Router, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, AddGeneralGoalComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  user: IUser | null = null;
  isNewUser = false;
  meals: IMeal[] = [];

  showGoalModal = false;

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private mealService: MealService,
    private generalGoalService: GeneralGoalService,
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getAuthResponse()?.user || null;

    if (!this.user) {
      console.error('No user found in auth');
      return;
    }

    this.loadUserState();
    this.loadMeals();
  }

  // -------------------------
  // DATA LOADING
  // -------------------------

  private loadUserState(): void {
    if (!this.user) return;

    this.userService.isNewUser(this.user.id).subscribe({
      next: (res) => {
        this.isNewUser = res.isNewUser;
      },
      error: (err) => {
        console.error('isNewUser failed:', err);
      },
    });
  }

  private loadMeals(): void {
    if (!this.user) return;

    this.mealService.getTodayMeals(this.user.id).subscribe({
      next: (meals) => {
        this.meals = meals;
      },
      error: (err) => {
        console.error('getTodayMeals failed:', err);
      },
    });
  }

  // -------------------------
  // MODAL CONTROL
  // -------------------------

  openGoalModal(): void {
    this.showGoalModal = true;
  }

  closeGoalModal(): void {
    this.showGoalModal = false;
  }

  // -------------------------
  // SAVE GOAL
  // -------------------------

  onGoalSave(goal: any): void {
    console.log('Saving goal:', goal);
    if (!this.user) return;

    this.generalGoalService
      .addGeneralGoal({
        ...goal,
        userId: this.user.id,
      })
      .subscribe({
        next: () => {
          this.showGoalModal = false;
          this.refreshDashboard();
        },
        error: (err) => {
          console.error('addGeneralGoal failed:', err);
        },
      });
  }

  // -------------------------
  // REFRESH
  // -------------------------

  refreshDashboard(): void {
    if (!this.user) return;

    this.userService.getUserById(this.user.id).subscribe({
      next: (user) => {
        this.user = user;
        this.loadMeals();
        this.loadUserState();
      },
      error: (err) => {
        console.error('refreshDashboard failed:', err);
      },
    });
  }

  navigateToAddMeal(): void {
    this.router.navigate(['/addMeal']);
  }
}
