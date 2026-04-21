import { INutrition } from './INutrition';

export interface IMealItem {
  name: string;
  quantityGrams: number;
  nutrition: INutrition;
  sugarG: number;
  fiberG: number;
  sodiumMg: number;
  potassiumMg: number;
  fatSaturatedG: number;
  calories: number;
  cholesterolMg: number;
  proteinG: number;
  carbohydratesTotalG: number;
}
