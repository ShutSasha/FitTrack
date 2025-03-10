import { ApiProperty } from '@nestjs/swagger'

export class UpdatePasswordResponseDto {
  @ApiProperty({ example: 'Password changed successfully' })
  message: string
}
