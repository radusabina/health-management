export interface IRecommendationIngredient {
  name: string;
  amount: number;
  unit: string;
}

export interface IRecommendationStep {
  stepNumber: number;
  stepText: string;
}

export interface IRecommendation {
  spoonacularId: number;
  title: string;
  summary: string;
  imageUrl: string;
  readyInMinutes: number;
  servings: number;
  healthScore: number;
  totalCalories: number;
  percentProtein: number;
  percentFat: number;
  percentCarbs: number;
  cuisines: string[];
  ingredients: IRecommendationIngredient[];
  steps: IRecommendationStep[];
}
