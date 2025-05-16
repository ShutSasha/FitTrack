import { ApiProperty } from '@nestjs/swagger'

export class LoginResponseDto {
  @ApiProperty({
    example: { accessToken: 'yourAccessToken', refreshToken: 'yourRefreshToken' },
    description: 'Token object',
  })
  tokens: {
    accessToken: string
    refreshToken: string
  }
}
