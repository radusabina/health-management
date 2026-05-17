import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { IRecommendation } from '../dtos/recommendation/IRecommendation';

@Component({
  selector: 'app-reccomendation-details',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './reccomendation-details.component.html',
  styleUrl: './reccomendation-details.component.css',
})
export class ReccomendationDetailsComponent implements OnInit {
  rec: IRecommendation | null = null;

  constructor(private router: Router) {}

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();
    const state = nav?.extras?.state as { recommendation: IRecommendation } | undefined;

    if (state?.recommendation) {
      this.rec = state.recommendation;
    } else {
      // State lost (e.g. page refresh) – fall back to last successful navigation state
      const fallback = this.router.lastSuccessfulNavigation?.extras?.state as
        | { recommendation?: IRecommendation }
        | undefined;
      if (fallback?.recommendation) {
        this.rec = fallback.recommendation;
      }
    }
  }

  goBack(): void {
    this.router.navigate(['/recommendations']);
  }

  healthScoreColor(score: number): string {
    if (score >= 70) return 'text-success';
    if (score >= 40) return 'text-warning';
    return 'text-danger';
  }

  healthScoreBadge(score: number): string {
    if (score >= 70) return 'det-badge-good';
    if (score >= 40) return 'det-badge-mid';
    return 'det-badge-low';
  }

  stripHtml(html: string): string {
    if (!html) return '';
    return html.replace(/<[^>]*>/g, '');
  }
}
