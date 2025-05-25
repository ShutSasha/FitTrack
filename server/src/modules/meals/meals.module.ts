import { Module } from '@nestjs/common'
import { MealsController } from './meals.controller'
import { MealsService } from './meals.service'
import { MongooseModule } from '@nestjs/mongoose'
import { Meal, MealSchema } from './meal.schema'
import { UsersModule } from 'modules/users/users.module'
import { NutritionProductsModule } from 'modules/nutrition-products/nutrition-products.module'

@Module({
  imports: [MongooseModule.forFeature([{ name: Meal.name, schema: MealSchema }]), UsersModule, NutritionProductsModule],
  controllers: [MealsController],
  providers: [MealsService],
})
export class MealsModule {}
