import { HttpException, HttpStatus, Injectable, UnauthorizedException } from '@nestjs/common'
import { UsersService } from '../users/users.service'
import { JwtService } from '@nestjs/jwt'
import { User, UserDocument } from '../users/users.schema'
import * as bcrypt from 'bcryptjs'
import { InjectModel } from '@nestjs/mongoose'
import { Model } from 'mongoose'
import { TokenResponseDto } from './dto/token-response.dto'
import { CreateUserDto } from '~types/users.types'
import { RegisterUserDto } from '~types/auth.types'

@Injectable()
export class AuthService {
  constructor(
    @InjectModel(User.name) private userModel: Model<UserDocument>,
    private readonly usersService: UsersService,
    private jwtService: JwtService,
  ) {}

  async login(dto: CreateUserDto): Promise<TokenResponseDto> {
    const user = await this.validateUser(dto)
    return this.generateTokens(user)
  }

  async registration(dto: RegisterUserDto): Promise<TokenResponseDto> {
    const candidate = await this.userModel
      .findOne({
        $or: [{ username: dto.username }, { email: dto.email }],
      })
      .exec()

    if (candidate) throw new HttpException('User with this username or email exist already', HttpStatus.BAD_REQUEST)

    if (dto.password !== dto.confirmPassword) throw new HttpException('Passwords do not match', HttpStatus.BAD_REQUEST)

    const hashPassword = await bcrypt.hash(dto.password, +process.env.PASSWORD_SALT)

    const user = await this.usersService.create({ ...dto, password: hashPassword })

    return this.generateTokens(user)
  }

  async refreshTokens(refreshToken: string): Promise<TokenResponseDto> {
    try {
      const payload = this.jwtService.verify(refreshToken, { secret: process.env.JWT_REFRESH_SECRET })

      const user = await this.usersService.getUserById(payload.id)

      if (!user) throw new HttpException('User by id for refresh tokens not found', HttpStatus.NOT_FOUND)

      return this.generateTokens(user)
    } catch (e) {
      throw new UnauthorizedException('Invalid refresh token')
    }
  }

  private async generateTokens(user: UserDocument): Promise<TokenResponseDto> {
    const payload = { username: user.username, id: user._id, roles: user.roles }

    const accessToken = this.jwtService.sign(payload, { expiresIn: '15d', secret: process.env.JWT_ACCESS_SECRET })
    const refreshToken = this.jwtService.sign(payload, { expiresIn: '30d', secret: process.env.JWT_REFRESH_SECRET })

    return { accessToken, refreshToken }
  }

  private async validateUser(dto: CreateUserDto): Promise<UserDocument> {
    const user = await this.usersService.getUserByUsername(dto.username)

    if (!user) throw new HttpException('User with this username has been not found', HttpStatus.NOT_FOUND)

    const passwordEquals = await bcrypt.compare(dto.password, user.password)

    if (user && passwordEquals) return user

    throw new HttpException({ message: 'Incorrect password' }, HttpStatus.BAD_REQUEST)
  }
}
