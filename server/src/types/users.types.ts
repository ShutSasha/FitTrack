import { ApiProperty } from '@nestjs/swagger'
import { IsEmail, IsInt, IsNotEmpty, IsOptional, IsString, Length, Min } from 'class-validator'
import { User } from 'modules/users/users.schema'

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

export class SearchUsersRes {
  @ApiProperty({ type: [User] })
  items: User[]

  @ApiProperty({ example: 100 })
  total: number

  @ApiProperty({ example: 1 })
  page: number

  @ApiProperty({ example: 10 })
  limit: number
}

export class UsersSearchDto {
  @ApiProperty({
    example: 'cdidk',
    description: 'Search query for users name (case-insensitive)',
    required: false,
  })
  @IsOptional()
  @IsString()
  query?: string

  @ApiProperty({ example: 1, description: 'Page number (1-based)', required: false })
  @IsOptional()
  @IsInt()
  @Min(1)
  page?: number = 1

  @ApiProperty({ example: 10, description: 'Number of items per page', required: false })
  @IsOptional()
  @IsInt()
  @Min(1)
  limit?: number = 10
}
