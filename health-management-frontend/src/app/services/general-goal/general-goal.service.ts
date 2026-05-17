import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { endpointAPI } from '../../config/appconfig';
import { Observable } from 'rxjs';

import { IGeneralGoalRequest } from '../../dtos/general-goal/IGeneralGoalRequest';
import { IGeneralGoal } from '../../dtos/general-goal/IGeneralGoal';
import { IUpdateGeneralGoalRequest } from '../../dtos/general-goal/IUpdateGeneralGoalRequest';

@Injectable({
  providedIn: 'root',
})
export class GeneralGoalService {
  private baseUrl = endpointAPI + 'general-goal';

  constructor(private http: HttpClient) {}

  // POST /api/general-goal
  add(req: IGeneralGoalRequest): Observable<IGeneralGoal> {
    return this.http.post<IGeneralGoal>(this.baseUrl, req);
  }

  // PUT /api/general-goal
  update(req: IUpdateGeneralGoalRequest): Observable<void> {
    return this.http.put<void>(this.baseUrl, req);
  }

  // GET /api/general-goal/{id}
  getById(id: string): Observable<IGeneralGoal> {
    return this.http.get<IGeneralGoal>(`${this.baseUrl}/${id}`);
  }

  // GET /api/general-goal/getByUserId/{userId}
  getByUserId(userId: string): Observable<IGeneralGoal> {
    return this.http.get<IGeneralGoal>(`${this.baseUrl}/getByUserId/${userId}`);
  }

  // PUT /api/general-goal/updateGeneralGoalForUser
  updateForUser(req: IUpdateGeneralGoalRequest): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/updateGeneralGoalForUser`, req);
  }

  // DELETE /api/general-goal/{id}
  delete(id: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  // DELETE /api/general-goal/deleteByUserId/{userId}
  deleteByUserId(userId: string): Observable<any> {
    return this.http.delete(`${this.baseUrl}/deleteByUserId/${userId}`);
  }
}
