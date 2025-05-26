import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { InjectModel } from '@nestjs/mongoose'
import { DailyLog, DailyLogDocument } from './daily-log.schema'
import { isValidObjectId, Model } from 'mongoose'
import { UsersService } from 'modules/users/users.service'
import { activityFactors, bodyTypeModifiers, goalModifiers, proteinPerKg } from 'consts/daily-log.consts'
import { CalculateTargetsDto, CalculateTargetsRes } from '~types/daily-log.types'
import { MealDocument } from 'modules/meals/meal.schema'
import { ActivityDocument } from 'modules/activities/activity.schema'

@Injectable()
export class DailyLogsService {
  constructor(
    @InjectModel(DailyLog.name) private dailyLogModel: Model<DailyLogDocument>,
    private readonly userService: UsersService,
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

  async getDailyLogByUserIdAndDate(userId: string, date: Date): Promise<DailyLogDocument> {
    // It gets user by id and at the same time check valid ObjectId
    const user = await this.userService.getUserById(userId)

    const dailyLog = await this.dailyLogModel.findOne({ userId, date }).exec()

    if (dailyLog) return dailyLog

    const weight = {
      current: user.weight,
      target: user.targetWeight,
    }

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

    return newDailyLog.save()
  }

  private calculateTargets(user: CalculateTargetsDto): CalculateTargetsRes {
    const age = new Date().getFullYear() - new Date(user.birthDate).getFullYear()

    const genderModifier = user.gender === 'male' ? 5 : user.gender === 'female' ? -161 : -78 // -78 avarage for other gender

    // ** BMR - Basal Metabolic Rate or Розрахунок базового метаболізму за Mifflin-St Jeor

    const bmr = 10 * user.weight + 6.25 * user.height - 5 * age + genderModifier

    const activityFactor = activityFactors[user.activityLevel] || 1.55
    const goalModifier = goalModifiers[user.goalType] || 1.0
    const bodyTypelModifier = bodyTypeModifiers[user.bodyType] || 1.0

    const totalCalories = bmr * activityFactor * goalModifier * bodyTypelModifier

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

  // TODO дергается после персонализации и апдейтит только сегоднешний лог, все предыдущие это история
  public updateTodayDailyLogTargets() {
    // water: { target: targetWater },
  }

  // TODO считывает и назначает текущие данные в дейли лог юзера. Считает это все по Meals. Вызывается в MealsService после добавление удаления Meal
  public async updateCurrentDailyNutrients(userId: string, date: Date) {
    const user = await this.userService.getUserById(userId)

    const dailyLog = await this.dailyLogModel
      .findOne({ userId: user._id, date })
      .populate<{ meals: MealDocument[] }>('meals')
      .populate<{ activities: { activity: ActivityDocument; totalMinutes: number }[] }>('activities')
      .exec()

    console.log(dailyLog) //при первом запросе meals пустйо, при втором запросе в meals уже два meals как и должно

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

    console.log(`totalCalories: ${totalCaloriesInMeals - burnedCalories}`)
    console.log(`calories.current: ${totalCaloriesInMeals}`)

    // TODO ДОДЕЛАТЬ ДЛЯ current значений белка, жира и углеводов

    await dailyLog.save()
  }
}
