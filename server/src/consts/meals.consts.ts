export const MealTypes = ['Breakfast', 'Lunch', 'Dinner', 'Snack1', 'Snack2', 'Snack3'] as const

export type MealType = (typeof MealTypes)[number]
