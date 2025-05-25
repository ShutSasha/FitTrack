import { Body, Controller, Delete, Get, Param, Post, Put, UsePipes, ValidationPipe } from '@nestjs/common'
import { NutritionProductsService } from './nutrition-products.service'
import { ApiOperation, ApiResponse } from '@nestjs/swagger'
import { NutritionProduct } from './nutrition-product.schema'
import { NutritionProductDto } from '~types/nutrition-products.types'

@Controller('nutrition-products')
export class NutritionProductsController {
  constructor(private readonly nutritionProductService: NutritionProductsService) {}

  @ApiOperation({ summary: 'Get all nutrition products ' })
  @ApiResponse({ status: 200, type: [NutritionProduct] })
  @Get()
  getAllNutritionProducts() {
    return this.nutritionProductService.getAllNutritionProducts()
  }

  @ApiOperation({ summary: 'Get nutrition product by id' })
  @ApiResponse({ status: 200, type: NutritionProduct })
  @Get('/:id')
  getNutritionById(@Param('id') id: string) {
    return this.nutritionProductService.getNutritionProductById(id)
  }

  @ApiOperation({ summary: 'Create nurition product' })
  @ApiResponse({ status: 200, type: NutritionProduct })
  @UsePipes(ValidationPipe)
  @Post()
  createNutritionProduct(@Body() dto: NutritionProductDto) {
    return this.nutritionProductService.create(dto)
  }

  @ApiOperation({ summary: 'Update nutrition product' })
  @ApiResponse({ status: 200, type: NutritionProduct })
  @UsePipes(ValidationPipe)
  @Put('/:id')
  updateNutritionProduct(@Param('id') id: string, @Body() dto: NutritionProductDto) {
    return this.nutritionProductService.update(id, dto)
  }

  @ApiOperation({ summary: 'Delete nutrition product' })
  @ApiResponse({ status: 200, type: NutritionProduct })
  @Delete('/:id')
  deleteNutritionProduct(@Param('id') id: string) {
    return this.nutritionProductService.delete(id)
  }
}
