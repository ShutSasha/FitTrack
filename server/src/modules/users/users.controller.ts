import { Controller, Delete, Get, Param, Query } from '@nestjs/common'
import { ApiOperation, ApiQuery, ApiResponse, ApiTags } from '@nestjs/swagger'
import { UsersService } from './users.service'
import { User } from './users.schema'
import { SearchUsersRes, UsersSearchDto } from '~types/users.types'

@ApiTags('users')
@Controller('users')
export class UsersController {
  constructor(private readonly usersService: UsersService) {}

  @ApiOperation({ summary: 'Search userst with pagination' })
  @ApiResponse({
    status: 200,
    description: 'List of users with pagination metadata',
    type: SearchUsersRes,
  })
  @ApiQuery({ name: 'query', required: false, type: String, example: 'cdidk' })
  @ApiQuery({ name: 'page', required: false, type: Number, example: 1 })
  @ApiQuery({ name: 'limit', required: false, type: Number, example: 10 })
  @Get('search')
  searchNutritionProducts(@Query() query: UsersSearchDto) {
    return this.usersService.findWithPagination(query)
  }

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

  @ApiOperation({ summary: 'Delete user' })
  @ApiResponse({ status: 200, type: User })
  @Delete('/:id')
  deleteUser(@Param('id') id: string) {
    return this.usersService.delete(id)
  }
}
