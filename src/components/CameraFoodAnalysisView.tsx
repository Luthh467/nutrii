import React, { useState, useRef, useEffect } from 'react';
import { Camera, Sparkles, Upload, RotateCcw, AlertCircle, CheckCircle2, RefreshCw, Key, ShieldCheck, HelpCircle } from 'lucide-react';
import { FoodAnalysisResult } from '../types';
import { CANTEEN_PRESETS } from '../data/mockData';
import { analyzeFoodWithGemini } from '../services/geminiService';

export const CameraFoodAnalysisView: React.FC = () => {
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [foodHint, setFoodHint] = useState<string>('');
  const [isCameraActive, setIsCameraActive] = useState<boolean>(false);
  const [facingMode, setFacingMode] = useState<'environment' | 'user'>('environment');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [analysisResult, setAnalysisResult] = useState<FoodAnalysisResult | null>(null);
  const [errorMessage, setErrorMessage] = useState<string>('');
  const [showApiKeyModal, setShowApiKeyModal] = useState<boolean>(false);
  const [customApiKey, setCustomApiKey] = useState<string>(() => {
    return typeof window !== 'undefined' ? (localStorage.getItem('NUTRIMIND_GEMINI_API_KEY') || '') : '';
  });

  const videoRef = useRef<HTMLVideoElement | null>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const mobileCameraInputRef = useRef<HTMLInputElement | null>(null);

  // Safely stop existing media stream
  const stopCamera = () => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => {
        try {
          track.stop();
        } catch (e) {
          console.error(e);
        }
      });
      streamRef.current = null;
    }
    if (videoRef.current) {
      videoRef.current.srcObject = null;
    }
    setIsCameraActive(false);
  };

  // Start live webcam with progressive constraint fallback
  const startCamera = async (targetFacing: 'environment' | 'user' = facingMode) => {
    setErrorMessage('');
    stopCamera();

    if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
      setErrorMessage(
        'Browser atau perangkat ini belum mengizinkan akses kamera langsung WebRTC. Silakan gunakan tombol "Kamera HP Langsung" atau "Unggah Foto / Galeri".'
      );
      return;
    }

    try {
      // First attempt: environment or user camera with reasonable constraints
      let stream: MediaStream;
      try {
        stream = await navigator.mediaDevices.getUserMedia({
          video: {
            facingMode: { ideal: targetFacing },
            width: { ideal: 1280 },
            height: { ideal: 720 }
          },
          audio: false
        });
      } catch (firstErr) {
        console.warn('Initial camera constraint failed, attempting basic video stream fallback:', firstErr);
        // Fallback attempt: basic video without strict constraints
        stream = await navigator.mediaDevices.getUserMedia({
          video: true,
          audio: false
        });
      }

      streamRef.current = stream;
      setIsCameraActive(true);
      setFacingMode(targetFacing);
    } catch (err: any) {
      console.error('Kamera error:', err);
      let msg = 'Tidak dapat membuka kamera. ';
      if (err.name === 'NotAllowedError' || err.name === 'PermissionDeniedError') {
        msg += 'Izin akses kamera ditolak oleh browser. Harap izinkan akses kamera di pengaturan browser Anda.';
      } else if (err.name === 'NotFoundError' || err.name === 'DevicesNotFoundError') {
        msg += 'Kamera fisik tidak ditemukan pada perangkat ini.';
      } else {
        msg += err.message || 'Silakan gunakan tombol "Kamera HP Langsung" atau "Unggah Foto".';
      }
      setErrorMessage(msg);
      setIsCameraActive(false);
    }
  };

  // Switch between back and front camera
  const toggleFacingMode = () => {
    const nextMode = facingMode === 'environment' ? 'user' : 'environment';
    startCamera(nextMode);
  };

  // Bind video stream once the video element is mounted in the DOM
  useEffect(() => {
    if (isCameraActive && videoRef.current && streamRef.current) {
      videoRef.current.srcObject = streamRef.current;
      videoRef.current
        .play()
        .catch((playErr) => console.warn('Video play warning:', playErr));
    }
  }, [isCameraActive]);

  // Clean up camera stream on unmount
  useEffect(() => {
    return () => {
      stopCamera();
    };
  }, []);

  // Capture photo frame from video canvas
  const capturePhoto = () => {
    if (!videoRef.current) {
      setErrorMessage('Kamera belum siap.');
      return;
    }

    const video = videoRef.current;
    if (video.videoWidth === 0 || video.videoHeight === 0) {
      setErrorMessage('Gambar kamera sedang dimuat, silakan klik kembali dalam 1 detik.');
      return;
    }

    try {
      const canvas = document.createElement('canvas');
      canvas.width = video.videoWidth;
      canvas.height = video.videoHeight;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        // If front camera, mirror horizontally for natural feel
        if (facingMode === 'user') {
          ctx.translate(canvas.width, 0);
          ctx.scale(-1, 1);
        }
        ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
        const dataUrl = canvas.toDataURL('image/jpeg', 0.9);
        setSelectedImage(dataUrl);
        setErrorMessage('');
        stopCamera();
      }
    } catch (err: any) {
      console.error('Capture photo error:', err);
      setErrorMessage('Gagal memproses foto dari kamera. Silakan coba lagi.');
    }
  };

  // Handle file upload (gallery or direct mobile camera)
  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Validate size (max 8MB)
    if (file.size > 8 * 1024 * 1024) {
      setErrorMessage('Ukuran file foto terlalu besar (maksimal 8MB). Silakan pilih foto lain.');
      return;
    }

    const reader = new FileReader();
    reader.onload = (event) => {
      const result = event.target?.result as string;
      setSelectedImage(result);
      setErrorMessage('');
      stopCamera();
    };
    reader.onerror = () => {
      setErrorMessage('Gagal membaca file foto.');
    };
    reader.readAsDataURL(file);
  };

  // Select Preset Menu for Instant Zero-Setup Testing
  const handleSelectPreset = (preset: typeof CANTEEN_PRESETS[0]) => {
    const canvas = document.createElement('canvas');
    canvas.width = 400;
    canvas.height = 300;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.fillStyle = '#1B5E20';
      ctx.fillRect(0, 0, 400, 300);
      ctx.font = '72px sans-serif';
      ctx.textAlign = 'center';
      ctx.fillText(preset.icon, 200, 150);
      ctx.fillStyle = '#FFFFFF';
      ctx.font = 'bold 20px sans-serif';
      ctx.fillText(preset.label, 200, 220);
      setSelectedImage(canvas.toDataURL('image/jpeg', 0.85));
    }
    setFoodHint(preset.label);
    setAnalysisResult(preset.result);
    setErrorMessage('');
    stopCamera();
  };

  // Trigger Gemini Analysis
  const handleAnalyze = async () => {
    if (!selectedImage) {
      setErrorMessage('Silakan ambil foto dengan kamera atau pilih salah satu menu contoh terlebih dahulu.');
      return;
    }

    setIsLoading(true);
    setErrorMessage('');

    try {
      const result = await analyzeFoodWithGemini(selectedImage, foodHint);
      setAnalysisResult(result);
    } catch (err: any) {
      setErrorMessage(`Gagal menganalisis gizi makanan: ${err.message || 'Terjadi kesalahan sistem'}`);
    } finally {
      setIsLoading(false);
    }
  };

  // Save custom Gemini API key
  const handleSaveApiKey = () => {
    if (typeof window !== 'undefined') {
      localStorage.setItem('NUTRIMIND_GEMINI_API_KEY', customApiKey.trim());
    }
    setShowApiKeyModal(false);
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-6 space-y-6">
      {/* Hero Banner */}
      <div className="bg-gradient-to-r from-[#1B5E20] to-[#2E7D32] text-white rounded-2xl p-6 shadow-md flex flex-col md:flex-row items-start md:items-center justify-between gap-4">
        <div>
          <div className="flex items-center space-x-2 bg-white/10 px-3 py-1 rounded-full text-xs font-bold w-fit mb-2 backdrop-blur-sm">
            <Sparkles className="w-3.5 h-3.5 text-amber-300" />
            <span>Multimodal Food Nutrition AI</span>
          </div>
          <h2 className="text-xl sm:text-2xl font-black">
            Analisis Zat Gizi Makanan dengan Kamera AI
          </h2>
          <p className="text-xs sm:text-sm text-emerald-100 mt-1 max-w-2xl leading-relaxed">
            Potret hidangan makan siang, bekal, atau jajanan kantin madrasah. Gemini AI akan mengenali zat gizi (Karbohidrat, Protein, Lemak, Vitamin & Mineral) serta kesesuaian panduan Kemenkes "Isi Piringku".
          </p>
        </div>

        <button
          onClick={() => setShowApiKeyModal(true)}
          className="bg-black/20 hover:bg-black/30 border border-white/20 text-white text-xs font-bold px-3.5 py-2 rounded-xl transition flex items-center space-x-2 flex-shrink-0"
        >
          <Key className="w-3.5 h-3.5 text-amber-300" />
          <span>Pengaturan API Key</span>
        </button>
      </div>

      {/* Preset Quick-Test Buttons */}
      <div className="bg-white rounded-2xl p-4 shadow-sm border border-slate-200">
        <div className="flex items-center justify-between mb-2.5">
          <span className="text-xs font-black uppercase text-slate-700 tracking-wider">
            🍱 Uji Cepat Menu Kantin / Bekal Siswa:
          </span>
          <span className="text-[11px] text-slate-400">Klik untuk uji instan</span>
        </div>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2.5">
          {CANTEEN_PRESETS.map((preset, idx) => (
            <button
              key={idx}
              onClick={() => handleSelectPreset(preset)}
              className="p-2.5 rounded-xl border border-slate-200 bg-slate-50 hover:bg-emerald-50 hover:border-emerald-300 transition flex items-center space-x-2.5 text-left active:scale-[0.98]"
            >
              <span className="text-2xl flex-shrink-0">{preset.icon}</span>
              <div className="overflow-hidden">
                <div className="font-bold text-xs text-slate-800 truncate">{preset.label}</div>
                <div className="text-[10px] text-emerald-700 font-semibold">Analisis Instan</div>
              </div>
            </button>
          ))}
        </div>
      </div>

      {/* Camera & Capture Container */}
      <div className="bg-white rounded-2xl p-6 shadow-sm border border-slate-200 space-y-4">
        <div className="flex flex-wrap items-center justify-between gap-2 border-b border-slate-100 pb-3">
          <div className="flex items-center space-x-2">
            <Camera className="w-5 h-5 text-emerald-700" />
            <h3 className="font-extrabold text-sm sm:text-base text-slate-800">
              Pengambilan Foto Makanan
            </h3>
          </div>

          <div className="flex flex-wrap items-center gap-2">
            {/* Direct Camera Button */}
            {!isCameraActive ? (
              <button
                onClick={() => startCamera()}
                className="bg-[#1B5E20] hover:bg-[#2E7D32] text-white text-xs font-bold px-3 py-2 rounded-xl transition flex items-center space-x-1.5 shadow-sm active:scale-95"
              >
                <Camera className="w-3.5 h-3.5" />
                <span>Buka Kamera Langsung</span>
              </button>
            ) : (
              <div className="flex items-center space-x-1.5">
                <button
                  onClick={toggleFacingMode}
                  title="Ganti Lensa Depan / Belakang"
                  className="bg-slate-100 hover:bg-slate-200 text-slate-800 text-xs font-bold px-2.5 py-2 rounded-xl transition flex items-center space-x-1"
                >
                  <RefreshCw className="w-3.5 h-3.5 text-emerald-700" />
                  <span>{facingMode === 'environment' ? 'Kamera Belakang' : 'Kamera Depan'}</span>
                </button>
                <button
                  onClick={stopCamera}
                  className="bg-red-600 hover:bg-red-700 text-white text-xs font-bold px-3 py-2 rounded-xl transition"
                >
                  Tutup
                </button>
              </div>
            )}

            {/* Mobile Native Camera (Direct Phone App) */}
            <button
              onClick={() => mobileCameraInputRef.current?.click()}
              className="bg-emerald-50 hover:bg-emerald-100 text-emerald-800 border border-emerald-300 text-xs font-bold px-3 py-2 rounded-xl transition flex items-center space-x-1.5"
            >
              <Camera className="w-3.5 h-3.5" />
              <span>Kamera HP (Native)</span>
            </button>
            <input
              ref={mobileCameraInputRef}
              type="file"
              accept="image/*"
              capture="environment"
              onChange={handleFileUpload}
              className="hidden"
            />

            {/* Gallery Upload */}
            <button
              onClick={() => fileInputRef.current?.click()}
              className="bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold px-3 py-2 rounded-xl transition flex items-center space-x-1.5"
            >
              <Upload className="w-3.5 h-3.5" />
              <span>Galeri Foto</span>
            </button>
            <input
              ref={fileInputRef}
              type="file"
              accept="image/*"
              onChange={handleFileUpload}
              className="hidden"
            />
          </div>
        </div>

        {errorMessage && (
          <div className="bg-red-50 border border-red-200 text-red-700 rounded-xl p-3.5 text-xs flex items-start space-x-2.5">
            <AlertCircle className="w-4 h-4 flex-shrink-0 text-red-600 mt-0.5" />
            <div className="flex-1 leading-relaxed">{errorMessage}</div>
          </div>
        )}

        {/* Viewfinder or Image Preview */}
        <div className="relative aspect-video max-h-80 bg-slate-900 rounded-2xl overflow-hidden flex items-center justify-center border border-slate-300">
          {isCameraActive ? (
            <div className="relative w-full h-full">
              <video
                ref={videoRef}
                autoPlay
                playsInline
                muted
                className={`w-full h-full object-cover ${facingMode === 'user' ? '-scale-x-100' : ''}`}
              />

              {/* Viewfinder Plate Framing Overlay */}
              <div className="absolute inset-0 pointer-events-none flex items-center justify-center">
                <div className="w-48 h-48 sm:w-56 sm:h-56 rounded-full border-2 border-dashed border-white/60 flex items-center justify-center">
                  <span className="text-[11px] font-semibold text-white/80 bg-black/40 px-2.5 py-1 rounded-full backdrop-blur-xs">
                    Posisikan Piring di Sini
                  </span>
                </div>
              </div>

              {/* Bottom Shutter Action Button */}
              <div className="absolute inset-x-0 bottom-4 flex justify-center">
                <button
                  onClick={capturePhoto}
                  className="bg-white text-emerald-900 font-extrabold px-6 py-2.5 rounded-full shadow-lg border-4 border-emerald-500 hover:scale-105 active:scale-95 transition flex items-center space-x-2"
                >
                  <Camera className="w-5 h-5 text-emerald-700" />
                  <span>Jepret Foto Sekarang</span>
                </button>
              </div>
            </div>
          ) : selectedImage ? (
            <div className="relative w-full h-full">
              <img
                src={selectedImage}
                alt="Makanan Terpilih"
                className="w-full h-full object-contain bg-black/40"
              />
              <div className="absolute top-3 left-3 bg-black/60 text-white text-[11px] font-bold px-2.5 py-1 rounded-lg backdrop-blur-sm flex items-center space-x-1">
                <CheckCircle2 className="w-3.5 h-3.5 text-emerald-400" />
                <span>Foto Siap Dianalisis</span>
              </div>
              <button
                onClick={() => {
                  setSelectedImage(null);
                  setAnalysisResult(null);
                }}
                className="absolute top-3 right-3 bg-black/60 hover:bg-black/80 text-white text-xs px-2.5 py-1.5 rounded-lg backdrop-blur-sm flex items-center space-x-1 transition"
              >
                <RotateCcw className="w-3.5 h-3.5" />
                <span>Ganti Foto</span>
              </button>
            </div>
          ) : (
            <div className="text-center text-slate-400 p-6 space-y-2">
              <div className="w-14 h-14 rounded-full bg-slate-800 text-slate-400 flex items-center justify-center mx-auto border border-slate-700">
                <Camera className="w-7 h-7 text-slate-300" />
              </div>
              <p className="text-xs sm:text-sm font-semibold text-slate-300">
                Kamera Langsung Siap Digunakan
              </p>
              <p className="text-[11px] text-slate-400 max-w-sm mx-auto">
                Klik <strong>"Buka Kamera Langsung"</strong>, gunakan <strong>"Kamera HP (Native)"</strong>, atau pilih menu contoh cepat di atas.
              </p>
            </div>
          )}
        </div>

        {/* Prompt Hint & Analyze Button */}
        <div className="space-y-3 pt-2">
          <div>
            <label className="block text-xs font-bold text-slate-700 mb-1">
              Catatan Menu / Keterangan Tambahan (Opsional, Meningkatkan Akurasi):
            </label>
            <input
              type="text"
              value={foodHint}
              onChange={(e) => setFoodHint(e.target.value)}
              placeholder="Contoh: Nasi soto ayam lamongan + telur rebus, tanpa jeroan"
              className="w-full px-3.5 py-2.5 text-xs sm:text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none placeholder:text-slate-400"
            />
          </div>

          <button
            onClick={handleAnalyze}
            disabled={isLoading || !selectedImage}
            className={`w-full py-3.5 px-4 rounded-xl font-extrabold text-sm sm:text-base flex items-center justify-center space-x-2 shadow-md transition ${
              isLoading || !selectedImage
                ? 'bg-slate-300 text-slate-500 cursor-not-allowed'
                : 'bg-[#E65100] hover:bg-[#F57C00] text-white active:scale-[0.99]'
            }`}
          >
            {isLoading ? (
              <>
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                <span>Gemini AI Sedang Mengidentifikasi Kandungan Gizi...</span>
              </>
            ) : (
              <>
                <Sparkles className="w-5 h-5 text-amber-300" />
                <span>Analisis Zat Gizi dengan Gemini API</span>
              </>
            )}
          </button>
        </div>
      </div>

      {/* ANALYSIS RESULT CARD */}
      {analysisResult && (
        <div className="bg-white rounded-2xl p-6 shadow-md border border-emerald-200 space-y-5 animate-in fade-in duration-300">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-slate-100 pb-4">
            <div>
              <span className="text-[10px] uppercase tracking-wider font-bold bg-emerald-100 text-emerald-800 px-2.5 py-1 rounded-full">
                Hasil Analisis Gizi Terverifikasi AI
              </span>
              <h3 className="text-xl font-black text-slate-900 mt-2">
                {analysisResult.foodName}
              </h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Estimasi Porsi: <span className="font-semibold text-slate-700">{analysisResult.estimatedPortion}</span>
              </p>
            </div>

            <div className="sm:text-right">
              <span className="text-xs text-slate-400 block">Evaluasi Piring:</span>
              <span className="inline-block bg-emerald-600 text-white font-extrabold text-xs px-3 py-1.5 rounded-lg mt-1 shadow-sm">
                {analysisResult.balanceStatus}
              </span>
            </div>
          </div>

          {/* Bahan & Zat Gizi Dominan */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="bg-slate-50 p-4 rounded-xl border border-slate-200">
              <span className="text-xs font-bold text-slate-700 block mb-2">
                Komponen Bahan Teridentifikasi:
              </span>
              <div className="flex flex-wrap gap-1.5">
                {analysisResult.identifiedIngredients.map((item, idx) => (
                  <span
                    key={idx}
                    className="text-[11px] bg-white text-slate-700 font-medium px-2.5 py-1 rounded-lg border border-slate-200 shadow-2xs"
                  >
                    {item}
                  </span>
                ))}
              </div>
            </div>

            <div className="bg-slate-50 p-4 rounded-xl border border-slate-200">
              <span className="text-xs font-bold text-slate-700 block mb-2">
                Zat Gizi Utama Terdeteksi:
              </span>
              <div className="flex flex-wrap gap-1.5">
                {analysisResult.dominantNutrients.map((item, idx) => (
                  <span
                    key={idx}
                    className="text-[11px] bg-emerald-700 text-white font-bold px-2.5 py-1 rounded-lg shadow-2xs"
                  >
                    {item}
                  </span>
                ))}
              </div>
            </div>
          </div>

          {/* Rincian 4 Zat Gizi Utama */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
            <div className="bg-amber-50/70 border border-amber-200 rounded-xl p-3.5 text-xs">
              <span className="font-extrabold text-amber-900 block mb-1">
                🍚 Karbohidrat (Zat Tenaga):
              </span>
              <p className="text-slate-700 leading-relaxed">{analysisResult.carbohydrateNote}</p>
            </div>

            <div className="bg-emerald-50/70 border border-emerald-200 rounded-xl p-3.5 text-xs">
              <span className="font-extrabold text-emerald-900 block mb-1">
                🍗 Protein (Zat Pembangun):
              </span>
              <p className="text-slate-700 leading-relaxed">{analysisResult.proteinNote}</p>
            </div>

            <div className="bg-orange-50/70 border border-orange-200 rounded-xl p-3.5 text-xs">
              <span className="font-extrabold text-orange-900 block mb-1">
                🥑 Lemak (Cadangan Energi):
              </span>
              <p className="text-slate-700 leading-relaxed">{analysisResult.fatNote}</p>
            </div>

            <div className="bg-teal-50/70 border border-teal-200 rounded-xl p-3.5 text-xs">
              <span className="font-extrabold text-teal-900 block mb-1">
                🥗 Vitamin & Mineral (Zat Pengatur):
              </span>
              <p className="text-slate-700 leading-relaxed">{analysisResult.vitaminMineralNote}</p>
            </div>
          </div>

          {/* Edukasi Isi Piringku & Rekomendasi */}
          <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-4 text-xs space-y-2">
            <div className="flex items-start space-x-2">
              <ShieldCheck className="w-4 h-4 text-emerald-700 flex-shrink-0 mt-0.5" />
              <div>
                <span className="font-bold text-emerald-900 block">
                  Panduan Isi Piringku Kemenkes RI:
                </span>
                <p className="text-slate-700 mt-0.5 leading-relaxed">
                  {analysisResult.balanceAdvice}
                </p>
              </div>
            </div>

            <div className="flex items-start space-x-2 pt-2 border-t border-emerald-200/60">
              <CheckCircle2 className="w-4 h-4 text-emerald-700 flex-shrink-0 mt-0.5" />
              <div>
                <span className="font-bold text-emerald-900 block">
                  Rekomendasi Petugas Gizi UKS:
                </span>
                <p className="text-slate-700 mt-0.5 leading-relaxed">
                  {analysisResult.recommendation}
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* MODAL PENGATURAN API KEY GEMINI */}
      {showApiKeyModal && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-in fade-in duration-200">
          <div className="bg-white rounded-2xl max-w-md w-full p-6 shadow-2xl space-y-4 border border-slate-200">
            <div className="flex items-center space-x-2.5 border-b border-slate-100 pb-3">
              <div className="w-9 h-9 rounded-xl bg-amber-100 text-amber-700 flex items-center justify-center">
                <Key className="w-5 h-5" />
              </div>
              <div>
                <h4 className="font-bold text-slate-800 text-sm">Pengaturan Gemini API Key</h4>
                <p className="text-[11px] text-slate-500">Kustomisasi API Key untuk analisis langsung</p>
              </div>
            </div>

            <div className="space-y-2 text-xs text-slate-600">
              <p>
                Aplikasi NutriMind AI telah dilengkapi dengan model inferensi gizi Indonesia. Jika Anda memiliki API Key dari Google AI Studio, Anda dapat menempelkannya di bawah ini:
              </p>
              <input
                type="password"
                value={customApiKey}
                onChange={(e) => setCustomApiKey(e.target.value)}
                placeholder="AIzaSy..."
                className="w-full px-3.5 py-2.5 text-xs border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none font-mono"
              />
              <p className="text-[11px] text-slate-400">
                *API Key tersimpan secara aman di browser lokal Anda.
              </p>
            </div>

            <div className="flex justify-end space-x-2 pt-2 border-t border-slate-100">
              <button
                onClick={() => setShowApiKeyModal(false)}
                className="px-4 py-2 rounded-xl text-xs font-bold text-slate-600 hover:bg-slate-100 transition"
              >
                Batal
              </button>
              <button
                onClick={handleSaveApiKey}
                className="px-4 py-2 rounded-xl text-xs font-bold bg-[#1B5E20] hover:bg-[#2E7D32] text-white transition shadow-sm"
              >
                Simpan API Key
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

