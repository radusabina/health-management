import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { endpointAPI } from '../../config/appconfig';
import { IGeneralGoalRequest } from '../../dtos/general-goal/IGeneralGoalRequest';

@Injectable({
  providedIn: 'root',
})
export class GeneralGoalService {
  private baseUrl = endpointAPI + 'general-goal';

  constructor(private http: HttpClient) {}

  addGeneralGoal(request: IGeneralGoalRequest): Observable<any> {
    return this.http.post<any>(this.baseUrl, request);
  }
}
