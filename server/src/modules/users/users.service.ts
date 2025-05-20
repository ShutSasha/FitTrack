import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { Model, Types, isValidObjectId } from 'mongoose'
import { User, UserDocument } from './users.schema'
import { InjectModel } from '@nestjs/mongoose'
import { RolesService } from '../roles/roles.service'
import { CreateUserDto } from '~types/users.types'

@Injectable()
export class UsersService {
  constructor(
    @InjectModel(User.name) private userModel: Model<UserDocument>,
    private readonly rolesService: RolesService,
  ) {}

  async getAllUsers(): Promise<UserDocument[]> {
    const users = await this.userModel.find().populate('roles').exec()
    return users
  }

  async getUserById(id: string): Promise<UserDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid ID format', HttpStatus.BAD_REQUEST)
    }

    const user = await this.userModel.findById(id).populate('roles').exec()

    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND)

    return user
  }

  async getUserByUsername(username: string): Promise<UserDocument> {
    const user = await this.userModel.findOne({ username }).populate('roles').exec()

    if (!user) throw new HttpException('User not found', HttpStatus.NOT_FOUND)

    return user
  }

  async create(dto: CreateUserDto & { emailConfirmationToken?: string }): Promise<UserDocument> {
    const candidate = await this.userModel.findOne({ username: dto.username }).exec()

    if (candidate) {
      throw new HttpException('User with this username already exist', HttpStatus.BAD_REQUEST)
    }

    const user = new this.userModel(dto)
    const role = await this.rolesService.getRoleByValue('USER')

    user.roles = [new Types.ObjectId(role._id)]

    return user.save()
  }

  async delete(id: string): Promise<UserDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid ID format', HttpStatus.BAD_REQUEST)
    }

    const user = await this.userModel.findById(id).exec()

    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND)
    }

    const deletedUser = await this.userModel.findByIdAndDelete(id).exec()

    return deletedUser
  }
}
