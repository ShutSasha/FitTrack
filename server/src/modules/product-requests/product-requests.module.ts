import { Module } from '@nestjs/common'
import { ProductRequestsController } from './product-requests.controller'
import { ProductRequestsService } from './product-requests.service'
import { MongooseModule } from '@nestjs/mongoose'
import { ProductRequest, ProductRequestSchema } from './product-request.schema'
import { NutritionProductsModule } from 'modules/nutrition-products/nutrition-products.module'
import { NutritionProduct, NutritionProductSchema } from 'modules/nutrition-products/nutrition-product.schema'
import { AuthModule } from 'modules/auth/auth.module'

@Module({
  imports: [
    MongooseModule.forFeature([{ name: ProductRequest.name, schema: ProductRequestSchema }]),
    MongooseModule.forFeature([{ name: NutritionProduct.name, schema: NutritionProductSchema }]),
    NutritionProductsModule,
    AuthModule,
  ],
  controllers: [ProductRequestsController],
  providers: [ProductRequestsService],
})
export class ProductRequestsModule {}
