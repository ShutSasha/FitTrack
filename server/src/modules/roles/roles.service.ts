import { HttpException, HttpStatus, Injectable } from '@nestjs/common'
import { InjectModel } from '@nestjs/mongoose'
import { Role, RoleDocument } from './roles.schema'
import { isValidObjectId, Model } from 'mongoose'
import { User, UserDocument } from '../users/users.schema'
import { CreateRoleDto, UpdateRoleDto } from '~types/roles.types'

@Injectable()
export class RolesService {
  constructor(
    @InjectModel(Role.name) private roleModel: Model<RoleDocument>,
    @InjectModel(User.name) private userModel: Model<UserDocument>,
  ) {}

  async getRoleByValue(value: string): Promise<RoleDocument> {
    const role = await this.roleModel.findOne({ value }).exec()

    if (!role) throw new HttpException('Role not found by this name or has not been initialized', HttpStatus.NOT_FOUND)

    return role
  }

  async getRoles(): Promise<RoleDocument[]> {
    return this.roleModel.find().exec()
  }

  async createRole(dto: CreateRoleDto): Promise<RoleDocument> {
    const isExist = await this.roleModel.exists({ value: dto.value })

    if (isExist) throw new HttpException('Role already exist', HttpStatus.BAD_REQUEST)

    const role = new this.roleModel(dto)
    return role.save()
  }

  async updateRole(dto: UpdateRoleDto): Promise<RoleDocument> {
    if (!isValidObjectId(dto.id)) {
      throw new HttpException('Invalid ID format', HttpStatus.BAD_REQUEST)
    }

    const role = await this.roleModel.findById(dto.id).exec()

    if (!role) {
      throw new HttpException('Role not found', HttpStatus.NOT_FOUND)
    }

    dto.value = dto.value.toUpperCase()

    const isExist = await this.roleModel.exists({ value: dto.value })

    if (isExist) throw new HttpException('Role already exist', HttpStatus.BAD_REQUEST)

    const updatedRole = await this.roleModel
      .findByIdAndUpdate(dto.id, { value: dto.value, description: dto.description }, { new: true })
      .exec()

    return updatedRole
  }

  async deleteRole(id: string): Promise<RoleDocument> {
    if (!isValidObjectId(id)) {
      throw new HttpException('Invalid ID format', HttpStatus.BAD_REQUEST)
    }

    const role = await this.roleModel.findById(id).exec()

    if (!role) {
      throw new HttpException('Role has not found', HttpStatus.NOT_FOUND)
    }

    const users = await this.userModel.find({ roles: { $in: [role._id] } }).exec()

    if (users.length) {
      throw new HttpException('Role is used by users', HttpStatus.BAD_REQUEST)
    }

    const deletedRole = await this.roleModel.findByIdAndDelete(id).exec()

    return deletedRole
  }
}
