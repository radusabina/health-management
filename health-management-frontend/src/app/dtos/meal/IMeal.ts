import { MealType } from '../enums/MealType';
import { IMealItem } from './IMealItem';

export interface IMeal {
  mealType: MealType;
  description: string;
  date: string;
  totalCalories: number;
  items: IMealItem[];
}
