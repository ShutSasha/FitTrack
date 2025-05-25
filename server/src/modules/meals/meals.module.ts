import { Module } from '@nestjs/common'
import { MealsController } from './meals.controller'
import { MealsService } from './meals.service'
import { MongooseModule } from '@nestjs/mongoose'
import { Meal, MealSchema } from './meal.schema'

@Module({
  imports: [MongooseModule.forFeature([{ name: Meal.name, schema: MealSchema }])],
  controllers: [MealsController],
  providers: [MealsService],
})
export class MealsModule {}
