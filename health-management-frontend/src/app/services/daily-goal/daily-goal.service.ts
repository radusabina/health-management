import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { endpointAPI } from '../../config/appconfig';
import { Observable } from 'rxjs';
import { IDailyGoal } from '../../dtos/daily-goal/IDailyGoal';
import { IDailyGoalRequest } from '../../dtos/daily-goal/IDailyGoalRequest';
import { IUpdateDailyGoalRequest } from '../../dtos/daily-goal/IUpdateDailyGoalRequest';

@Injectable({
  providedIn: 'root',
})
export class DailyGoalService {
  private baseUrl = endpointAPI + 'daily-goal';

  constructor(private http: HttpClient) {}

  // POST /api/daily-goal
  addDailyGoal(req: IDailyGoalRequest): Observable<IDailyGoal> {
    return this.http.post<IDailyGoal>(this.baseUrl, req);
  }

  // GET /api/daily-goal/{id}
  getById(id: string): Observable<IDailyGoal> {
    return this.http.get<IDailyGoal>(`${this.baseUrl}/${id}`);
  }

  // GET /api/daily-goal/today/{userId}
  getToday(userId: string): Observable<IDailyGoal> {
    return this.http.get<IDailyGoal>(`${this.baseUrl}/today/${userId}`);
  }

  // GET /api/daily-goal/userDailyGoals/{userId}
  getAllForUser(userId: string): Observable<IDailyGoal[]> {
    return this.http.get<IDailyGoal[]>(
      `${this.baseUrl}/userDailyGoals/${userId}`,
    );
  }

  // PUT /api/daily-goal
  update(req: IUpdateDailyGoalRequest): Observable<IDailyGoal> {
    return this.http.put<IDailyGoal>(this.baseUrl, req);
  }

  // PUT /api/daily-goal/incrementCalories?id=...&caloriesToAdd=...
  incrementCalories(id: string, caloriesToAdd: number): Observable<void> {
    const params = new HttpParams()
      .set('id', id)
      .set('caloriesToAdd', caloriesToAdd);

    return this.http.put<void>(`${this.baseUrl}/incrementCalories`, null, {
      params,
    });
  }

  // PUT /api/daily-goal/incrementWater?id=...&waterToAdd=...
  incrementWater(id: string, waterToAdd: number): Observable<void> {
    const params = new HttpParams().set('id', id).set('waterToAdd', waterToAdd);

    return this.http.put<void>(`${this.baseUrl}/incrementWater`, null, {
      params,
    });
  }

  // PUT /api/daily-goal/updateTodayWeight?id=...&weight=...
  updateTodayWeight(id: string, weight: number): Observable<void> {
    const params = new HttpParams().set('id', id).set('weight', weight);
    return this.http.put<void>(`${this.baseUrl}/updateTodayWeight`, null, { params });
  }

  // DELETE /api/daily-goal/{id}
  delete(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
