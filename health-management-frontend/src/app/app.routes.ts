import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignComponent } from './auth/sign/sign.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AddMealComponent } from './add-meal/add-meal.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'signUp', component: SignComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'addMeal', component: AddMealComponent },
];
