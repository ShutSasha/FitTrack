import { Body, Controller, Delete, Get, Param, Post, UsePipes, ValidationPipe } from '@nestjs/common'
import { ApiOperation, ApiParam, ApiResponse } from '@nestjs/swagger'
import { MealsService } from './meals.service'
import { Meal } from './meal.schema'
import { MealDto } from '~types/meal.types'

@Controller('meals')
export class MealsController {
  constructor(private readonly mealService: MealsService) {}

  @ApiOperation({ summary: 'Get all meals ' })
  @ApiResponse({ status: 200, type: [Meal] })
  @Get()
  getAllMeals() {
    return this.mealService.getAllMeals()
  }

  @ApiOperation({ summary: 'Get meal by id' })
  @ApiResponse({ status: 200, type: Meal })
  @Get('/:id')
  getMealById(@Param('id') id: string) {
    return this.mealService.getMealById(id)
  }

  @ApiOperation({ summary: 'Add or create meal to daily log' })
  @ApiResponse({ status: 200, type: Meal })
  @UsePipes(ValidationPipe)
  @Post()
  addMeal(@Body() dto: MealDto) {
    return this.mealService.addMeal(dto)
  }

  @ApiOperation({ summary: 'Delete meal by id' })
  @ApiResponse({ status: 200, type: Meal })
  @Delete('/:id')
  deleteMeal(@Param('id') id: string) {
    return this.mealService.delete(id)
  }

  @Delete('/:mealId/:nutritionEntryId')
  @ApiOperation({
    summary: 'Delete a nutrition product entry in a meal by mealId and the entry’s unique _id',
  })
  @ApiParam({
    name: 'mealId',
    description: 'ID of the meal',
    example: '6651b9d17b9e6a4ad8dbb126',
  })
  @ApiParam({
    name: 'nutritionEntryId',
    description: 'Unique _id of the nutrition entry inside the meal.nutritionProducts array',
    example: '6651ba087b9e6a4ad8dbb128',
  })
  @ApiResponse({ status: 200, type: Meal })
  deleteNutritionProductInMeal(@Param('mealId') mealId: string, @Param('nutritionEntryId') nutritionEntryId: string) {
    return this.mealService.deleteNutritionProduct(mealId, nutritionEntryId)
  }
}
