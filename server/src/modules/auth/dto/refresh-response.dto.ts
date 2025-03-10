import { ApiProperty } from '@nestjs/swagger'

export class RefreshResponseDto {
  @ApiProperty({ example: 'Token updated', description: 'Response message' })
  message: string

  @ApiProperty({ example: { accessToken: 'yourAccessToken' }, description: 'Token object' })
  token: {
    accessToken: string
  }
}
