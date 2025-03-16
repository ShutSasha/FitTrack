import { ApiProperty } from '@nestjs/swagger'
import { IsNotEmpty, IsString, Length } from 'class-validator'

export class CreateUserDto {
  @ApiProperty({ example: 'cdidk1', description: 'username' })
  @IsString({ message: 'Should be a string' })
  @Length(4, 24, { message: 'No less than 4 and no more than 24 symbols' })
  readonly username: string

  @ApiProperty({ example: 'VERY_STRONG_PASSWORD', description: 'user password' })
  @IsString({ message: 'Should be a string' })
  @IsNotEmpty()
  @Length(8, 24, { message: 'Password should not be less than 8 and no more than 24 symbols' })
  readonly password: string
}

export class RegisterUserDto extends CreateUserDto {
  @ApiProperty({ example: 'VERY_STRONG_PASSWORD', description: 'Confirm user password' })
  @IsString({ message: 'Should be a string' })
  @IsNotEmpty()
  @Length(8, 24, { message: 'Confirm password should not be less than 8 and no more than 24 symbols' })
  readonly confirmPassword: string
}
