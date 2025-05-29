import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { InjectModel } from '@nestjs/mongoose'
import { ProductRequest, ProductRequestDocument } from './product-request.schema'
import { NutritionProductDto, NutritionProductSearchDto } from '~types/nutrition-products.types'
import { isValidObjectId, Model } from 'mongoose'
import { NutritionProductsService } from 'modules/nutrition-products/nutrition-products.service'
import { NutritionProduct, NutritionProductDocument } from 'modules/nutrition-products/nutrition-product.schema'

@Injectable()
export class ProductRequestsService {
  constructor(
    @InjectModel(ProductRequest.name) private productRequestModel: Model<ProductRequestDocument>,
    @InjectModel(NutritionProduct.name) private nutritionProductModel: Model<NutritionProductDocument>,
    private readonly nutritionProductService: NutritionProductsService,
  ) {}

  async findWithPagination(dto: NutritionProductSearchDto): Promise<{
    items: ProductRequestDocument[]
    total: number
    page: number
    limit: number
  }> {
    const { query, page = 1, limit = 10, sortBy, sortOrder, productType } = dto

    const filter: any = {}
    if (query) {
      filter.name = { $regex: query, $options: 'i' }
    }
    if (productType) {
      filter.productType = productType
    }

    const sort: any = {}
    if (sortBy && sortOrder) {
      sort[sortBy] = sortOrder === 'asc' ? 1 : -1
    }

    const skip = (page - 1) * limit

    const [items, total] = await Promise.all([
      this.productRequestModel.find(filter).sort(sort).skip(skip).limit(limit).exec(),
      this.productRequestModel.countDocuments(filter).exec(),
    ])

    return {
      items: items,
      total,
      page,
      limit,
    }
  }

  async create(dto: NutritionProductDto): Promise<ProductRequestDocument> {
    dto.name = dto.name.charAt(0).toUpperCase() + dto.name.slice(1)

    const isProductExist = await this.productRequestModel.findOne({ name: dto.name }).exec()

    if (isProductExist) {
      throw new HttpException('This product request with this name already exist', HttpStatus.BAD_REQUEST)
    }

    const product = new this.productRequestModel(dto)

    return product.save()
  }

  async aproveRequest(requestId: string): Promise<ProductRequestDocument> {
    if (!isValidObjectId(requestId)) {
      throw new HttpException('Invalid requestId format (not in ObjectId)', HttpStatus.BAD_REQUEST)
    }

    const productRequest = await this.productRequestModel.findById(requestId)

    if (!productRequest) {
      throw new HttpException('This product request with this id not found', HttpStatus.BAD_REQUEST)
    }

    const isProductExistWithThisName = await this.nutritionProductModel.findOne({ name: productRequest.name }).exec()

    if (isProductExistWithThisName) {
      return this.productRequestModel.findByIdAndDelete(requestId)
    }

    await this.nutritionProductService.create({
      name: productRequest.name,
      calories: productRequest.calories,
      protein: productRequest.protein,
      fat: productRequest.fat,
      carbs: productRequest.carbs,
      productType: productRequest.productType,
    })

    const deletedRequest = await this.productRequestModel.findByIdAndDelete(requestId)

    return deletedRequest
  }

  async rejectRequest(requestId: string): Promise<ProductRequestDocument> {
    if (!isValidObjectId(requestId)) {
      throw new HttpException('Invalid requestId format (not in ObjectId)', HttpStatus.BAD_REQUEST)
    }

    return this.productRequestModel.findByIdAndDelete(requestId)
  }
}
