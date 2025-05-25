import { ApiProperty } from '@nestjs/swagger'
import { IsEnum, IsNotEmpty, IsNumber, IsString } from 'class-validator'
import { ProductTypes } from 'consts/products.consts'

export class NutritionProductDto {
  @ApiProperty({
    example: 'buckwheat',
    description: 'Name of the nutrition product (e.g., Buckwheat, Apple, Chicken)',
  })
  @IsString()
  @IsNotEmpty()
  name: string

  @ApiProperty({
    example: 343,
    description: 'Calories per 100g of the nutrition product (in kcal)',
  })
  @IsNumber()
  @IsNotEmpty()
  calories: number

  @ApiProperty({
    example: 13.3,
    description: 'Protein content per 100g of the nutrition product (in grams)',
  })
  @IsNumber()
  @IsNotEmpty()
  protein: number

  @ApiProperty({
    example: 3.4,
    description: 'Fat content per 100g of the nutrition product (in grams)',
  })
  @IsNumber()
  @IsNotEmpty()
  fat: number

  @ApiProperty({
    example: 71.5,
    description: 'Carbohydrate content per 100g of the nutrition product (in grams)',
  })
  @IsNumber()
  @IsNotEmpty()
  carbs: number

  @ApiProperty({
    example: 'grain',
    description: 'Type of the nutrition product (e.g., grain, fruit, vegetable, meat, etc.)',
    enum: ProductTypes,
  })
  @IsEnum(ProductTypes, {
    message: `Product type must be one of: ${ProductTypes.join(', ')}`,
  })
  @IsNotEmpty()
  productType: string
}
