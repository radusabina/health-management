import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { RecommendationService } from '../services/recommendation/recommendation.service';
import { IRecommendation } from '../dtos/recommendation/IRecommendation';

@Component({
  selector: 'app-reccomendations',
  standalone: true,
  imports: [CommonModule, FormsModule, NavbarComponent],
  templateUrl: './reccomendations.component.html',
  styleUrl: './reccomendations.component.css',
})
export class ReccomendationsComponent implements OnInit {
  // search inputs
  includeIngredients = '';
  excludeIngredients = '';
  maxCalories: number | null = null;

  // state
  recommendations: IRecommendation[] = [];
  isLoading = false;
  hasSearched = false;
  errorMessage = '';
  expandedCards = new Set<number>();
  activeTab: Record<number, 'ingredients' | 'steps'> = {};

  constructor(
    private recommendationService: RecommendationService,
    private router: Router,
  ) {}

  ngOnInit(): void {
    this.loadRandom();
  }

  loadRandom(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.recommendationService.getRandom(25).subscribe({
      next: (data) => {
        this.recommendations = data;
        this.isLoading = false;
      },
      error: () => {
        this.recommendations = [];
        this.isLoading = false;
      },
    });
  }

  search(): void {
    if (!this.includeIngredients.trim()) return;
    this.isLoading = true;
    this.hasSearched = true;
    this.errorMessage = '';
    this.expandedCards.clear();
    this.activeTab = {};

    this.recommendationService
      .search(
        this.includeIngredients.trim(),
        this.excludeIngredients || undefined,
        this.maxCalories ?? undefined,
      )
      .subscribe({
        next: (data) => {
          this.recommendations = data;
          this.isLoading = false;
          if (data.length === 0) {
            this.errorMessage = 'No recipes found for your search. Try different ingredients.';
          }
        },
        error: () => {
          this.recommendations = [];
          this.isLoading = false;
          this.errorMessage = 'Something went wrong. Please try again.';
        },
      });
  }

  clearSearch(): void {
    this.includeIngredients = '';
    this.excludeIngredients = '';
    this.maxCalories = null;
    this.hasSearched = false;
    this.errorMessage = '';
    this.expandedCards.clear();
    this.activeTab = {};
    this.loadRandom();
  }

  goToDetails(rec: IRecommendation): void {
    this.router.navigate(['/recommendations', rec.spoonacularId], {
      state: { recommendation: rec },
    });
  }

  toggleCard(id: number): void {
    if (this.expandedCards.has(id)) {
      this.expandedCards.delete(id);
    } else {
      this.expandedCards.add(id);
      if (!this.activeTab[id]) {
        this.activeTab[id] = 'ingredients';
      }
    }
  }

  isExpanded(id: number): boolean {
    return this.expandedCards.has(id);
  }

  setTab(id: number, tab: 'ingredients' | 'steps'): void {
    this.activeTab[id] = tab;
  }

  healthScoreColor(score: number): string {
    if (score >= 70) return 'text-success';
    if (score >= 40) return 'text-warning';
    return 'text-danger';
  }

  stripHtml(html: string): string {
    if (!html) return '';
    return html.replace(/<[^>]*>/g, '');
  }
}
