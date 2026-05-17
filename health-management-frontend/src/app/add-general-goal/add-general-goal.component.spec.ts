import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddGeneralGoalComponent } from './add-general-goal.component';

describe('AddGeneralGoalComponent', () => {
  let component: AddGeneralGoalComponent;
  let fixture: ComponentFixture<AddGeneralGoalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddGeneralGoalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AddGeneralGoalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
