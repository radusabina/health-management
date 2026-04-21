import { MealType } from '../enums/MealType';

export interface IMealUpdateRequest {
  mealId: string;
  mealType: MealType;
  description: string;
}
