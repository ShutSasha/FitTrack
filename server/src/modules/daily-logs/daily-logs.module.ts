import { Module } from '@nestjs/common'
import { DailyLogsController } from './daily-logs.controller'
import { DailyLogsService } from './daily-logs.service'
import { MongooseModule } from '@nestjs/mongoose'
import { DailyLog, DailyLogSchema } from './daily-log.schema'

@Module({
  imports: [MongooseModule.forFeature([{ name: DailyLog.name, schema: DailyLogSchema }])],
  controllers: [DailyLogsController],
  providers: [DailyLogsService],
})
export class DailyLogsModule {}
