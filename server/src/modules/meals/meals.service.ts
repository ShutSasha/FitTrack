import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { InjectModel } from '@nestjs/mongoose'
import { Meal, MealDocument } from './meal.schema'
import { isValidObjectId, Model } from 'mongoose'
import { MealDto } from '~types/meal.types'
import { UsersService } from 'modules/users/users.service'
import { NutritionProductsService } from 'modules/nutrition-products/nutrition-products.service'
import { Types } from 'mongoose'
import { DailyLogsService } from 'modules/daily-logs/daily-logs.service'
import { DailyLogDocument } from 'modules/daily-logs/daily-log.schema'

@Injectable()
export class MealsService {
  constructor(
    @InjectModel(Meal.name) private mealModel: Model<MealDocument>,
    private readonly userService: UsersService,
    private readonly nutritionProductService: NutritionProductsService,
    private readonly dailyLogService: DailyLogsService,
  ) {}

  async getAllMeals(): Promise<MealDocument[]> {
    return this.mealModel.find().exec()
  }

  async getMealById(id: string): Promise<MealDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid Meal ID format', HttpStatus.BAD_REQUEST)
    }

    const meal = await this.mealModel.findById(id).exec()

    if (!meal) throw new HttpException('Meal by this id not found', HttpStatus.NOT_FOUND)

    return meal
  }

  async addMeal(dto: MealDto): Promise<MealDocument> {
    const [dayLog, product] = await Promise.all([
      this.dailyLogService.getDailyLogByUserIdAndDate(dto.userId, dto.date),
      this.nutritionProductService.getNutritionProductById(dto.nutritionProduct.nutritionProductId),
      this.userService.getUserById(dto.userId), // It checks user exist or not
    ])

    const totalCalories = (dto.nutritionProduct.amount / 100) * product.calories

    let meal = await this.mealModel.findOne({ userId: dto.userId, type: dto.type, date: dto.date })

    const nutritionProductEntry = {
      nutritionProductId: new Types.ObjectId(dto.nutritionProduct.nutritionProductId),
      amount: dto.nutritionProduct.amount,
    }

    if (meal) {
      meal.totalCalories += totalCalories
      meal.nutritionProducts.push(nutritionProductEntry)
      await meal.save()
    } else {
      meal = new this.mealModel({ totalCalories, ...dto })
      meal.nutritionProducts.push(nutritionProductEntry)
      await meal.save()
    }

    await this.addMealToDayLogIfNotExists(dayLog, meal._id)

    await this.dailyLogService.updateCurrentDailyNutrients(dto.userId, dto.date)

    return meal
  }

  private async addMealToDayLogIfNotExists(dayLog: DailyLogDocument, mealId: Types.ObjectId) {
    const alreadyExists = dayLog.meals.some(existingMealId => existingMealId.equals(mealId))
    if (!alreadyExists) {
      dayLog.meals.push(mealId)
      await dayLog.save()
    }
  }

  async delete(id: string): Promise<any> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid Meal ID format', HttpStatus.BAD_REQUEST)
    }

    const meal = await this.mealModel.findById(id).exec()

    if (!meal) {
      throw new HttpException('Meal by this id not found', HttpStatus.NOT_FOUND)
    }

    const dailyLog = await this.dailyLogService.getDailyLogByMealId(id)

    dailyLog.meals = dailyLog.meals.filter(meal => !meal.equals(id))
    await dailyLog.save()

    const deletedMeal = await this.mealModel.findByIdAndDelete(id).exec()

    this.dailyLogService.updateCurrentDailyNutrients(dailyLog.userId.toString(), dailyLog.date)

    return deletedMeal
  }

  async deleteNutritionProduct(mealId: string, nutritionEntryId: string): Promise<MealDocument> {
    const meal = await this.getMealById(mealId)

    const deletedNutritionEntryInMeal = meal.nutritionProducts.find(item => item._id.equals(nutritionEntryId))

    if (!deletedNutritionEntryInMeal) {
      throw new HttpException('deleted nutrition entry in meal not found', HttpStatus.BAD_REQUEST)
    }

    const product = await this.nutritionProductService.getNutritionProductById(
      deletedNutritionEntryInMeal.nutritionProductId.toString(),
    )

    meal.nutritionProducts = meal.nutritionProducts.filter(
      nutProduct => nutProduct._id !== deletedNutritionEntryInMeal._id,
    )

    meal.totalCalories -= (deletedNutritionEntryInMeal.amount / 100) * product.calories

    meal.save()

    return meal
  }
}
