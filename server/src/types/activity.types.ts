import { ApiProperty } from '@nestjs/swagger'
import { IsEnum, IsInt, IsNotEmpty, IsNumber, IsOptional, IsString, Min } from 'class-validator'
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
