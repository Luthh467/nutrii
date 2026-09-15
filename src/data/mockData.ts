import { StudentProfile, BmiScreening, DailyNutritionLog, FoodAnalysisResult } from '../types';

export const DEMO_STUDENTS: StudentProfile[] = [
  {
    id: 'std-1',
    name: 'Ahmad Fauzi',
    nisn: '0071234561',
    className: 'XI MIPA 2',
    gender: 'L',
    age: 16
  },
  {
    id: 'std-2',
    name: 'Siti Aisyah',
    nisn: '0087654321',
    className: 'X-3',
    gender: 'P',
    age: 15
  },
  {
    id: 'std-3',
    name: 'Rian Hidayat',
    nisn: '0069876543',
    className: 'XII IPS 1',
    gender: 'L',
    age: 17
  }
];

export const INITIAL_SCREENINGS: BmiScreening[] = [
  {
    id: 'scr-1',
    studentId: 'std-1',
    studentName: 'Ahmad Fauzi',
    className: 'XI MIPA 2',
    gender: 'L',
    age: 16,
    weightKg: 55,
    heightCm: 168,
    bmiValue: 19.5,
    zScore: -0.5,
    status: 'Gizi Baik (Normal)',
    recommendation: 'Status gizi normal ideal. Pertahankan pola makan gizi seimbang "Isi Piringku" dan hidrasi yang cukup saat belajar di madrasah.',
    screenedAt: '2025-02-10 08:30',
    followUpStatus: 'Selesai'
  },
  {
    id: 'scr-2',
    studentId: 'std-2',
    studentName: 'Siti Aisyah',
    className: 'X-3',
    gender: 'P',
    age: 15,
    weightKg: 38,
    heightCm: 153,
    bmiValue: 16.2,
    zScore: -2.3,
    status: 'Gizi Kurang',
    recommendation: 'Kategori Gizi Kurang (Kurus). Perlu penambahan asupan kalori & protein (telur, susu, tempe) serta konsumsi rutin Tablet Tambah Darah (TTD) pencegah anemia siswi.',
    screenedAt: '2025-02-12 09:15',
    followUpNotes: 'Diberikan konseling gizi dan suplemen TTD oleh UKS.',
    followUpStatus: 'Konseling Diberikan'
  },
  {
    id: 'scr-3',
    studentId: 'std-3',
    studentName: 'Rian Hidayat',
    className: 'XII IPS 1',
    gender: 'L',
    age: 17,
    weightKg: 82,
    heightCm: 170,
    bmiValue: 28.4,
    zScore: 2.2,
    status: 'Obesitas',
    recommendation: 'Kategori Obesitas. Disarankan pengaturan porsi makan gizi seimbang, perbanyak konsumsi sayur-buah, dan olahraga kardio minimal 30 menit 3x seminggu.',
    screenedAt: '2025-02-14 10:00',
    followUpNotes: 'Dijadwalkan evaluasi berkala lingkar pinggang tiap 2 minggu.',
    followUpStatus: 'Menunggu'
  },
  {
    id: 'scr-4',
    studentId: 'std-4',
    studentName: 'Nurul Hasanah',
    className: 'XI Agama 1',
    gender: 'P',
    age: 16,
    weightKg: 49,
    heightCm: 158,
    bmiValue: 19.6,
    zScore: -0.6,
    status: 'Gizi Baik (Normal)',
    recommendation: 'Status gizi normal sehat. Pertahankan konsumsi sayuran hijau dan kepatuhan konsumsi Tablet Tambah Darah (TTD) setiap pekan.',
    screenedAt: '2025-02-15 11:20',
    followUpStatus: 'Selesai'
  },
  {
    id: 'scr-5',
    studentId: 'std-5',
    studentName: 'Budi Santoso',
    className: 'X-1',
    gender: 'L',
    age: 15,
    weightKg: 42,
    heightCm: 162,
    bmiValue: 16.0,
    zScore: -2.4,
    status: 'Gizi Kurang',
    recommendation: 'Perlu Pemberian Makanan Tambahan (PMT) tinggi protein nabati & hewani di jam istirahat sekolah.',
    screenedAt: '2025-02-16 08:00',
    followUpNotes: 'Daftar penerima PMT sari kacang hijau UKS.',
    followUpStatus: 'Konseling Diberikan'
  },
  {
    id: 'scr-6',
    studentId: 'std-6',
    studentName: 'Dewi Anggraini',
    className: 'X-2',
    gender: 'P',
    age: 15,
    weightKg: 50,
    heightCm: 156,
    bmiValue: 20.5,
    zScore: 0.1,
    status: 'Gizi Baik (Normal)',
    recommendation: 'Pertumbuhan fisik sangat baik dan proporsional.',
    screenedAt: '2025-02-16 08:30',
    followUpStatus: 'Selesai'
  },
  {
    id: 'scr-7',
    studentId: 'std-7',
    studentName: 'Fahri Ramadhan',
    className: 'XI MIPA 1',
    gender: 'L',
    age: 16,
    weightKg: 68,
    heightCm: 165,
    bmiValue: 25.0,
    zScore: 1.6,
    status: 'Gizi Lebih',
    recommendation: 'Berisiko menuju obesitas. Kurangi minuman manis kemasan & gorengan kantin.',
    screenedAt: '2025-02-17 09:00',
    followUpNotes: 'Edukasi batasi jajanan manis kantin madrasah.',
    followUpStatus: 'Konseling Diberikan'
  },
  {
    id: 'scr-8',
    studentId: 'std-8',
    studentName: 'Zahra Muthia',
    className: 'XI IPS 2',
    gender: 'P',
    age: 16,
    weightKg: 35,
    heightCm: 155,
    bmiValue: 14.6,
    zScore: -3.2,
    status: 'Gizi Buruk',
    recommendation: 'Prioritas rujukan ke Puskesmas mitra madrasah untuk skrining klinis malnutrisi & penanganan medis.',
    screenedAt: '2025-02-17 10:15',
    followUpNotes: 'Surat rujukan ke Puskesmas Kecamatan telah diterbitkan UKS.',
    followUpStatus: 'Rujukan Puskesmas'
  },
  {
    id: 'scr-9',
    studentId: 'std-9',
    studentName: 'Ilham Pratama',
    className: 'XII MIPA 1',
    gender: 'L',
    age: 17,
    weightKg: 61,
    heightCm: 172,
    bmiValue: 20.6,
    zScore: 0.0,
    status: 'Gizi Baik (Normal)',
    recommendation: 'Kondisi fisik prima untuk persiapan ujian akhir madrasah.',
    screenedAt: '2025-02-18 08:45',
    followUpStatus: 'Selesai'
  },
  {
    id: 'scr-10',
    studentId: 'std-10',
    studentName: 'Putri Melinda',
    className: 'XII IPS 2',
    gender: 'P',
    age: 17,
    weightKg: 53,
    heightCm: 160,
    bmiValue: 20.7,
    zScore: 0.2,
    status: 'Gizi Baik (Normal)',
    recommendation: 'Status gizi optimal. Teratur minum air putih 8 gelas per hari.',
    screenedAt: '2025-02-18 09:30',
    followUpStatus: 'Selesai'
  },
  {
    id: 'scr-11',
    studentId: 'std-11',
    studentName: 'Rizky Kurniawan',
    className: 'XII MIPA 2',
    gender: 'L',
    age: 17,
    weightKg: 78,
    heightCm: 168,
    bmiValue: 27.6,
    zScore: 2.1,
    status: 'Obesitas',
    recommendation: 'Perlu pendampingan aktif guru penjasorkes dan pembatasan makanan ultra-proses.',
    screenedAt: '2025-02-19 10:00',
    followUpNotes: 'Daftar program kebugaran UKS sepulang sekolah.',
    followUpStatus: 'Konseling Diberikan'
  },
  {
    id: 'scr-12',
    studentId: 'std-12',
    studentName: 'Anisa Rahmawati',
    className: 'X-1',
    gender: 'P',
    age: 15,
    weightKg: 46,
    heightCm: 154,
    bmiValue: 19.4,
    zScore: -0.4,
    status: 'Gizi Baik (Normal)',
    recommendation: 'Status gizi sehat dan aktif.',
    screenedAt: '2025-02-19 11:00',
    followUpStatus: 'Selesai'
  }
];

