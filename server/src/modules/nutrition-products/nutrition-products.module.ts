import { Module } from '@nestjs/common'
import { NutritionProductsController } from './nutrition-products.controller'
import { NutritionProductsService } from './nutrition-products.service'
import { MongooseModule } from '@nestjs/mongoose'
import { NutritionProduct, NutritionProductSchema } from './nutrition-product.schema'

@Module({
  imports: [MongooseModule.forFeature([{ name: NutritionProduct.name, schema: NutritionProductSchema }])],
  controllers: [NutritionProductsController],
  providers: [NutritionProductsService],
  exports: [NutritionProductsService],
})
export class NutritionProductsModule {}
