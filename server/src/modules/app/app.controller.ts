import { Controller, Get, UseGuards } from '@nestjs/common'
import { AppService } from './app.service'
import { JwtAuthGuard } from '../auth/jwt-auth.guard'
import { ApiBearerAuth } from '@nestjs/swagger'

@Controller()
export class AppController {
  constructor(private readonly appService: AppService) {}

  @Get('/hello')
  async getHello(): Promise<string> {
    return this.appService.getHello()
  }

  @Get('/hello-with-auth')
  @ApiBearerAuth()
  @UseGuards(JwtAuthGuard)
  async getHelloAuth(): Promise<string> {
    return this.appService.getHello()
  }
}
