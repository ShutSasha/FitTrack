import { ApiProperty } from '@nestjs/swagger'
import { IsNotEmpty, IsString } from 'class-validator'

export class CreateRoleDto {
  @ApiProperty({ example: 'USER', description: 'Here you can create role whatever you want' })
  @IsNotEmpty()
  @IsString({ message: 'Should be a string' })
  value: string

  @ApiProperty({ example: 'default user role', description: 'Here you can create role whatever you want' })
  @IsNotEmpty()
  @IsString({ message: 'Should be a string' })
  description: string
}
