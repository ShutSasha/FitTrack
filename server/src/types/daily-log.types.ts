import { IsMongoId, Matches } from 'class-validator'

export class GetDailyLogParams {
  @IsMongoId()
  userId: string

  @Matches(/^\d{4}-\d{2}-\d{2}$/, { message: 'Date must be in format YYYY-MM-DD' })
  date: string
}

export class CalculateTargetsDto {
  gender: string
  height: number
  weight: number
  bodyType: string
  activityLevel: string
  birthDate: Date
  goalType: string
  targetWeight: number
}

export class CalculateTargetsRes {
  targetCalories: number
  targetProtein: number
  targetFat: number
  targetCarbs: number
  targetWater: number
}

export type NutritionTotals = {
  currentProtein: number
  currentFat: number
  currentCarbs: number
  currentWater: number
}
