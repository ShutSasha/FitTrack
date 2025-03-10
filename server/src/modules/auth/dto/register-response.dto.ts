import { ApiProperty } from '@nestjs/swagger'

export class RegisterResponseDto {
  @ApiProperty({ example: 'Register successful', description: 'Response message' })
  message: string

  @ApiProperty({
    example: { tokens: { accessToken: 'ababab1', refreshToken: 'ababab2' } },
    description: 'Register response dto with tokens',
  })
  tokens: {
    accessToken: string
    refreshToken: string
  }
}
