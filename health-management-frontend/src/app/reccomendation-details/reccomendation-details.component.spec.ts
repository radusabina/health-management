import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReccomendationDetailsComponent } from './reccomendation-details.component';

describe('ReccomendationDetailsComponent', () => {
  let component: ReccomendationDetailsComponent;
  let fixture: ComponentFixture<ReccomendationDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReccomendationDetailsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ReccomendationDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
