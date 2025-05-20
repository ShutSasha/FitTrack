import { Module } from '@nestjs/common'
import { ConfigModule } from '@nestjs/config'
import { MongooseModule } from '@nestjs/mongoose'
import { UsersModule } from '../users/users.module'
import { RolesModule } from '../roles/roles.module'
import { AuthModule } from '../auth/auth.module'
import { EmailModule } from 'modules/email/email.module'

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
  ],
})
export class AppModule {}
