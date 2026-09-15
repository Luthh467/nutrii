import React, { useState, useRef } from 'react';
import { Camera, Sparkles, Upload, RotateCcw, AlertCircle, CheckCircle2, ChevronRight, Apple } from 'lucide-react';
import { FoodAnalysisResult } from '../types';
import { CANTEEN_PRESETS } from '../data/mockData';
import { analyzeFoodWithGemini } from '../services/geminiService';

export const CameraFoodAnalysisView: React.FC = () => {
  const [selectedImage, setSelectedImage] = useState<string | null>(null);
  const [foodHint, setFoodHint] = useState<string>('');
  const [isCameraActive, setIsCameraActive] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [analysisResult, setAnalysisResult] = useState<FoodAnalysisResult | null>(null);
  const [errorMessage, setErrorMessage] = useState<string>('');

  const videoRef = useRef<HTMLVideoElement | null>(null);
  const streamRef = useRef<MediaStream | null>(null);
  const fileInputRef = useRef<HTMLInputElement | null>(null);

  // Start live webcam
  const startCamera = async () => {
    setErrorMessage('');
    try {
      const stream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: 'environment', width: { ideal: 1280 }, height: { ideal: 720 } }
      });
      streamRef.current = stream;
      if (videoRef.current) {
        videoRef.current.srcObject = stream;
        videoRef.current.play();
      }
      setIsCameraActive(true);
    } catch (err: any) {
      console.error('Kamera error:', err);
      setErrorMessage('Tidak dapat mengakses kamera (mungkin izin belum diberikan atau perangkat tidak memiliki kamera). Silakan gunakan tombol "Unggah Foto / Galeri" atau pilih menu contoh.');
    }
  };

  // Stop live webcam
  const stopCamera = () => {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach((track) => track.stop());
      streamRef.current = null;
    }
    setIsCameraActive(false);
  };

  // Capture frame from webcam
  const capturePhoto = () => {
    if (!videoRef.current) return;
    const canvas = document.createElement('canvas');
    canvas.width = videoRef.current.videoWidth || 640;
    canvas.height = videoRef.current.videoHeight || 480;
    const ctx = canvas.getContext('2d');
    if (ctx) {
      ctx.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height);
      const dataUrl = canvas.toDataURL('image/jpeg', 0.85);
      setSelectedImage(dataUrl);
      stopCamera();
    }
  };

  // Handle file upload
  const handleFileUpload = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      const result = event.target?.result as string;
      setSelectedImage(result);
      stopCamera();
    };
    reader.readAsDataURL(file);
  };

  // Select Preset Menu
  const handleSelectPreset = (preset: typeof CANTEEN_PRESETS[0]) => {
    // Generate a pleasant SVG data URL for the preset
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
    stopCamera();
  };

  // Trigger Gemini Multimodal Analysis
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
      setErrorMessage(`Gagal menganalisis gizi makanan: ${err.message || 'Error tidak diketahui'}`);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 py-6 space-y-6">
      {/* Hero Banner */}
      <div className="bg-gradient-to-r from-[#1B5E20] to-[#2E7D32] text-white rounded-2xl p-6 shadow-md">
        <div className="flex items-center space-x-2 bg-white/10 px-3 py-1 rounded-full text-xs font-bold w-fit mb-2">
          <Sparkles className="w-3.5 h-3.5 text-amber-300" />
          <span>Multimodal Food Nutrition AI</span>
        </div>
        <h2 className="text-xl sm:text-2xl font-black">
          Analisis Zat Gizi Makanan dengan Kamera AI
        </h2>
        <p className="text-xs sm:text-sm text-emerald-100 mt-1 max-w-2xl">
          Foto hidangan makan siang atau jajanan kantin madrasahmu. Gemini AI akan mengenali zat gizi (Karbohidrat, Protein, Lemak, Vitamin & Mineral) serta evaluasi panduan "Isi Piringku".
        </p>
      </div>

      {/* Preset Quick-Test Buttons */}
      <div className="bg-white rounded-2xl p-4 shadow-sm border border-slate-200">
        <span className="block text-xs font-black uppercase text-slate-600 tracking-wider mb-2.5">
          Uji Cepat Menu Kantin Madrasah:
        </span>
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-2.5">
          {CANTEEN_PRESETS.map((preset, idx) => (
            <button
              key={idx}
              onClick={() => handleSelectPreset(preset)}
              className="p-2.5 rounded-xl border border-slate-200 bg-slate-50 hover:bg-emerald-50 hover:border-emerald-300 transition flex items-center space-x-2 text-left"
            >
              <span className="text-2xl">{preset.icon}</span>
              <div className="overflow-hidden">
                <div className="font-bold text-xs text-slate-800 truncate">{preset.label}</div>
                <div className="text-[10px] text-emerald-700">Analisis Instan</div>
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

          <div className="flex items-center space-x-2">
            {!isCameraActive ? (
              <button
                onClick={startCamera}
                className="bg-[#1B5E20] hover:bg-[#2E7D32] text-white text-xs font-bold px-3 py-2 rounded-xl transition flex items-center space-x-1.5 shadow-sm"
              >
                <Camera className="w-3.5 h-3.5" />
                <span>Buka Kamera</span>
              </button>
            ) : (
              <button
                onClick={stopCamera}
                className="bg-red-600 hover:bg-red-700 text-white text-xs font-bold px-3 py-2 rounded-xl transition"
              >
                Tutup Kamera
              </button>
            )}

            <button
              onClick={() => fileInputRef.current?.click()}
              className="bg-slate-100 hover:bg-slate-200 text-slate-700 text-xs font-bold px-3 py-2 rounded-xl transition flex items-center space-x-1.5"
            >
              <Upload className="w-3.5 h-3.5" />
              <span>Unggah Foto</span>
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
          <div className="bg-red-50 border border-red-200 text-red-700 rounded-xl p-3 text-xs flex items-center space-x-2">
            <AlertCircle className="w-4 h-4 flex-shrink-0 text-red-600" />
            <span>{errorMessage}</span>
          </div>
        )}

        {/* Viewfinder or Image Preview */}
        <div className="relative aspect-video max-h-80 bg-slate-900 rounded-2xl overflow-hidden flex items-center justify-center border border-slate-300">
          {isCameraActive ? (
            <>
              <video
                ref={videoRef}
                autoPlay
                playsInline
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-x-0 bottom-4 flex justify-center">
                <button
                  onClick={capturePhoto}
                  className="bg-white text-emerald-900 font-extrabold px-6 py-2.5 rounded-full shadow-lg border-4 border-emerald-500 hover:scale-105 active:scale-95 transition flex items-center space-x-2"
                >
                  <Camera className="w-5 h-5 text-emerald-700" />
                  <span>Jepret Foto Sekarang</span>
                </button>
              </div>
            </>
          ) : selectedImage ? (
            <div className="relative w-full h-full">
              <img
                src={selectedImage}
                alt="Makanan Terpilih"
                className="w-full h-full object-contain bg-black/40"
              />
              <button
                onClick={() => setSelectedImage(null)}
                className="absolute top-3 right-3 bg-black/60 hover:bg-black/80 text-white text-xs px-2.5 py-1.5 rounded-lg backdrop-blur-sm flex items-center space-x-1 transition"
              >
                <RotateCcw className="w-3.5 h-3.5" />
                <span>Ganti Foto</span>
              </button>
            </div>
          ) : (
            <div className="text-center text-slate-400 p-6">
              <Camera className="w-12 h-12 mx-auto mb-2 text-slate-500" />
              <p className="text-xs sm:text-sm font-medium">
                Kamera belum aktif. Klik "Buka Kamera", "Unggah Foto", atau klik menu contoh di atas.
              </p>
            </div>
          )}
        </div>

        {/* Prompt Hint & Analyze Button */}
        <div className="space-y-3 pt-2">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Catatan Menu / Keterangan Tambahan (Opsional):
            </label>
            <input
              type="text"
              value={foodHint}
              onChange={(e) => setFoodHint(e.target.value)}
              placeholder="Contoh: Soto ayam dengan nasi putih dan telur tanpa jeroan"
              className="w-full px-3 py-2 text-xs sm:text-sm border border-slate-300 rounded-xl focus:ring-2 focus:ring-emerald-500 focus:outline-none"
            />
          </div>

          <button
            onClick={handleAnalyze}
            disabled={isLoading || !selectedImage}
            className={`w-full py-3 px-4 rounded-xl font-extrabold text-sm sm:text-base flex items-center justify-center space-x-2 shadow-md transition ${
              isLoading || !selectedImage
                ? 'bg-slate-300 text-slate-500 cursor-not-allowed'
                : 'bg-[#E65100] hover:bg-[#F57C00] text-white'
            }`}
          >
            {isLoading ? (
              <>
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
                <span>Gemini AI Sedang Mengidentifikasi Kandungan Gizi...</span>
              </>
            ) : (
              <>
                <Sparkles className="w-5 h-5" />
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
                Hasil Analisis Gizi Terverifikasi
              </span>
              <h3 className="text-xl font-black text-slate-900 mt-2">
                {analysisResult.foodName}
              </h3>
              <p className="text-xs text-slate-500 mt-0.5">
                Estimasi Porsi: <span className="font-semibold text-slate-700">{analysisResult.estimatedPortion}</span>
              </p>
            </div>

            <div className="text-right">
              <span className="text-xs text-slate-400 block">Evaluasi Piring:</span>
              <span className="inline-block bg-emerald-600 text-white font-extrabold text-xs px-3 py-1 rounded-lg mt-1 shadow-sm">
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
            <div className="bg-amber-50/60 border border-amber-200 rounded-xl p-3.5 text-xs">
              <span className="font-extrabold text-amber-900 block mb-1">
                🍚 Karbohidrat:
              </span>
              <p className="text-slate-700 leading-relaxed">{analysisResult.carbohydrateNote}</p>
            </div>

            <div className="bg-blue-50/60 border border-blue-200 rounded-xl p-3.5 text-xs">
              <span className="font-extrabold text-blue-900 block mb-1">
                🍗 Protein:
              </span>
              <p className="text-slate-700 leading-relaxed">{analysisResult.proteinNote}</p>
            </div>

            <div className="bg-orange-50/60 border border-orange-200 rounded-xl p-3.5 text-xs">
              <span className="font-extrabold text-orange-900 block mb-1">
                🥑 Lemak:
              </span>
              <p className="text-slate-700 leading-relaxed">{analysisResult.fatNote}</p>
            </div>

            <div className="bg-emerald-50/60 border border-emerald-200 rounded-xl p-3.5 text-xs">
              <span className="font-extrabold text-emerald-900 block mb-1">
                🥗 Vitamin & Mineral:
              </span>
              <p className="text-slate-700 leading-relaxed">{analysisResult.vitaminMineralNote}</p>
            </div>
          </div>

          {/* Edukasi Isi Piringku & Rekomendasi */}
          <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-4 text-xs space-y-2">
            <div className="flex items-start space-x-2">
              <Apple className="w-4 h-4 text-emerald-700 flex-shrink-0 mt-0.5" />
              <div>
                <span className="font-bold text-emerald-900 block">
                  Panduan Isi Piringku & Keseimbangan:
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
                  Rekomendasi Kebiasaan Makan Remaja:
                </span>
                <p className="text-slate-700 mt-0.5 leading-relaxed">
                  {analysisResult.recommendation}
                </p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
