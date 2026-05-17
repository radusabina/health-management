import { TestBed } from '@angular/core/testing';

import { DailyGoalService } from './daily-goal.service';

describe('DailyGoalService', () => {
  let service: DailyGoalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DailyGoalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
