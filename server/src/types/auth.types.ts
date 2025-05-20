import { ApiProperty } from '@nestjs/swagger'
import { CreateUserDto } from './users.types'
import { IsNotEmpty, IsString, Length, MinLength } from 'class-validator'

export class RegisterUserDto extends CreateUserDto {
  @ApiProperty({
    example: 'qwerty123123',
    description: 'This field must match the password entered above.',
  })
  @IsString({ message: 'Confirm password must be a string.' })
  @IsNotEmpty({ message: 'Confirm password cannot be empty.' })
  @Length(8, 24, { message: 'Confirm password must be between 8 and 24 characters long.' })
  readonly confirmPassword: string
}

export class LoginUserDto extends CreateUserDto {}

export class LoginRes {
  @ApiProperty({
    example: { accessToken: 'yourAccessToken', refreshToken: 'yourRefreshToken' },
    description: 'Tokens object',
  })
  tokens: {
    accessToken: string
    refreshToken: string
  }
}

export class RegisterRes {
  @ApiProperty({
    example: { accessToken: 'ababab1', refreshToken: 'ababab2' },
    description: 'Register response dto with tokens',
  })
  tokens: {
    accessToken: string
    refreshToken: string
  }
}

export class RefreshRes {
  @ApiProperty({
    example: { accessToken: 'yourAccessToken', refreshToken: 'yourRefreshToken' },
    description: 'Token object',
  })
  tokens: {
    accessToken: string
    refreshToken: string
  }
}

export class TokensRes {
  @ApiProperty({ example: 'bdsfdsfkl.bearsr2152.215342', description: 'Just a access token' })
  readonly accessToken: string

  @ApiProperty({ example: 'bdsfdsfkl.bearsr2152.215342', description: 'Just a refresh token' })
  readonly refreshToken: string
}

export class ConfirmResetPasswordCodeReq {
  @ApiProperty({ example: '12345667890qwertyuiop[', description: 'userId' })
  readonly userId: string

  @ApiProperty({ example: '1234', description: 'confirm code' })
  readonly code: number

  @ApiProperty({
    example: 'newpassword123',
    description: 'The new password for the user',
  })
  @IsString()
  @MinLength(8)
  readonly newPassword: string

  @ApiProperty({
    example: 'newpassword123',
    description: 'Confirmation of the new password',
  })
  @IsString()
  @MinLength(8)
  readonly newPasswordConfirm: string
}

export class SendResetPasswordCodeResponseDto {
  @ApiProperty({
    example: 'Confirmation code sent to email',
    description: 'Message indicating that the reset code was sent',
  })
  message: string
}

export class ConfirmResetPasswordCodeResponseDto {
  @ApiProperty({
    example: true,
    description: 'Indicates if the confirmation code was valid',
  })
  success: boolean

  @ApiProperty({
    example: 'Password changed successfully',
    description: 'Message indicating the result of code confirmation',
  })
  message: string
}
