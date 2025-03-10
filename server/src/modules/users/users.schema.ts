import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose'
import mongoose, { HydratedDocument, Types } from 'mongoose'

export type UserDocument = HydratedDocument<User>

@Schema()
export class User {
  @Prop({ required: true, unique: true, minlength: 4, maxlength: 24 })
  username: string

  @Prop({ required: true, minlength: 8 })
  password: string

  @Prop({ type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Role' }] })
  roles: Types.ObjectId[]
}

export const UserSchema = SchemaFactory.createForClass(User)
