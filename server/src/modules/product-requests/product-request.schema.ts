import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose'
import { ApiProperty } from '@nestjs/swagger'
import { ProductTypes } from 'consts/products.consts'
import { HydratedDocument, Types } from 'mongoose'

export type ProductRequestDocument = HydratedDocument<ProductRequest>

@Schema({ timestamps: true })
export class ProductRequest {
  @ApiProperty({ example: '67cef3dfd47b32d7c0f129b0', description: 'Unique identifier of the nutrition product' })
  _id: Types.ObjectId

  @ApiProperty({ example: 'Apple', description: 'Name of the nutrition product' })
  @Prop({ type: String, required: true })
  name: string

  @ApiProperty({ example: 52, description: 'Calories per 100g of the nutrition product' })
  @Prop({ type: Number, required: true, min: 0 })
  calories: number

  @ApiProperty({ example: 0.3, description: 'Protein content per 100g of the nutrition product (in grams)' })
  @Prop({ type: Number, required: true, min: 0 })
  protein: number

  @ApiProperty({ example: 0.2, description: 'Fat content per 100g of the nutrition product (in grams)' })
  @Prop({ type: Number, required: true, min: 0 })
  fat: number

  @ApiProperty({ example: 14, description: 'Carbohydrate content per 100g of the nutrition product (in grams)' })
  @Prop({ type: Number, required: true, min: 0 })
  carbs: number

  @ApiProperty({
    example: 'fruit',
    description: 'Type of the nutrition product (e.g., fruit, vegetable, meat, fish, etc.)',
    enum: ProductTypes,
  })
  @Prop({ type: String, required: true, enum: ProductTypes })
  productType: string
}

export const ProductRequestSchema = SchemaFactory.createForClass(ProductRequest)
