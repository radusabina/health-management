import { CommonModule } from '@angular/common';
import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { forkJoin } from 'rxjs';
import { AuthService } from '../services/auth/auth.service';
import { DailyGoalService } from '../services/daily-goal/daily-goal.service';
import { GeneralGoalService } from '../services/general-goal/general-goal.service';
import { IUser } from '../dtos/user/IUser';
import { IDailyGoal } from '../dtos/daily-goal/IDailyGoal';
import { IGeneralGoal } from '../dtos/general-goal/IGeneralGoal';
import { NavbarComponent } from '../navbar/navbar.component';

interface CalendarCell {
  date: Date;
  progress: number;
  hasData: boolean;
  isToday: boolean;
  isFuture: boolean;
}

@Component({
  selector: 'app-overview',
  standalone: true,
  imports: [CommonModule, NavbarComponent],
  templateUrl: './overview.component.html',
  styleUrl: './overview.component.css',
})
export class OverviewComponent implements OnInit {
  user: IUser | null = null;
  generalGoal: IGeneralGoal | null = null;

  private dailyGoalMap = new Map<string, IDailyGoal>();
  private readonly today = new Date();

  currentMonth = this.today.getMonth();
  currentYear = this.today.getFullYear();

  weekdayLabels: ReadonlyArray<string> = [
    'Mon',
    'Tue',
    'Wed',
    'Thu',
    'Fri',
    'Sat',
    'Sun',
  ];

  calendarCells: Array<CalendarCell | null> = [];
  isLoading = false;

  selectedCell: CalendarCell | null = null;
  selectedDailyGoal: IDailyGoal | null = null;
  @ViewChild('dayDetailsModal') dayDetailsModal?: ElementRef<HTMLElement>;
  private lastFocusedElement: HTMLElement | null = null;

  constructor(
    private authService: AuthService,
    private dailyGoalService: DailyGoalService,
    private generalGoalService: GeneralGoalService,
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getAuthResponse()?.user || null;
    if (!this.user) {
      this.buildCalendar();
      return;
    }
    this.loadData();
  }

  private loadData(): void {
    if (!this.user) return;
    this.isLoading = true;

    forkJoin({
      dailyGoals: this.dailyGoalService.getAllForUser(this.user.id),
      generalGoal: this.generalGoalService.getByUserId(this.user.id),
    }).subscribe({
      next: ({ dailyGoals, generalGoal }) => {
        this.generalGoal = generalGoal;
        this.dailyGoalMap.clear();
        for (const dg of dailyGoals) {
          const key = this.toIsoKey(new Date(dg.date));
          this.dailyGoalMap.set(key, dg);
        }
        this.buildCalendar();
      },
      error: (err) => {
        console.error('Failed to load overview data:', err);
        this.buildCalendar();
      },
      complete: () => {
        this.isLoading = false;
      },
    });
  }

  get monthLabel(): string {
    const d = new Date(this.currentYear, this.currentMonth, 1);
    return d.toLocaleString('en-US', { month: 'long', year: 'numeric' });
  }

  prevMonth(): void {
    if (this.currentMonth === 0) {
      this.currentMonth = 11;
      this.currentYear -= 1;
    } else {
      this.currentMonth -= 1;
    }
    this.buildCalendar();
  }

  nextMonth(): void {
    if (this.currentMonth === 11) {
      this.currentMonth = 0;
      this.currentYear += 1;
    } else {
      this.currentMonth += 1;
    }
    this.buildCalendar();
  }

  getCircleStyle(cell: CalendarCell): { [k: string]: string } {
    const deg = (cell.progress / 100) * 360;
    return {
      background: `conic-gradient(var(--fitbite-primary) ${deg}deg, rgba(86, 144, 105, 0.15) ${deg}deg 360deg)`,
    };
  }

  isClickableCell(cell: CalendarCell): boolean {
    return cell.hasData && !cell.isFuture;
  }

  getCircleAriaLabel(cell: CalendarCell): string {
    const dateLabel = cell.date.toLocaleDateString('en-US', {
      month: 'long',
      day: 'numeric',
      year: 'numeric',
    });

    return `Open details: ${cell.progress}% of daily goal on ${dateLabel}`;
  }

  openDay(cell: CalendarCell): void {
    if (!this.isClickableCell(cell)) return;
    this.lastFocusedElement =
      document.activeElement instanceof HTMLElement
        ? document.activeElement
        : null;
    this.selectedCell = cell;
    this.selectedDailyGoal =
      this.dailyGoalMap.get(this.toIsoKey(cell.date)) ?? null;

    setTimeout(() => this.focusModal(), 0);
  }

