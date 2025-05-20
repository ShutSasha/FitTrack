import { HttpException, HttpStatus, Injectable, UnauthorizedException } from '@nestjs/common'
import { UsersService } from '../users/users.service'
import { JwtService } from '@nestjs/jwt'
import { User, UserDocument } from '../users/users.schema'
import * as bcrypt from 'bcryptjs'
import { InjectModel } from '@nestjs/mongoose'
import { isValidObjectId, Model } from 'mongoose'
import { CreateUserDto } from '~types/users.types'
import { ConfirmResetPasswordCodeReq, RegisterUserDto, TokensRes } from '~types/auth.types'
import { EmailService } from 'modules/email/email.service'
import { v4 as uuidv4 } from 'uuid'

@Injectable()
export class AuthService {
  constructor(
    @InjectModel(User.name) private userModel: Model<UserDocument>,
    private readonly usersService: UsersService,
    private readonly jwtService: JwtService,
    private readonly emailService: EmailService,
  ) {}

  async login(dto: CreateUserDto): Promise<TokensRes> {
    const user = await this.validateUser(dto)

    if (!user.isEmailConfirmed) {
      throw new HttpException('Please confirm your email first', HttpStatus.FORBIDDEN)
    }

    return this.generateTokens(user)
  }

  async registration(dto: RegisterUserDto): Promise<TokensRes> {
    const candidate = await this.userModel
      .findOne({
        $or: [{ username: dto.username }, { email: dto.email }],
      })
      .exec()

    if (candidate) throw new HttpException('User with this username or email exist already', HttpStatus.BAD_REQUEST)

    if (dto.password !== dto.confirmPassword) throw new HttpException('Passwords do not match', HttpStatus.BAD_REQUEST)

    const hashPassword = await bcrypt.hash(dto.password, +process.env.PASSWORD_SALT)
    const confirmationToken = uuidv4()

    const user = await this.usersService.create({
      ...dto,
      password: hashPassword,
      emailConfirmationToken: confirmationToken,
    })

    await this.emailService.sendConfirmationEmail(dto.email, confirmationToken)

    return this.generateTokens(user)
  }

  async confirmEmail(token: string): Promise<void> {
    const user = await this.userModel.findOne({ emailConfirmationToken: token }).exec()

    if (!user) {
      throw new HttpException('Invalid or expired confirmation token', HttpStatus.BAD_REQUEST)
    }

    user.isEmailConfirmed = true
    user.emailConfirmationToken = null
    await user.save()
  }

  async resendConfirmationEmail(userId: string): Promise<void> {
    const user = await this.userModel.findById(userId).exec()

    if (!user) {
      throw new HttpException('User not found', HttpStatus.NOT_FOUND)
    }

    if (user.isEmailConfirmed) {
      throw new HttpException('Email already confirmed', HttpStatus.BAD_REQUEST)
    }

    const confirmationToken = uuidv4()
    user.emailConfirmationToken = confirmationToken
    await user.save()

    await this.emailService.sendConfirmationEmail(user.email, confirmationToken)
  }

  async sendResetPasswordCode(email: string): Promise<void> {
    const user = await this.userModel.findOne({ email }).exec()

    if (!user) {
      throw new HttpException('User not found with this email', HttpStatus.NOT_FOUND)
    }

    if (!user.isEmailConfirmed) {
      throw new HttpException('Please confirm your email firstly', HttpStatus.BAD_REQUEST)
    }

    const confirmationCode = Math.floor(1000 + Math.random() * 9000)

    user.resetPasswordCode = +confirmationCode
    await user.save()

    await this.emailService.sendConfirmationResetPasswordCode(user.email, +confirmationCode)
  }

  async confirmResetPasswordCode(dto: ConfirmResetPasswordCodeReq): Promise<void> {
    if (!isValidObjectId(dto.userId)) {
      throw new HttpException('Invalid user ID format', HttpStatus.BAD_REQUEST)
    }

    const user = await this.userModel.findOne({ _id: dto.userId }).exec()

    if (!user) {
      throw new HttpException('User not found with this id', HttpStatus.NOT_FOUND)
    }

    if (user.resetPasswordCode !== dto.code) {
      throw new HttpException('Code doesnt match with confirmed code', HttpStatus.NOT_FOUND)
    }

    if (dto.newPassword !== dto.newPasswordConfirm) {
      throw new HttpException('New passwords do not match', HttpStatus.BAD_REQUEST)
    }

    user.resetPasswordCode = null
    const hashPassword = await bcrypt.hash(dto.newPassword, +process.env.PASSWORD_SALT)
    user.password = hashPassword

    await user.save()
  }

  async refreshTokens(refreshToken: string): Promise<TokensRes> {
    try {
      const payload = this.jwtService.verify(refreshToken, { secret: process.env.JWT_REFRESH_SECRET })

      const user = await this.usersService.getUserById(payload.id)

      if (!user) throw new HttpException('User by id for refresh tokens not found', HttpStatus.NOT_FOUND)

      return this.generateTokens(user)
    } catch (e) {
      throw new UnauthorizedException('Invalid refresh token')
    }
  }

  private async generateTokens(user: UserDocument): Promise<TokensRes> {
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
