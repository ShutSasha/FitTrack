import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose'
import { ApiProperty } from '@nestjs/swagger'
import { MealTypes } from 'consts/meals.consts'
import mongoose, { HydratedDocument, Types } from 'mongoose'

export type MealDocument = HydratedDocument<Meal>

@Schema({ timestamps: true })
export class Meal {
  @ApiProperty({ example: '67cef3dfd47b32d7c0f129b0', description: 'Unique identifier of the meal' })
  _id: Types.ObjectId

  @ApiProperty({ example: '64eabf891c85a90fc8f3e7e5', description: 'User id' })
  @Prop({ type: mongoose.Schema.Types.ObjectId, ref: 'User' })
  userId: Types.ObjectId

  @ApiProperty({
    example: 'Breakfast',
    description: 'Type of the meal (e.g., Breakfast, Lunch, Dinner, Snack1, Snack2, Snack3)',
    enum: MealTypes,
  })
  @Prop({ type: String, required: true, enum: MealTypes })
  type: string

  @ApiProperty({ example: '2025-05-25', description: 'The date of the meal' })
  @Prop({ default: () => new Date() })
  date: Date

  @ApiProperty({ example: '500', description: 'Total calories of the meal' })
  @Prop({ type: Number, default: 0 })
  totalCalories: number

  @ApiProperty({
    example: [
      {
        _id: '66522f8b9b8cbe5d1d25ab9c',
        nutritionProductId: '64eabf891c85a90fc8f3e7e5',
        amount: 100,
        productName: 'Salmon',
        productCalories: 204,
      },
    ],
    description: 'Array of nutrition products with their IDs and amounts (in grams)',
  })
  @Prop({
    type: [
      {
        nutritionProductId: { type: mongoose.Schema.Types.ObjectId, ref: 'NutritionProduct' },
        amount: Number,
        productCalories: Number,
        productName: String,
      },
    ],
    default: [],
  })
  nutritionProducts: {
    _id?: Types.ObjectId
    nutritionProductId: Types.ObjectId
    amount: number
    productName: string
    productCalories: number
  }[]
}

export const MealSchema = SchemaFactory.createForClass(Meal)
