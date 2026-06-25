# 🌿 HealthyLife: Aplikasi Pemantauan Aktivitas dan Kebiasaan Sehat

HealthyLife adalah aplikasi mobile berbasis Android yang membantu pengguna memantau dan mengelola gaya hidup sehat secara komprehensif. Aplikasi ini mengintegrasikan empat aspek utama kesehatan — **aktivitas olahraga**, **pola nutrisi**, **kualitas tidur**, dan **profil kesehatan pribadi** — dalam satu platform yang mudah digunakan.

Dikembangkan menggunakan **Kotlin** dan **Jetpack Compose**, HealthyLife mengusung konsep **dark mode** dengan kombinasi warna navy sebagai warna utama, hijau (`HealthGreen`) sebagai simbol kesehatan, dan aksen biru langit (`SkyBlue`) untuk memberikan kesan profesional, modern, dan elegan.

---

## 📌 Tentang Project

> Tugas Kelompok 6 — Mata Kuliah Pemrograman Mobile A
> Program Studi Sarjana Teknologi Informasi, Fakultas Teknik, Universitas Udayana (2026)

**Dosen Pengampu:** Anak Agung Ketut Agung Cahyawan Wiranatha, ST, MT

**Disusun oleh:**
| Nama | NIM |
|---|---|
| I Gusti Bagus Narendratanaya Wiweka | 2405551007 |
| Anak Agung Narendera Sancaya | 2405551038 |
| I Made Sandika Wijaya | 2405551082 |

---

## 🎯 Tujuan Aplikasi

HealthyLife bertujuan menyediakan platform terpadu yang membantu pengguna menerapkan pola hidup sehat secara konsisten, melalui:

- Pelacakan aktivitas fisik harian
- Pemantauan asupan kalori serta kandungan nutrisi makanan
- Pencatatan durasi dan kualitas tidur setiap hari
- Ringkasan aktivitas kesehatan harian secara terintegrasi
- Fitur **streak motivasi** untuk meningkatkan konsistensi kebiasaan sehat
- Profil kesehatan dengan perhitungan **Body Mass Index (BMI)**

---

## 🛠️ Teknologi dan Dependensi Utama

| Komponen | Teknologi |
|---|---|
| Bahasa Pemrograman | Kotlin (target JVM 11) |
| UI Framework | Jetpack Compose + Material Design 3 |
| Navigasi | Navigation Compose (`NavHost`, `NavController`) |
| Ikon | Material Icons Extended |
| Build System | Gradle KTS (Kotlin DSL), `libs.versions.toml` |
| Min SDK | API 24 (Android 7.0) |
| Target SDK | API 36 |

---

## 📂 Struktur Direktori

```
com.example.healthylife
│
├── MainActivity.kt
│
├── navigation
│   ├── AppNavigation.kt
│   └── Screen.kt
│
├── model
│   ├── Food.kt
│   ├── SleepRecord.kt
│   ├── Exercise.kt
│   └── User.kt
│
├── data
│   └── DummyData.kt
│
├── ui
│   ├── screens
│   │   ├── HomeScreen.kt
│   │   ├── ExerciseScreen.kt
│   │   ├── NutritionScreen.kt
│   │   ├── SleepScreen.kt
│   │   ├── ProgressScreen.kt
│   │   └── ProfileScreen.kt
│   │
│   ├── components
│   │   ├── BottomBar.kt
│   │   ├── ExerciseCard.kt
│   │   ├── StreakCard.kt
│   │   ├── SummaryCard.kt
│   │   └── PortionCard.kt
│   │
│   └── theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
```

Struktur ini disusun berdasarkan prinsip **separation of concerns**, sehingga setiap komponen aplikasi ditempatkan pada package yang sesuai dengan peran dan fungsinya:

- **`navigation`** — mengelola navigasi antarhalaman
- **`model`** — model data utama (makanan, olahraga, tidur, pengguna)
- **`data`** — sumber data sementara (dummy/mock repository)
- **`ui/screens`** — halaman utama aplikasi
- **`ui/components`** — komponen UI yang dapat digunakan kembali
- **`ui/theme`** — definisi warna, tema, dan tipografi

---

## ✨ Fitur Aplikasi

Navigasi aplikasi diatur secara dinamis melalui **5 menu utama** pada Bottom Navigation Bar.

