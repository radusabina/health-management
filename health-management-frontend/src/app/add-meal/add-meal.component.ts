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

  // form fields
  description = '';
  mealType = '';
  mealItems: IMealAnalyzed = { items: [] };

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
    if (!this.description?.trim()) return;

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

    this.mealService.addMeal(request).subscribe({
      next: () => {
        this.isSaving = false;
        console.log('Meal added successfully');
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        console.error('Add meal failed', err);
        this.isSaving = false;
      },
    });
  }

  submitMeal(form: NgForm) {
    if (!form) {
      // fallback: just call addMeal
      this.addMeal();
      return;
    }

    if (form.invalid) {
      form.form.markAllAsTouched();
      return;
    }

    this.addMeal();
  }
}
