import { ApiProperty } from '@nestjs/swagger'

export class LoginResponseDto {
  @ApiProperty({ example: 'Login successful', description: 'Response message' })
  message: string

  @ApiProperty({ example: { accessToken: 'yourAccessToken' }, description: 'Token object' })
  token: {
    accessToken: string
  }
}