  closeDay(): void {
    this.selectedCell = null;
    this.selectedDailyGoal = null;

    const focusToRestore = this.lastFocusedElement;
    this.lastFocusedElement = null;
    if (focusToRestore) {
      setTimeout(() => focusToRestore.focus(), 0);
    }
  }

  onModalKeydown(event: KeyboardEvent): void {
    if (event.key === 'Escape') {
      event.preventDefault();
      this.closeDay();
      return;
    }

    if (event.key !== 'Tab') {
      return;
    }

    const modal = this.dayDetailsModal?.nativeElement;
    if (!modal) return;

    const focusableElements = this.getFocusableElements(modal);
    if (focusableElements.length === 0) {
      event.preventDefault();
      modal.focus();
      return;
    }

    const first = focusableElements[0];
    const last = focusableElements[focusableElements.length - 1];
    const active = document.activeElement as HTMLElement | null;

    if (event.shiftKey) {
      if (active === first || !active || !modal.contains(active)) {
        event.preventDefault();
        last.focus();
      }
      return;
    }

    if (active === last) {
      event.preventDefault();
      first.focus();
    }
  }

  get selectedDateLabel(): string {
    if (!this.selectedCell) return '';
    return this.selectedCell.date.toLocaleDateString('en-US', {
      weekday: 'long',
      month: 'long',
      day: 'numeric',
      year: 'numeric',
    });
  }

  private buildCalendar(): void {
    const first = new Date(this.currentYear, this.currentMonth, 1);
    const lastDay = new Date(
      this.currentYear,
      this.currentMonth + 1,
      0,
    ).getDate();
    const firstWeekday = (first.getDay() + 6) % 7; // Monday-first offset

    const cells: Array<CalendarCell | null> = [];
    for (let i = 0; i < firstWeekday; i++) {
      cells.push(null);
    }

    for (let d = 1; d <= lastDay; d++) {
      const date = new Date(this.currentYear, this.currentMonth, d);
      const progress = this.computeProgress(date);
      cells.push({
        date,
        progress,
        hasData: progress > 0,
        isToday: this.isSameDate(date, this.today),
        isFuture: date > this.today && !this.isSameDate(date, this.today),
      });
    }

    // Keep a stable calendar height by always reserving 6 full weeks (42 day cells).
    // This prevents content below the grid from moving between months.
    while (cells.length < 42) {
      cells.push(null);
    }

    this.calendarCells = cells;
  }

  private focusModal(): void {
    const modal = this.dayDetailsModal?.nativeElement;
    if (!modal) return;

    const focusableElements = this.getFocusableElements(modal);
    if (focusableElements.length > 0) {
      focusableElements[0].focus();
      return;
    }

    modal.focus();
  }

  private getFocusableElements(container: HTMLElement): HTMLElement[] {
    const selectors = [
      'button:not([disabled])',
      '[href]',
      'input:not([disabled])',
      'select:not([disabled])',
      'textarea:not([disabled])',
      '[tabindex]:not([tabindex="-1"])',
    ];

    return Array.from(
      container.querySelectorAll<HTMLElement>(selectors.join(',')),
    ).filter(
      (el) =>
        !el.hasAttribute('disabled') &&
        el.getAttribute('aria-hidden') !== 'true' &&
        el.offsetParent !== null,
    );
  }

  private computeProgress(date: Date): number {
    if (!this.generalGoal) return 0;

    const key = this.toIsoKey(date);
    const dg = this.dailyGoalMap.get(key);
    if (!dg) return 0;

    const calorieGoal = this.generalGoal.calorieGoal || 0;
    const waterGoal = this.generalGoal.waterGoal || 0;

    const calProgress =
      calorieGoal > 0 ? (dg.caloriesDone / calorieGoal) * 100 : 0;
    const waterProgress =
      waterGoal > 0 ? (dg.waterDone / waterGoal) * 100 : 0;

    const avg = (calProgress + waterProgress) / 2;
    return Math.min(100, Math.max(0, Math.round(avg)));
  }

  private toIsoKey(d: Date): string {
    const y = d.getFullYear();
    const m = `${d.getMonth() + 1}`.padStart(2, '0');
    const day = `${d.getDate()}`.padStart(2, '0');
    return `${y}-${m}-${day}`;
  }

  private isSameDate(a: Date, b: Date): boolean {
    return (
      a.getFullYear() === b.getFullYear() &&
      a.getMonth() === b.getMonth() &&
      a.getDate() === b.getDate()
    );
  }
}
