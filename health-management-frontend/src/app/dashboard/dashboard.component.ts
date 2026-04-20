import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../services/auth/auth.service';
import { IUser, UserService } from '../services/user/user.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class DashboardComponent implements OnInit {
  user: IUser | null = null;
  isNewUser = false;

  constructor(
    private authService: AuthService,
    private userService: UserService,
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getUser();

    if (!this.user) {
      console.error('No user found in auth');
      return;
    }

    this.userService.isNewUser(this.user.id).subscribe({
      next: (res) => {
        this.isNewUser = res.isNewUser;
      },
      error: (err) => {
        console.error('Operation=isNewUser; failed to check user state:', err);
      },
    });
  }
}
