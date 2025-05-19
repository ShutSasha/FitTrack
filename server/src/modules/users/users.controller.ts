import { Body, Controller, Delete, Get, Param, Post, UsePipes } from '@nestjs/common'
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger'
import { UsersService } from './users.service'
import { User } from './users.schema'
import { ValidationPipe } from '../../pipes/validation.pipe'
import { CreateUserDto } from '~types/users.types'

@ApiTags('users')
@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @ApiOperation({ summary: 'Get all users ' })
  @ApiResponse({ status: 200, type: [User] })
  @Get()
  getAllUsers() {
    return this.usersService.getAllUsers()
  }

  @ApiOperation({ summary: 'Get user by id' })
  @ApiResponse({ status: 200, type: User })
  @Get('/:id')
  getUserById(@Param('id') id: string) {
    return this.usersService.getUserById(id)
  }

  @ApiOperation({ summary: 'User creation' })
  @ApiResponse({ status: 200, type: User })
  @UsePipes(ValidationPipe)
  @Post()
  createUser(@Body() dto: CreateUserDto) {
    return this.usersService.create(dto)
  }

  @ApiOperation({ summary: 'Delete user' })
  @ApiResponse({ status: 200, type: User })
  @Delete('/:id')
  deleteUser(@Param('id') id: string) {
    return this.usersService.delete(id)
  }
}
