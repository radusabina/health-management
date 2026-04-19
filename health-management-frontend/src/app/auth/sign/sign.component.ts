import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-sign',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './sign.component.html',
  styleUrl: './sign.component.css',
})
export class SignComponent {
  user = {
    email: '',
    password: '',
    fullName: '',
    age: null as number | null,
    gender: '',
    heightCm: null as number | null,
  };
}
