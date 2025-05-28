import { ApiProperty } from '@nestjs/swagger'
import { IsEnum, IsInt, IsNotEmpty, IsNumber, IsOptional, IsString, Matches, Min } from 'class-validator'
import { Activity } from 'modules/activities/activity.schema'

export class SearchActivityRes {
  @ApiProperty({ type: [Activity] })
  items: Activity[]

  @ApiProperty({ example: 100 })
  total: number

  @ApiProperty({ example: 1 })
  page: number

  @ApiProperty({ example: 10 })
  limit: number
}

export enum SortOrder {
  ASC = 'asc',
  DESC = 'desc',
}

export enum SortField {
  CALORIES = 'caloriesPerMin',
}

export class ActivitySearchDto {
  @ApiProperty({
    example: 'Running',
    description: 'Search query for activity name (case-insensitive)',
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

  @ApiProperty({
    example: 'calories',
    description: 'Field to sort by calories',
    enum: SortField,
    required: false,
  })
  @IsOptional()
  @IsEnum(SortField)
  sortBy?: string

  @ApiProperty({
    example: 'asc',
    description: 'Sort order (asc or desc)',
    enum: SortOrder,
    required: false,
  })
  @IsOptional()
  @IsEnum(SortOrder)
  sortOrder?: string
}

export class ActivityDto {
  @ApiProperty({
    example: 'Running',
    description: 'Name of the activity (e.g., Running)',
  })
  @IsString()
  @IsNotEmpty()
  name: string

  @ApiProperty({
    example: 11.5,
    description: 'Burned calories per minute',
  })
  @IsNumber()
  @IsNotEmpty()
  caloriesPerMin: number
}

export class AddActivityToLogDto {
  @ApiProperty({
    example: '152789891kfgs7583729853hdkf1276',
    description: 'The field should contain user id in ObjectId type',
  })
  @IsString()
  @IsNotEmpty()
  userId: string

  @ApiProperty({
    example: '2025-05-26',
    description: 'The field should contain date of the daily log',
  })
  @Matches(/^\d{4}-\d{2}-\d{2}$/, { message: 'Date must be in format YYYY-MM-DD' })
  date: Date

  @ApiProperty({
    example: '152789891kfgs7583729853hdkf1276',
    description: 'The field should contain activity id in ObjectId type',
  })
  @IsString()
  @IsNotEmpty()
  activityId: string

  @ApiProperty({
    example: 60,
    description: 'The field should contain total minutes that user trains with this activity',
  })
  @IsNumber()
  @IsNotEmpty()
  totalMinutes: number
}

export class RemoveActivityFromLogDto {
  @ApiProperty({
    example: '152789891kfgs7583729853hdkf1276',
    description: 'The field should contain user id in ObjectId type',
  })
  @IsString()
  @IsNotEmpty()
  userId: string

  @ApiProperty({
    example: '2025-05-26',
    description: 'The field should contain date of the daily log',
  })
  @Matches(/^\d{4}-\d{2}-\d{2}$/, { message: 'Date must be in format YYYY-MM-DD' })
  date: Date

  @ApiProperty({
    example: '152789891kfgs7583729853hdkf1276',
    description: 'The field should contain activity id in ObjectId type (in array activities property - _id)',
  })
  @IsString()
  @IsNotEmpty()
  activityId: string
}
