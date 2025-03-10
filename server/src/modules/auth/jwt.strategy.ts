import { Request } from 'express'
import { Strategy } from 'passport-jwt'
import { PassportStrategy } from '@nestjs/passport'
import { Injectable } from '@nestjs/common'

@Injectable()
export class JwtStrategy extends PassportStrategy(Strategy) {
  constructor() {
    super({
      jwtFromRequest: tokenExtractor,
      ignoreExpiration: false,
      secretOrKey: process.env.JWT_ACCESS_SECRET,
    })
  }

  async validate<T>(payload: T): Promise<T> {
    return { ...payload }
  }
}

const tokenExtractor = (req: Request): string | null => {
  let token = null
  if (req && req.cookies) {
    // extracting token from cookies or from req.headers.authorization
    token = req.cookies['accessToken'] ? req.cookies['accessToken'] : req.headers.authorization.split(' ')[1]
  }
  return token
}
