import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  Patch,
  Post,
  UseGuards,
  UsePipes,
  ValidationPipe,
} from '@nestjs/common'
import { RolesService } from './roles.service'
import { Role } from './roles.schema'
import { ApiBearerAuth, ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger'
import { Roles } from './roles-auth.decorator'
import { RolesGuard } from './roles.guard'
import { ChangeRoleDto, CreateRoleDto, UpdateRoleDto } from '~types/roles.types'

@ApiTags('roles')
@ApiBearerAuth()
@Controller('roles')
export class RolesController {
  constructor(private readonly roleService: RolesService) {}

  @ApiOperation({ summary: 'Get all roles ' })
  @ApiResponse({ status: 200, type: [Role] })
  @Roles('ADMIN')
  @UseGuards(RolesGuard)
  @Get()
  @HttpCode(HttpStatus.OK)
  getRoles() {
    return this.roleService.getRoles()
  }

  @ApiOperation({ summary: 'Get role by value' })
  @ApiResponse({ status: 200, type: Role })
  @Roles('ADMIN')
  @UseGuards(RolesGuard)
  @Get('/:value')
  @HttpCode(HttpStatus.OK)
  getByValue(@Param('value') value: string) {
    return this.roleService.getRoleByValue(value)
  }

  @ApiOperation({ summary: 'Create role' })
  @ApiResponse({ status: 201, type: Role })
  @UsePipes(ValidationPipe)
  @Roles('ADMIN')
  @UseGuards(RolesGuard)
  @Post()
  @HttpCode(HttpStatus.CREATED)
  create(@Body() dto: CreateRoleDto) {
    dto.value = dto.value.toUpperCase()
    return this.roleService.createRole(dto)
  }

  @ApiOperation({ summary: 'Change user role' })
  @ApiResponse({ status: 201, type: Role })
  @UsePipes(ValidationPipe)
  @Roles('ADMIN')
  @UseGuards(RolesGuard)
  @Post('/change-user-role')
  @HttpCode(HttpStatus.CREATED)
  changeUserRole(@Body() dto: ChangeRoleDto) {
    return this.roleService.changeUserRole(dto)
  }

  @ApiOperation({ summary: 'Update role' })
  @ApiResponse({ status: 200, type: Role })
  @UsePipes(ValidationPipe)
  @Roles('ADMIN')
  @UseGuards(RolesGuard)
  @Patch()
  @HttpCode(HttpStatus.OK)
  update(@Body() dto: UpdateRoleDto) {
    return this.roleService.updateRole(dto)
  }

  @ApiOperation({ summary: 'Delete role' })
  @ApiResponse({ status: 200, type: Role })
  @Roles('ADMIN')
  @UseGuards(RolesGuard)
  @Delete('/:id')
  delete(@Param('id') id: string) {
    return this.roleService.deleteRole(id)
  }
}
