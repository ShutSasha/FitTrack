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
import { CreateRoleDto } from './dto/create-role.dto'
import { Role, RoleDocument } from './roles.schema'
import { ApiBearerAuth, ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger'
import { UpdateRoleDto } from './dto/update-role.dto'
import { JwtAuthGuard } from '../auth/jwt-auth.guard'
// import { Roles } from './roles-auth.decorator'
// import { RolesGuard } from './roles.guard'

@ApiTags('roles')
@ApiBearerAuth()
@Controller('roles')
export class RolesController {
  constructor(private readonly roleService: RolesService) {}

  @ApiOperation({ summary: 'Get all roles ' })
  @ApiResponse({ status: 200, type: [Role] })
  // @Roles('ADMIN')
  @UseGuards(JwtAuthGuard)
  @Get()
  @HttpCode(HttpStatus.OK)
  getRoles(): Promise<RoleDocument[]> {
    return this.roleService.getRoles()
  }

  @ApiOperation({ summary: 'Get role by value' })
  @ApiResponse({ status: 200, type: Role })
  // @Roles('ADMIN')
  // @UseGuards(RolesGuard)
  @Get('/:value')
  @HttpCode(HttpStatus.OK)
  getByValue(@Param('value') value: string): Promise<RoleDocument> {
    return this.roleService.getRoleByValue(value)
  }

  @ApiOperation({ summary: 'Create role' })
  @ApiResponse({ status: 201, type: Role })
  @UsePipes(ValidationPipe)
  // @Roles('ADMIN')
  // @UseGuards(RolesGuard)
  @Post()
  @HttpCode(HttpStatus.CREATED)
  create(@Body() dto: CreateRoleDto): Promise<RoleDocument> {
    dto.value = dto.value.toUpperCase()
    return this.roleService.createRole(dto)
  }

  @ApiOperation({ summary: 'Update role' })
  @ApiResponse({ status: 200, type: Role })
  @UsePipes(ValidationPipe)
  // @Roles('ADMIN')
  // @UseGuards(RolesGuard)
  @Patch()
  @HttpCode(HttpStatus.OK)
  update(@Body() dto: UpdateRoleDto): Promise<RoleDocument> {
    return this.roleService.updateRole(dto)
  }

  @ApiOperation({ summary: 'Delete role' })
  @ApiResponse({ status: 200, type: Role })
  // @Roles('ADMIN')
  // @UseGuards(RolesGuard)
  @Delete('/:id')
  delete(@Param('id') id: string): Promise<RoleDocument> {
    return this.roleService.deleteRole(id)
  }
}
