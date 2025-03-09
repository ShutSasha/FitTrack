import { DocumentBuilder } from '@nestjs/swagger'

export const swaggerPath = 'api'

export const config = new DocumentBuilder()
  .setTitle('Project api')
  .setDescription('Here stores api documentation')
  .setVersion('1.0')
  .addBearerAuth()
  .build()
