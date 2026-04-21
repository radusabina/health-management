import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MealType } from '../dtos/enums/MealType';

@Component({
  selector: 'app-add-meal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-meal.component.html',
  styleUrl: './add-meal.component.css',
})
export class AddMealComponent {
  manualMode = false;

  mealType = '';
  description = '';

  items: any[] = [];

  mealTypes = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

  enableManualMode() {
    this.manualMode = true;

    // opțional: inițializezi primul item
    if (this.items.length === 0) {
      this.items.push({ name: '', quantity: null, unit: '' });
    }
  }

  backToSimpleMode() {
    this.manualMode = false;
  }

  addItem() {
    this.items.push({ name: '', quantity: null, unit: '' });
  }

  removeItem(i: number) {
    this.items.splice(i, 1);
  }
}
