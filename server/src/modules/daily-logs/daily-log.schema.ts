import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose'
import { ApiProperty } from '@nestjs/swagger'
import mongoose, { HydratedDocument, Types } from 'mongoose'

export type DailyLogDocument = HydratedDocument<DailyLog>

@Schema({ timestamps: true })
export class DailyLog {
  @ApiProperty({ example: '67cef3dfd47b32d7c0f129b0', description: 'Unique identifier of the user' })
  _id: Types.ObjectId

  @ApiProperty({ example: '64eabf891c85a90fc8f3e7e5', description: 'Field with user id' })
  @Prop({ type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true })
  userId: Types.ObjectId

  @ApiProperty({ example: '1990-01-01', description: 'The date of daily log' })
  @Prop({ default: () => new Date() })
  date: Date

  @ApiProperty({ example: 300, description: 'The number of burned calories in this day' })
  @Prop({ default: 0 })
  burnedCalories: number

  @ApiProperty({ example: 300, description: 'The number of calories inluded burned calories' })
  @Prop({ default: 0 })
  totalCalories: number

  @ApiProperty({ example: ['64eabf891c85a90fc8f3e7e5'], description: 'Array of meals IDs' })
  @Prop({ type: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Meal' }], default: [] })
  meals: Types.ObjectId[]

  @ApiProperty({
    example: [
      {
        _id: '1235136366ksdj3562',
        activity: '64eabf891c85a90fc8f3e7e5',
        totalMinutes: 30,
        burnedCalories: 400,
        activityName: 'Running',
      },
    ],
    description: 'Array of activities with their IDs and total minutes',
  })
  @Prop({
    type: [
      {
        activity: { type: mongoose.Schema.Types.ObjectId, ref: 'Activity' },
        totalMinutes: Number,
        burnedCalories: Number,
        activityName: String,
      },
    ],
    default: [],
  })
  activities: {
    _id?: Types.ObjectId
    activity: Types.ObjectId
    totalMinutes: number
    burnedCalories: number
    activityName: string
  }[]

  @ApiProperty({
    example: { current: 1500, target: 2000 },
    description: 'Current and target calories for the day',
  })
  @Prop({
    type: { current: { type: Number, default: 0 }, target: { type: Number, default: 0 } },
    default: { current: 0, target: 0 },
  })
  calories: { current: number; target: number }

  @ApiProperty({
    example: { current: 50, target: 80 },
    description: 'Current and target protein intake for the day',
  })
  @Prop({
    type: { current: { type: Number, default: 0 }, target: { type: Number, default: 0 } },
    default: { current: 0, target: 0 },
  })
  protein: { current: number; target: number }

  @ApiProperty({
    example: { current: 30, target: 60 },
    description: 'Current and target fat intake for the day',
  })
  @Prop({
    type: { current: { type: Number, default: 0 }, target: { type: Number, default: 0 } },
    default: { current: 0, target: 0 },
  })
  fat: { current: number; target: number }

  @ApiProperty({
    example: { current: 150, target: 250 },
    description: 'Current and target carbohydrate intake for the day',
  })
  @Prop({
    type: { current: { type: Number, default: 0 }, target: { type: Number, default: 0 } },
    default: { current: 0, target: 0 },
  })
  carbs: { current: number; target: number }

  @ApiProperty({
    example: { current: 1.5, target: 2.0 },
    description: 'Current and target water intake for the day (in liters)',
  })
  @Prop({
    type: { current: { type: Number, default: 0 }, target: { type: Number, default: 0 } },
    default: { current: 0, target: 0 },
  })
  water: { current: number; target: number }

  @ApiProperty({
    example: { current: 70.5, target: 65.0 },
    description: 'Current and target weight for the day (in kg)',
  })
  @Prop({
    type: { current: { type: Number, default: 0 }, target: { type: Number, default: 0 } },
    default: { current: 0, target: 0 },
  })
  weight: { current: number; target: number }
}

export const DailyLogSchema = SchemaFactory.createForClass(DailyLog)
