import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose'
import { ApiProperty } from '@nestjs/swagger'
import { HydratedDocument, Types } from 'mongoose'

export type RoleDocument = HydratedDocument<Role>

@Schema()
export class Role {
  @ApiProperty({ example: '67cef3dfd47b32d7c0f129b0', description: 'Unique identifier of the role' })
  _id: Types.ObjectId

  @ApiProperty({ example: 'ADMIN', description: 'The unique name of the role (e.g., ADMIN, USER)' })
  @Prop({ unique: true })
  value: string

  @ApiProperty({
    example: 'Administrator role with full access to the system.',
    description: 'A brief description of what this role does.',
  })
  @Prop()
  description: string
}

export const RoleSchema = SchemaFactory.createForClass(Role)
