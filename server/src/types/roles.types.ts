import { ApiProperty } from '@nestjs/swagger'
import { IsNotEmpty, IsString } from 'class-validator'

export class CreateRoleDto {
  @ApiProperty({ example: 'USER', description: 'Here you can create role whatever you want' })
  @IsNotEmpty({ message: 'Value field cannot be empty' })
  @IsString({ message: 'Should be a string' })
  value: string

  @ApiProperty({ example: 'default user role', description: 'Here you can create role whatever you want' })
  @IsNotEmpty({ message: 'description field cannot be empty' })
  @IsString({ message: 'Should be a string' })
  readonly description: string
}

export class UpdateRoleDto {
  @ApiProperty({ example: '12134512df2144', description: 'Id of role that u wanna update' })
  @IsNotEmpty({ message: 'id field cannot be empty' })
  @IsString({ message: 'Should be a string' })
  readonly id: string

  @ApiProperty({ example: 'Admin', description: 'It`s about of the role name' })
  @IsNotEmpty({ message: 'value field cannot be empty' })
  @IsString({ message: 'Should be a string' })
  value: string

  @ApiProperty({ example: 'This role can do everything', description: 'It`s about of the role description' })
  @IsNotEmpty({ message: 'description field cannot be empty' })
  @IsString({ message: 'Should be a string' })
  readonly description: string
}

export class ChangeRoleDto {
  @ApiProperty({ example: '12134512df2144', description: 'Id of role that u wanna update' })
  @IsNotEmpty({ message: 'id field cannot be empty' })
  @IsString({ message: 'Should be a string' })
  readonly roleId: string

  @ApiProperty({ example: '12134512df2144', description: 'Id of user that u wanna update' })
  @IsNotEmpty({ message: 'id field cannot be empty' })
  @IsString({ message: 'Should be a string' })
  readonly userId: string
}