export const INITIAL_LOGS: DailyNutritionLog[] = [
  {
    id: 'log-1',
    studentId: 'std-1',
    studentName: 'Ahmad Fauzi',
    date: '2025-02-16',
    mealType: 'Sarapan',
    menuDescription: 'Nasi uduk + telur dadar + tempe orek + timun',
    waterGlasses: 2,
    notes: 'Sarapan sebelum berangkat madrasah'
  },
  {
    id: 'log-2',
    studentId: 'std-1',
    studentName: 'Ahmad Fauzi',
    date: '2025-02-16',
    mealType: 'Makan Siang',
    menuDescription: 'Soto ayam lamongan + nasi putih + tauge & kol + jeruk nipis',
    waterGlasses: 3,
    notes: 'Makan siang di kantin madrasah'
  },
  {
    id: 'log-3',
    studentId: 'std-2',
    studentName: 'Siti Aisyah',
    date: '2025-02-16',
    mealType: 'Makan Siang',
    menuDescription: 'Gado-gado lontong + tahu tempe + telur rebus',
    waterGlasses: 3,
    tookIronTablet: true,
    notes: 'Minum Tablet Tambah Darah (TTD) program UKS'
  }
];

export const CANTEEN_PRESETS: { label: string; icon: string; result: FoodAnalysisResult }[] = [
  {
    label: 'Soto Ayam Lamongan',
    icon: '🍲',
    result: {
      foodName: 'Soto Ayam Lamongan dengan Nasi Putih & Telur Rebus',
      identifiedIngredients: ['Nasi putih', 'Daging ayam suwir', 'Telur rebus utuh', 'Tauge segar', 'Kol rajang', 'Kuah kaldu rempah', 'Koya gurih', 'Perasan jeruk nipis'],
      dominantNutrients: ['Karbohidrat Kompleks', 'Protein Hewani Tinggi', 'Vitamin C & Kalium', 'Cairan & Elektrolit'],
      estimatedPortion: '1 mangkok soto + 1 centong nasi putih (~420 kkal)',
      carbohydrateNote: 'Nasi putih (~45g karbohidrat). Menjadi pasokan glukosa primer bagi konsentrasi otak selama jam pelajaran madrasah.',
      proteinNote: 'Ayam kampung suwir dan telur rebus (~20g protein). Membantu pembentukan jaringan otot, antibodi, dan pemulihan stamina remaja.',
      fatNote: 'Kaldu ayam alami & minyak tumis (~10g lemak). Mendukung penyerapan vitamin A, D, E, dan K.',
      vitaminMineralNote: 'Tauge, kol, seledri, dan jeruk nipis kaya Vitamin C, Kalium, dan bioflavonoid pencegah infeksi.',
      balanceStatus: 'Cukup Seimbang',
      balanceAdvice: 'Sudah memiliki komponen energi dan protein hewani yang sangat baik. Tambahkan buah potong segar (pepaya/pisang) di kantin untuk melengkapi serat harian.',
      recommendation: 'Batasi penggunaan garam/kuah terlalu asin jika memiliki riwayat hipertensi keluarga. Minum 1-2 gelas air mineral setelahnya.',
      analyzedAt: 'Baru saja'
    }
  },
  {
    label: 'Nasi Goreng Telur',
    icon: '🍳',
    result: {
      foodName: 'Nasi Goreng Spesial Telur Mata Sapi & Acar Timun',
      identifiedIngredients: ['Nasi goreng bumbu bawang', 'Telur mata sapi', 'Kacang polong & wortel cincang', 'Acar timun', 'Kerupuk'],
      dominantNutrients: ['Karbohidrat Cepat Cerna', 'Protein Hewani', 'Lemak Nabati'],
      estimatedPortion: '1 piring sedang (~480 kkal)',
      carbohydrateNote: 'Nasi putih goreng (~55g karbohidrat). Padat kalori dan cepat diserap tubuh.',
      proteinNote: 'Telur ceplok (~7g protein). Menyediakan asam amino esensial dan kolin pendukung daya ingat.',
      fatNote: 'Minyak goreng kelapa sawit (~16g lemak). Perlu diperhatikan agar tidak berlebih.',
      vitaminMineralNote: 'Wortel dan timun menyediakan sedikit Vitamin A dan serat larut.',
      balanceStatus: 'Tinggi Kalori & Lemak',
      balanceAdvice: 'Komposisi karbohidrat dan lemak dominan. Dianjurkan memperbanyak potongan timun/tomat atau sayur pelengkap untuk menyeimbangkan indeks glikemik.',
      recommendation: 'Hindari menambah terlalu banyak kerupuk berminyak. Imbangi dengan air putih hangat dan aktivitas fisik aktif di sekolah.',
      analyzedAt: 'Baru saja'
    }
  },
  {
    label: 'Gado-Gado Komplit',
    icon: '🥗',
    result: {
      foodName: 'Gado-Gado Siram Kacang dengan Tahu, Tempe & Telur',
      identifiedIngredients: ['Lontong beras', 'Kacang panjang', 'Tauge', 'Kangkung', 'Tahu goreng', 'Tempe goreng', 'Telur rebus', 'Bumbu kacang tanah gurih'],
      dominantNutrients: ['Serat Pangan Tinggi', 'Protein Nabati & Hewani', 'Lemak Tidak Jenuh', 'Zat Besi & Folat'],
      estimatedPortion: '1 piring sedang gado-gado komplit (~390 kkal)',
      carbohydrateNote: 'Lontong beras (~35g karbohidrat). Sumber tenaga yang pas untuk jam siang madrasah.',
      proteinNote: 'Telur rebus, tempe, dan tahu (~18g protein ganda). Sangat baik untuk regenerasi sel darah merah siswi.',
      fatNote: 'Kacang tanah giling (~12g lemak nabati baik). Mengandung asam lemak tak jenuh dan vitamin E.',
      vitaminMineralNote: 'Kangkung, tauge, dan kacang panjang kaya zat besi, kalsium, folat, dan serat alami pencegah sembelit.',
      balanceStatus: 'Sangat Seimbang (Sesuai Isi Piringku)',
      balanceAdvice: 'Pilihan makan siang yang ideal sesuai anjuran gizi seimbang Kemenkes RI. Memenuhi 50% porsi sayuran dan protein.',
      recommendation: 'Pilihan menu terbaik untuk menjaga stamina belajar dan pencegahan anemia remaja putri.',
      analyzedAt: 'Baru saja'
    }
  },
  {
    label: 'Mie Rebus Sayur Telur',
    icon: '🍜',
    result: {
      foodName: 'Mie Rebus Kuah Kaldu dengan Sawi Hijau & Telur',
      identifiedIngredients: ['Mie kuning terigu', 'Telur kocok/rebus', 'Sawi hijau (caisim) melimpah', 'Bawang goreng', 'Kuah kaldu'],
      dominantNutrients: ['Karbohidrat Olahan', 'Protein', 'Natrium/Garam Kuah'],
      estimatedPortion: '1 mangkok mie rebus sayur (~380 kkal)',
      carbohydrateNote: 'Tepung terigu mie (~48g karbohidrat). Memberi rasa kenyang cepat.',
      proteinNote: 'Telur (~7g protein). Sumber nutrisi penting.',
      fatNote: 'Minyak bumbu kaldu (~11g lemak).',
      vitaminMineralNote: 'Sawi hijau menambahkan Vitamin K, Vitamin A, dan serat.',
      balanceStatus: 'Cukup Seimbang dengan Catatan Natrium',
      balanceAdvice: 'Penambahan sawi hijau dan telur menaikkan nilai gizi mie secara drastis. Jangan menghabiskan seluruh kuah asin untuk membatasi asupan natrium harian.',
      recommendation: 'Bagus untuk menu hangat cuaca dingin, cukupi minum air putih 2 gelas sesudah makan.',
      analyzedAt: 'Baru saja'
    }
  }
];
