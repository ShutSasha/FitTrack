import { AuthService } from './auth.service'
import { Body, Controller, Get, HttpCode, HttpStatus, Param, Post, Res, UsePipes } from '@nestjs/common'
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger'
import { ValidationPipe } from '../../pipes/validation.pipe'
import { Response } from 'express'
import { LoginRes, LoginUserDto, RefreshRes, RegisterRes, RegisterUserDto } from '~types/auth.types'

@ApiTags('auth')
@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @ApiOperation({ summary: 'user login' })
  @ApiResponse({ status: 200, type: LoginRes })
  @UsePipes(ValidationPipe)
  @Post('/login')
  @HttpCode(HttpStatus.OK)
  async login(@Body() dto: LoginUserDto, @Res() res: Response) {
    const { accessToken, refreshToken } = await this.authService.login(dto)

    return res.send({ tokens: { accessToken, refreshToken } })
  }

  @ApiOperation({ summary: 'user register' })
  @ApiResponse({ status: 200, type: RegisterRes })
  @UsePipes(ValidationPipe)
  @Post('/registration')
  @HttpCode(HttpStatus.CREATED)
  async registration(@Body() userDto: RegisterUserDto, @Res() res: Response) {
    const tokens = await this.authService.registration(userDto)

    res.send({ tokens: tokens })
  }

  @ApiOperation({ summary: 'refreshing tokens' })
  @ApiResponse({ status: 200, type: RefreshRes })
  @Post('/refresh/:refreshToken')
  @HttpCode(HttpStatus.OK)
  async refresh(@Param('refreshToken') refreshTokenReq: string, @Res() res: Response) {
    const { accessToken, refreshToken } = await this.authService.refreshTokens(refreshTokenReq)

    res.send({ tokens: { accessToken, refreshToken } })
  }

  @ApiOperation({ summary: 'confirm email' })
  @ApiResponse({ status: 200, description: 'Email confirmed successfully' })
  @Get('/confirm-email/:token')
  @HttpCode(HttpStatus.OK)
  async confirmEmail(@Param('token') token: string) {
    await this.authService.confirmEmail(token)
    return { message: 'Email confirmed successfully' }
  }

  @ApiOperation({ summary: 'resend confirmation email' })
  @ApiResponse({ status: 200, description: 'Confirmation email resent' })
  @Post('/resend-confirmation/:userId')
  @HttpCode(HttpStatus.OK)
  async resendConfirmationEmail(@Param('userId') userId: string) {
    await this.authService.resendConfirmationEmail(userId)
    return { message: 'Confirmation email sent' }
  }
}
