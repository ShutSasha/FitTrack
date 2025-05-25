import { ApiProperty } from '@nestjs/swagger'
import { IsNotEmpty, IsNumber, IsString } from 'class-validator'

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
