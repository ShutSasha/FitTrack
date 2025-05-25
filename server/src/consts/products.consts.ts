export const ProductTypes = [
  'fruit',
  'vegetable',
  'meat',
  'fish',
  'drink',
  'bakery',
  'pastry',
  'milk and milk products',
  'sweets',
  'sauces',
  'water',
  'grain',
] as const

export type ProductType = (typeof ProductTypes)[number]
