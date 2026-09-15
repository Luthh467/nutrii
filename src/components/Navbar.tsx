import React from 'react';
import { Apple, LogOut, User, ShieldCheck } from 'lucide-react';
import { StudentProfile } from '../types';

interface NavbarProps {
  currentRole: 'siswa' | 'uks' | null;
  studentProfile: StudentProfile | null;
  activeTab: string;
  setActiveTab: (tab: string) => void;
  onLogout: () => void;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentRole,
  studentProfile,
  activeTab,
  setActiveTab,
  onLogout
}) => {
  return (
    <header className="bg-[#1B5E20] text-white shadow-md sticky top-0 z-50">
      <div className="max-w-6xl mx-auto px-4 py-3 flex items-center justify-between">
        {/* Brand */}
        <div className="flex items-center space-x-2">
          <div className="w-10 h-10 rounded-xl bg-white/10 flex items-center justify-center text-white backdrop-blur-sm border border-white/20">
            <Apple className="w-6 h-6 text-emerald-300" />
          </div>
          <div>
            <div className="flex items-center space-x-1.5">
              <span className="font-extrabold text-lg tracking-tight">NutriMind AI</span>
              <span className="text-[10px] bg-emerald-400 text-[#0F3813] font-bold px-1.5 py-0.5 rounded uppercase tracking-wider">
                Madrasah
              </span>
            </div>
            <p className="text-xs text-emerald-200 hidden sm:block">
              Skrining Gizi & Analisis Makanan Remaja
            </p>
          </div>
        </div>

        {/* User Info & Actions */}
        {currentRole && (
          <div className="flex items-center space-x-3">
            <div className="flex items-center space-x-2 bg-black/20 px-3 py-1.5 rounded-full border border-white/10 text-xs">
              {currentRole === 'siswa' ? (
                <>
                  <User className="w-3.5 h-3.5 text-emerald-300" />
                  <span className="font-semibold text-white max-w-[120px] truncate">
                    {studentProfile?.name || 'Siswa'}
                  </span>
                  <span className="text-emerald-300 bg-emerald-950/60 px-1.5 py-0.5 rounded text-[10px]">
                    {studentProfile?.className || 'Siswa'}
                  </span>
                </>
              ) : (
                <>
                  <ShieldCheck className="w-3.5 h-3.5 text-amber-300" />
                  <span className="font-semibold text-amber-100">Petugas UKS</span>
                </>
              )}
            </div>

            <button
              onClick={onLogout}
              className="flex items-center space-x-1 text-xs bg-red-600/80 hover:bg-red-600 text-white px-2.5 py-1.5 rounded-lg transition"
              title="Keluar"
            >
              <LogOut className="w-3.5 h-3.5" />
              <span className="hidden sm:inline">Keluar</span>
            </button>
          </div>
        )}
      </div>

      {/* Tabs navigation for logged-in user */}
      {currentRole === 'siswa' && (
        <div className="bg-[#154a19] border-t border-white/10 px-4">
          <div className="max-w-6xl mx-auto flex space-x-1 sm:space-x-4 overflow-x-auto text-xs sm:text-sm font-medium py-1">
            <button
              onClick={() => setActiveTab('bmi')}
              className={`px-3 py-2 rounded-lg transition whitespace-nowrap ${
                activeTab === 'bmi'
                  ? 'bg-emerald-500 text-white font-bold shadow-sm'
                  : 'text-emerald-100 hover:bg-white/10'
              }`}
            >
              📏 Skrining IMT Remaja
            </button>
            <button
              onClick={() => setActiveTab('camera')}
              className={`px-3 py-2 rounded-lg transition whitespace-nowrap ${
                activeTab === 'camera'
                  ? 'bg-emerald-500 text-white font-bold shadow-sm'
                  : 'text-emerald-100 hover:bg-white/10'
              }`}
            >
              📷 Analisis Kamera Makanan
            </button>
            <button
              onClick={() => setActiveTab('log')}
              className={`px-3 py-2 rounded-lg transition whitespace-nowrap ${
                activeTab === 'log'
                  ? 'bg-emerald-500 text-white font-bold shadow-sm'
                  : 'text-emerald-100 hover:bg-white/10'
              }`}
            >
              📝 Log Gizi Harian
            </button>
            <button
              onClick={() => setActiveTab('edu')}
              className={`px-3 py-2 rounded-lg transition whitespace-nowrap ${
                activeTab === 'edu'
                  ? 'bg-emerald-500 text-white font-bold shadow-sm'
                  : 'text-emerald-100 hover:bg-white/10'
              }`}
            >
              💡 Edukasi Gizi
            </button>
          </div>
        </div>
      )}

      {currentRole === 'uks' && (
        <div className="bg-[#154a19] border-t border-white/10 px-4">
          <div className="max-w-6xl mx-auto flex space-x-2 text-xs sm:text-sm font-medium py-1">
            <button
              onClick={() => setActiveTab('uks-dashboard')}
              className={`px-3 py-2 rounded-lg transition ${
                activeTab === 'uks-dashboard'
                  ? 'bg-amber-500 text-slate-900 font-bold shadow-sm'
                  : 'text-emerald-100 hover:bg-white/10'
              }`}
            >
              📊 Dashboard Data Gizi Madrasah
            </button>
            <button
              onClick={() => setActiveTab('edu')}
              className={`px-3 py-2 rounded-lg transition ${
                activeTab === 'edu'
                  ? 'bg-amber-500 text-slate-900 font-bold shadow-sm'
                  : 'text-emerald-100 hover:bg-white/10'
              }`}
            >
              📚 Pedoman Klinis Kemenkes RI
            </button>
          </div>
        </div>
      )}
    </header>
  );
};
