import { AuthService } from './auth.service'
import { Body, Controller, Get, HttpCode, HttpStatus, Param, Post, Res, UsePipes } from '@nestjs/common'
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger'
import { ValidationPipe } from '../../pipes/validation.pipe'
import { Response } from 'express'
import {
  ConfirmResetPasswordCodeReq,
  ConfirmResetPasswordCodeResponseDto,
  LoginRes,
  LoginUserDto,
  PersonalizeDto,
  RefreshRes,
  RegisterRes,
  RegisterUserDto,
  SendResetPasswordCodeResponseDto,
} from '~types/auth.types'
import { User } from 'modules/users/users.schema'

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

  @ApiOperation({ summary: 'This endpoint signing up user to the system and send code to email' })
  @ApiResponse({ status: 200, type: RegisterRes })
  @UsePipes(ValidationPipe)
  @Post('/registration')
  @HttpCode(HttpStatus.CREATED)
  async registration(@Body() userDto: RegisterUserDto, @Res() res: Response) {
    const tokens = await this.authService.registration(userDto)

    res.send({ tokens: tokens })
  }

  @ApiOperation({ summary: 'This endpoint set up personalization info about user' })
  @ApiResponse({ status: 200, type: User })
  @UsePipes(ValidationPipe)
  @Post('/personalization')
  @HttpCode(HttpStatus.OK)
  async personalization(@Body() dto: PersonalizeDto) {
    return this.authService.personalize(dto)
  }

  @ApiOperation({ summary: 'refreshing tokens' })
  @ApiResponse({ status: 200, type: RefreshRes })
  @Post('/refresh/:refreshToken')
  @HttpCode(HttpStatus.OK)
  async refresh(@Param('refreshToken') refreshTokenReq: string, @Res() res: Response) {
    const { accessToken, refreshToken } = await this.authService.refreshTokens(refreshTokenReq)

    res.send({ tokens: { accessToken, refreshToken } })
  }

  @ApiOperation({ summary: 'confirm email (only for users api, it uses only in email box)' })
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

  @ApiOperation({ summary: 'Send code confirmation to email about reset password and it works as resend code' })
  @ApiResponse({
    status: 200,
    description: 'Confirmation code sent to email',
    type: SendResetPasswordCodeResponseDto,
  })
  @Post('/send-reset-password-code/:email')
  @HttpCode(HttpStatus.OK)
  async sendResetPasswordCode(@Param('email') email: string) {
    await this.authService.sendResetPasswordCode(email)
    return { message: 'Confirmation code sent to email' }
  }

  @ApiOperation({ summary: 'Confirm code and setup new password' })
  @ApiResponse({
    status: 200,
    description: 'Confirmation code confirmed',
    type: ConfirmResetPasswordCodeResponseDto,
  })
  @Post('/confirm-reset-password')
  @HttpCode(HttpStatus.OK)
  async confirmResetPasswordCode(@Body() req: ConfirmResetPasswordCodeReq) {
    await this.authService.confirmResetPasswordCode(req)
    return { success: true, message: 'Password changed successfully' }
  }
}
