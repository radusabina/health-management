import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MealService } from '../services/meal/meal.service';
import { IMealItem } from '../dtos/meal/IMealItem';
import { INutrition } from '../dtos/meal/INutrition';
import {
  animate,
  state,
  style,
  transition,
  trigger,
} from '@angular/animations';
import { IMealAnalyzed } from '../dtos/meal/IMealAnalyzed';

@Component({
  selector: 'app-add-meal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-meal.component.html',
  styleUrl: './add-meal.component.css',
  animations: [
    trigger('expandCollapse', [
      state(
        'void',
        style({
          height: '0',
          opacity: 0,
          overflow: 'hidden',
        }),
      ),
      state(
        '*',
        style({
          height: '*',
          opacity: 1,
        }),
      ),
      transition('void <=> *', [animate('200ms ease-in-out')]),
    ]),
  ],
})
export class AddMealComponent {
  // state flags
  manualMode: boolean = false;
  analyzedMode: boolean = false;
  isAnalyzing: boolean = false;
  showDetails: boolean = false;

  mealNutrition: INutrition | null = null;
  description = '';
  mealType = '';

  mealTypes: string[] = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

  mealItems: IMealAnalyzed = {
    items: [],
  };

  constructor(private mealService: MealService) {}

  toggleDetails() {
    this.showDetails = !this.showDetails;
  }

  analyzeMeal() {
    this.isAnalyzing = true;

    this.mealService.analyzeMeal(this.description).subscribe({
      next: (res) => {
        this.mealItems = this.mapAnalyzeResponse(res);
        this.analyzedMode = true;
        this.manualMode = false;
        this.isAnalyzing = false;
      },

      error: (err) => {
        console.error('Analyze failed:', err);
        this.isAnalyzing = false;
      },
    });
  }

  // =========================
  // MANUAL MODE
  // =========================
  enableManualMode() {
    this.manualMode = true;
    this.analyzedMode = false;

    if (this.mealItems.items.length === 0) {
      this.addItem();
    }
  }

  backToStep1() {
    this.manualMode = false;
    this.analyzedMode = false;
    this.mealItems.items = [];
    this.description = '';
  }

  addItem() {
    const items = this.mealItems.items;

    if (items.length === 0) {
      items.push(this.createEmptyItem());
      return;
    }
    const lastItem = items[items.length - 1];
    if (
      !lastItem.name?.trim() ||
      !lastItem.quantityGrams ||
      lastItem.quantityGrams <= 0
    ) {
      return;
    }

    items.push(this.createEmptyItem());
  }

  private createEmptyItem(): IMealItem {
    return {
      name: '',
      quantityGrams: 100,
    } as IMealItem;
  }

  canAddItem(): boolean {
    const items = this.mealItems.items;
    if (items.length === 0) return true;

    const last = items[items.length - 1];
    return (
      !!last.name?.trim() && !!last.quantityGrams && last.quantityGrams > 0
    );
  }

  removeItem(index: number) {
    this.mealItems.items.splice(index, 1);
  }

  confirmMeal() {
    //TODO: save meal to backend
  }

  editAnalysis() {
    this.analyzedMode = false;
    this.manualMode = false;
  }

  private mapAnalyzeResponse(res: any): IMealAnalyzed {
    return {
      items: (res.items ?? []).map((item: any) => ({
        name: item.name,
        quantityGrams: item.quantityGrams ?? item.quantity ?? 0,
      })),
    };
  }
}
