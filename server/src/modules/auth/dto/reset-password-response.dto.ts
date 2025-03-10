import { ApiProperty } from '@nestjs/swagger'

export class ResetPasswordResponseDto {
  @ApiProperty({ example: 'Password changed successfully' })
  readonly message: string

  @ApiProperty({ example: 'newPassword123' })
  readonly newPassword: string
}
