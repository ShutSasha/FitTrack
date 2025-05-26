import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { InjectModel } from '@nestjs/mongoose'
import { Meal, MealDocument } from './meal.schema'
import { isValidObjectId, Model } from 'mongoose'
import { MealDto } from '~types/meal.types'
import { UsersService } from 'modules/users/users.service'
import { NutritionProductsService } from 'modules/nutrition-products/nutrition-products.service'
import { Types } from 'mongoose'
import { DailyLogsService } from 'modules/daily-logs/daily-logs.service'

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

    const isMealTodayExist = await this.mealModel.findOne({ userId: dto.userId, type: dto.type, date: dto.date })

    if (isMealTodayExist) {
      isMealTodayExist.totalCalories += totalCalories

      isMealTodayExist.nutritionProducts.push({
        nutritionProductId: new Types.ObjectId(dto.nutritionProduct.nutritionProductId),
        amount: dto.nutritionProduct.amount,
      })
      await isMealTodayExist.save()

      const mealAlreadyInLog = dayLog.meals.find(meal => meal.equals(isMealTodayExist._id))

      if (!mealAlreadyInLog) {
        dayLog.meals.push(isMealTodayExist._id)
        await dayLog.save()
      }

      await this.dailyLogService.updateCurrentDailyNutrients(dto.userId, dto.date)

      return isMealTodayExist
    }

    const meal = new this.mealModel({ totalCalories, ...dto })

    meal.nutritionProducts.push({
      nutritionProductId: new Types.ObjectId(dto.nutritionProduct.nutritionProductId),
      amount: dto.nutritionProduct.amount,
    })
    await meal.save()

    const mealAlreadyInLog = dayLog.meals.find(meal => meal.equals(meal._id))

    if (!mealAlreadyInLog) {
      dayLog.meals.push(meal._id)
      await dayLog.save()
    }

    dayLog.meals.push(meal._id)
    await dayLog.save()

    await this.dailyLogService.updateCurrentDailyNutrients(dto.userId, dto.date)
    return meal
  }

  // async mealProductUpdate

  async delete(id: string): Promise<MealDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid Meal ID format', HttpStatus.BAD_REQUEST)
    }

    const meal = await this.mealModel.findById(id).exec()

    if (!meal) {
      throw new HttpException('Meal by this id not found', HttpStatus.NOT_FOUND)
    }

    const deletedMeal = await this.mealModel.findByIdAndDelete(id).exec()

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
