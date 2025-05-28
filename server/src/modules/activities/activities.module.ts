import { Module } from '@nestjs/common'
import { ActivitiesService } from './activities.service'
import { ActivitiesController } from './activities.controller'
import { MongooseModule } from '@nestjs/mongoose'
import { Activity, ActivitySchema } from './activity.schema'
import { DailyLogsModule } from 'modules/daily-logs/daily-logs.module'

@Module({
  imports: [MongooseModule.forFeature([{ name: Activity.name, schema: ActivitySchema }]), DailyLogsModule],
  providers: [ActivitiesService],
  controllers: [ActivitiesController],
})
export class ActivitiesModule {}
