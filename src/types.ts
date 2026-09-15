export type Gender = 'L' | 'P';

export interface StudentProfile {
  id: string;
  name: string;
  nisn: string;
  className: string;
  gender: Gender;
  age: number;
}

export type NutritionalStatus = 
  | 'Gizi Buruk' 
  | 'Gizi Kurang' 
  | 'Gizi Baik (Normal)' 
  | 'Berisiko Gizi Lebih' 
  | 'Gizi Lebih' 
  | 'Obesitas';

export interface BmiScreening {
  id: string;
  studentId: string;
  studentName: string;
  className: string;
  gender: Gender;
  age: number;
  weightKg: number;
  heightCm: number;
  bmiValue: number;
  zScore: number;
  status: NutritionalStatus;
  recommendation: string;
  screenedAt: string;
  followUpNotes?: string;
  followUpStatus?: 'Menunggu' | 'Konseling Diberikan' | 'Rujukan Puskesmas' | 'Selesai';
}

export interface FoodAnalysisResult {
  foodName: string;
  identifiedIngredients: string[];
  dominantNutrients: string[];
  estimatedPortion: string;
  carbohydrateNote: string;
  proteinNote: string;
  fatNote: string;
  vitaminMineralNote: string;
  balanceStatus: string;
  balanceAdvice: string;
  recommendation: string;
  imageUrl?: string;
  analyzedAt: string;
}

export interface DailyNutritionLog {
  id: string;
  studentId: string;
  studentName: string;
  date: string;
  mealType: 'Sarapan' | 'Makan Siang' | 'Makan Malam' | 'Camilan';
  menuDescription: string;
  waterGlasses: number;
  tookIronTablet?: boolean;
  notes?: string;
}
