import { ApiProperty } from '@nestjs/swagger'
import { IsEmail, IsNotEmpty, IsString } from 'class-validator'

export class SendConfirmEmailDto {
  @ApiProperty({ example: 'yaroslav@gmail.com', description: 'The user’s email' })
  @IsString({ message: 'Email must be a string.' })
  @IsNotEmpty({ message: 'Email cannot be empty.' })
  @IsEmail()
  readonly email: string

  @ApiProperty({ example: '11567123868743682', description: 'The token of confirm email' })
  @IsString({ message: 'Token must be a string.' })
  @IsNotEmpty({ message: 'Token cannot be empty.' })
  readonly token: string
}