### 🏠 1. Dashboard / Home Screen
- **Greeting & User Profile Banner** — sapaan personal berdasarkan nama akun dan avatar pengguna
- **Streak Counter** — indikator konsistensi penggunaan aplikasi secara berturut-turut
- **Animated Progress Rings** — tiga lingkaran progress untuk Kalori, Olahraga, dan Tidur
- **Quick Add Dialogs** — log cepat tidur, makanan, dan olahraga langsung dari halaman utama
- **Riwayat Aktivitas Hari Ini** — daftar olahraga harian beserta kalori terbakar
- **Ringkasan Nutrisi & Progress Makro** — total kalori serta rincian karbohidrat, protein, dan lemak
- **Insight Mingguan (Smart Insights)** — kartu geser berisi tips/analisis kebiasaan berbasis data

### 🏃 2. Exercise Screen
- Statistik kebugaran harian (durasi, kalori terbakar, jumlah sesi)
- Grid pemilihan 6 jenis olahraga: Running, Walking, Yoga, Gym, Cycling, Swimming
- Slider durasi olahraga (15–120 menit) dengan tombol preset cepat
- Estimasi kalori terbakar secara real-time
- Riwayat olahraga kronologis

### 🍽️ 3. Nutrition Screen
- Calorie Target & Tracker dengan visualisasi progres
- Macronutrient Summary (Karbohidrat, Protein, Lemak)
- Filter jenis makanan (Breakfast, Lunch, Dinner, Snack, Semua)
- Pencarian makanan dinamis
- Quick Add Food — tambah makanan ke log harian dengan satu tombol

### 😴 4. Sleep Screen
- Ringkasan tidur semalam (durasi, bedtime, wake-up time, kualitas)
- Analisis tren tidur mingguan vs target harian
- Grafik batang mingguan dengan warna dinamis berdasarkan kualitas tidur
- Pencatat kualitas tidur (Excellent, Normal, Poor) dengan ikon emoji
- Riwayat tidur terperinci

### 👤 5. Profile Screen
- Informasi demografis & akun, lencana streak aktif
- Kartu dimensi fisik (umur, tinggi, berat badan)
- Kalkulator BMI (Body Mass Index) otomatis
- Visualisasi skala BMI dengan kategori warna (Kurus/Teal, Normal/Hijau, Gemuk-Obesitas/Pink)

---

## 🧩 Progress Pengerjaan Teknis

### Modul Model
Lapisan data terstruktur yang mendefinisikan entitas bisnis aplikasi:
- `User.kt` — skema profil pengguna (nama, berat, tinggi, target kalori/tidur/olahraga, streak)
- `Food.kt` — entri konsumsi makanan (nama, emoji, mealType, kalori, makronutrien)
- `Exercise.kt` — data latihan olahraga (jenis, durasi, kalori terbakar, tanggal)
- `SleepRecord.kt` — data kualitas tidur (tanggal, bedtime, wake time, durasi, kualitas)

### Modul Data
- `DummyData.kt` — mock repository berisi data in-memory user aktif, riwayat olahraga, log makanan, dan catatan tidur, lengkap dengan kalkulasi agregat dan logika `smartInsights`.

### Modul UI
- **Screens** — `HomeScreen`, `NutritionScreen`, `SleepScreen`, `ExerciseScreen`, `ProfileScreen`
- **Navigation** — `AppNavigation.kt` mengatur transisi antarlayar via Navigation Compose
- **Components** — komponen terpisah (`BottomBar`, `ExerciseCard`, `StreakCard`, `SummaryCard`) untuk mempermudah maintenance

### Modul Desain
- `Color.kt` — palet warna dark mode (HealthGreen, AccentTeal, AccentSage) + opasitas glassmorphism
- `Theme.kt` — pengelolaan ColorScheme Dark/Light secara dinamis via `CompositionLocalProvider`

---

## ✅ Fitur yang Telah Diselesaikan

- Sistem Navigasi Utama via Bottom Navigation Bar
- Dashboard Agregat Dinamis dengan indikator melingkar beranimasi
- Fitur Catat Cepat (Quick Add) untuk tidur, makanan, dan olahraga
- Penyaringan & Pencarian Makanan
- Bottom Sheet Entri Nutrisi Baru
- Pengubah Tema Instan (dark/light mode)

---

## 📱 Tampilan Aplikasi

| Home | Exercise | Nutrition |
|---|---|---|
| Dashboard ringkasan kalori, olahraga, tidur, dan insight mingguan | Pencatatan olahraga dengan slider durasi & estimasi kalori | Pelacakan kalori, filter & pencarian makanan |

| Sleep | Profile |
|---|---|
| Pemantauan kualitas dan riwayat tidur mingguan | Data biometrik pengguna & kalkulator BMI |

> Screenshot lengkap tersedia pada laporan progress project.

---

## 🔗 Repository

GitHub: [Sands225/Healthy-Life_Mobile-Apps](https://github.com/Sands225/Healthy-Life_Mobile-Apps.git)
