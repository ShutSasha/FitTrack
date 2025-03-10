import { ApiProperty } from '@nestjs/swagger'

export class ResetPasswordReqDto {
  @ApiProperty({ example: 'cdidk123' })
  readonly username: string
}
