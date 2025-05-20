import { HttpException, HttpStatus } from '@nestjs/common'

export class ValidationException extends HttpException {
  messages: { messages: string[] }

  constructor(response: { messages: string[] }) {
    super(response, HttpStatus.BAD_REQUEST)
    this.messages = response
  }
}
