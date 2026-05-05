import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth/auth.service';
import { UserService } from '../services/user/user.service';
import { MealService } from '../services/meal/meal.service';
import { GeneralGoalService } from '../services/general-goal/general-goal.service';
import { AddGeneralGoalComponent } from '../add-general-goal/add-general-goal.component';
import { EditGeneralGoalComponent } from '../edit-general-goal/edit-general-goal.component';
import { IMeal } from '../dtos/meal/IMeal';
import { Router, RouterOutlet } from '@angular/router';
import { DailyGoalService } from '../services/daily-goal/daily-goal.service';
import { IDailyGoal } from '../dtos/daily-goal/IDailyGoal';
import { IGeneralGoal } from '../dtos/general-goal/IGeneralGoal';
import { IUser } from '../dtos/user/IUser';
import { NavbarComponent } from '../navbar/navbar.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, AddGeneralGoalComponent, EditGeneralGoalComponent, NavbarComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  // state
  showGoalModal = false;
  showEditGoalModal = false;
  todayWeightInput: number | null = null;
  mealPendingDelete: IMeal | null = null;
  user: IUser | null = null;
  isNewUser = false;
  meals: IMeal[] = [];
  dailyGoal: IDailyGoal | null = null;
  generalGoal: IGeneralGoal | null = null;
  expandedMeals: Set<number> = new Set();
  showWaterToast = false;
  toastMessage = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private mealService: MealService,
    private generalGoalService: GeneralGoalService,
    private dailyGoalService: DailyGoalService,
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getAuthResponse()?.user || null;

    if (!this.user) {
      return;
    }

    this.loadUserState();
    this.loadMeals();
    this.loadDailyGoal();
    this.loadGeneralGoal();
  }

  // data loading
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

  private loadDailyGoal(): void {
    if (!this.user) return;

    this.dailyGoalService.getToday(this.user.id).subscribe({
      next: (goal: IDailyGoal) => {
        this.dailyGoal = goal;
        this.dailyGoal.waterDone = this.dailyGoal.waterDone / 1000;
      },
      error: (err: any) => {
        console.error('getTodayDailyGoal failed:', err);
      },
    });
  }

  private loadGeneralGoal(): void {
    if (!this.user) return;

    this.generalGoalService.getByUserId(this.user.id).subscribe({
      next: (goal) => {
        this.generalGoal = goal;
        this.generalGoal.waterGoal = this.generalGoal.waterGoal / 1000;
      },
      error: (err) => {
        console.error('getByUserId failed:', err);
      },
    });
  }

  // modal control
  openGoalModal(): void {
    this.showGoalModal = true;
  }

  onGoalClose(): void {
    this.showGoalModal = false;
  }

  openEditGoalModal(): void {
    this.showEditGoalModal = true;
  }

  onEditGoalClose(): void {
    this.showEditGoalModal = false;
  }

  onEditGoalSave(goal: Partial<IGeneralGoal>): void {
    if (!this.user) return;

    this.generalGoalService
      .updateForUser({
        userId: this.user.id,
        calorieGoal: goal.calorieGoal!,
        waterGoal: goal.waterGoal!,
        weightTarget: goal.weightTarget!,
        bottleAmountMl: goal.bottleAmountMl!,
      })
      .subscribe({
        next: () => {
          this.showEditGoalModal = false;
          this.refreshDashboard();
        },
        error: (err) => {
          console.error('updateGeneralGoalForUser failed:', err);
        },
      });
  }

  // toggle meal details
  toggleMeal(index: number): void {
    if (this.expandedMeals.has(index)) {
      this.expandedMeals.delete(index);
    } else {
      this.expandedMeals.add(index);
    }
  }

  isExpanded(index: number): boolean {
    return this.expandedMeals.has(index);
  }

  // operations
  onGoalSave(goal: any): void {
    console.log('Saving goal:', goal);
    if (!this.user) return;

    this.generalGoalService
      .add({
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

  addWater(amount: number): void {
    this.dailyGoalService.incrementWater(this.dailyGoal!.id, amount).subscribe({
      next: () => {
        this.refreshDashboard();
        // show success toast
        const amountLabel = amount > 0 ? `+${amount}` : `${amount}ml`;
        this.toastMessage = `Water updated successfully! (${amountLabel})`;
        this.showWaterToast = true;
        setTimeout(() => (this.showWaterToast = false), 3000);
      },
      error: (err: any) => {
        console.error('addWater failed:', err);
      },
    });
  }

  saveTodayWeight(): void {
    if (!this.dailyGoal || this.todayWeightInput === null) return;

    this.dailyGoalService
      .updateTodayWeight(this.dailyGoal.id, this.todayWeightInput)
      .subscribe({
        next: () => {
          this.toastMessage = `Weight saved: ${this.todayWeightInput} kg`;
          this.showWaterToast = true;
          setTimeout(() => (this.showWaterToast = false), 3000);
          this.refreshDashboard();
          this.todayWeightInput = null;
        },
        error: (err: any) => {
          console.error('updateTodayWeight failed:', err);
        },
      });
  }

  confirmDeleteMeal(meal: IMeal): void {
    this.mealPendingDelete = meal;
  }

  cancelDeleteMeal(): void {
    this.mealPendingDelete = null;
  }

  deleteMeal(): void {
    if (!this.mealPendingDelete) return;
    this.mealService.deleteMeal(this.mealPendingDelete.id).subscribe({
      next: () => {
        this.mealPendingDelete = null;
        this.refreshDashboard();
      },
      error: (err) => {
        console.error('deleteMeal failed:', err);
        this.mealPendingDelete = null;
      },
    });
  }

  // dashboard refresh
  refreshDashboard(): void {
    if (!this.user) return;

    this.userService.getUserById(this.user.id).subscribe({
      next: (user) => {
        this.user = user;
        this.loadMeals();
        this.loadUserState();
        this.loadDailyGoal();
        this.loadGeneralGoal();
      },
      error: (err) => {
        console.error('refreshDashboard failed:', err);
      },
    });
  }

  // others
  navigateToAddMeal(): void {
    this.router.navigate(['/addMeal']);
  }

  get progressBar(): number {
    if (!this.dailyGoal || !this.generalGoal) return 0;

    const caloriesRaw =
      (this.dailyGoal.caloriesDone / this.generalGoal.calorieGoal) * 100;

    const waterRaw =
      (this.dailyGoal.waterDone / this.generalGoal.waterGoal) * 100;

    // Only allow the average to exceed 100% when both goals are over 100%.
    // If just one exceeds 100%, cap it at 100 so it doesn't inflate the total.
    const bothOver100 = caloriesRaw >= 100 && waterRaw >= 100;
    const caloriesForAvg = bothOver100 ? caloriesRaw : Math.min(100, caloriesRaw);
    const waterForAvg = bothOver100 ? waterRaw : Math.min(100, waterRaw);

    return Math.round((caloriesForAvg + waterForAvg) / 2);
  }

  get caloriesProgress(): number {
    if (!this.dailyGoal || !this.generalGoal || !this.generalGoal.calorieGoal) {
      return 0;
    }
    return Math.round(
      (this.dailyGoal.caloriesDone / this.generalGoal.calorieGoal) * 100,
    );
  }

  get waterProgress(): number {
    if (!this.dailyGoal || !this.generalGoal || !this.generalGoal.waterGoal) {
      return 0;
    }
    return Math.round(
      (this.dailyGoal.waterDone / this.generalGoal.waterGoal) * 100,
    );
  }

  getMealNutrition(meal: IMeal) {
    return meal.items.reduce(
      (acc, item) => {
        acc.calories += item.calories || 0;
        acc.protein += item.proteinG || 0;
        acc.carbs += item.carbohydratesTotalG || 0;
        acc.fat += item.fatSaturatedG || 0;
        acc.sugar += item.sugarG || 0;
        acc.fiber += item.fiberG || 0;
        acc.sodium += item.sodiumMg || 0;
        acc.potassium += item.potassiumMg || 0;
        acc.cholesterol += item.cholesterolMg || 0;
        return acc;
      },
      {
        calories: 0,
        protein: 0,
        carbs: 0,
        fat: 0,
        sugar: 0,
        fiber: 0,
        sodium: 0,
        potassium: 0,
        cholesterol: 0,
      },
    );
  }
}
