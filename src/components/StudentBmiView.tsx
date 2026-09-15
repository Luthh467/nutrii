import React, { useState } from 'react';
import { Scale, Activity, Info, CheckCircle, ArrowRight } from 'lucide-react';
import { StudentProfile, BmiScreening, NutritionalStatus } from '../types';
import { calculateBmiZScore } from '../utils/bmiCalculator';

interface StudentBmiViewProps {
  student: StudentProfile;
  onSaveScreening: (screening: BmiScreening) => void;
  recentScreenings: BmiScreening[];
  onNavigateToCamera: () => void;
}

export const StudentBmiView: React.FC<StudentBmiViewProps> = ({
  student,
  onSaveScreening,
  recentScreenings,
  onNavigateToCamera
}) => {
  const [weight, setWeight] = useState<string>('54');
  const [height, setHeight] = useState<string>('165');
  const [currentResult, setCurrentResult] = useState<{
    bmiValue: number;
    zScore: number;
    status: NutritionalStatus;
    recommendation: string;
  } | null>(null);
  const [savedSuccess, setSavedSuccess] = useState(false);

  const handleCalculate = (e: React.FormEvent) => {
    e.preventDefault();
    const w = parseFloat(weight);
    const h = parseFloat(height);
    if (!w || !h || w <= 0 || h <= 0) return;

    const res = calculateBmiZScore(w, h, student.gender, student.age);
    setCurrentResult(res);
    setSavedSuccess(false);
  };

  const handleSave = () => {
    if (!currentResult) return;

    const screening: BmiScreening = {
      id: `scr-${Date.now()}`,
      studentId: student.id,
      studentName: student.name,
      className: student.className,
      gender: student.gender,
      age: student.age,
      weightKg: parseFloat(weight),
      heightCm: parseFloat(height),
      bmiValue: currentResult.bmiValue,
      zScore: currentResult.zScore,
      status: currentResult.status,
      recommendation: currentResult.recommendation,
      screenedAt: new Date().toLocaleString('id-ID', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      }),
      followUpStatus: 'Menunggu'
    };

    onSaveScreening(screening);
    setSavedSuccess(true);
  };

  const getStatusBadgeColor = (status: NutritionalStatus) => {
    switch (status) {
      case 'Gizi Buruk': return 'bg-red-600 text-white';
      case 'Gizi Kurang': return 'bg-amber-500 text-white';
      case 'Gizi Baik (Normal)': return 'bg-emerald-600 text-white';
      case 'Berisiko Gizi Lebih': return 'bg-yellow-500 text-slate-900';
      case 'Gizi Lebih': return 'bg-orange-500 text-white';
      case 'Obesitas': return 'bg-red-700 text-white';
      default: return 'bg-slate-600 text-white';
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-6 space-y-6">
      {/* Intro Card */}
      <div className="bg-gradient-to-r from-emerald-800 to-emerald-900 text-white rounded-2xl p-6 shadow-md flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-1.5 bg-white/20 text-emerald-100 text-xs px-2.5 py-1 rounded-full mb-2 backdrop-blur-sm font-semibold">
            <Activity className="w-3.5 h-3.5" />
            <span>Standar Permenkes RI No. 2 Tahun 2020</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black tracking-tight">
            Skrining Status Gizi & IMT/U Remaja
          </h2>
          <p className="text-xs sm:text-sm text-emerald-200 mt-1 max-w-xl">
            Halo, <span className="font-bold text-white">{student.name}</span> ({student.className})! Masukkan berat badan dan tinggi badanmu untuk mengetahui indeks masa tubuh dan kategori gizi klinis.
          </p>
        </div>

        <button
          onClick={onNavigateToCamera}
          className="bg-amber-500 hover:bg-amber-600 text-slate-950 font-black px-4 py-2.5 rounded-xl text-xs sm:text-sm shadow-md flex items-center space-x-2 transition flex-shrink-0"
        >
          <span>📷 Analisis Kamera Makanan</span>
          <ArrowRight className="w-4 h-4" />
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Calculator Form */}
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
          <h3 className="font-extrabold text-base text-slate-800 flex items-center space-x-2 mb-4">
            <Scale className="w-5 h-5 text-emerald-700" />
            <span>Form Pengukuran Siswa</span>
          </h3>

          <form onSubmit={handleCalculate} className="space-y-4">
            <div className="grid grid-cols-2 gap-3 bg-slate-50 p-3 rounded-xl border border-slate-200 text-xs text-slate-600 mb-2">
              <div>
                <span className="text-slate-500 block">Jenis Kelamin:</span>
                <span className="font-bold text-slate-800">{student.gender === 'L' ? 'Laki-laki' : 'Perempuan'}</span>
              </div>
              <div>
                <span className="text-slate-500 block">Usia Siswa:</span>
                <span className="font-bold text-slate-800">{student.age} Tahun</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Berat Badan (kg)
              </label>
              <div className="relative">
                <input
                  type="number"
                  step="0.1"
                  min="20"
                  max="160"
                  value={weight}
                  onChange={(e) => setWeight(e.target.value)}
                  className="w-full px-3 py-2 text-base font-bold text-slate-800 border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                  placeholder="54"
                  required
                />
                <span className="absolute right-3 top-2.5 text-xs text-slate-400 font-semibold">kg</span>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-slate-700 mb-1">
                Tinggi Badan (cm)
              </label>
              <div className="relative">
                <input
                  type="number"
                  step="0.1"
                  min="90"
                  max="210"
                  value={height}
                  onChange={(e) => setHeight(e.target.value)}
                  className="w-full px-3 py-2 text-base font-bold text-slate-800 border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
                  placeholder="165"
                  required
                />
                <span className="absolute right-3 top-2.5 text-xs text-slate-400 font-semibold">cm</span>
              </div>
            </div>

            <button
              type="submit"
              className="w-full bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-extrabold py-3 px-4 rounded-xl shadow transition"
            >
              Hitung IMT & Z-Score
            </button>
          </form>
        </div>

        {/* Calculation Result */}
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200 flex flex-col justify-between">
          <div>
            <h3 className="font-extrabold text-base text-slate-800 flex items-center space-x-2 mb-4">
              <Activity className="w-5 h-5 text-emerald-700" />
              <span>Hasil Evaluasi Gizi</span>
            </h3>

            {currentResult ? (
              <div className="space-y-4">
                <div className="flex items-center justify-between bg-slate-50 p-4 rounded-xl border border-slate-200">
                  <div>
                    <span className="text-xs text-slate-500 font-medium">Nilai IMT:</span>
                    <div className="text-3xl font-black text-slate-900">
                      {currentResult.bmiValue} <span className="text-xs font-normal text-slate-500">kg/m²</span>
                    </div>
                  </div>
                  <div className="text-right">
                    <span className="text-xs text-slate-500 font-medium">Z-Score IMT/U:</span>
                    <div className="text-xl font-bold text-slate-700">
                      {currentResult.zScore > 0 ? `+${currentResult.zScore}` : currentResult.zScore} SD
                    </div>
                  </div>
                </div>

                <div>
                  <span className="text-xs text-slate-500 block mb-1">Kategori Status Gizi (Kemenkes):</span>
                  <span className={`inline-block px-3 py-1.5 rounded-lg text-sm font-black shadow-sm ${getStatusBadgeColor(currentResult.status)}`}>
                    {currentResult.status}
                  </span>
                </div>

                <div className="bg-emerald-50/70 border border-emerald-200 rounded-xl p-3.5 text-xs text-emerald-950">
                  <span className="font-bold flex items-center space-x-1 text-emerald-900 mb-1">
                    <Info className="w-4 h-4 text-emerald-700" />
                    <span>Rekomendasi Klinis & Edukasi:</span>
                  </span>
                  <p className="leading-relaxed">{currentResult.recommendation}</p>
                </div>
              </div>
            ) : (
              <div className="h-48 flex flex-col items-center justify-center text-center text-slate-400">
                <Scale className="w-12 h-12 stroke-[1.5] mb-2 text-slate-300" />
                <p className="text-xs">
                  Masukkan berat dan tinggi badan lalu klik "Hitung IMT & Z-Score" untuk melihat hasil.
                </p>
              </div>
            )}
          </div>

          {currentResult && (
            <div className="pt-4 border-t border-slate-100 mt-4">
              {savedSuccess ? (
                <div className="bg-emerald-100 text-emerald-800 text-xs font-bold p-2.5 rounded-xl flex items-center justify-center space-x-1.5">
                  <CheckCircle className="w-4 h-4 text-emerald-600" />
                  <span>Data skrining berhasil tersimpan ke sistem UKS!</span>
                </div>
              ) : (
                <button
                  type="button"
                  onClick={handleSave}
                  className="w-full bg-emerald-600 hover:bg-emerald-700 text-white font-bold py-2.5 px-4 rounded-xl text-xs shadow transition flex items-center justify-center space-x-1.5"
                >
                  <span>Simpan ke Riwayat Skrining UKS</span>
                </button>
              )}
            </div>
          )}
        </div>
      </div>

      {/* Recent Screenings Table */}
      <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
        <h3 className="font-extrabold text-base text-slate-800 mb-3">
          Riwayat Skrining Gizi Siswa Madrasah
        </h3>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs text-slate-600">
            <thead className="bg-slate-50 text-slate-700 font-bold uppercase text-[10px] border-b border-slate-200">
              <tr>
                <th className="py-2.5 px-3">Tanggal</th>
                <th className="py-2.5 px-3">Siswa</th>
                <th className="py-2.5 px-3">BB / TB</th>
                <th className="py-2.5 px-3">IMT (Z-Score)</th>
                <th className="py-2.5 px-3">Status Gizi</th>
                <th className="py-2.5 px-3">Tindak Lanjut UKS</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {recentScreenings.map((scr) => (
                <tr key={scr.id} className="hover:bg-slate-50/50">
                  <td className="py-2.5 px-3 text-slate-500 whitespace-nowrap">{scr.screenedAt}</td>
                  <td className="py-2.5 px-3 font-semibold text-slate-800 whitespace-nowrap">
                    {scr.studentName} <span className="text-[10px] text-slate-400">({scr.className})</span>
                  </td>
                  <td className="py-2.5 px-3 whitespace-nowrap">{scr.weightKg} kg / {scr.heightCm} cm</td>
                  <td className="py-2.5 px-3 font-bold text-slate-800 whitespace-nowrap">
                    {scr.bmiValue} ({scr.zScore > 0 ? `+${scr.zScore}` : scr.zScore} SD)
                  </td>
                  <td className="py-2.5 px-3 whitespace-nowrap">
                    <span className={`px-2 py-0.5 rounded text-[10px] font-bold ${getStatusBadgeColor(scr.status)}`}>
                      {scr.status}
                    </span>
                  </td>
                  <td className="py-2.5 px-3 whitespace-nowrap">
                    <span className="text-[10px] bg-slate-100 text-slate-700 px-2 py-0.5 rounded font-medium">
                      {scr.followUpStatus || 'Menunggu'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
