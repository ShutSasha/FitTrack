import { ApiProperty } from '@nestjs/swagger'
import { IsEnum, IsInt, IsNotEmpty, IsNumber, IsOptional, IsString, Min } from 'class-validator'
import { ProductTypes } from 'consts/products.consts'
import { NutritionProduct } from 'modules/nutrition-products/nutrition-product.schema'

export enum SortOrder {
  ASC = 'asc',
  DESC = 'desc',
}

export enum SortField {
  CALORIES = 'calories',
  PROTEIN = 'protein',
  FAT = 'fat',
  CARBS = 'carbs',
}

export class SearchRes {
  @ApiProperty({ type: [NutritionProduct] })
  items: NutritionProduct[]

  @ApiProperty({ example: 100 })
  total: number

  @ApiProperty({ example: 1 })
  page: number

  @ApiProperty({ example: 10 })
  limit: number
}

export class NutritionProductSearchDto {
  @ApiProperty({
    example: 'Buckwheat',
    description: 'Search query for product name (case-insensitive)',
    required: false,
  })
  @IsOptional()
  @IsString()
  query?: string

  @ApiProperty({ example: 1, description: 'Page number (1-based)', required: false })
  @IsOptional()
  @IsInt()
  @Min(1)
  page?: number = 1

  @ApiProperty({ example: 10, description: 'Number of items per page', required: false })
  @IsOptional()
  @IsInt()
  @Min(1)
  limit?: number = 10

  @ApiProperty({
    example: 'calories',
    description: 'Field to sort by (calories, protein, fat, carbs)',
    enum: SortField,
    required: false,
  })
  @IsOptional()
  @IsEnum(SortField)
  sortBy?: string

  @ApiProperty({
    example: 'asc',
    description: 'Sort order (asc or desc)',
    enum: SortOrder,
    required: false,
  })
  @IsOptional()
  @IsEnum(SortOrder)
  sortOrder?: string

  @ApiProperty({
    example: 'grain',
    description: 'Filter by product type (e.g., grain, fruit, vegetable, meat, etc.)',
    enum: ProductTypes,
    required: false,
  })
  @IsOptional()
  @IsString()
  @IsEnum(ProductTypes)
  productType?: string
}

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
