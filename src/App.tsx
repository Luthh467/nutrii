import React, { useState, useEffect } from 'react';
import { Navbar } from './components/Navbar';
import { LoginView } from './components/LoginView';
import { StudentBmiView } from './components/StudentBmiView';
import { CameraFoodAnalysisView } from './components/CameraFoodAnalysisView';
import { DailyLogView } from './components/DailyLogView';
import { UksDashboardView } from './components/UksDashboardView';
import { EducationView } from './components/EducationView';
import { StudentProfile, BmiScreening, DailyNutritionLog } from './types';
import { INITIAL_SCREENINGS, INITIAL_LOGS, DEMO_STUDENTS } from './data/mockData';

export const App: React.FC = () => {
  const [currentRole, setCurrentRole] = useState<'siswa' | 'uks' | null>('siswa');
  const [studentProfile, setStudentProfile] = useState<StudentProfile | null>(DEMO_STUDENTS[0]);
  const [activeTab, setActiveTab] = useState<string>('bmi');

  const [screenings, setScreenings] = useState<BmiScreening[]>(() => {
    const saved = localStorage.getItem('nutrimind_screenings');
    return saved ? JSON.parse(saved) : INITIAL_SCREENINGS;
  });

  const [logs, setLogs] = useState<DailyNutritionLog[]>(() => {
    const saved = localStorage.getItem('nutrimind_logs');
    return saved ? JSON.parse(saved) : INITIAL_LOGS;
  });

  // Sync to localStorage
  useEffect(() => {
    localStorage.setItem('nutrimind_screenings', JSON.stringify(screenings));
  }, [screenings]);

  useEffect(() => {
    localStorage.setItem('nutrimind_logs', JSON.stringify(logs));
  }, [logs]);

  const handleLoginAsStudent = (profile: StudentProfile) => {
    setStudentProfile(profile);
    setCurrentRole('siswa');
    setActiveTab('bmi');
  };

  const handleLoginAsUks = () => {
    setCurrentRole('uks');
    setActiveTab('uks-dashboard');
  };

  const handleLogout = () => {
    setCurrentRole(null);
  };

  const handleSaveScreening = (newScreening: BmiScreening) => {
    setScreenings((prev) => [newScreening, ...prev]);
  };

  const handleAddLog = (newLog: DailyNutritionLog) => {
    setLogs((prev) => [newLog, ...prev]);
  };

  const handleUpdateScreeningFollowUp = (
    id: string,
    notes: string,
    status: BmiScreening['followUpStatus']
  ) => {
    setScreenings((prev) =>
      prev.map((s) => (s.id === id ? { ...s, followUpNotes: notes, followUpStatus: status } : s))
    );
  };

  return (
    <div className="min-h-screen bg-[#F8FAF8] text-slate-800 flex flex-col font-sans">
      <Navbar
        currentRole={currentRole}
        studentProfile={studentProfile}
        activeTab={activeTab}
        setActiveTab={setActiveTab}
        onLogout={handleLogout}
      />

      <main className="flex-1">
        {!currentRole ? (
          <LoginView
            onLoginAsStudent={handleLoginAsStudent}
            onLoginAsUks={handleLoginAsUks}
          />
        ) : (
          <>
            {currentRole === 'siswa' && studentProfile && (
              <>
                {activeTab === 'bmi' && (
                  <StudentBmiView
                    student={studentProfile}
                    onSaveScreening={handleSaveScreening}
                    recentScreenings={screenings}
                    onNavigateToCamera={() => setActiveTab('camera')}
                  />
                )}
                {activeTab === 'camera' && <CameraFoodAnalysisView />}
                {activeTab === 'log' && (
                  <DailyLogView
                    student={studentProfile}
                    logs={logs}
                    onAddLog={handleAddLog}
                  />
                )}
                {activeTab === 'edu' && <EducationView />}
              </>
            )}

            {currentRole === 'uks' && (
              <>
                {activeTab === 'uks-dashboard' && (
                  <UksDashboardView
                    screenings={screenings}
                    logs={logs}
                    onUpdateScreeningFollowUp={handleUpdateScreeningFollowUp}
                  />
                )}
                {activeTab === 'edu' && <EducationView />}
              </>
            )}
          </>
        )}
      </main>

      {/* Footer */}
      <footer className="bg-white border-t border-slate-200 py-4 text-center text-xs text-slate-500">
        <div className="max-w-4xl mx-auto px-4">
          <p className="font-semibold text-slate-700">
            NutriMind AI • Sistem Edukasi & Skrining Nutrisi Siswa Madrasah
          </p>
          <p className="text-[11px] text-slate-400 mt-0.5">
            Sesuai Standar Antropometri Permenkes RI No. 2 Tahun 2020 & Gemini Multimodal AI
          </p>
        </div>
      </footer>
    </div>
  );
};
export default App;
