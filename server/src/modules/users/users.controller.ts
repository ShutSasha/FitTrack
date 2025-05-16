import { Body, Controller, Delete, Get, Param, Post, UseGuards, UsePipes } from '@nestjs/common'
import { ApiBearerAuth, ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger'
import { UsersService } from './users.service'
import { User, UserDocument } from './users.schema'
import { ValidationPipe } from '../../pipes/validation.pipe'
import { CreateUserDto } from './dto/create-user.dto'
import { JwtAuthGuard } from '../auth/jwt-auth.guard'

@ApiTags('users')
@ApiBearerAuth()
@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @ApiOperation({ summary: 'Get all users ' })
  @ApiResponse({ status: 200, type: [User] })
  @Get()
  @UseGuards(JwtAuthGuard)
  getAllUser(): Promise<UserDocument[]> {
    return this.usersService.getAllUsers()
  }

  @ApiOperation({ summary: 'Get user by id' })
  @ApiResponse({ status: 200, type: User })
  @Get('/:id')
  getUser(@Param('id') id: string): Promise<UserDocument> {
    return this.usersService.getUserById(id)
  }

  @ApiOperation({ summary: 'User creation' })
  @ApiResponse({ status: 200, type: User })
  @UsePipes(ValidationPipe)
  @Post()
  create(@Body() dto: CreateUserDto): Promise<UserDocument> {
    return this.usersService.create(dto)
  }

  @ApiOperation({ summary: 'Delete user' })
  @ApiResponse({ status: 200, type: User })
  @Delete('/:id')
  delete(@Param('id') id: string): Promise<UserDocument> {
    return this.usersService.delete(id)
  }
}
