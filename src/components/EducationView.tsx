import React from 'react';
import { BookOpen, Utensils, Heart, Activity, CheckCircle2 } from 'lucide-react';

export const EducationView: React.FC = () => {
  return (
    <div className="max-w-4xl mx-auto px-4 py-6 space-y-6">
      <div className="bg-gradient-to-r from-emerald-800 to-emerald-900 text-white rounded-2xl p-6 shadow-md">
        <div className="inline-flex items-center space-x-1.5 bg-white/20 text-emerald-100 text-xs px-2.5 py-1 rounded-full mb-2 font-bold backdrop-blur-sm">
          <BookOpen className="w-3.5 h-3.5" />
          <span>Pedoman Gizi Seimbang Kemenkes RI</span>
        </div>
        <h2 className="text-xl sm:text-2xl font-black">
          Edukasi Gizi & Pencegahan Anemia Remaja
        </h2>
        <p className="text-xs sm:text-sm text-emerald-200 mt-1 max-w-2xl">
          Panduan praktis menjaga daya tahan tubuh, konsentrasi belajar, dan pemenuhan gizi remaja madrasah.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Isi Piringku */}
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200 space-y-3">
          <div className="flex items-center space-x-2 text-emerald-800 font-extrabold text-base">
            <Utensils className="w-5 h-5 text-emerald-600" />
            <span>Porsi "Isi Piringku" Sekali Makan</span>
          </div>
          <p className="text-xs text-slate-600 leading-relaxed">
            Dalam satu piring makan, bagi menjadi 4 bagian ideal sesuai anjuran Kementerian Kesehatan RI:
          </p>
          <ul className="space-y-2 text-xs text-slate-700">
            <li className="flex items-start space-x-2">
              <span className="w-2 h-2 rounded-full bg-amber-500 mt-1.5 flex-shrink-0"></span>
              <span><strong>2/3 dari 1/2 piring:</strong> Makanan Pokok (Nasi, singkong, jagung, ubi, atau mie).</span>
            </li>
            <li className="flex items-start space-x-2">
              <span className="w-2 h-2 rounded-full bg-emerald-500 mt-1.5 flex-shrink-0"></span>
              <span><strong>2/3 dari 1/2 piring:</strong> Sayuran (Bayam, kangkung, sawi, wortel, buncis, brokoli).</span>
            </li>
            <li className="flex items-start space-x-2">
              <span className="w-2 h-2 rounded-full bg-blue-500 mt-1.5 flex-shrink-0"></span>
              <span><strong>1/3 dari 1/2 piring:</strong> Lauk Pauk (Ayam, ikan, telur, tahu, tempe).</span>
            </li>
            <li className="flex items-start space-x-2">
              <span className="w-2 h-2 rounded-full bg-red-500 mt-1.5 flex-shrink-0"></span>
              <span><strong>1/3 dari 1/2 piring:</strong> Buah-buahan (Pepaya, pisang, jeruk, semangka).</span>
            </li>
          </ul>
        </div>

        {/* Pencegahan Anemia */}
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200 space-y-3">
          <div className="flex items-center space-x-2 text-rose-800 font-extrabold text-base">
            <Heart className="w-5 h-5 text-rose-600" />
            <span>Pencegahan Anemia Remaja Putri</span>
          </div>
          <p className="text-xs text-slate-600 leading-relaxed">
            Remaja putri rentan mengalami anemia akibat menstruasi rutin dan percepatan pertumbuhan (growth spurt):
          </p>
          <div className="bg-rose-50 border border-rose-200 rounded-xl p-3 text-xs space-y-2 text-rose-950">
            <div className="flex items-center space-x-1.5 font-bold text-rose-900">
              <CheckCircle2 className="w-4 h-4 text-rose-600" />
              <span>Program Tablet Tambah Darah (TTD):</span>
            </div>
            <p className="leading-relaxed text-slate-700">
              Minum 1 Tablet Tambah Darah setiap pekan (misalnya setiap hari Jumat di madrasah) secara teratur.
            </p>
            <div className="flex items-center space-x-1.5 font-bold text-rose-900 pt-1">
              <CheckCircle2 className="w-4 h-4 text-rose-600" />
              <span>Tips Penyerapan Optimal:</span>
            </div>
            <p className="leading-relaxed text-slate-700">
              Minum TTD bersama air putih atau jus jeruk (Vitamin C membantu penyerapan zat besi). Hindari minum bersamaan dengan teh, kopi, atau susu karena menghambat absorpsi.
            </p>
          </div>
        </div>
      </div>

      {/* 4 Pilar Gizi Seimbang */}
      <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
        <h3 className="font-extrabold text-base text-slate-800 mb-3 flex items-center space-x-2">
          <Activity className="w-5 h-5 text-emerald-700" />
          <span>4 Pilar Gizi Seimbang Remaja</span>
        </h3>
        <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-3 text-xs">
          <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
            <div className="font-bold text-slate-800 mb-1">1. Pola Makan Beragam</div>
            <p className="text-slate-500 leading-relaxed">Tidak ada satu pun jenis makanan yang memiliki kandungan gizi lengkap kecuali ASI bagi bayi.</p>
          </div>
          <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
            <div className="font-bold text-slate-800 mb-1">2. Perilaku Hidup Bersih</div>
            <p className="text-slate-500 leading-relaxed">Cuci tangan pakai sabun dengan air mengalir sebelum dan sesudah makan untuk mencegah diare & infeksi.</p>
          </div>
          <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
            <div className="font-bold text-slate-800 mb-1">3. Aktivitas Fisik Aktif</div>
            <p className="text-slate-500 leading-relaxed">Lakukan olahraga minimal 30 menit sehari untuk mengoptimalkan massa otot dan kepadatan tulang.</p>
          </div>
          <div className="bg-slate-50 p-3 rounded-xl border border-slate-200">
            <div className="font-bold text-slate-800 mb-1">4. Pantau Berat Badan</div>
            <p className="text-slate-500 leading-relaxed">Timbang berat badan dan ukur tinggi badan berkala sebulan sekali di UKS madrasah.</p>
          </div>
        </div>
      </div>
    </div>
  );
};
