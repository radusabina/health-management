export interface IDailyGoal {
  id: string;
  date: string; // ISO format date string
  caloriesDone: number;
  waterDone: number;
  generalGoalId: string;
  todayWeight: number | null;
}
