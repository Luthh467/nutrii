import React, { useState } from 'react';
import { Plus, Droplet, Calendar, Heart, Check } from 'lucide-react';
import { StudentProfile, DailyNutritionLog } from '../types';

interface DailyLogViewProps {
  student: StudentProfile;
  logs: DailyNutritionLog[];
  onAddLog: (log: DailyNutritionLog) => void;
}

export const DailyLogView: React.FC<DailyLogViewProps> = ({ student, logs, onAddLog }) => {
  const [mealType, setMealType] = useState<DailyNutritionLog['mealType']>('Sarapan');
  const [menuDescription, setMenuDescription] = useState('');
  const [waterGlasses, setWaterGlasses] = useState(3);
  const [tookIronTablet, setTookIronTablet] = useState(false);
  const [notes, setNotes] = useState('');
  const [showSuccess, setShowSuccess] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!menuDescription.trim()) return;

    const newLog: DailyNutritionLog = {
      id: `log-${Date.now()}`,
      studentId: student.id,
      studentName: student.name,
      date: new Date().toISOString().split('T')[0],
      mealType,
      menuDescription: menuDescription.trim(),
      waterGlasses,
      tookIronTablet: student.gender === 'P' ? tookIronTablet : undefined,
      notes: notes.trim() || undefined
    };

    onAddLog(newLog);
    setMenuDescription('');
    setNotes('');
    setShowSuccess(true);
    setTimeout(() => setShowSuccess(false), 3000);
  };

  const studentLogs = logs.filter((l) => l.studentId === student.id || l.studentName === student.name);

  return (
    <div className="max-w-4xl mx-auto px-4 py-6 space-y-6">
      <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
        <h2 className="text-xl font-extrabold text-slate-800 mb-1">
          Catatan Harian Gizi & Hidrasi
        </h2>
        <p className="text-xs text-slate-500 mb-6">
          Catat makanan yang kamu konsumsi hari ini untuk membantu petugas UKS memantau pola asupanmu.
        </p>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 sm:grid-cols-4 gap-2">
            {(['Sarapan', 'Makan Siang', 'Makan Malam', 'Camilan'] as const).map((type) => (
              <button
                key={type}
                type="button"
                onClick={() => setMealType(type)}
                className={`py-2 px-3 rounded-xl text-xs font-bold transition border ${
                  mealType === type
                    ? 'bg-[#1B5E20] text-white border-[#1B5E20] shadow-sm'
                    : 'bg-slate-50 text-slate-700 border-slate-200 hover:bg-slate-100'
                }`}
              >
                {type}
              </button>
            ))}
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Menu Makanan & Minuman
            </label>
            <input
              type="text"
              value={menuDescription}
              onChange={(e) => setMenuDescription(e.target.value)}
              placeholder="Contoh: Nasi uduk, telur dadar, tempe orek, dan teh manis hangat"
              className="w-full px-3 py-2 text-xs sm:text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
              required
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {/* Water intake */}
            <div className="bg-blue-50/60 border border-blue-200 rounded-xl p-3.5">
              <label className="block text-xs font-bold text-blue-900 mb-2 flex items-center space-x-1.5">
                <Droplet className="w-4 h-4 text-blue-600" />
                <span>Konsumsi Air Putih (Target 8 Gelas)</span>
              </label>
              <div className="flex items-center space-x-2">
                {[1, 2, 3, 4, 5, 6, 7, 8].map((g) => (
                  <button
                    key={g}
                    type="button"
                    onClick={() => setWaterGlasses(g)}
                    className={`w-7 h-7 rounded-lg text-xs font-bold transition ${
                      waterGlasses >= g
                        ? 'bg-blue-600 text-white shadow-2xs'
                        : 'bg-white text-blue-800 border border-blue-200'
                    }`}
                  >
                    {g}
                  </button>
                ))}
              </div>
            </div>

            {/* Iron tablet for female students */}
            {student.gender === 'P' && (
              <div className="bg-rose-50/60 border border-rose-200 rounded-xl p-3.5 flex items-center justify-between">
                <div>
                  <span className="text-xs font-bold text-rose-900 block flex items-center space-x-1">
                    <Heart className="w-3.5 h-3.5 text-rose-600" />
                    <span>Tablet Tambah Darah (TTD)</span>
                  </span>
                  <span className="text-[11px] text-rose-700">Program UKS cegah anemia remaja putri</span>
                </div>
                <button
                  type="button"
                  onClick={() => setTookIronTablet(!tookIronTablet)}
                  className={`w-6 h-6 rounded-lg border flex items-center justify-center transition ${
                    tookIronTablet
                      ? 'bg-rose-600 text-white border-rose-700'
                      : 'bg-white border-slate-300 text-transparent'
                  }`}
                >
                  <Check className="w-4 h-4 stroke-[3]" />
                </button>
              </div>
            )}
          </div>

          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Catatan Tambahan (Opsional)
            </label>
            <input
              type="text"
              value={notes}
              onChange={(e) => setNotes(e.target.value)}
              placeholder="Contoh: Merasa lebih bugar, tidak pusing saat pelajaran olahraga"
              className="w-full px-3 py-2 text-xs sm:text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
            />
          </div>

          {showSuccess && (
            <div className="bg-emerald-100 text-emerald-800 text-xs font-bold p-2.5 rounded-xl text-center">
              ✓ Log gizi harian berhasil disimpan!
            </div>
          )}

          <button
            type="submit"
            className="w-full bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-extrabold py-2.5 px-4 rounded-xl shadow transition flex items-center justify-center space-x-1.5"
          >
            <Plus className="w-4 h-4" />
            <span>Simpan Log Gizi Harian</span>
          </button>
        </form>
      </div>

      {/* Log History */}
      <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
        <h3 className="font-extrabold text-base text-slate-800 mb-4 flex items-center space-x-2">
          <Calendar className="w-4 h-4 text-emerald-700" />
          <span>Riwayat Log Makananmu</span>
        </h3>

        {studentLogs.length === 0 ? (
          <p className="text-xs text-slate-400 text-center py-6">
            Belum ada catatan log gizi. Mulai catat makanan pertamamu di atas!
          </p>
        ) : (
          <div className="space-y-2.5">
            {studentLogs.map((log) => (
              <div
                key={log.id}
                className="p-3 rounded-xl border border-slate-200 hover:bg-slate-50 flex items-start justify-between gap-3 text-xs"
              >
                <div>
                  <div className="flex items-center space-x-2">
                    <span className="font-bold text-emerald-800 bg-emerald-50 px-2 py-0.5 rounded text-[10px]">
                      {log.mealType}
                    </span>
                    <span className="text-slate-400 text-[10px]">{log.date}</span>
                  </div>
                  <div className="font-semibold text-slate-800 mt-1">{log.menuDescription}</div>
                  {log.notes && <div className="text-slate-500 text-[11px] mt-0.5 italic">{log.notes}</div>}
                </div>

                <div className="text-right flex-shrink-0">
                  <span className="text-[11px] text-blue-700 bg-blue-50 px-2 py-0.5 rounded font-bold block">
                    💧 {log.waterGlasses} Gelas
                  </span>
                  {log.tookIronTablet && (
                    <span className="text-[10px] text-rose-700 bg-rose-50 px-1.5 py-0.5 rounded font-bold mt-1 inline-block">
                      💊 Minum TTD
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};
