import { DailyLog } from 'modules/daily-logs/daily-log.schema'
import { Body, Controller, Delete, Get, Param, Post, Put, Query, UsePipes, ValidationPipe } from '@nestjs/common'
import { ApiOperation, ApiQuery, ApiResponse } from '@nestjs/swagger'
import { ActivitiesService } from './activities.service'
import { Activity } from './activity.schema'
import {
  ActivityDto,
  ActivitySearchDto,
  AddActivityToLogDto,
  EditActivityInLogDto,
  RemoveActivityFromLogDto,
  SearchActivityRes,
  SortField,
} from '~types/activity.types'

@Controller('activities')
export class ActivitiesController {
  constructor(private readonly activityService: ActivitiesService) {}

  @ApiOperation({ summary: 'Search activities with pagination and sorting' })
  @ApiResponse({
    status: 200,
    description: 'List of activities with pagination metadata',
    type: SearchActivityRes,
  })
  @ApiQuery({ name: 'query', required: false, type: String, example: '' })
  @ApiQuery({ name: 'page', required: false, type: Number, example: 1 })
  @ApiQuery({ name: 'limit', required: false, type: Number, example: 10 })
  @ApiQuery({ name: 'sortBy', required: false, enum: SortField })
  @ApiQuery({ name: 'sortOrder', required: false, enum: ['asc', 'desc'] })
  @Get('search')
  searchNutritionProducts(@Query() query: ActivitySearchDto) {
    return this.activityService.findWithPagination(query)
  }

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

  @ApiOperation({ summary: 'Add activity to daily log' })
  @ApiResponse({ status: 200, type: Activity })
  @UsePipes(ValidationPipe)
  @Post('/add-to-daily-log')
  addActivityToDailyLog(@Body() dto: AddActivityToLogDto) {
    return this.activityService.addActivityToDailyLog(dto)
  }

  @ApiOperation({ summary: 'Remove activity from daily log' })
  @ApiResponse({ status: 200, type: DailyLog })
  @UsePipes(ValidationPipe)
  @Delete('/remove-from-daily-log')
  removeActivityFromDailyLog(@Body() dto: RemoveActivityFromLogDto) {
    return this.activityService.removeActivityFromDailyLog(dto)
  }

  @ApiOperation({ summary: 'Edit activity from daily log' })
  @ApiResponse({ status: 200, type: DailyLog })
  @UsePipes(ValidationPipe)
  @Put('/edit-from-daily-log')
  editActivityFromDailyLog(@Body() dto: EditActivityInLogDto) {
    return this.activityService.editActivityInDailyLog(dto)
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
