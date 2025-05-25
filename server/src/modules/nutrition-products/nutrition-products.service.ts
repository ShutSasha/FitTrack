import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { InjectModel } from '@nestjs/mongoose'
import { NutritionProduct, NutritionProductDocument } from './nutrition-product.schema'
import { isValidObjectId, Model } from 'mongoose'
import { NutritionProductDto } from '~types/nutrition-products.types'

@Injectable()
export class NutritionProductsService {
  constructor(@InjectModel(NutritionProduct.name) private nutritionProductModel: Model<NutritionProductDocument>) {}

  async getAllNutritionProducts(): Promise<NutritionProductDocument[]> {
    return this.nutritionProductModel.find().exec()
  }

  async getNutritionProductById(id: string): Promise<NutritionProductDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid ID format', HttpStatus.BAD_REQUEST)
    }

    const product = await this.nutritionProductModel.findById(id).exec()

    if (!product) throw new HttpException('Product by this id not found', HttpStatus.NOT_FOUND)

    return product
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
      throw new HttpException('Invalid ID format', HttpStatus.BAD_REQUEST)
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
      throw new HttpException('Invalid ID format', HttpStatus.BAD_REQUEST)
    }

    const product = await this.nutritionProductModel.findById(id).exec()

    if (!product) {
      throw new HttpException('Product by this id not found', HttpStatus.NOT_FOUND)
    }

    const deletedProduct = await this.nutritionProductModel.findByIdAndDelete(id).exec()

    return deletedProduct
  }
}
