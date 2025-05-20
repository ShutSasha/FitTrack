import { ApiProperty } from '@nestjs/swagger'
import { CreateUserDto } from './users.types'
import { IsNotEmpty, IsString, Length } from 'class-validator'

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
