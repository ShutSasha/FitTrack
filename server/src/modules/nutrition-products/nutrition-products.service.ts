import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { InjectModel } from '@nestjs/mongoose'
import { NutritionProduct, NutritionProductDocument } from './nutrition-product.schema'
import { isValidObjectId, Model } from 'mongoose'
import { NutritionProductDto, NutritionProductSearchDto } from '~types/nutrition-products.types'

@Injectable()
export class NutritionProductsService {
  constructor(@InjectModel(NutritionProduct.name) private nutritionProductModel: Model<NutritionProductDocument>) {}

  async getAllNutritionProducts(): Promise<NutritionProductDocument[]> {
    return this.nutritionProductModel.find().exec()
  }

  async getNutritionProductById(id: string): Promise<NutritionProductDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid nutrition product ID format', HttpStatus.BAD_REQUEST)
    }

    const product = await this.nutritionProductModel.findById(id).exec()

    if (!product) throw new HttpException('Product by this id not found', HttpStatus.NOT_FOUND)

    return product
  }

  async findWithPagination(dto: NutritionProductSearchDto): Promise<{
    items: NutritionProductDocument[]
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
      this.nutritionProductModel.find(filter).sort(sort).skip(skip).limit(limit).exec(),
      this.nutritionProductModel.countDocuments(filter).exec(),
    ])

    return {
      items: items,
      total,
      page,
      limit,
    }
  }

  async create(dto: NutritionProductDto): Promise<NutritionProductDocument> {
    dto.name = dto.name.charAt(0).toUpperCase() + dto.name.slice(1)

    const isProductExist = await this.nutritionProductModel.findOne({ name: dto.name }).exec()

    if (isProductExist) {
      throw new HttpException('Product with this name already exist', HttpStatus.BAD_REQUEST)
    }

    const product = new this.nutritionProductModel(dto)

    return product.save()
  }

  async update(id: string, dto: NutritionProductDto): Promise<NutritionProductDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid nutrition product ID format', HttpStatus.BAD_REQUEST)
    }

    const product = await this.nutritionProductModel.findById(id).exec()

    if (!product) {
      throw new HttpException('Product with this id not found', HttpStatus.BAD_REQUEST)
    }

    dto.name = dto.name.charAt(0).toUpperCase() + dto.name.slice(1)

    Object.assign(product, dto)

    return product.save()
  }

  async delete(id: string): Promise<NutritionProductDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid nutrition product ID format', HttpStatus.BAD_REQUEST)
    }

    const product = await this.nutritionProductModel.findById(id).exec()

    if (!product) {
      throw new HttpException('Product by this id not found', HttpStatus.NOT_FOUND)
    }

    const deletedProduct = await this.nutritionProductModel.findByIdAndDelete(id).exec()

    return deletedProduct
  }
}
