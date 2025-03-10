import { ApiProperty } from '@nestjs/swagger'

export class RefreshResponseDto {
  @ApiProperty({ example: 'Tokens have been updated', description: 'Response message' })
  message: string

  @ApiProperty({
    example: { accessToken: 'yourAccessToken', refreshToken: 'yourRefreshToken' },
    description: 'Token object',
  })
  tokens: {
    accessToken: string
    refreshToken: string
  }
}
