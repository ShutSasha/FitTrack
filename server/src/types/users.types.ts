import { ApiProperty } from '@nestjs/swagger'
import { IsEmail, IsNotEmpty, IsString, Length } from 'class-validator'

export class CreateUserDto {
  @ApiProperty({ example: 'yaroslav@gmail.com', description: 'The user’s email' })
  @IsString({ message: 'Email must be a string.' })
  @IsNotEmpty({ message: 'Email cannot be empty.' })
  @IsEmail()
  readonly email: string

  @ApiProperty({ example: 'cdidk', description: 'The username of the user' })
  @IsString({ message: 'Username must be a string.' })
  @IsNotEmpty({ message: 'Username cannot be empty.' })
  @Length(4, 24, { message: 'Username must be between 4 and 24 characters long.' })
  readonly username: string

  @ApiProperty({ example: 'qwerty123123', description: 'The user’s password' })
  @IsString({ message: 'Password must be a string.' })
  @IsNotEmpty({ message: 'Password cannot be empty.' })
  @Length(8, 24, { message: 'Password must be between 8 and 24 characters long.' })
  readonly password: string
}
