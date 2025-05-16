import { AuthService } from './auth.service'
import { Body, Controller, HttpCode, HttpStatus, Param, Post, Res, UsePipes } from '@nestjs/common'
import { ApiOperation, ApiResponse, ApiTags } from '@nestjs/swagger'
import { ValidationPipe } from '../../pipes/validation.pipe'
import { CreateUserDto, RegisterUserDto } from '../users/dto/create-user.dto'
import { Response } from 'express'
import { LoginResponseDto } from './dto/login-response.dto'
import { RegisterResponseDto } from './dto/register-response.dto'
import { RefreshResponseDto } from './dto/refresh-response.dto'
import { UpdatePasswordResponseDto } from './dto/update-password-response.dto'
import { UpdatePasswordReqDto } from './dto/update-password-req.dto'
import { ResetPasswordResponseDto } from './dto/reset-password-response.dto'
import { ResetPasswordReqDto } from './dto/reset-password-req.dto'

@ApiTags('auth')
@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @ApiOperation({ summary: 'user login' })
  @ApiResponse({ status: 200, type: LoginResponseDto })
  @UsePipes(ValidationPipe)
  @Post('/login')
  @HttpCode(HttpStatus.OK)
  async login(@Body() dto: CreateUserDto, @Res() res: Response): Promise<void> {
    const { accessToken, refreshToken } = await this.authService.login(dto)

    res.send({ tokens: { accessToken, refreshToken } })
  }

  @ApiOperation({ summary: 'user register' })
  @ApiResponse({ status: 200, type: RegisterResponseDto })
  @UsePipes(ValidationPipe)
  @Post('/registration')
  @HttpCode(HttpStatus.CREATED)
  async registration(@Body() userDto: RegisterUserDto, @Res() res: Response): Promise<void> {
    const { accessToken, refreshToken } = await this.authService.registration(userDto)

    res.send({ message: 'Registration successful', tokens: { accessToken, refreshToken } })
  }

  @ApiOperation({ summary: 'change user password to another one' })
  @ApiResponse({ status: 200, type: UpdatePasswordResponseDto })
  @UsePipes(ValidationPipe)
  @Post('/update-password')
  @HttpCode(HttpStatus.OK)
  async updatePassword(@Body() dto: UpdatePasswordReqDto): Promise<UpdatePasswordResponseDto> {
    return this.authService.updatePassword(dto)
  }

  @ApiOperation({ summary: 'reset password' })
  @ApiResponse({ status: 200, type: ResetPasswordResponseDto })
  @UsePipes(ValidationPipe)
  @Post('/reset-password')
  @HttpCode(HttpStatus.OK)
  async resetPassword(@Body() dto: ResetPasswordReqDto): Promise<ResetPasswordResponseDto> {
    return this.authService.resetPassword(dto.username)
  }

  @ApiOperation({ summary: 'refreshing tokens' })
  @ApiResponse({ status: 200, type: RefreshResponseDto })
  @Post('/refresh/:refreshToken')
  @HttpCode(HttpStatus.OK)
  async refresh(@Param('refreshToken') refreshTokenReq: string, @Res() res: Response): Promise<void> {
    const { accessToken, refreshToken } = await this.authService.refreshTokens(refreshTokenReq)

    res.send({ message: 'Token updated', tokens: { accessToken, refreshToken } })
  }
}
