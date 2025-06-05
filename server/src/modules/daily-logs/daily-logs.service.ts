import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { InjectModel } from '@nestjs/mongoose'
import { DailyLog, DailyLogDocument } from './daily-log.schema'
import { isValidObjectId, Model } from 'mongoose'
import { UsersService } from 'modules/users/users.service'
import { activityFactors, bodyTypeModifiers, goalModifiers, proteinPerKg } from 'consts/daily-log.consts'
import { CalculateTargetsDto, CalculateTargetsRes, NutritionTotals } from '~types/daily-log.types'
import { MealDocument } from 'modules/meals/meal.schema'
import { ActivityDocument } from 'modules/activities/activity.schema'
import { NutritionProductsService } from 'modules/nutrition-products/nutrition-products.service'

@Injectable()
export class DailyLogsService {
  constructor(
    @InjectModel(DailyLog.name) private dailyLogModel: Model<DailyLogDocument>,
    private readonly userService: UsersService,
    private readonly nutritionProductService: NutritionProductsService,
  ) {}

  async getAllDailyLogs(): Promise<DailyLogDocument[]> {
    return this.dailyLogModel.find().exec()
  }

  async getDailyLogById(id: string): Promise<DailyLogDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid daily log ID format', HttpStatus.BAD_REQUEST)
    }

    const dailyLog = await this.dailyLogModel.findById(id).exec()

    if (!dailyLog) throw new HttpException('Daily log by this id not found', HttpStatus.NOT_FOUND)

    return dailyLog
  }

  async getDailyLogByMealId(mealId: string): Promise<DailyLogDocument> {
    if (!isValidObjectId(mealId)) {
      throw new HttpException('Invalid meal ID format', HttpStatus.BAD_REQUEST)
    }

    const meal = await this.dailyLogModel.findOne({ meals: mealId }).exec()

    if (!meal) throw new HttpException('Meal by this id not found', HttpStatus.NOT_FOUND)

    return meal
  }

  async getDailyLogByUserIdAndDate(userId: string, date: Date): Promise<DailyLogDocument> {
    // It gets user by id and at the same time check valid ObjectId
    const user = await this.userService.getUserById(userId)

    const dailyLog = await this.dailyLogModel.findOne({ userId, date }).populate('meals').exec()

    const targets = this.calculateTargets({
      gender: user.gender,
      height: user.height,
      weight: user.weight,
      bodyType: user.bodyType,
      activityLevel: user.activityLevel,
      birthDate: user.birthDate,
      goalType: user.goalType,
      targetWeight: user.targetWeight,
    })

    if (dailyLog) {
      dailyLog.calories.target = targets.targetCalories
      dailyLog.protein.target = targets.targetProtein
      dailyLog.fat.target = targets.targetFat
      dailyLog.carbs.target = targets.targetCarbs
      dailyLog.water.target = targets.targetWater
      dailyLog.weight.current = user.weight
      dailyLog.weight.target = user.targetWeight
      await dailyLog.save()

      return dailyLog
    }

    const weight = {
      current: user.weight,
      target: user.targetWeight,
    }

    const newDailyLog = new this.dailyLogModel({
      date,
      userId,
      calories: { target: targets.targetCalories },
      protein: { target: targets.targetProtein },
      fat: { target: targets.targetFat },
      carbs: { target: targets.targetCarbs },
      water: { target: targets.targetWater },
      weight,
    })

    await newDailyLog.save()

    return newDailyLog
  }

  public calculateTargets(user: CalculateTargetsDto): CalculateTargetsRes {
    const age = new Date().getFullYear() - new Date(user.birthDate).getFullYear()

    const genderModifier = user.gender === 'male' ? 5 : user.gender === 'female' ? -161 : -78

    const bmr = 10 * user.weight + 6.25 * user.height - 5 * age + genderModifier

    const activityFactor = activityFactors[user.activityLevel] || 1.55
    const goalModifier = goalModifiers[user.goalType] || 1.0
    const bodyTypelModifier = bodyTypeModifiers[user.bodyType] || 1.0

    let weightModifier = 1.0
    const weightDiff = user.targetWeight - user.weight
    if (weightDiff < -3) {
      weightModifier = 0.85
    } else if (weightDiff > 3) {
      weightModifier = 1.15
    }

    const totalCalories = bmr * activityFactor * goalModifier * bodyTypelModifier * weightModifier

    const protein = proteinPerKg[user.goalType] * user.weight
    const fat = 0.9 * user.weight
    const proteinCals = protein * 4
    const fatCals = fat * 9
    const remainingCals = totalCalories - proteinCals - fatCals
    const carbs = remainingCals / 4
    const water = user.weight * 35

    return {
      targetCalories: Math.round(totalCalories),
      targetProtein: Math.round(protein),
      targetFat: Math.round(fat),
      targetCarbs: Math.round(carbs),
      targetWater: Math.round(water),
    }
  }

  /**
   * Updates the current daily nutrition values (protein, fat, carbs, water, calories)
   * for a user on a specific date based on their meals and activities.
   *
   * This method should be called after adding or removing a meal,
   * so that the daily log stays accurate.
   */
  public async updateCurrentDailyNutrients(userId: string, date: Date) {
    const user = await this.userService.getUserById(userId)

    const dailyLog = await this.dailyLogModel
      .findOne({ userId: user._id, date })
      .populate<{ meals: MealDocument[] }>('meals')
      .populate<{ activities: { activity: ActivityDocument; totalMinutes: number }[] }>('activities.activity')
      .exec()

    if (!dailyLog)
      throw new HttpException(
        'Daily log not found by this userID and date, cannot calculate new current daily nutritions and total calories',
        HttpStatus.NOT_FOUND,
      )

    const totalCaloriesInMeals = dailyLog.meals.reduce((total, meal) => total + meal.totalCalories, 0)
    const burnedCalories = dailyLog.activities.reduce(
      (total, entry) => total + entry.activity.caloriesPerMin * entry.totalMinutes,
      0,
    )

    dailyLog.totalCalories = totalCaloriesInMeals - burnedCalories
    dailyLog.calories.current = totalCaloriesInMeals
    dailyLog.burnedCalories = burnedCalories

    const meals = dailyLog.meals

    const nutritionSums = await this.nutritionSums(meals)

    dailyLog.protein.current = Math.round(nutritionSums.currentProtein)
    dailyLog.fat.current = Math.round(nutritionSums.currentFat)
    dailyLog.carbs.current = Math.round(nutritionSums.currentCarbs)
    dailyLog.water.current = Math.round(nutritionSums.currentWater)

    await dailyLog.save()
  }

  private async nutritionSums(meals: MealDocument[]): Promise<NutritionTotals> {
    const nutritionSums = await Promise.all(
      meals.map(async meal => {
        return meal.nutritionProducts.reduce(
          async (accPromise: Promise<NutritionTotals>, nProductObj) => {
            const acc = await accPromise

            const product = await this.nutritionProductService.getNutritionProductById(
              nProductObj.nutritionProductId.toString(),
            )
            const amount = nProductObj.amount

            const isWater = product.name === 'Water'

            return {
              currentProtein: acc.currentProtein + (amount / 100) * product.protein,
              currentFat: acc.currentFat + (amount / 100) * product.fat,
              currentCarbs: acc.currentCarbs + (amount / 100) * product.carbs,
              currentWater: acc.currentWater + (isWater ? amount : 0),
            }
          },
          Promise.resolve({ currentProtein: 0, currentFat: 0, currentCarbs: 0, currentWater: 0 }),
        )
      }),
    )

    return nutritionSums.reduce(
      (acc, curr) => ({
        currentProtein: acc.currentProtein + curr.currentProtein,
        currentFat: acc.currentFat + curr.currentFat,
        currentCarbs: acc.currentCarbs + curr.currentCarbs,
        currentWater: acc.currentWater + curr.currentWater,
      }),
      { currentProtein: 0, currentFat: 0, currentCarbs: 0, currentWater: 0 },
    )
  }
}
