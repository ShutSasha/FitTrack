import { Body, Controller, Delete, Get, Param, Post, Put, Query, UsePipes, ValidationPipe } from '@nestjs/common'
import { NutritionProductsService } from './nutrition-products.service'
import { ApiOperation, ApiQuery, ApiResponse } from '@nestjs/swagger'
import { NutritionProduct } from './nutrition-product.schema'
import { NutritionProductDto, NutritionProductSearchDto, SearchRes } from '~types/nutrition-products.types'
import { ProductTypes } from 'consts/products.consts'

@Controller('nutrition-products')
export class NutritionProductsController {
  constructor(private readonly nutritionProductService: NutritionProductsService) {}

  @ApiOperation({ summary: 'Search nutrition products with pagination, sorting, and filtering' })
  @ApiResponse({
    status: 200,
    description: 'List of nutrition products with pagination metadata',
    type: SearchRes,
  })
  @ApiQuery({ name: 'query', required: false, type: String, example: 'buckwheat' })
  @ApiQuery({ name: 'page', required: false, type: Number, example: 1 })
  @ApiQuery({ name: 'limit', required: false, type: Number, example: 10 })
  @ApiQuery({ name: 'sortBy', required: false, enum: ['calories', 'protein', 'fat', 'carbs'] })
  @ApiQuery({ name: 'sortOrder', required: false, enum: ['asc', 'desc'] })
  @ApiQuery({ name: 'productType', required: false, enum: ProductTypes })
  @Get('search')
  searchNutritionProducts(@Query() query: NutritionProductSearchDto) {
    return this.nutritionProductService.findWithPagination(query)
  }

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
