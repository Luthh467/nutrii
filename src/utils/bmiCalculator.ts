import { Gender, NutritionalStatus } from '../types';

// Referensi Median & SD IMT/U WHO 2007 / Permenkes RI No. 2 Tahun 2020 (Usia 10-18 Tahun)
interface ReferenceBmi {
  median: number;
  sdPlus1: number;
  sdMinus1: number;
  sdPlus2: number;
  sdMinus2: number;
  sdMinus3: number;
}

const boyReference: Record<number, ReferenceBmi> = {
  10: { median: 16.4, sdMinus3: 12.4, sdMinus2: 13.5, sdMinus1: 14.8, sdPlus1: 18.5, sdPlus2: 21.4 },
  11: { median: 16.9, sdMinus3: 12.7, sdMinus2: 13.9, sdMinus1: 15.3, sdPlus1: 19.2, sdPlus2: 22.5 },
  12: { median: 17.5, sdMinus3: 13.2, sdMinus2: 14.4, sdMinus1: 15.9, sdPlus1: 20.0, sdPlus2: 23.6 },
  13: { median: 18.2, sdMinus3: 13.7, sdMinus2: 14.9, sdMinus1: 16.5, sdPlus1: 20.8, sdPlus2: 24.8 },
  14: { median: 19.0, sdMinus3: 14.3, sdMinus2: 15.5, sdMinus1: 17.2, sdPlus1: 21.8, sdPlus2: 25.9 },
  15: { median: 19.8, sdMinus3: 14.9, sdMinus2: 16.2, sdMinus1: 17.9, sdPlus1: 22.7, sdPlus2: 27.0 },
  16: { median: 20.5, sdMinus3: 15.4, sdMinus2: 16.7, sdMinus1: 18.5, sdPlus1: 23.5, sdPlus2: 27.9 },
  17: { median: 21.1, sdMinus3: 15.8, sdMinus2: 17.2, sdMinus1: 19.0, sdPlus1: 24.3, sdPlus2: 28.6 },
  18: { median: 21.6, sdMinus3: 16.1, sdMinus2: 17.5, sdMinus1: 19.4, sdPlus1: 24.9, sdPlus2: 29.2 }
};

const girlReference: Record<number, ReferenceBmi> = {
  10: { median: 16.6, sdMinus3: 12.4, sdMinus2: 13.5, sdMinus1: 14.9, sdPlus1: 19.0, sdPlus2: 22.6 },
  11: { median: 17.2, sdMinus3: 12.7, sdMinus2: 13.9, sdMinus1: 15.4, sdPlus1: 19.9, sdPlus2: 23.7 },
  12: { median: 18.0, sdMinus3: 13.2, sdMinus2: 14.4, sdMinus1: 16.0, sdPlus1: 20.8, sdPlus2: 25.0 },
  13: { median: 18.8, sdMinus3: 13.7, sdMinus2: 15.0, sdMinus1: 16.7, sdPlus1: 21.8, sdPlus2: 26.2 },
  14: { median: 19.6, sdMinus3: 14.3, sdMinus2: 15.6, sdMinus1: 17.4, sdPlus1: 22.7, sdPlus2: 27.3 },
  15: { median: 20.2, sdMinus3: 14.7, sdMinus2: 16.0, sdMinus1: 17.9, sdPlus1: 23.5, sdPlus2: 28.2 },
  16: { median: 20.7, sdMinus3: 15.1, sdMinus2: 16.4, sdMinus1: 18.3, sdPlus1: 24.1, sdPlus2: 28.9 },
  17: { median: 21.0, sdMinus3: 15.3, sdMinus2: 16.6, sdMinus1: 18.5, sdPlus1: 24.5, sdPlus2: 29.3 },
  18: { median: 21.3, sdMinus3: 15.4, sdMinus2: 16.8, sdMinus1: 18.7, sdPlus1: 24.8, sdPlus2: 29.5 }
};

export function calculateBmiZScore(
  weightKg: number,
  heightCm: number,
  gender: Gender,
  age: number
): { bmiValue: number; zScore: number; status: NutritionalStatus; recommendation: string } {
  const heightM = heightCm / 100;
  const bmiValue = Number((weightKg / (heightM * heightM)).toFixed(1));

  const safeAge = Math.min(18, Math.max(10, Math.round(age)));
  const refTable = gender === 'L' ? boyReference : girlReference;
  const ref = refTable[safeAge] || refTable[16];

  let zScore = 0;
  if (bmiValue >= ref.median) {
    const sdDiff = ref.sdPlus1 - ref.median;
    zScore = (bmiValue - ref.median) / (sdDiff || 1);
  } else {
    const sdDiff = ref.median - ref.sdMinus1;
    zScore = (bmiValue - ref.median) / (sdDiff || 1);
  }
  zScore = Number(zScore.toFixed(2));

  let status: NutritionalStatus = 'Gizi Baik (Normal)';
  let recommendation = '';

  if (zScore < -3) {
    status = 'Gizi Buruk';
    recommendation = 'Kategori Gizi Buruk (Sangat Kurus). Segera lakukan rujukan ke Petugas UKS dan Puskesmas terdekat untuk evaluasi medis, asupan kalori padat gizi, dan penanganan defisiensi zat gizi mikro.';
  } else if (zScore < -2) {
    status = 'Gizi Kurang';
    recommendation = 'Kategori Gizi Kurang (Kurus). Perlu penambahan asupan energi dan protein (telur, ikan, tempe, susu). Konsultasikan dengan Petugas UKS untuk program PMT (Pemberian Makanan Tambahan) madrasah.';
  } else if (zScore <= 1) {
    status = 'Gizi Baik (Normal)';
    recommendation = 'Selamat! Status gizi berada dalam rentang normal sehat (Permenkes RI No. 2/2020). Pertahankan pola makan gizi seimbang "Isi Piringku", minum air 8 gelas sehari, dan aktif berolahraga 30 menit per hari.';
  } else if (zScore <= 2) {
    status = 'Berisiko Gizi Lebih';
    recommendation = 'Kategori Berisiko Gizi Lebih. Batasi minuman manis kemasan, gorengan, dan makanan cepat saji. Tingkatkan porsi sayur segar dan aktivitas fisik jalan santai atau senam madrasah.';
  } else {
    status = 'Obesitas';
    recommendation = 'Kategori Obesitas. Disarankan konsultasi dengan Petugas UKS/Tenaga Kesehatan untuk perencanaan defisit kalori aman, peningkatan aktivitas fisik aerobik harian, dan pencegahan risiko metabolik sejak remaja.';
  }

  return { bmiValue, zScore, status, recommendation };
}
