import { ApiProperty } from '@nestjs/swagger'
import { IsNotEmpty, IsString } from 'class-validator'

export class UpdateRoleDto {
  @ApiProperty({ example: '12134512df2144', description: 'Id of role that u wanna update' })
  @IsNotEmpty()
  @IsString({ message: 'Should be a string' })
  readonly id: string

  @ApiProperty({ example: 'Admin', description: 'It`s about of the role name' })
  @IsNotEmpty()
  @IsString({ message: 'Should be a string' })
  value: string

  @ApiProperty({ example: 'This role can do everything', description: 'It`s about of the role description' })
  @IsNotEmpty()
  @IsString({ message: 'Should be a string' })
  description: string
}
