import { Module } from '@nestjs/common'
import { DailyLogsController } from './daily-logs.controller'
import { DailyLogsService } from './daily-logs.service'
import { MongooseModule } from '@nestjs/mongoose'
import { DailyLog, DailyLogSchema } from './daily-log.schema'
import { UsersModule } from 'modules/users/users.module'
import { NutritionProductsModule } from 'modules/nutrition-products/nutrition-products.module'

@Module({
  imports: [
    MongooseModule.forFeature([{ name: DailyLog.name, schema: DailyLogSchema }]),
    UsersModule,
    NutritionProductsModule,
  ],
  controllers: [DailyLogsController],
  providers: [DailyLogsService],
  exports: [DailyLogsService],
})
export class DailyLogsModule {}
