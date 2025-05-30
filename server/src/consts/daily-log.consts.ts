export const activityFactors: Record<string, number> = {
  sedentary: 1.04,
  light: 1.11,
  moderate: 1.23,
  active: 1.38,
  very_active: 1.57,
}

export const goalModifiers: Record<string, number> = {
  weight_loss: 0.8,
  muscle_gain: 1.15,
  maintenance: 1.0,
  endurance: 1.05,
}

export const bodyTypeModifiers: Record<string, number> = {
  slim: 1.1,
  average: 1,
  athletic: 1.02,
  muscular: 1.12,
  heavy: 0.9,
}

export const proteinPerKg: Record<string, number> = {
  weight_loss: 2.0,
  muscle_gain: 2.2,
  maintenance: 1.8,
  endurance: 1.6,
}
