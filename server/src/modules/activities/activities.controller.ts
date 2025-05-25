import { Body, Controller, Delete, Get, Param, Post, Put, UsePipes, ValidationPipe } from '@nestjs/common'
import { ApiOperation, ApiResponse } from '@nestjs/swagger'
import { ActivitiesService } from './activities.service'
import { Activity } from './activity.schema'
import { ActivityDto } from '~types/activity.types'

@Controller('activities')
export class ActivitiesController {
  constructor(private readonly activityService: ActivitiesService) {}

  @ApiOperation({ summary: 'Get all activities ' })
  @ApiResponse({ status: 200, type: [Activity] })
  @Get()
  getAllNutritionProducts() {
    return this.activityService.getAllActivities()
  }

  @ApiOperation({ summary: 'Get activity by id' })
  @ApiResponse({ status: 200, type: Activity })
  @Get('/:id')
  getNutritionById(@Param('id') id: string) {
    return this.activityService.getActivityById(id)
  }

  @ApiOperation({ summary: 'Create activity' })
  @ApiResponse({ status: 200, type: Activity })
  @UsePipes(ValidationPipe)
  @Post()
  createNutritionProduct(@Body() dto: ActivityDto) {
    return this.activityService.create(dto)
  }

  @ApiOperation({ summary: 'Update activity' })
  @ApiResponse({ status: 200, type: Activity })
  @UsePipes(ValidationPipe)
  @Put('/:id')
  updateNutritionProduct(@Param('id') id: string, @Body() dto: ActivityDto) {
    return this.activityService.update(id, dto)
  }

  @ApiOperation({ summary: 'Delete activity' })
  @ApiResponse({ status: 200, type: Activity })
  @Delete('/:id')
  deleteNutritionProduct(@Param('id') id: string) {
    return this.activityService.delete(id)
  }
}
