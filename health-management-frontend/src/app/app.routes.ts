import { Routes } from '@angular/router';
import { LoginComponent } from './auth/login/login.component';
import { SignComponent } from './auth/sign/sign.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { AddMealComponent } from './add-meal/add-meal.component';
import { UpdateUserComponent } from './update-user/update-user.component';
import { OverviewComponent } from './overview/overview.component';
import { StartPageComponent } from './start-page/start-page.component';
import { ReccomendationsComponent } from './reccomendations/reccomendations.component';

export const routes: Routes = [
  {path: '', redirectTo: 'start', pathMatch: 'full'},
  { path: 'login', component: LoginComponent },
  { path: 'signUp', component: SignComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'addMeal', component: AddMealComponent },
  { path: 'profile', component: UpdateUserComponent },
  { path: 'overview', component: OverviewComponent },
  { path: 'start', component: StartPageComponent },
  { path: 'recommendations', component: ReccomendationsComponent },
];
