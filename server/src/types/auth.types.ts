import { ApiProperty } from '@nestjs/swagger'
import { CreateUserDto } from './users.types'
import {
  IsDateString,
  IsEmail,
  IsEnum,
  IsNotEmpty,
  IsNumber,
  IsString,
  Length,
  Max,
  Min,
  MinLength,
} from 'class-validator'

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
  @ApiProperty({ example: 'yaroslav@gmail.com', description: 'The user’s email' })
  @IsString({ message: 'Email must be a string.' })
  @IsNotEmpty({ message: 'Email cannot be empty.' })
  @IsEmail()
  readonly email: string

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

export class PersonalizeDto {
  @ApiProperty({ example: '12345667890qwertyuiop[', description: 'userId' })
  readonly userId: string

  @ApiProperty({
    description: 'The current date yyyy-mm-dd format',
    example: '1990.01.01',
  })
  @IsDateString(
    { strict: true },
    { message: 'Current date must be a valid date in yyyy-mm-dd format (e.g., 1990-01-29)' },
  )
  currentDate: string

  @ApiProperty({
    description: 'The gender of the user',
    example: 'male',
    enum: ['male', 'female', 'other'],
  })
  @IsEnum(['male', 'female', 'other'], { message: 'Gender must be one of: male, female, other' })
  gender: 'male' | 'female' | 'other'

  @ApiProperty({
    description: 'The height of the user in centimeters',
    example: 175,
  })
  @IsNumber({}, { message: 'Height must be a number' })
  @Min(50, { message: 'Height must be at least 50 cm' })
  @Max(300, { message: 'Height must not exceed 300 cm' })
  height: number

  @ApiProperty({
    description: 'The weight of the user in kilograms',
    example: 70,
  })
  @IsNumber({}, { message: 'Weight must be a number' })
  @Min(20, { message: 'Weight must be at least 20 kg' })
  @Max(500, { message: 'Weight must not exceed 500 kg' })
  weight: number

  @ApiProperty({
    description: 'The body type of the user',
    example: 'athletic',
    enum: ['slim', 'average', 'athletic', 'muscular', 'heavy'],
  })
  @IsEnum(['slim', 'average', 'athletic', 'muscular', 'heavy'], {
    message: 'Body type must be one of: slim, average, athletic, muscular, heavy',
  })
  bodyType: 'slim' | 'average' | 'athletic' | 'muscular' | 'heavy'

  @ApiProperty({
    description: 'The activity level of the user',
    example: 'moderate',
    enum: ['sedentary', 'light', 'moderate', 'active', 'very active'],
  })
  @IsEnum(['sedentary', 'light', 'moderate', 'active', 'very active'], {
    message: 'Activity level must be one of: sedentary, light, moderate, active, very active',
  })
  activityLevel: 'sedentary' | 'light' | 'moderate' | 'active' | 'very active'

  @ApiProperty({
    description: 'The birth date of the user in yyyy-mm-dd format',
    example: '1990.01.01',
  })
  @IsDateString(
    { strict: true },
    { message: 'Birth date must be a valid date in yyyy-mm-dd format (e.g., 1990-01-29)' },
  )
  birthDate: string

  @ApiProperty({
    description: 'The fitness goal of the user',
    example: 'weight_loss',
    enum: ['weight_loss', 'muscle_gain', 'maintenance', 'endurance'],
  })
  @IsEnum(['weight_loss', 'muscle_gain', 'maintenance', 'endurance'], {
    message: 'Goal type must be one of: weight_loss, muscle_gain, maintenance, endurance',
  })
  goalType: 'weight_loss' | 'muscle_gain' | 'maintenance' | 'endurance'

  @ApiProperty({
    description: 'The target weight of the user in kilograms',
    example: 65,
  })
  @IsNumber({}, { message: 'Target weight must be a number' })
  @Min(20, { message: 'Target weight must be at least 20 kg' })
  @Max(500, { message: 'Target weight must not exceed 500 kg' })
  targetWeight: number
}
