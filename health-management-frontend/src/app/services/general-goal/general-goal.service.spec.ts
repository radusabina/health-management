import { TestBed } from '@angular/core/testing';

import { GeneralGoalService } from './general-goal.service';

describe('GeneralGoalService', () => {
  let service: GeneralGoalService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(GeneralGoalService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
