// TODO: fix ValidationException response, make a suit form of response like an obj {messages: []}

import { HttpException, HttpStatus } from '@nestjs/common'

export class ValidationException extends HttpException {
  messages: { messages: string[] }

  constructor(response: { messages: string[] }) {
    super(response, HttpStatus.BAD_REQUEST)
    this.messages = response
  }
}
