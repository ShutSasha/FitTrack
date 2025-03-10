import { ApiProperty } from '@nestjs/swagger'

export class UpdatePasswordReqDto {
  @ApiProperty({ example: 'cdidk' })
  username: string

  @ApiProperty({ example: 'VERY_STRONG_PASSWORD' })
  oldPassword: string

  @ApiProperty({ example: 'VERY_STRONG_PASSWORD1' })
  newPassword: string

  @ApiProperty({ example: 'VERY_STRONG_PASSWORD1' })
  confirmNewPassword: string
}
