import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose'
import { ApiProperty } from '@nestjs/swagger'
import mongoose, { HydratedDocument, Types } from 'mongoose'

export type UserDocument = HydratedDocument<User>

@Schema({ timestamps: true })
export class User {
  @ApiProperty({ example: '67cef3dfd47b32d7c0f129b0', description: 'Unique identifier of the user' })
  _id: Types.ObjectId

  @ApiProperty({ example: 'cdidk', description: 'The username of the user' })
  @Prop({ required: true, unique: true, minlength: 4, maxlength: 24 })
  username: string

  @ApiProperty({ example: 'user@example.com', description: 'The email address of the user' })
  @Prop({ required: true, unique: true, match: /^[^\s@]+@[^\s@]+\.[^\s@]+$/ })
  email: string

  @ApiProperty({ example: false, description: 'Whether the email is confirmed' })
  @Prop({ default: false })
  isEmailConfirmed: boolean

  @ApiProperty({ example: 'abc123', description: 'Token for email confirmation', nullable: true })
  @Prop({ default: null })
  emailConfirmationToken: string

  @ApiProperty({ example: 'qwerty123123', description: 'The hashed user password' })
  @Prop({ required: true, minlength: 8 })
  password: string

  @ApiProperty({ example: ['64eabf891c85a90fc8f3e7e5'], description: 'Array of role IDs' })
  @Prop({ type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Role' }] })
  roles: Types.ObjectId[]

  @ApiProperty({ example: 'https://example.com/avatar.jpg', description: 'URL to the user’s avatar image' })
  @Prop({ default: '' })
  avatar: string

  @ApiProperty({ example: 'male', description: 'The gender of the user', enum: ['male', 'female', 'other'] })
  @Prop({ enum: ['male', 'female', 'other'], default: 'other' })
  gender: string

  @ApiProperty({ example: 175, description: 'The height of the user in centimeters' })
  @Prop({ min: 50, max: 300, default: null })
  height: number

  @ApiProperty({ example: 70, description: 'The weight of the user in kilograms' })
  @Prop({ min: 20, max: 500, default: null })
  weight: number

  @ApiProperty({
    example: 'athletic',
    description: 'The body type of the user',
    enum: ['slim', 'average', 'athletic', 'muscular', 'heavy'],
  })
  @Prop({ enum: ['slim', 'average', 'athletic', 'muscular', 'heavy'], default: 'average' })
  bodyType: string

  @ApiProperty({
    example: 'moderate',
    description: 'The activity level of the user',
    enum: ['sedentary', 'light', 'moderate', 'active', 'very active'],
  })
  @Prop({ enum: ['sedentary', 'light', 'moderate', 'active', 'very active'], default: 'moderate' })
  activityLevel: string

  @ApiProperty({ example: '1990-01-01', description: 'The birth date of the user' })
  @Prop({ default: null })
  birthDate: Date

  @ApiProperty({
    example: 'weight_loss',
    description: 'The fitness goal of the user',
    enum: ['weight_loss', 'muscle_gain', 'maintenance', 'endurance'],
  })
  @Prop({ enum: ['weight_loss', 'muscle_gain', 'maintenance', 'endurance'], default: 'maintenance' })
  goalType: string

  @ApiProperty({ example: 65, description: 'The target weight of the user in kilograms' })
  @Prop({ min: 20, max: 500, default: null })
  targetWeight: number
}

export const UserSchema = SchemaFactory.createForClass(User)
