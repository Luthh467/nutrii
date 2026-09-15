import React, { useState } from 'react';
import { GraduationCap, ShieldCheck, CheckCircle2, ArrowRight, UserCheck, AlertTriangle, KeyRound } from 'lucide-react';
import { StudentProfile, Gender } from '../types';
import { DEMO_STUDENTS } from '../data/mockData';

interface LoginViewProps {
  onLoginAsStudent: (student: StudentProfile) => void;
  onLoginAsUks: () => void;
}

export const LoginView: React.FC<LoginViewProps> = ({ onLoginAsStudent, onLoginAsUks }) => {
  const [selectedRole, setSelectedRole] = useState<'siswa' | 'uks'>('siswa');

  // Student form state
  const [studentName, setStudentName] = useState(DEMO_STUDENTS[0].name);
  const [studentNisn, setStudentNisn] = useState(DEMO_STUDENTS[0].nisn);
  const [studentClass, setStudentClass] = useState(DEMO_STUDENTS[0].className);
  const [studentGender, setStudentGender] = useState<Gender>(DEMO_STUDENTS[0].gender);
  const [studentAge, setStudentAge] = useState<number>(DEMO_STUDENTS[0].age);
  const [studentError, setStudentError] = useState('');

  // UKS form state
  const [uksUser, setUksUser] = useState('');
  const [uksPass, setUksPass] = useState('');
  const [uksError, setUksError] = useState('');

  const handleSelectDemoStudent = (demo: StudentProfile) => {
    setStudentName(demo.name);
    setStudentNisn(demo.nisn);
    setStudentClass(demo.className);
    setStudentGender(demo.gender);
    setStudentAge(demo.age);
    setStudentError('');
  };

  const handleStudentSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (!studentName.trim() || !studentNisn.trim() || !studentClass.trim()) {
      setStudentError('Mohon isi Nama Lengkap, NISN, dan Kelas.');
      return;
    }

    const profile: StudentProfile = {
      id: `std-${studentNisn.trim()}`,
      name: studentName.trim(),
      nisn: studentNisn.trim(),
      className: studentClass.trim(),
      gender: studentGender,
      age: Number(studentAge) || 16
    };

    onLoginAsStudent(profile);
  };

  const handleUksSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (uksUser === 'petugas_uks' && uksPass === 'uks123') {
      onLoginAsUks();
    } else {
      setUksError('Username atau password salah. Gunakan: petugas_uks / uks123');
    }
  };

  const handleAutofillUks = () => {
    setUksUser('petugas_uks');
    setUksPass('uks123');
    setUksError('');
  };

  return (
    <div className="max-w-xl mx-auto px-4 py-8">
      {/* Disclaimer Banner */}
      <div className="mb-6 bg-amber-50 border border-amber-200 rounded-xl p-3.5 flex items-start space-x-3 text-xs text-amber-900 shadow-sm">
        <AlertTriangle className="w-5 h-5 text-amber-600 flex-shrink-0 mt-0.5" />
        <div>
          <span className="font-bold">Aplikasi Edukasi & Skrining Nutrisi Siswa:</span> NutriMind AI dirancang untuk mempermudah pemantauan gizi remaja berpedoman pada standar Permenkes RI No. 2/2020 dan edukasi "Isi Piringku".
        </div>
      </div>

      {/* Main Login Card */}
      <div className="bg-white rounded-2xl shadow-md border border-slate-200 overflow-hidden">
        {/* Header */}
        <div className="bg-gradient-to-r from-[#1B5E20] to-[#2E7D32] p-6 text-white text-center">
          <h2 className="text-xl font-extrabold tracking-tight">Selamat Datang di NutriMind AI</h2>
          <p className="text-xs text-emerald-100 mt-1">
            Silakan pilih peran masuk di bawah ini untuk melanjutkan
          </p>
        </div>

        <div className="p-6">
          {/* ROLE SELECTOR - CLEAR, PROMINENT & HIGH CONTRAST */}
          <div className="mb-6">
            <label className="block text-xs font-black uppercase text-slate-800 tracking-wider mb-2">
              Pilihan Peran Masuk:
            </label>
            <div className="grid grid-cols-2 gap-3">
              {/* Option 1: Siswa */}
              <button
                type="button"
                onClick={() => setSelectedRole('siswa')}
                className={`p-3.5 rounded-xl border-2 text-left transition relative flex flex-col items-center sm:items-start text-center sm:text-left ${
                  selectedRole === 'siswa'
                    ? 'bg-[#1B5E20] border-[#1B5E20] text-white shadow-md'
                    : 'bg-slate-50 hover:bg-slate-100 border-slate-300 text-slate-700'
                }`}
              >
                <div className="flex items-center space-x-2">
                  <GraduationCap className={`w-5 h-5 ${selectedRole === 'siswa' ? 'text-emerald-300' : 'text-[#1B5E20]'}`} />
                  <span className="font-extrabold text-sm sm:text-base">Siswa</span>
                  {selectedRole === 'siswa' && (
                    <CheckCircle2 className="w-4 h-4 text-emerald-300 ml-auto hidden sm:block" />
                  )}
                </div>
                <span className={`text-[11px] mt-1 ${selectedRole === 'siswa' ? 'text-emerald-100' : 'text-slate-500'}`}>
                  Skrining IMT & Kamera Gizi
                </span>
              </button>

              {/* Option 2: Petugas UKS */}
              <button
                type="button"
                onClick={() => setSelectedRole('uks')}
                className={`p-3.5 rounded-xl border-2 text-left transition relative flex flex-col items-center sm:items-start text-center sm:text-left ${
                  selectedRole === 'uks'
                    ? 'bg-[#E65100] border-[#E65100] text-white shadow-md'
                    : 'bg-slate-50 hover:bg-slate-100 border-slate-300 text-slate-700'
                }`}
              >
                <div className="flex items-center space-x-2">
                  <ShieldCheck className={`w-5 h-5 ${selectedRole === 'uks' ? 'text-amber-200' : 'text-[#E65100]'}`} />
                  <span className="font-extrabold text-sm sm:text-base">Petugas UKS</span>
                  {selectedRole === 'uks' && (
                    <CheckCircle2 className="w-4 h-4 text-amber-200 ml-auto hidden sm:block" />
                  )}
                </div>
                <span className={`text-[11px] mt-1 ${selectedRole === 'uks' ? 'text-amber-100' : 'text-slate-500'}`}>
                  Dashboard & Pantau Gizi
                </span>
              </button>
            </div>
          </div>

          {/* TAB CONTENT: SISWA */}
          {selectedRole === 'siswa' && (
            <div>
              {/* Quick Demo Student Pills */}
              <div className="mb-4">
                <span className="block text-xs font-bold text-emerald-800 mb-1.5 flex items-center">
                  <UserCheck className="w-3.5 h-3.5 mr-1" />
                  Pilih Cepat Akun Siswa (Demo Langsung):
                </span>
                <div className="grid grid-cols-1 sm:grid-cols-3 gap-2">
                  {DEMO_STUDENTS.map((demo) => {
                    const isSelected = studentNisn === demo.nisn;
                    return (
                      <button
                        key={demo.id}
                        type="button"
                        onClick={() => handleSelectDemoStudent(demo)}
                        className={`p-2 rounded-lg text-left text-xs transition border ${
                          isSelected
                            ? 'bg-emerald-600 text-white border-emerald-700 font-bold shadow-sm'
                            : 'bg-emerald-50 hover:bg-emerald-100 text-emerald-900 border-emerald-200'
                        }`}
                      >
                        <div className="font-bold truncate">{demo.name}</div>
                        <div className={`text-[10px] ${isSelected ? 'text-emerald-100' : 'text-emerald-700'}`}>
                          {demo.className} • {demo.age} th ({demo.gender})
                        </div>
                      </button>
                    );
                  })}
                </div>
              </div>

              {/* Student Form */}
              <form onSubmit={handleStudentSubmit} className="space-y-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Nama Lengkap Siswa
                  </label>
                  <input
                    type="text"
                    value={studentName}
                    onChange={(e) => { setStudentName(e.target.value); setStudentError(''); }}
                    className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    placeholder="Contoh: Ahmad Fauzi"
                    required
                  />
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      NISN (Nomor Induk)
                    </label>
                    <input
                      type="text"
                      value={studentNisn}
                      onChange={(e) => { setStudentNisn(e.target.value); setStudentError(''); }}
                      className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                      placeholder="0071234561"
                      required
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Kelas
                    </label>
                    <input
                      type="text"
                      value={studentClass}
                      onChange={(e) => { setStudentClass(e.target.value); setStudentError(''); }}
                      className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                      placeholder="XI MIPA 2"
                      required
                    />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Jenis Kelamin
                    </label>
                    <select
                      value={studentGender}
                      onChange={(e) => setStudentGender(e.target.value as Gender)}
                      className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500 bg-white"
                    >
                      <option value="L">Laki-laki (L)</option>
                      <option value="P">Perempuan (P)</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Usia (Tahun)
                    </label>
                    <input
                      type="number"
                      min={10}
                      max={18}
                      value={studentAge}
                      onChange={(e) => setStudentAge(Number(e.target.value))}
                      className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    />
                  </div>
                </div>

                {studentError && (
                  <p className="text-xs text-red-600 font-semibold">{studentError}</p>
                )}

                <button
                  type="submit"
                  className="w-full mt-2 bg-[#1B5E20] hover:bg-[#2E7D32] text-white font-bold py-2.5 px-4 rounded-xl flex items-center justify-center space-x-2 shadow-md transition"
                >
                  <span>Masuk Sebagai Siswa</span>
                  <ArrowRight className="w-4 h-4" />
                </button>
              </form>
            </div>
          )}

          {/* TAB CONTENT: PETUGAS UKS */}
          {selectedRole === 'uks' && (
            <div>
              {/* Info box for UKS */}
              <div className="mb-4 bg-orange-50 border border-orange-200 rounded-xl p-3 text-xs text-orange-900">
                <span className="font-bold">Akses Khusus Petugas UKS / Guru Pembina:</span>
                <p className="mt-0.5 text-slate-600">
                  Digunakan untuk mengakses agregat statistik status gizi madrasah, input tindak lanjut klinis, dan pantauan gizi siswa.
                </p>
              </div>

              {/* Quick Autofill Button */}
              <button
                type="button"
                onClick={handleAutofillUks}
                className="w-full mb-4 bg-orange-100 hover:bg-orange-200 text-orange-800 border border-orange-300 font-bold py-2 px-3 rounded-xl text-xs flex items-center justify-center space-x-1.5 transition"
              >
                <KeyRound className="w-3.5 h-3.5" />
                <span>⚡ Klik di Sini: Isi Akun Demo (petugas_uks / uks123)</span>
              </button>

              {/* UKS Form */}
              <form onSubmit={handleUksSubmit} className="space-y-3">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Username Petugas UKS
                  </label>
                  <input
                    type="text"
                    value={uksUser}
                    onChange={(e) => { setUksUser(e.target.value); setUksError(''); }}
                    className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                    placeholder="petugas_uks"
                    required
                  />
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Password
                  </label>
                  <input
                    type="password"
                    value={uksPass}
                    onChange={(e) => { setUksPass(e.target.value); setUksError(''); }}
                    className="w-full px-3 py-2 text-sm border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-orange-500"
                    placeholder="••••••••"
                    required
                  />
                </div>

                {uksError && (
                  <p className="text-xs text-red-600 font-semibold">{uksError}</p>
                )}

                <button
                  type="submit"
                  className="w-full mt-2 bg-[#E65100] hover:bg-[#F57C00] text-white font-bold py-2.5 px-4 rounded-xl flex items-center justify-center space-x-2 shadow-md transition"
                >
                  <span>Masuk Dashboard UKS</span>
                  <ArrowRight className="w-4 h-4" />
                </button>
              </form>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
