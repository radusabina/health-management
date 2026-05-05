import { CommonModule } from '@angular/common';
import {
  Component,
  EventEmitter,
  Input,
  OnChanges,
  Output,
  SimpleChanges,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { IGeneralGoal } from '../dtos/general-goal/IGeneralGoal';

@Component({
  selector: 'app-edit-general-goal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './edit-general-goal.component.html',
  styleUrl: './edit-general-goal.component.css',
})
export class EditGeneralGoalComponent implements OnChanges {
  @Input() isOpen = false;
  @Input() currentGoal: IGeneralGoal | null = null;
  @Output() save = new EventEmitter<Partial<IGeneralGoal>>();
  @Output() close = new EventEmitter<void>();

  goal: Partial<IGeneralGoal> = {};

  ngOnChanges(changes: SimpleChanges): void {
    const isOpenChange = changes['isOpen'];
    const openingModal =
      isOpenChange &&
      isOpenChange.previousValue === false &&
      isOpenChange.currentValue === true;

    if ((changes['currentGoal'] || openingModal) && this.currentGoal) {
      // waterGoal is stored in liters on the frontend after load, convert back to ml for the form
      this.goal = {
        ...this.currentGoal,
        waterGoal: Math.round(this.currentGoal.waterGoal * 1000),
      };
    }
  }

  onClose(): void {
    this.close.emit();
  }

  onSave(): void {
    this.save.emit({ ...this.goal });
  }
}
