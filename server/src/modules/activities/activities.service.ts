import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { InjectModel } from '@nestjs/mongoose'
import { Activity, ActivityDocument } from './activity.schema'
import { isValidObjectId, Model } from 'mongoose'
import { ActivityDto, ActivitySearchDto } from '~types/activity.types'

@Injectable()
export class ActivitiesService {
  constructor(@InjectModel(Activity.name) private activityModel: Model<ActivityDocument>) {}

  async findWithPagination(dto: ActivitySearchDto): Promise<{
    items: ActivityDocument[]
    total: number
    page: number
    limit: number
  }> {
    const { query, page = 1, limit = 10, sortBy, sortOrder } = dto

    const filter: any = {}
    if (query) {
      filter.name = { $regex: query, $options: 'i' }
    }

    const sort: any = {}
    if (sortBy && sortOrder) {
      sort[sortBy] = sortOrder === 'asc' ? 1 : -1
    }

    const skip = (page - 1) * limit

    const [items, total] = await Promise.all([
      this.activityModel.find(filter).sort(sort).skip(skip).limit(limit).exec(),
      this.activityModel.countDocuments(filter).exec(),
    ])

    return {
      items: items,
      total,
      page,
      limit,
    }
  }

  async getAllActivities(): Promise<ActivityDocument[]> {
    return this.activityModel.find().exec()
  }

  async getActivityById(id: string): Promise<ActivityDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid activity ID format', HttpStatus.BAD_REQUEST)
    }

    const activity = await this.activityModel.findById(id).exec()

    if (!activity) throw new HttpException('Activity by this id not found', HttpStatus.NOT_FOUND)

    return activity
  }

  async create(dto: ActivityDto): Promise<ActivityDocument> {
    dto.name = dto.name.charAt(0).toUpperCase() + dto.name.slice(1)

    const isActivityExist = await this.activityModel.findOne({ name: dto.name }).exec()

    if (isActivityExist) {
      throw new HttpException('Activity with this name already exist', HttpStatus.BAD_REQUEST)
    }

    const activity = new this.activityModel(dto)

    return activity.save()
  }

  async update(id: string, dto: ActivityDto): Promise<ActivityDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid activity ID format', HttpStatus.BAD_REQUEST)
    }

    const activity = await this.activityModel.findById(id).exec()

    if (!activity) {
      throw new HttpException('Activity with this id not found', HttpStatus.BAD_REQUEST)
    }

    dto.name = dto.name.charAt(0).toUpperCase() + dto.name.slice(1)

    Object.assign(activity, dto)

    return activity.save()
  }

  async delete(id: string): Promise<ActivityDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid activity ID format', HttpStatus.BAD_REQUEST)
    }

    const activity = await this.activityModel.findById(id).exec()

    if (!activity) {
      throw new HttpException('Activity by this id not found', HttpStatus.NOT_FOUND)
    }

    const deletedActivity = await this.activityModel.findByIdAndDelete(id).exec()

    return deletedActivity
  }
}
