import { NestFactory } from '@nestjs/core'
import { AppModule } from './modules/app/app.module'
import * as cookieParser from 'cookie-parser'
import { SwaggerModule } from '@nestjs/swagger'
import { config, swaggerPath } from './swagger'

const PORT = process.env.PORT || 3000

async function bootstrap() {
  const app = await NestFactory.create(AppModule)

  app.setGlobalPrefix('api')

  app.enableCors({
    credentials: true,
    origin: true,
  })

  app.use(cookieParser())

  const document = SwaggerModule.createDocument(app, config)
  SwaggerModule.setup(swaggerPath, app, document)

  await app.listen(PORT)
}

bootstrap()
