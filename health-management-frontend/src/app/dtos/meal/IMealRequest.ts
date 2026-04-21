import { MealType } from '../enums/MealType';

export interface IMealRequest {
  userId: string;
  mealType: MealType;
  description: string;
}
