import { Module } from '@nestjs/common'
import { MealsController } from './meals.controller'
import { MealsService } from './meals.service'
import { MongooseModule } from '@nestjs/mongoose'
import { Meal, MealSchema } from './meal.schema'
import { UsersModule } from 'modules/users/users.module'
import { NutritionProductsModule } from 'modules/nutrition-products/nutrition-products.module'
import { DailyLogsModule } from 'modules/daily-logs/daily-logs.module'

@Module({
  imports: [
    MongooseModule.forFeature([{ name: Meal.name, schema: MealSchema }]),
    UsersModule,
    NutritionProductsModule,
    DailyLogsModule,
  ],
  controllers: [MealsController],
  providers: [MealsService],
})
export class MealsModule {}
