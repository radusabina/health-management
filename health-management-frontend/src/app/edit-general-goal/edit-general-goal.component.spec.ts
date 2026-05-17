import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditGeneralGoalComponent } from './edit-general-goal.component';

describe('EditGeneralGoalComponent', () => {
  let component: EditGeneralGoalComponent;
  let fixture: ComponentFixture<EditGeneralGoalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditGeneralGoalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditGeneralGoalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
