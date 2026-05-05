import { CommonModule } from '@angular/common';
import { Component, OnInit, OnDestroy } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Subscription } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { MealService } from '../services/meal/meal.service';
import { AuthService } from '../services/auth/auth.service';

import { IMealAnalyzed } from '../dtos/meal/IMealAnalyzed';
import { IMealRequest } from '../dtos/meal/IMealRequest';

@Component({
  selector: 'app-add-meal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-meal.component.html',
  styleUrl: './add-meal.component.css',
})
export class AddMealComponent implements OnInit, OnDestroy {
  // states
  manualMode: boolean = false;
  isAnalyzing = false;
  isSaving = false;
  analyzeErrorMsg = '';
  descriptionError = '';
  saveMealError = '';

  // form fields
  description = '';
  mealType = '';
  mealItems: IMealAnalyzed = { items: [] };
  itemsError: boolean = false;

  // enums
  mealTypes: string[] = ['BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'];

  private userSub: Subscription | null = null;
  private currentUser: any | null = null;

  constructor(
    private router: Router,
    private mealService: MealService,
    private authService: AuthService,
  ) {}

  ngOnInit(): void {
    this.userSub = this.authService.currentUser$.subscribe((u) => {
      this.currentUser = u;
    });
  }

  ngOnDestroy(): void {
    this.userSub?.unsubscribe();
  }

  // use the reactive currentUser value populated from AuthService
  private get user() {
    return this.currentUser;
  }

  analyzeMeal() {
    this.descriptionError = '';

    if (!this.description?.trim()) {
      this.descriptionError = 'Please enter a meal description before analyzing.';
      return;
    }

    this.isAnalyzing = true;

    this.mealService.analyzeMeal(this.description).subscribe({
      next: (res) => {
        this.mealItems = res;
        this.manualMode = true;
        this.isAnalyzing = false;
      },
      error: (err) => {
        console.error('Analyze failed:', err);
        this.isAnalyzing = false;

        if (err?.status === 422) {
          // API couldn't recognize the meal → inline error below the button, stay on step 1
          this.analyzeErrorMsg =
            err?.error?.message ?? 'Your description could not be processed. Try describing your meal differently or add items manually.';
        } else {
          // 400 blank description or other client error → inline field error
          this.descriptionError =
            err?.error?.message ?? 'Invalid description. Please check your input and try again.';
        }
      },
    });
  }

  enableManualMode() {
    this.manualMode = true;
    if (this.mealItems.items.length === 0) {
      this.addItem();
    }
  }

  backToStep1() {
    this.manualMode = false;
    this.mealItems = { items: [] };
    this.description = '';
    this.mealType = '';
  }

  addItem(): void {
    if (!this.canAddItem()) return;

    this.mealItems.items.push(this.createEmptyItem());
  }

  removeItem(index: number): void {
    this.mealItems.items.splice(index, 1);
  }

  canAddItem(): boolean {
    if (this.mealItems.items.length === 0) return true;

    const last = this.mealItems.items[this.mealItems.items.length - 1];
    return this.isValidItem(last);
  }

  private isValidItem(item: any): boolean {
    return !!item?.name?.trim() && (item?.quantityGrams ?? 0) > 0;
  }

  private createEmptyItem(): IMealAnalyzed['items'][0] {
    return {
      name: '',
      quantityGrams: 100,
    };
  }

  addMeal() {
    console.log('Adding meal with items:', this.mealItems);
    if (!this.user) {
      console.error('User not authenticated');
      return;
    }

    if (!this.mealType) return;

    const validItems = this.mealItems.items
      .filter((item) => this.isValidItem(item))
      .map((item) => ({
        name: item.name.trim(),
        quantityGrams: item.quantityGrams,
      }));
    console.log('Valid items to save:', validItems);

    if (validItems.length === 0) return;

    const request: IMealRequest = {
      userId: this.user.id,
      mealType: this.mealType as any,
      description: this.description,
      items: validItems.map((item) => ({
        name: item.name,
        quantityGrams: item.quantityGrams,
      })),
    };

    this.isSaving = true;
    this.saveMealError = '';

    this.mealService.addMeal(request).subscribe({
      next: () => {
        this.isSaving = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Add meal failed', err);
        this.isSaving = false;
        this.saveMealError =
          err?.error?.message ?? 'Could not save the meal. Please check the item names and try again.';
      },
    });
  }

  submitMeal(form: NgForm) {
    if (!form) {
      this.addMeal();
      return;
    }

    // Remove trailing empty items when there is at least one other valid item
    this.pruneTrailingEmptyItems();

    const validItems = this.mealItems.items.filter((it) =>
      this.isValidItem(it),
    );
    if (validItems.length === 0) {
      this.itemsError = true;
      form.form.markAllAsTouched();
      return;
    }

    this.itemsError = false;

    // Re-evaluate form validity (pruning may have removed invalid controls)
    // If we have at least one valid item and a meal type, submit immediately.
    if (this.mealType && validItems.length > 0) {
      this.addMeal();
      return;
    }

    // Otherwise, mark form controls touched so the user sees missing-field errors.
    form.form.markAllAsTouched();
  }

  private pruneTrailingEmptyItems() {
    // Remove trailing invalid items but keep at least one item if removing would leave zero valid items.
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
