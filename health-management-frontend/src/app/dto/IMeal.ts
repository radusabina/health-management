import { MealType } from './enums/MealType';
import { IMealItem } from './IMealItem';
import { INutrition } from './INutrition';

export interface IMeal {
  mealType: MealType;
  description: string;
  date: string;
  totalCalories: number;

  nutritions: INutrition;
  items: IMealItem[];
}
