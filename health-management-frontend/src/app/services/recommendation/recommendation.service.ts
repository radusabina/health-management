import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { endpointAPI } from '../../config/appconfig';
import { IRecommendation } from '../../dtos/recommendation/IRecommendation';

@Injectable({
  providedIn: 'root',
})
export class RecommendationService {
  private baseUrl = endpointAPI + 'recommendations';

  constructor(private http: HttpClient) {}

  getRandom(count = 25): Observable<IRecommendation[]> {
    const params = new HttpParams().set('count', count);
    return this.http.get<IRecommendation[]>(`${this.baseUrl}/random`, { params });
  }

  search(
    includeIngredients: string,
    excludeIngredients?: string,
    maxCalories?: number,
  ): Observable<IRecommendation[]> {
    let params = new HttpParams().set('includeIngredients', includeIngredients);
    if (excludeIngredients?.trim()) {
      params = params.set('excludeIngredients', excludeIngredients.trim());
    }
    if (maxCalories != null) {
      params = params.set('maxCalories', maxCalories);
    }
    return this.http.get<IRecommendation[]>(this.baseUrl, { params });
  }
}
