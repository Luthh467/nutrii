import React, { useState } from 'react';
import { ShieldCheck, Search, Users, AlertCircle, CheckCircle2, MessageSquare, Plus } from 'lucide-react';
import { BmiScreening, DailyNutritionLog, NutritionalStatus } from '../types';

interface UksDashboardViewProps {
  screenings: BmiScreening[];
  logs: DailyNutritionLog[];
  onUpdateScreeningFollowUp: (id: string, notes: string, status: BmiScreening['followUpStatus']) => void;
}

export const UksDashboardView: React.FC<UksDashboardViewProps> = ({
  screenings,
  logs,
  onUpdateScreeningFollowUp
}) => {
  const [activeTab, setActiveTab] = useState<'screenings' | 'logs'>('screenings');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRiskFilter, setSelectedRiskFilter] = useState<string>('all');
  const [editingScreeningId, setEditingScreeningId] = useState<string | null>(null);
  const [followUpNotes, setFollowUpNotes] = useState('');
  const [followUpStatus, setFollowUpStatus] = useState<BmiScreening['followUpStatus']>('Konseling Diberikan');

  // Compute statistics
  const totalScreened = screenings.length;
  const normalCount = screenings.filter((s) => s.status === 'Gizi Baik (Normal)').length;
  const underCount = screenings.filter((s) => s.status === 'Gizi Buruk' || s.status === 'Gizi Kurang').length;
  const overCount = screenings.filter((s) => s.status === 'Gizi Lebih' || s.status === 'Berisiko Gizi Lebih' || s.status === 'Obesitas').length;
  const normalPercentage = totalScreened > 0 ? Math.round((normalCount / totalScreened) * 100) : 0;

  // Filter screenings
  const filteredScreenings = screenings.filter((s) => {
    const matchQuery = s.studentName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.className.toLowerCase().includes(searchQuery.toLowerCase());

    if (!matchQuery) return false;
    if (selectedRiskFilter === 'normal') return s.status === 'Gizi Baik (Normal)';
    if (selectedRiskFilter === 'kurus') return s.status === 'Gizi Buruk' || s.status === 'Gizi Kurang';
    if (selectedRiskFilter === 'gemuk') return s.status === 'Gizi Lebih' || s.status === 'Berisiko Gizi Lebih' || s.status === 'Obesitas';
    return true;
  });

  const handleOpenEdit = (scr: BmiScreening) => {
    setEditingScreeningId(scr.id);
    setFollowUpNotes(scr.followUpNotes || '');
    setFollowUpStatus(scr.followUpStatus || 'Konseling Diberikan');
  };

  const handleSaveEdit = (id: string) => {
    onUpdateScreeningFollowUp(id, followUpNotes, followUpStatus);
    setEditingScreeningId(null);
  };

  const getBadgeClass = (status: NutritionalStatus) => {
    switch (status) {
      case 'Gizi Buruk': return 'bg-red-600 text-white';
      case 'Gizi Kurang': return 'bg-amber-500 text-white';
      case 'Gizi Baik (Normal)': return 'bg-emerald-600 text-white';
      case 'Berisiko Gizi Lebih': return 'bg-yellow-500 text-slate-900';
      case 'Gizi Lebih': return 'bg-orange-500 text-white';
      case 'Obesitas': return 'bg-red-700 text-white';
      default: return 'bg-slate-500 text-white';
    }
  };

  return (
    <div className="max-w-6xl mx-auto px-4 py-6 space-y-6">
      {/* UKS Header */}
      <div className="bg-gradient-to-r from-[#E65100] to-[#F57C00] text-white rounded-2xl p-6 shadow-md flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-1.5 bg-white/20 text-amber-100 text-xs px-2.5 py-1 rounded-full mb-2 font-bold backdrop-blur-sm">
            <ShieldCheck className="w-4 h-4" />
            <span>Dashboard Pembina Kesehatan Madrasah / Sekolah</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black tracking-tight">
            Monitoring Status Gizi Siswa & Tindak Lanjut UKS
          </h2>
          <p className="text-xs sm:text-sm text-amber-100 mt-1 max-w-xl">
            Pantau status IMT/U seluruh siswa, identifikasi risiko gizi kurang atau obesitas, dan catat intervensi rujukan Puskesmas.
          </p>
        </div>

        <div className="bg-black/20 backdrop-blur-sm border border-white/20 px-4 py-3 rounded-xl text-center flex-shrink-0">
          <div className="text-xs text-amber-200">Indeks Gizi Sehat Sekolah:</div>
          <div className="text-3xl font-black text-white">{normalPercentage}%</div>
          <div className="text-[10px] text-amber-100">Siswa Berstatus Normal</div>
        </div>
      </div>

      {/* Aggregate Stats Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3.5">
        <div className="bg-white rounded-2xl p-4 shadow-sm border border-slate-200">
          <div className="text-slate-400 text-xs font-semibold">Total Siswa Diperiksa</div>
          <div className="text-2xl font-black text-slate-900 mt-1">{totalScreened}</div>
          <div className="text-[10px] text-slate-500 mt-0.5">Siswa terdata</div>
        </div>

        <div className="bg-white rounded-2xl p-4 shadow-sm border border-emerald-200 bg-emerald-50/20">
          <div className="text-emerald-700 text-xs font-semibold">Gizi Normal (Ideal)</div>
          <div className="text-2xl font-black text-emerald-700 mt-1">{normalCount}</div>
          <div className="text-[10px] text-emerald-600 mt-0.5">{normalPercentage}% dari total</div>
        </div>

        <div className="bg-white rounded-2xl p-4 shadow-sm border border-amber-200 bg-amber-50/20">
          <div className="text-amber-700 text-xs font-semibold">Gizi Kurang / Buruk</div>
          <div className="text-2xl font-black text-amber-700 mt-1">{underCount}</div>
          <div className="text-[10px] text-amber-600 mt-0.5">Perlu PMT & Suplemen TTD</div>
        </div>

        <div className="bg-white rounded-2xl p-4 shadow-sm border border-rose-200 bg-rose-50/20">
          <div className="text-rose-700 text-xs font-semibold">Gizi Lebih / Obesitas</div>
          <div className="text-2xl font-black text-rose-700 mt-1">{overCount}</div>
          <div className="text-[10px] text-rose-600 mt-0.5">Perlu edukasi pola makan</div>
        </div>
      </div>

      {/* Sub Tabs: Skrining vs Log Gizi */}
      <div className="flex border-b border-slate-200 space-x-4 text-xs sm:text-sm font-bold">
        <button
          onClick={() => setActiveTab('screenings')}
          className={`pb-3 px-1 border-b-2 transition ${
            activeTab === 'screenings'
              ? 'border-[#E65100] text-[#E65100]'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          Daftar Skrining Siswa ({screenings.length})
        </button>
        <button
          onClick={() => setActiveTab('logs')}
          className={`pb-3 px-1 border-b-2 transition ${
            activeTab === 'logs'
              ? 'border-[#E65100] text-[#E65100]'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          Log Asupan Harian Siswa ({logs.length})
        </button>
      </div>

      {/* TAB 1: SCREENINGS TABLE */}
      {activeTab === 'screenings' && (
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200 space-y-4">
          {/* Filter & Search Bar */}
          <div className="flex flex-col sm:flex-row gap-3 items-center justify-between">
            <div className="relative w-full sm:w-72">
              <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Cari nama siswa atau kelas..."
                className="w-full pl-9 pr-3 py-2 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-orange-500 focus:outline-none"
              />
            </div>

            <div className="flex items-center space-x-2 w-full sm:w-auto overflow-x-auto text-xs">
              <button
                onClick={() => setSelectedRiskFilter('all')}
                className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                  selectedRiskFilter === 'all'
                    ? 'bg-slate-800 text-white'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
              >
                Semua Siswa
              </button>
              <button
                onClick={() => setSelectedRiskFilter('kurus')}
                className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                  selectedRiskFilter === 'kurus'
                    ? 'bg-amber-600 text-white'
                    : 'bg-amber-50 text-amber-800 hover:bg-amber-100'
                }`}
              >
                Risiko Kurus ({underCount})
              </button>
              <button
                onClick={() => setSelectedRiskFilter('gemuk')}
                className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                  selectedRiskFilter === 'gemuk'
                    ? 'bg-rose-600 text-white'
                    : 'bg-rose-50 text-rose-800 hover:bg-rose-100'
                }`}
              >
                Risiko Obesitas ({overCount})
              </button>
              <button
                onClick={() => setSelectedRiskFilter('normal')}
                className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                  selectedRiskFilter === 'normal'
                    ? 'bg-emerald-600 text-white'
                    : 'bg-emerald-50 text-emerald-800 hover:bg-emerald-100'
                }`}
              >
                Normal ({normalCount})
              </button>
            </div>
          </div>

          {/* Table */}
          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs text-slate-600">
              <thead className="bg-slate-50 text-slate-700 font-bold uppercase text-[10px] border-b border-slate-200">
                <tr>
                  <th className="py-3 px-3">Siswa / Kelas</th>
                  <th className="py-3 px-3">Usia / JK</th>
                  <th className="py-3 px-3">BB / TB</th>
                  <th className="py-3 px-3">IMT & Z-Score</th>
                  <th className="py-3 px-3">Status Gizi</th>
                  <th className="py-3 px-3">Status Tindak Lanjut</th>
                  <th className="py-3 px-3 text-right">Aksi UKS</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {filteredScreenings.map((scr) => (
                  <React.Fragment key={scr.id}>
                    <tr className="hover:bg-slate-50/50">
                      <td className="py-3 px-3">
                        <div className="font-bold text-slate-900">{scr.studentName}</div>
                        <div className="text-[10px] text-slate-400">{scr.className} • {scr.screenedAt}</div>
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        {scr.age} th ({scr.gender === 'L' ? 'Laki-laki' : 'Perempuan'})
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        {scr.weightKg} kg / {scr.heightCm} cm
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        <span className="font-black text-slate-800">{scr.bmiValue}</span>
                        <span className="text-[10px] text-slate-400 ml-1">({scr.zScore > 0 ? `+${scr.zScore}` : scr.zScore} SD)</span>
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        <span className={`px-2 py-0.5 rounded-md text-[10px] font-bold ${getBadgeClass(scr.status)}`}>
                          {scr.status}
                        </span>
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        <span className="text-[10px] bg-slate-100 text-slate-700 px-2 py-0.5 rounded font-semibold">
                          {scr.followUpStatus || 'Menunggu'}
                        </span>
                        {scr.followUpNotes && (
                          <div className="text-[10px] text-slate-500 truncate max-w-[140px] mt-0.5">
                            "{scr.followUpNotes}"
                          </div>
                        )}
                      </td>
                      <td className="py-3 px-3 text-right whitespace-nowrap">
                        <button
                          onClick={() => handleOpenEdit(scr)}
                          className="bg-orange-50 hover:bg-orange-100 text-orange-700 font-bold px-2.5 py-1 rounded-lg border border-orange-200 transition text-[11px]"
                        >
                          Catat Tindak Lanjut
                        </button>
                      </td>
                    </tr>

                    {/* Follow-up inline edit box */}
                    {editingScreeningId === scr.id && (
                      <tr className="bg-orange-50/40">
                        <td colSpan={7} className="p-4 border-y border-orange-200">
                          <div className="space-y-3">
                            <span className="text-xs font-bold text-orange-950 block">
                              Input Intervensi & Tindak Lanjut Petugas UKS untuk {scr.studentName}:
                            </span>
                            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
                              <div className="sm:col-span-2">
                                <input
                                  type="text"
                                  value={followUpNotes}
                                  onChange={(e) => setFollowUpNotes(e.target.value)}
                                  placeholder="Contoh: Diberikan konseling gizi 'Isi Piringku', tablet penambah darah, dan surat rujukan Puskesmas."
                                  className="w-full px-3 py-2 text-xs border border-orange-300 rounded-xl focus:ring-2 focus:ring-orange-500 focus:outline-none bg-white"
                                />
                              </div>
                              <div>
                                <select
                                  value={followUpStatus}
                                  onChange={(e) => setFollowUpStatus(e.target.value as any)}
                                  className="w-full px-3 py-2 text-xs border border-orange-300 rounded-xl focus:ring-2 focus:ring-orange-500 focus:outline-none bg-white"
                                >
                                  <option value="Menunggu">Menunggu</option>
                                  <option value="Konseling Diberikan">Konseling Diberikan</option>
                                  <option value="Rujukan Puskesmas">Rujukan Puskesmas</option>
                                  <option value="Selesai">Selesai</option>
                                </select>
                              </div>
                            </div>
                            <div className="flex justify-end space-x-2">
                              <button
                                onClick={() => setEditingScreeningId(null)}
                                className="px-3 py-1.5 rounded-lg border border-slate-300 text-slate-700 text-xs"
                              >
                                Batal
                              </button>
                              <button
                                onClick={() => handleSaveEdit(scr.id)}
                                className="px-3 py-1.5 rounded-lg bg-[#E65100] text-white font-bold text-xs"
                              >
                                Simpan Tindak Lanjut
                              </button>
                            </div>
                          </div>
                        </td>
                      </tr>
                    )}
                  </React.Fragment>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* TAB 2: LOGS TABLE */}
      {activeTab === 'logs' && (
        <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200">
          <h3 className="font-extrabold text-base text-slate-800 mb-4">
            Catatan Asupan Harian Siswa Terkini
          </h3>
          <div className="divide-y divide-slate-100">
            {logs.map((log) => (
              <div key={log.id} className="py-3 flex items-start justify-between gap-4 text-xs">
                <div>
                  <div className="flex items-center space-x-2">
                    <span className="font-bold text-slate-900">{log.studentName}</span>
                    <span className="bg-slate-100 text-slate-600 px-2 py-0.5 rounded text-[10px] font-bold">
                      {log.mealType}
                    </span>
                    <span className="text-slate-400 text-[10px]">{log.date}</span>
                  </div>
                  <div className="text-slate-700 font-medium mt-1">{log.menuDescription}</div>
                  {log.notes && <div className="text-slate-400 text-[11px] mt-0.5 italic">{log.notes}</div>}
                </div>

                <div className="text-right flex-shrink-0">
                  <span className="text-blue-700 bg-blue-50 px-2 py-0.5 rounded font-bold text-[10px] block">
                    💧 {log.waterGlasses} Gelas Air
                  </span>
                  {log.tookIronTablet && (
                    <span className="text-rose-700 bg-rose-50 px-2 py-0.5 rounded font-bold text-[10px] inline-block mt-1">
                      💊 Tablet Tambah Darah (TTD)
                    </span>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};
