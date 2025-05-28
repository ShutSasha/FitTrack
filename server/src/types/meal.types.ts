import { ApiProperty } from '@nestjs/swagger'
import { IsEnum, IsNotEmpty } from 'class-validator'
import { MealTypes } from 'consts/meals.consts'

export class MealDto {
  @ApiProperty({
    example: 'Lunch',
    description: `Type of the meal ${MealTypes.join(', ')}`,
    enum: MealTypes,
  })
  @IsEnum(MealTypes, {
    message: `Meal type must be one of: ${MealTypes.join(', ')}`,
  })
  @IsNotEmpty()
  type: string

  @ApiProperty({
    example: '603467896473967843',
    description: `User id in ObjectId`,
  })
  userId: string

  @ApiProperty({
    description: 'Array of nutrition products with their respective amounts (in grams)',
    example: {
      nutritionProductId: '664ee0fb1a1a2e62f5a2b911',
      amount: 150,
    },
  })
  @IsNotEmpty()
  nutritionProduct: {
    nutritionProductId: string
    amount: number
  }

  @ApiProperty({
    example: '2025-05-25',
    description: 'The date of the meal',
  })
  date: Date
}

export class MealUpdateBody {
  @ApiProperty({
    example: '603467896473967843',
    description: `Meal id in ObjectId`,
  })
  readonly id: string

  @ApiProperty({
    example: 'Lunch',
    description: `Type of the meal ${MealTypes.join(', ')}`,
    enum: MealTypes,
  })
  @IsEnum(MealTypes, {
    message: `Meal type must be one of: ${MealTypes.join(', ')}`,
  })
  @IsNotEmpty()
  type: string

  @ApiProperty({
    description: 'Array of nutrition products with their respective amounts (in grams)',
    example: [
      {
        nutritionProduct: '664ee0fb1a1a2e62f5a2b911',
        amount: 150,
      },
      {
        nutritionProduct: '664ee0fb1a1a2e62f5a2b912',
        amount: 100,
      },
    ],
  })
  @IsNotEmpty()
  nutritionProduct: {
    nutritionProductId: string
    amount: number
  }[]
}

export class DeleteNutritionProductInMealResponse {
  @ApiProperty({
    example: 'Meal document has been deleted totally cause nutritionProducts array is empty',
  })
  readonly message: string
}
