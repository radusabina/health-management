import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { MealService } from '../services/meal/meal.service';
import { IMealAnalyzed } from '../dtos/meal/IMealAnalyzed';
import { MealType } from '../dtos/enums/MealType';
import { IMealUpdateRequest } from '../dtos/meal/IMealUpdateRequest';

@Component({
  selector: 'app-edit-meal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-meal.component.html',
  styleUrl: './edit-meal.component.css',
})
export class EditMealComponent implements OnInit, OnDestroy {
  mealId = '';
  isLoading = true;
  loadError = '';
  isAnalyzing = false;
  isSaving = false;
  analyzeErrorMsg = '';
  descriptionError = '';
  saveMealError = '';
  itemsError = false;

  description = '';
  mealType = '';
  mealItems: IMealAnalyzed = { items: [] };
  mealTypes: MealType[] = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

  private routeSub: Subscription | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private mealService: MealService,
  ) {}

  ngOnInit(): void {
    this.routeSub = this.route.paramMap.subscribe((params) => {
      const id = params.get('id');
      if (!id) {
        this.loadError = 'Meal not found.';
        this.isLoading = false;
        return;
      }
      this.mealId = id;
      this.loadMeal();
    });
  }

  ngOnDestroy(): void {
    this.routeSub?.unsubscribe();
  }

  private loadMeal(): void {
    this.isLoading = true;
    this.loadError = '';

    this.mealService.getMealById(this.mealId).subscribe({
      next: (meal) => {
        this.mealType = meal.mealType;
        this.description = meal.description;
        this.mealItems = {
          items: meal.items.map((item) => ({
            name: item.name,
            quantityGrams: item.quantityGrams,
          })),
        };
        if (this.mealItems.items.length === 0) {
          this.addItem();
        }
        this.isLoading = false;
      },
      error: (err) => {
        console.error('getMealById failed:', err);
        this.loadError =
          err?.error?.message ?? 'Could not load this meal. Please try again.';
        this.isLoading = false;
      },
    });
  }

  analyzeMeal(): void {
    this.descriptionError = '';
    this.analyzeErrorMsg = '';

    if (!this.description?.trim()) {
      this.descriptionError =
        'Please enter a meal description before analyzing.';
      return;
    }

    this.isAnalyzing = true;

    this.mealService.analyzeMeal(this.description).subscribe({
      next: (res) => {
        this.mealItems = res;
        this.itemsError = false;
        this.isAnalyzing = false;
      },
      error: (err) => {
        console.error('Analyze failed:', err);
        this.isAnalyzing = false;

        if (err?.status === 422) {
          this.analyzeErrorMsg =
            err?.error?.message ??
            'Your description could not be processed. Try describing your meal differently or edit items manually.';
        } else {
          this.descriptionError =
            err?.error?.message ??
            'Invalid description. Please check your input and try again.';
        }
      },
    });
  }

  addItem(): void {
    if (!this.canAddItem()) return;
    this.mealItems.items.push(this.createEmptyItem());
  }

  removeItem(index: number): void {
    this.mealItems.items.splice(index, 1);
    this.itemsError = false;
  }

  canAddItem(): boolean {
    if (this.mealItems.items.length === 0) return true;
    const last = this.mealItems.items[this.mealItems.items.length - 1];
    return this.isValidItem(last);
  }

  cancel(): void {
    this.router.navigate(['/dashboard']);
  }

  submitMeal(form: NgForm): void {
    if (!form) {
      this.saveMeal();
      return;
    }

    this.saveMealError = '';
    this.analyzeErrorMsg = '';
    this.descriptionError = '';

    if (!this.description?.trim()) {
      this.descriptionError =
        'Please enter a meal description before saving.';
      form.form.markAllAsTouched();
      return;
    }

    this.pruneTrailingEmptyItems();

    const validItems = this.mealItems.items.filter((it) => this.isValidItem(it));
    if (validItems.length === 0) {
      this.itemsError = true;
      form.form.markAllAsTouched();
      return;
    }

    this.itemsError = false;

    if (this.mealType && validItems.length > 0) {
      this.saveMeal(validItems);
      return;
    }

    form.form.markAllAsTouched();
  }

  private saveMeal(validItems?: IMealAnalyzed['items']): void {
    const items =
      validItems ??
      this.mealItems.items
        .filter((item) => this.isValidItem(item))
        .map((item) => ({
          name: item.name.trim(),
          quantityGrams: item.quantityGrams,
        }));

    if (!this.mealType || items.length === 0) return;

    if (!this.description?.trim()) {
      this.descriptionError =
        'Please enter a meal description before saving.';
      return;
    }

    const request: IMealUpdateRequest = {
      mealId: this.mealId,
      mealType: this.mealType as MealType,
      description: this.description.trim(),
      items: items.map((item) => ({
        name: item.name.trim(),
        quantityGrams: item.quantityGrams,
      })),
    };

    this.isSaving = true;
    this.saveMealError = '';

    this.mealService.updateMeal(request).subscribe({
      next: () => {
        this.isSaving = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Update meal failed', err);
        this.isSaving = false;

        if (err?.status === 422) {
          this.saveMealError =
            err?.error?.message ??
            'One or more food items could not be found. Please check the names and try again.';
        } else {
          this.saveMealError =
            err?.error?.message ??
            'Could not save the meal. Please check the item names and try again.';
        }
      },
    });
  }

  private isValidItem(item: { name: string; quantityGrams: number }): boolean {
    return !!item?.name?.trim() && (item?.quantityGrams ?? 0) > 0;
  }

  private createEmptyItem(): IMealAnalyzed['items'][0] {
    return {
      name: '',
      quantityGrams: 100,
    };
  }

  private pruneTrailingEmptyItems(): void {
    while (this.mealItems.items.length > 1) {
      const last = this.mealItems.items[this.mealItems.items.length - 1];
      if (this.isValidItem(last)) break;

      const others = this.mealItems.items.slice(0, -1);
      if (others.some((it) => this.isValidItem(it))) {
        this.mealItems.items.pop();
      } else {
        break;
      }
    }
  }
}
