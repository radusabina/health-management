import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth/auth.service';
import { IUser } from '../dto/IUser';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent {
  user: IUser | null = null;
  userName: string = 'John Doe';
  isNewUser = true;
  meals = [
    { name: 'Breakfast', calories: 500 },
    { name: 'Lunch', calories: 700 },
    { name: 'Dinner', calories: 600 },
  ];
  totalCalories = this.meals.reduce((sum, meal) => sum + meal.calories, 0);
  dailyGoalCalories = 2000;
  waterDrank = 2;
  dailyWaterGoal = 3;
  mealsCount = this.meals.length;

  constructor(private authService: AuthService) {
    this.authService.currentUser$.subscribe((user) => {
      this.user = user;
      console.log('User in DashboardComponent:', this.user);
    });
  }
}
