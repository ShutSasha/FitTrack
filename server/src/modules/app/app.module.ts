import { Module } from '@nestjs/common'
import { ConfigModule } from '@nestjs/config'
import { MongooseModule } from '@nestjs/mongoose'
import { UsersModule } from '../users/users.module'
import { RolesModule } from '../roles/roles.module'
import { AuthModule } from '../auth/auth.module'
import { EmailModule } from 'modules/email/email.module'
import { DailyLogsModule } from 'modules/daily-logs/daily-logs.module'
import { MealsModule } from 'modules/meals/meals.module'
import { ActivitiesModule } from 'modules/activities/activities.module'
import { NutritionProductsModule } from 'modules/nutrition-products/nutrition-products.module'
import { ProductRequestsModule } from 'modules/product-requests/product-requests.module'

@Module({
  imports: [
    ConfigModule.forRoot({ envFilePath: `.env` }),
    MongooseModule.forRoot(
      `mongodb+srv://${process.env.DB_USERNAME}:${process.env.DB_PASSWORD}@cluster0.pdc0f.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0`,
    ),
    EmailModule,
    UsersModule,
    RolesModule,
    AuthModule,
    DailyLogsModule,
    MealsModule,
    ActivitiesModule,
    NutritionProductsModule,
    ProductRequestsModule,
  ],
})
export class AppModule {}
