import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose'
import { ApiProperty } from '@nestjs/swagger'
import { HydratedDocument, Types } from 'mongoose'

export type ActivityDocument = HydratedDocument<Activity>

@Schema({ timestamps: true })
export class Activity {
  @ApiProperty({ example: '67cef3dfd47b32d7c0f129b0', description: 'Unique identifier of the activity' })
  _id: Types.ObjectId

  @ApiProperty({ example: 'Running', description: 'Name of the activity (e.g., Running, Swimming, Cycling)' })
  @Prop({ type: String, required: true })
  name: string

  @ApiProperty({ example: 10, description: 'Calories burned per minute during the activity' })
  @Prop({ type: Number, required: true, min: 0 })
  caloriesPerMin: number
}

export const ActivitySchema = SchemaFactory.createForClass(Activity)
