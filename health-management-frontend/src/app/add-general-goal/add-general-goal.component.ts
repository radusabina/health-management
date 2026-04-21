import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  Output,
  OnChanges,
  SimpleChanges,
} from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-general-goal',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './add-general-goal.component.html',
  styleUrls: ['./add-general-goal.component.css'],
})
export class AddGeneralGoalComponent {
  @Input() isOpen = false;
  @Output() save = new EventEmitter<any>();
  @Output() close = new EventEmitter<void>();

  goal = {
    calorieGoal: null,
    waterGoal: null,
    weightTarget: null,
  };

  onClose(): void {
    console.log('AddGeneralGoalComponent: onClose called');
    this.close.emit();
  }

  onSave(): void {
    console.log('AddGeneralGoalComponent: onSave called', this.goal);
    this.save.emit({ ...this.goal });
  }

  ngOnChanges(changes: SimpleChanges): void {
    const isOpenChange = changes['isOpen'];
    if (
      isOpenChange &&
      isOpenChange.previousValue === true &&
      isOpenChange.currentValue === false
    ) {
      this.resetForm();
    }
  }

  private resetForm(): void {
    this.goal = {
      calorieGoal: null,
      waterGoal: null,
      weightTarget: null,
    };
  }
}
