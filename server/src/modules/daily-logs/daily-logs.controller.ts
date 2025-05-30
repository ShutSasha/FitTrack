import { Controller, Get, Param, UsePipes, ValidationPipe } from '@nestjs/common'
import { DailyLogsService } from './daily-logs.service'
import { ApiOperation, ApiParam, ApiResponse } from '@nestjs/swagger'
import { DailyLog } from './daily-log.schema'
import { GetDailyLogParams } from '~types/daily-log.types'

@Controller('daily-logs')
export class DailyLogsController {
  constructor(private readonly dailyLogService: DailyLogsService) {}

  @ApiOperation({ summary: 'Get all daily logs ' })
  @ApiResponse({ status: 200, type: [DailyLog] })
  @Get()
  getAllDailyLogs() {
    return this.dailyLogService.getAllDailyLogs()
  }

  @ApiOperation({ summary: 'Get daily log by id' })
  @ApiResponse({ status: 200, type: DailyLog })
  @Get('/:id')
  getDailyLogById(@Param('id') id: string) {
    return this.dailyLogService.getDailyLogById(id)
  }

  @ApiOperation({ summary: 'Get or create daily log by date and userId' })
  @ApiParam({
    name: 'userId',
    description: 'ID of the user for daily log',
    example: '6651b9d17b9e6a4ad8dbb126',
  })
  @ApiParam({
    name: 'date',
    description: 'The date describes in which day this daily log was created or should be created',
    example: '6651ba087b9e6a4ad8dbb128',
  })
  @ApiResponse({ status: 200, type: DailyLog })
  @Get('/:userId/:date')
  @UsePipes(ValidationPipe)
  getDailyLogByUserIdAndDate(@Param() params: GetDailyLogParams) {
    const parsedDate = new Date(params.date)
    return this.dailyLogService.getDailyLogByUserIdAndDate(params.userId, parsedDate)
  }
}
