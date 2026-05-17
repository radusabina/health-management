import { MealType } from '../enums/MealType';
import { IMealItemRequest } from './IMealItemRequest';

export interface IMealUpdateRequest {
  mealId: string;
  mealType: MealType;
  description: string;
  items?: IMealItemRequest[];
}
