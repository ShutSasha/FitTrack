import { AuthService } from './auth.service'
import { Body, Controller, HttpCode, HttpStatus, Param, Post, Res, UsePipes } from '@nestjs/common'
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
    console.log(tokens)

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

  // TODO with email confirm
  // @ApiOperation({ summary: 'change user password to another one' })
  // @ApiResponse({ status: 200, type: UpdatePasswordResponseDto })
  // @UsePipes(ValidationPipe)
  // @Post('/update-password')
  // @HttpCode(HttpStatus.OK)
  // async updatePassword(@Body() dto: UpdatePasswordReqDto) {
  //   return this.authService.updatePassword(dto)
  // }

  // @ApiOperation({ summary: 'reset password' })
  // @ApiResponse({ status: 200, type: ResetPasswordResponseDto })
  // @UsePipes(ValidationPipe)
  // @Post('/reset-password')
  // @HttpCode(HttpStatus.OK)
  // async resetPassword(@Body() dto: ResetPasswordReqDto): Promise<ResetPasswordResponseDto> {
  //   return this.authService.resetPassword(dto.username)
  // }
}
