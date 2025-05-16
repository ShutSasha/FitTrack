import { ApiProperty } from '@nestjs/swagger'

export class RefreshResponseDto {
  @ApiProperty({
    example: { accessToken: 'yourAccessToken', refreshToken: 'yourRefreshToken' },
    description: 'Token object',
  })
  tokens: {
    accessToken: string
    refreshToken: string
  }
}
