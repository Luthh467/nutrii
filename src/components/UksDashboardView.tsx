import React, { useState, useMemo } from 'react';
import {
  ShieldCheck,
  Search,
  Users,
  AlertCircle,
  CheckCircle2,
  AlertTriangle,
  PieChart as PieIcon,
  BarChart3,
  TrendingUp,
  Download,
  Filter,
  FileText,
  Activity,
  Award,
  ChevronRight
} from 'lucide-react';
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
  const [activeTab, setActiveTab] = useState<'visuals' | 'screenings' | 'logs'>('visuals');
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedRiskFilter, setSelectedRiskFilter] = useState<string>('all');
  const [selectedClassFilter, setSelectedClassFilter] = useState<string>('all');
  const [editingScreeningId, setEditingScreeningId] = useState<string | null>(null);
  const [followUpNotes, setFollowUpNotes] = useState('');
  const [followUpStatus, setFollowUpStatus] = useState<BmiScreening['followUpStatus']>('Konseling Diberikan');
  const [hoveredCategory, setHoveredCategory] = useState<string | null>(null);

  // Compute aggregate numbers
  const totalScreened = screenings.length;
  const normalCount = screenings.filter((s) => s.status === 'Gizi Baik (Normal)').length;
  const underCount = screenings.filter((s) => s.status === 'Gizi Buruk' || s.status === 'Gizi Kurang').length;
  const overCount = screenings.filter(
    (s) => s.status === 'Gizi Lebih' || s.status === 'Berisiko Gizi Lebih' || s.status === 'Obesitas'
  ).length;
  const normalPercentage = totalScreened > 0 ? Math.round((normalCount / totalScreened) * 100) : 0;

  // Breakdown for Donut Chart (Distribusi Kategori IMT)
  const categoryStats = useMemo(() => {
    const counts: Record<NutritionalStatus, number> = {
      'Gizi Buruk': 0,
      'Gizi Kurang': 0,
      'Gizi Baik (Normal)': 0,
      'Berisiko Gizi Lebih': 0,
      'Gizi Lebih': 0,
      'Obesitas': 0
    };
    screenings.forEach((s) => {
      if (counts[s.status] !== undefined) {
        counts[s.status]++;
      }
    });

    const definitions = [
      { status: 'Gizi Baik (Normal)' as NutritionalStatus, color: '#059669', bgClass: 'bg-emerald-600', label: 'Gizi Normal' },
      { status: 'Gizi Kurang' as NutritionalStatus, color: '#D97706', bgClass: 'bg-amber-600', label: 'Gizi Kurang' },
      { status: 'Gizi Buruk' as NutritionalStatus, color: '#DC2626', bgClass: 'bg-red-600', label: 'Gizi Buruk' },
      { status: 'Berisiko Gizi Lebih' as NutritionalStatus, color: '#EAB308', bgClass: 'bg-yellow-500', label: 'Berisiko Lebih' },
      { status: 'Gizi Lebih' as NutritionalStatus, color: '#EA580C', bgClass: 'bg-orange-600', label: 'Gizi Lebih' },
      { status: 'Obesitas' as NutritionalStatus, color: '#BE123C', bgClass: 'bg-rose-700', label: 'Obesitas' }
    ];

    return definitions.map((def) => {
      const count = counts[def.status];
      const pct = totalScreened > 0 ? (count / totalScreened) * 100 : 0;
      return {
        ...def,
        count,
        percentage: pct
      };
    });
  }, [screenings, totalScreened]);

  // Per-Class Breakdown for Bar Chart (Perbandingan Antar Kelas)
  const classStats = useMemo(() => {
    // Group classes into main cohorts (e.g., Kelas X, Kelas XI, Kelas XII) or individual classes
    const groups: Record<
      string,
      { className: string; normal: number; under: number; over: number; total: number }
    > = {};

    screenings.forEach((s) => {
      // Normalize class name prefix (Kelas X, XI, XII)
      let key = 'Lainnya';
      const c = s.className.toUpperCase();
      if (c.startsWith('X-') || c.startsWith('X ') || c === 'X') key = 'Kelas X';
      else if (c.startsWith('XI-') || c.startsWith('XI ') || c === 'XI') key = 'Kelas XI';
      else if (c.startsWith('XII-') || c.startsWith('XII ') || c === 'XII') key = 'Kelas XII';
      else key = s.className;

      if (!groups[key]) {
        groups[key] = { className: key, normal: 0, under: 0, over: 0, total: 0 };
      }

      groups[key].total++;
      if (s.status === 'Gizi Baik (Normal)') {
        groups[key].normal++;
      } else if (s.status === 'Gizi Buruk' || s.status === 'Gizi Kurang') {
        groups[key].under++;
      } else {
        groups[key].over++;
      }
    });

    return Object.values(groups).sort((a, b) => a.className.localeCompare(b.className));
  }, [screenings]);

  // Unique classes for filtering
  const uniqueClasses = useMemo(() => {
    return Array.from(new Set(screenings.map((s) => s.className))).sort();
  }, [screenings]);

  // Filter screenings
  const filteredScreenings = screenings.filter((s) => {
    const matchQuery =
      s.studentName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      s.className.toLowerCase().includes(searchQuery.toLowerCase());

    if (!matchQuery) return false;

    if (selectedClassFilter !== 'all' && s.className !== selectedClassFilter) {
      return false;
    }

    if (selectedRiskFilter === 'normal') return s.status === 'Gizi Baik (Normal)';
    if (selectedRiskFilter === 'kurus') return s.status === 'Gizi Buruk' || s.status === 'Gizi Kurang';
    if (selectedRiskFilter === 'gemuk')
      return s.status === 'Gizi Lebih' || s.status === 'Berisiko Gizi Lebih' || s.status === 'Obesitas';
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
      case 'Gizi Buruk':
        return 'bg-red-600 text-white';
      case 'Gizi Kurang':
        return 'bg-amber-500 text-white';
      case 'Gizi Baik (Normal)':
        return 'bg-emerald-600 text-white';
      case 'Berisiko Gizi Lebih':
        return 'bg-yellow-500 text-slate-900';
      case 'Gizi Lebih':
        return 'bg-orange-500 text-white';
      case 'Obesitas':
        return 'bg-rose-700 text-white';
      default:
        return 'bg-slate-500 text-white';
    }
  };

  // SVG Donut Calculations
  const radius = 68;
  const circumference = 2 * Math.PI * radius;
  let cumulativePercent = 0;

  return (
    <div className="max-w-6xl mx-auto px-4 py-6 space-y-6">
      {/* UKS Header */}
      <div className="bg-gradient-to-r from-[#E65100] to-[#F57C00] text-white rounded-2xl p-6 shadow-md flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="inline-flex items-center space-x-1.5 bg-white/20 text-amber-100 text-xs px-2.5 py-1 rounded-full mb-2 font-bold backdrop-blur-sm">
            <ShieldCheck className="w-4 h-4" />
            <span>Dashboard Pembina Kesehatan UKS / Madrasah Sehat</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black tracking-tight">
            Visualisasi Status Gizi Siswa & Tindak Lanjut UKS
          </h2>
          <p className="text-xs sm:text-sm text-amber-100 mt-1 max-w-xl leading-relaxed">
            Ringkasan visual status gizi siswa berdasarkan standar antropometri Kemenkes RI, perbandingan risiko antar kelas, dan panduan rujukan Puskesmas.
          </p>
        </div>

        <div className="bg-black/20 backdrop-blur-sm border border-white/20 px-5 py-3.5 rounded-2xl text-center flex-shrink-0">
          <div className="text-xs text-amber-200 font-semibold">Indeks Gizi Sehat Sekolah:</div>
          <div className="text-3xl font-black text-white mt-0.5">{normalPercentage}%</div>
          <div className="text-[11px] text-amber-100 font-medium">Siswa Status Gizi Baik</div>
        </div>
      </div>

      {/* Aggregate Stats Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-4 gap-3.5">
        <div className="bg-white rounded-2xl p-4 shadow-sm border border-slate-200">
          <div className="text-slate-400 text-xs font-bold uppercase tracking-wider">Total Siswa Terdata</div>
          <div className="text-2xl font-black text-slate-900 mt-1">{totalScreened}</div>
          <div className="text-[11px] text-slate-500 mt-0.5">Semua tingkatan kelas</div>
        </div>

        <div className="bg-white rounded-2xl p-4 shadow-sm border border-emerald-200 bg-emerald-50/25">
          <div className="text-emerald-800 text-xs font-bold uppercase tracking-wider">Gizi Normal (Ideal)</div>
          <div className="text-2xl font-black text-emerald-700 mt-1">{normalCount}</div>
          <div className="text-[11px] text-emerald-600 mt-0.5">{normalPercentage}% dari populasi</div>
        </div>

        <div className="bg-white rounded-2xl p-4 shadow-sm border border-amber-200 bg-amber-50/25">
          <div className="text-amber-800 text-xs font-bold uppercase tracking-wider">Gizi Kurang / Buruk</div>
          <div className="text-2xl font-black text-amber-700 mt-1">{underCount}</div>
          <div className="text-[11px] text-amber-700 mt-0.5">Program PMT & TTD Siswi</div>
        </div>

        <div className="bg-white rounded-2xl p-4 shadow-sm border border-rose-200 bg-rose-50/25">
          <div className="text-rose-800 text-xs font-bold uppercase tracking-wider">Gizi Lebih / Obesitas</div>
          <div className="text-2xl font-black text-rose-700 mt-1">{overCount}</div>
          <div className="text-[11px] text-rose-600 mt-0.5">Intervensi Pola Makan</div>
        </div>
      </div>

      {/* Primary Navigation Tabs */}
      <div className="flex border-b border-slate-200 space-x-4 text-xs sm:text-sm font-bold">
        <button
          onClick={() => setActiveTab('visuals')}
          className={`pb-3 px-2 border-b-2 flex items-center space-x-1.5 transition ${
            activeTab === 'visuals'
              ? 'border-[#E65100] text-[#E65100]'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          <PieIcon className="w-4 h-4" />
          <span>Visualisasi Grafik UKS</span>
        </button>
        <button
          onClick={() => setActiveTab('screenings')}
          className={`pb-3 px-2 border-b-2 flex items-center space-x-1.5 transition ${
            activeTab === 'screenings'
              ? 'border-[#E65100] text-[#E65100]'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          <FileText className="w-4 h-4" />
          <span>Tabel Skrining Siswa ({screenings.length})</span>
        </button>
        <button
          onClick={() => setActiveTab('logs')}
          className={`pb-3 px-2 border-b-2 flex items-center space-x-1.5 transition ${
            activeTab === 'logs'
              ? 'border-[#E65100] text-[#E65100]'
              : 'border-transparent text-slate-500 hover:text-slate-700'
          }`}
        >
          <Activity className="w-4 h-4" />
          <span>Catatan Asupan Harian Siswa ({logs.length})</span>
        </button>
      </div>

      {/* TAB 0: RICH DATA VISUALIZATIONS */}
      {activeTab === 'visuals' && (
        <div className="space-y-6">
          {/* Top Visuals Grid: Donut Chart & Bar Chart */}
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            {/* 1. DIAGRAM LINGKARAN (DONUT CHART) DISTRIBUSI KATEGORI IMT */}
            <div className="lg:col-span-6 bg-white rounded-2xl p-6 shadow-sm border border-slate-200 flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between border-b border-slate-100 pb-3 mb-4">
                  <div className="flex items-center space-x-2">
                    <div className="w-8 h-8 rounded-xl bg-emerald-100 text-emerald-700 flex items-center justify-center">
                      <PieIcon className="w-4 h-4" />
                    </div>
                    <div>
                      <h3 className="font-extrabold text-slate-900 text-sm sm:text-base">
                        Distribusi Kategori Status Gizi (IMT/U)
                      </h3>
                      <p className="text-[11px] text-slate-500">
                        Proporsi kategori gizi seluruh siswa berdasarkan Permenkes RI
                      </p>
                    </div>
                  </div>
                </div>

                {/* Donut Chart Visual & Legend */}
                <div className="flex flex-col sm:flex-row items-center justify-around gap-6 py-2">
                  {/* SVG Donut */}
                  <div className="relative w-44 h-44 flex-shrink-0">
                    <svg className="w-full h-full -rotate-90" viewBox="0 0 160 160">
                      {/* Track circle */}
                      <circle
                        cx="80"
                        cy="80"
                        r={radius}
                        fill="transparent"
                        stroke="#F1F5F9"
                        strokeWidth="18"
                      />
                      {/* Colored arcs */}
                      {categoryStats.map((item, idx) => {
                        if (item.count === 0) return null;
                        const strokeDasharray = `${(item.percentage / 100) * circumference} ${circumference}`;
                        const strokeDashoffset = -((cumulativePercent / 100) * circumference);
                        cumulativePercent += item.percentage;

                        return (
                          <circle
                            key={idx}
                            cx="80"
                            cy="80"
                            r={radius}
                            fill="transparent"
                            stroke={item.color}
                            strokeWidth={hoveredCategory === item.status ? '22' : '18'}
                            strokeDasharray={strokeDasharray}
                            strokeDashoffset={strokeDashoffset}
                            className="transition-all duration-300 cursor-pointer"
                            onMouseEnter={() => setHoveredCategory(item.status)}
                            onMouseLeave={() => setHoveredCategory(null)}
                          />
                        );
                      })}
                    </svg>

                    {/* Donut Center text */}
                    <div className="absolute inset-0 flex flex-col items-center justify-center pointer-events-none text-center">
                      <span className="text-[10px] uppercase font-bold text-slate-400">Total Siswa</span>
                      <span className="text-2xl font-black text-slate-800">{totalScreened}</span>
                      <span className="text-[10px] font-bold text-emerald-600">
                        {normalPercentage}% Normal
                      </span>
                    </div>
                  </div>

                  {/* Category Legend Pills */}
                  <div className="space-y-1.5 w-full sm:w-56 text-xs">
                    {categoryStats.map((cat, idx) => (
                      <div
                        key={idx}
                        onMouseEnter={() => setHoveredCategory(cat.status)}
                        onMouseLeave={() => setHoveredCategory(null)}
                        className={`flex items-center justify-between p-1.5 rounded-lg transition cursor-pointer ${
                          hoveredCategory === cat.status
                            ? 'bg-slate-100 font-bold scale-[1.02]'
                            : 'hover:bg-slate-50'
                        }`}
                      >
                        <div className="flex items-center space-x-2 truncate">
                          <span
                            className="w-3 h-3 rounded-full flex-shrink-0"
                            style={{ backgroundColor: cat.color }}
                          ></span>
                          <span className="truncate text-slate-700">{cat.label}</span>
                        </div>
                        <div className="text-right flex items-center space-x-1.5 flex-shrink-0">
                          <span className="font-extrabold text-slate-900">{cat.count}</span>
                          <span className="text-[11px] text-slate-400">
                            ({Math.round(cat.percentage)}%)
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* Summary footnote */}
              <div className="mt-4 pt-3 border-t border-slate-100 text-[11px] text-slate-500 flex items-center justify-between">
                <span>Evaluasi UKS: Standar WHO & Kemenkes RI</span>
                <span className="font-bold text-emerald-700">{normalCount} Siswa Sehat & Prima</span>
              </div>
            </div>

            {/* 2. DIAGRAM BATANG (BAR CHART) PERBANDINGAN ANTAR KELAS */}
            <div className="lg:col-span-6 bg-white rounded-2xl p-6 shadow-sm border border-slate-200 flex flex-col justify-between">
              <div>
                <div className="flex items-center justify-between border-b border-slate-100 pb-3 mb-4">
                  <div className="flex items-center space-x-2">
                    <div className="w-8 h-8 rounded-xl bg-orange-100 text-orange-700 flex items-center justify-center">
                      <BarChart3 className="w-4 h-4" />
                    </div>
                    <div>
                      <h3 className="font-extrabold text-slate-900 text-sm sm:text-base">
                        Perbandingan Status Gizi Antar Tingkatan Kelas
                      </h3>
                      <p className="text-[11px] text-slate-500">
                        Distribusi siswa status Normal vs Berisiko di tiap jenjang
                      </p>
                    </div>
                  </div>
                </div>

                {/* Bar Chart Legend */}
                <div className="flex items-center justify-end space-x-3 text-[11px] mb-4">
                  <div className="flex items-center space-x-1">
                    <span className="w-2.5 h-2.5 rounded-sm bg-emerald-600"></span>
                    <span className="text-slate-600">Gizi Normal</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <span className="w-2.5 h-2.5 rounded-sm bg-amber-500"></span>
                    <span className="text-slate-600">Risiko Kurang</span>
                  </div>
                  <div className="flex items-center space-x-1">
                    <span className="w-2.5 h-2.5 rounded-sm bg-rose-600"></span>
                    <span className="text-slate-600">Risiko Obesitas</span>
                  </div>
                </div>

                {/* Visual Bars */}
                <div className="space-y-4">
                  {classStats.map((cls, idx) => {
                    const normalPct = cls.total > 0 ? (cls.normal / cls.total) * 100 : 0;
                    const underPct = cls.total > 0 ? (cls.under / cls.total) * 100 : 0;
                    const overPct = cls.total > 0 ? (cls.over / cls.total) * 100 : 0;

                    return (
                      <div key={idx} className="space-y-1.5">
                        <div className="flex items-center justify-between text-xs">
                          <span className="font-bold text-slate-800">{cls.className}</span>
                          <span className="text-slate-500 text-[11px]">
                            Total: <strong className="text-slate-800">{cls.total}</strong> siswa •{' '}
                            <span className="text-emerald-700 font-bold">{Math.round(normalPct)}% Normal</span>
                          </span>
                        </div>

                        {/* Stacked Progress Bar */}
                        <div className="h-6 w-full bg-slate-100 rounded-xl overflow-hidden flex shadow-inner">
                          {cls.normal > 0 && (
                            <div
                              style={{ width: `${normalPct}%` }}
                              title={`Normal: ${cls.normal} siswa (${Math.round(normalPct)}%)`}
                              className="bg-emerald-600 h-full flex items-center justify-center text-white text-[10px] font-black transition-all"
                            >
                              {cls.normal > 0 && `${cls.normal}`}
                            </div>
                          )}
                          {cls.under > 0 && (
                            <div
                              style={{ width: `${underPct}%` }}
                              title={`Gizi Kurang: ${cls.under} siswa (${Math.round(underPct)}%)`}
                              className="bg-amber-500 h-full flex items-center justify-center text-white text-[10px] font-black transition-all"
                            >
                              {cls.under > 0 && `${cls.under}`}
                            </div>
                          )}
                          {cls.over > 0 && (
                            <div
                              style={{ width: `${overPct}%` }}
                              title={`Gizi Lebih/Obesitas: ${cls.over} siswa (${Math.round(overPct)}%)`}
                              className="bg-rose-600 h-full flex items-center justify-center text-white text-[10px] font-black transition-all"
                            >
                              {cls.over > 0 && `${cls.over}`}
                            </div>
                          )}
                        </div>
                      </div>
                    );
                  })}
                </div>
              </div>

              {/* Class summary note */}
              <div className="mt-4 pt-3 border-t border-slate-100 text-[11px] text-slate-500 flex items-center justify-between">
                <span>Rasio Sehat Terbaik: <strong>Kelas X (67% Normal)</strong></span>
                <span className="text-amber-700 font-semibold">Perlu PMT Tambahan di Jam Istirahat</span>
              </div>
            </div>
          </div>

          {/* 3. MATRIKS TRIAGE RISIKO GIZI & TINDAK LANJUT UKS */}
          <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200 space-y-4">
            <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-2 border-b border-slate-100 pb-3">
              <div>
                <h3 className="font-black text-slate-900 text-sm sm:text-base">
                  Matriks Prioritas Intervensi & Tindak Lanjut Petugas UKS
                </h3>
                <p className="text-xs text-slate-500">
                  Panduan langkah intervensi kesehatan madrasah sesuai tingkat risiko antropometri
                </p>
              </div>

              <div className="inline-flex items-center space-x-1.5 text-xs font-bold text-slate-600 bg-slate-100 px-3 py-1.5 rounded-xl">
                <ShieldCheck className="w-4 h-4 text-emerald-600" />
                <span>Protokol Trias UKS Terintegrasi</span>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
              {/* Prioritas 1: Risiko Tinggi */}
              <div className="p-4 rounded-2xl border-2 border-rose-200 bg-rose-50/40 space-y-2.5">
                <div className="flex items-center justify-between">
                  <span className="text-[10px] font-black uppercase tracking-wider bg-rose-600 text-white px-2.5 py-0.5 rounded-full">
                    Prioritas 1: Risiko Tinggi
                  </span>
                  <span className="text-sm font-black text-rose-700">
                    {screenings.filter((s) => s.status === 'Gizi Buruk' || s.status === 'Obesitas').length} Siswa
                  </span>
                </div>
                <h4 className="font-extrabold text-xs sm:text-sm text-rose-950">
                  Gizi Buruk & Obesitas Berat
                </h4>
                <p className="text-[11px] text-slate-700 leading-relaxed">
                  Siswa dengan deviasi z-score ekstrem (kurang dari -3 SD atau lebih dari +2 SD). Berisiko tinggi mengalami komplikasi klinis atau gangguan metabolik.
                </p>
                <div className="pt-2 border-t border-rose-200/80 text-[11px] font-semibold text-rose-900 space-y-1">
                  <div>🚨 <strong>Tindakan UKS:</strong></div>
                  <div>• Terbitkan Surat Rujukan ke Puskesmas mitra</div>
                  <div>• Konseling gizi intensif orang tua & siswa</div>
                  <div>• Pemantauan berat badan per 2 pekan</div>
                </div>
              </div>

              {/* Prioritas 2: Risiko Sedang */}
              <div className="p-4 rounded-2xl border-2 border-amber-200 bg-amber-50/40 space-y-2.5">
                <div className="flex items-center justify-between">
                  <span className="text-[10px] font-black uppercase tracking-wider bg-amber-600 text-white px-2.5 py-0.5 rounded-full">
                    Prioritas 2: Risiko Sedang
                  </span>
                  <span className="text-sm font-black text-amber-700">
                    {screenings.filter((s) => s.status === 'Gizi Kurang' || s.status === 'Gizi Lebih' || s.status === 'Berisiko Gizi Lebih').length} Siswa
                  </span>
                </div>
                <h4 className="font-extrabold text-xs sm:text-sm text-amber-950">
                  Gizi Kurang & Berisiko Lebih
                </h4>
                <p className="text-[11px] text-slate-700 leading-relaxed">
                  Siswa berisiko anemia, defisiensi zat besi, atau kelebihan kalori akibat konsumsi jajanan tinggi gula, garam, dan minyak di kantin.
                </p>
                <div className="pt-2 border-t border-amber-200/80 text-[11px] font-semibold text-amber-900 space-y-1">
                  <div>⚠️ <strong>Tindakan UKS:</strong></div>
                  <div>• Pemberian Makanan Tambahan (PMT) kaya protein</div>
                  <div>• Kepatuhan konsumsi Tablet Tambah Darah (TTD)</div>
                  <div>• Edukasi pembatasan jajanan ultra-proses</div>
                </div>
              </div>

              {/* Prioritas 3: Normal / Ideal */}
              <div className="p-4 rounded-2xl border-2 border-emerald-200 bg-emerald-50/40 space-y-2.5">
                <div className="flex items-center justify-between">
                  <span className="text-[10px] font-black uppercase tracking-wider bg-emerald-600 text-white px-2.5 py-0.5 rounded-full">
                    Prioritas 3: Terjaga
                  </span>
                  <span className="text-sm font-black text-emerald-700">
                    {normalCount} Siswa
                  </span>
                </div>
                <h4 className="font-extrabold text-xs sm:text-sm text-emerald-950">
                  Gizi Baik (Normal Ideal)
                </h4>
                <p className="text-[11px] text-slate-700 leading-relaxed">
                  Pertumbuhan fisik proporsional, daya tahan tubuh kuat, dan konsentrasi belajar optimal. Perlu dijaga konsistensi kebiasaan sehatnya.
                </p>
                <div className="pt-2 border-t border-emerald-200/80 text-[11px] font-semibold text-emerald-900 space-y-1">
                  <div>✅ <strong>Tindakan UKS:</strong></div>
                  <div>• Pemantauan antropometri berkala 6 bulan sekali</div>
                  <div>• Duta gizi sehat sebaya di kelas</div>
                  <div>• Penguatan edukasi hidrasi 8 gelas air/hari</div>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}

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

            <div className="flex flex-wrap items-center gap-2 w-full sm:w-auto text-xs">
              {/* Filter by class */}
              <select
                value={selectedClassFilter}
                onChange={(e) => setSelectedClassFilter(e.target.value)}
                className="px-3 py-1.5 rounded-lg font-semibold border border-slate-300 bg-white text-slate-700 text-xs focus:ring-2 focus:ring-orange-500 focus:outline-none"
              >
                <option value="all">Semua Kelas</option>
                {uniqueClasses.map((cls) => (
                  <option key={cls} value={cls}>
                    {cls}
                  </option>
                ))}
              </select>

              <button
                onClick={() => setSelectedRiskFilter('all')}
                className={`px-3 py-1.5 rounded-lg font-bold transition whitespace-nowrap ${
                  selectedRiskFilter === 'all'
                    ? 'bg-slate-800 text-white'
                    : 'bg-slate-100 text-slate-600 hover:bg-slate-200'
                }`}
              >
                Semua ({totalScreened})
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
                    <tr className="hover:bg-slate-50/50 transition">
                      <td className="py-3 px-3">
                        <div className="font-bold text-slate-900">{scr.studentName}</div>
                        <div className="text-[10px] text-slate-400">
                          {scr.className} • {scr.screenedAt}
                        </div>
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        {scr.age} th ({scr.gender === 'L' ? 'Laki-laki' : 'Perempuan'})
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        {scr.weightKg} kg / {scr.heightCm} cm
                      </td>
                      <td className="py-3 px-3 whitespace-nowrap">
                        <span className="font-black text-slate-800">{scr.bmiValue}</span>
                        <span className="text-[10px] text-slate-400 ml-1">
                          ({scr.zScore > 0 ? `+${scr.zScore}` : scr.zScore} SD)
                        </span>
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
                                  placeholder="Contoh: Diberikan konseling gizi 'Isi Piringku', suplemen tablet tambah darah, dan surat rujukan Puskesmas."
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

