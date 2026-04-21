import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { endpointAPI } from '../../config/appconfig';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { IMealRequest } from '../../dtos/meal/IMealRequest';
import { IMealUpdateRequest } from '../../dtos/meal/IMealUpdateRequest';
import { IMeal } from '../../dtos/meal/IMeal';
import { MealType } from '../../dtos/enums/MealType';

@Injectable({
  providedIn: 'root',
})
export class MealService {
  private baseUrl = endpointAPI + 'meal';

  constructor(private http: HttpClient) {}

  addMeal(request: IMealRequest): Observable<any> {
    return this.http.post(this.baseUrl, request);
  }

  updateMeal(request: IMealUpdateRequest): Observable<void> {
    return this.http.put<void>(this.baseUrl, request);
  }

  getMealsByUserId(userId: string): Observable<IMeal[]> {
    return this.http.get<IMeal[]>(`${this.baseUrl}/user/${userId}`);
  }

  getMealsByType(userId: string, mealType: MealType): Observable<IMeal[]> {
    const params = new HttpParams()
      .set('userId', userId)
      .set('mealType', mealType);
    return this.http.get<IMeal[]>(this.baseUrl, { params });
  }

  getTodayMeals(userId: string): Observable<IMeal[]> {
    const params = new HttpParams().set('userId', userId);
    return this.http.get<IMeal[]>(`${this.baseUrl}/today`, { params });
  }

  getMealById(id: string): Observable<IMeal> {
    return this.http.get<IMeal>(`${this.baseUrl}/${id}`);
  }

  deleteMeal(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
