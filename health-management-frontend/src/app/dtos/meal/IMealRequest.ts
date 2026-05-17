import { MealType } from '../enums/MealType';
import { IMealItemRequest } from './IMealItemRequest';

export interface IMealRequest {
  userId: string;
  mealType: MealType;
  description: string;
  items: IMealItemRequest[];
}
