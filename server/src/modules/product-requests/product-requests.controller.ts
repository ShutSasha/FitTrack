import { Body, Controller, Get, Param, Post, Query, UseGuards, UsePipes, ValidationPipe } from '@nestjs/common'
import { ProductRequestsService } from './product-requests.service'
import { ApiBearerAuth, ApiOperation, ApiQuery, ApiResponse } from '@nestjs/swagger'
import { NutritionProductDto, NutritionProductSearchDto, SearchRes } from '~types/nutrition-products.types'
import { ProductTypes } from 'consts/products.consts'
import { ProductRequest } from './product-request.schema'
import { Roles } from 'modules/roles/roles-auth.decorator'
import { RolesGuard } from 'modules/roles/roles.guard'
import { JwtAuthGuard } from 'modules/auth/jwt-auth.guard'

@ApiBearerAuth()
@Controller('product-requests')
export class ProductRequestsController {
  constructor(private readonly productRequestService: ProductRequestsService) {}

  @Roles('ADMIN', 'MODERATOR')
  @UseGuards(RolesGuard)
  @ApiOperation({ summary: 'Search products request with pagination, sorting, and filtering' })
  @ApiResponse({
    status: 200,
    description: 'List of product requests with pagination metadata',
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
    return this.productRequestService.findWithPagination(query)
  }

  @UseGuards(JwtAuthGuard)
  @ApiOperation({ summary: 'Create product request' })
  @ApiResponse({ status: 200, type: ProductRequest })
  @UsePipes(ValidationPipe)
  @Post()
  createProductRequest(@Body() dto: NutritionProductDto) {
    return this.productRequestService.create(dto)
  }

  @Roles('ADMIN', 'MODERATOR')
  @UseGuards(RolesGuard)
  @ApiOperation({ summary: 'Aprove product request' })
  @ApiResponse({ status: 200, type: ProductRequest })
  @UsePipes(ValidationPipe)
  @Post('/aprove/:requestId')
  aproveProductRequest(@Param('requestId') requestId: string) {
    return this.productRequestService.aproveRequest(requestId)
  }

  @Roles('ADMIN', 'MODERATOR')
  @UseGuards(RolesGuard)
  @ApiOperation({ summary: 'Reject product request' })
  @ApiResponse({ status: 200, type: ProductRequest })
  @UsePipes(ValidationPipe)
  @Post('/reject/:requestId')
  rejectProductRequest(@Param('requestId') requestId: string) {
    return this.productRequestService.rejectRequest(requestId)
  }
}
