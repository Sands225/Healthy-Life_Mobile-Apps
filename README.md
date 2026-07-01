# 🌿 HealthyLife Mobile Apps
### *Track Better. Live Healthier.*

HealthyLife merupakan aplikasi Android yang membantu pengguna membangun kebiasaan hidup sehat melalui pemantauan aktivitas olahraga, nutrisi, tidur, serta profil kesehatan dalam satu aplikasi yang sederhana, modern, dan mudah digunakan.

> 📱 Tugas Project Mata Kuliah **Pemrograman Mobile A**  
> Program Studi Teknologi Informasi  
> Fakultas Teknik — Universitas Udayana (2026)

---

# 📖 Tentang Project

HealthyLife dikembangkan sebagai aplikasi **Health & Lifestyle Tracker** yang memungkinkan pengguna mencatat berbagai aktivitas kesehatannya setiap hari.

Aplikasi mengintegrasikan beberapa aspek kesehatan dalam satu dashboard sehingga pengguna dapat memonitor perkembangan gaya hidup sehat secara lebih efektif.

Fokus utama aplikasi meliputi:

- 🏃 Aktivitas olahraga
- 🥗 Nutrisi dan konsumsi makanan
- 😴 Monitoring tidur
- ❤️ Profil kesehatan pengguna
- 📊 Visualisasi statistik aktivitas

---

# ✨ Fitur yang Telah Diimplementasikan

## 🏠 Home Dashboard

- Ringkasan aktivitas harian
- Statistik kesehatan
- Ring Progress
- Bar Chart Analytics
- Quick Navigation Menu

---

## 🏃 Exercise Tracker

- Daftar aktivitas olahraga
- Durasi olahraga
- Kalori terbakar
- Riwayat olahraga
- RecyclerView Adapter

---

## 🥗 Nutrition Tracker

- Daftar makanan
- Informasi kalori
- Tracking konsumsi nutrisi
- RecyclerView Adapter

---

## 😴 Sleep Tracker

- Durasi tidur
- Riwayat tidur
- Monitoring jam tidur
- RecyclerView Adapter

---

## 👤 Profile

- Informasi pengguna
- Body Mass Index (BMI)
- Data kesehatan dasar

---

## ⚙ Settings

- Pengaturan aplikasi
- Theme Preferences
- Pengaturan tampilan

---

## 📈 Analytics

HealthyLife telah memiliki komponen visualisasi data berupa:

- Ring Progress Indicator
- Bar Chart
- Activity Analytics
- Daily Summary

---

# 🏗 Arsitektur Project

Project menggunakan struktur package yang cukup modular.

```
app/src/main
│
├── java/com/example/healthylife
│   │
│   ├── MainActivity.kt 
│   │
│   ├── data                      
│   │   ├── DatabaseHelper.kt         
│   │   ├── DummyData.kt            
│   │   └── HealthRepository.kt     
│   │
│   ├── model                          
│   │   ├── User.kt                   
│   │   ├── Exercise.kt                
│   │   ├── Food.kt
│   │   └── SleepRecord.kt             
│   │
│   ├── ui                             
│   │   └── view
│   │       ├── Navigator.kt           
│   │       ├── HomeFragment.kt        
│   │       ├── ExerciseFragment.kt    
│   │       ├── NutritionFragment.kt   
│   │       ├── SleepFragment.kt       
│   │       ├── ProfileFragment.kt     
│   │       ├── SettingsFragment.kt    
│   │       │
│   │       ├── adapter                
│   │       │   ├── ExerciseAdapter.kt 
│   │       │   ├── FoodAdapter.kt     
│   │       │   └── SleepAdapter.kt    
│   │       │
│   │       └── widget                
│   │           ├── RingProgressView.kt 
│   │           ├── BarChartView.kt    
│   │           ├── AnalyticsBinder.kt 
│   │           └── Segmented.kt       
│   │
│   └── util                           
│       ├── Analytics.kt               
│       ├── DateUtils.kt               
│       ├── ThemePrefs.kt              
│       └── TimeFilter.kt              
│
└── res                                
    │
    ├── layout                         
    │   ├── activity_main.xml          
    │   │
    │   ├── fragment_home.xml          
    │   ├── fragment_exercise.xml      
    │   ├── fragment_nutrition.xml     
    │   ├── fragment_sleep.xml         
    │   ├── fragment_profile.xml       
    │   ├── fragment_settings.xml      
    │   │
    │   ├── item_bottom_tab.xml        
    │   ├── item_exercise.xml          
    │   ├── item_food.xml              
    │   ├── item_sleep.xml             
    │   ├── item_progress_ring.xml     
    │   ├── item_quick_add.xml         
    │   │
    │   ├── dialog_quick_add.xml       
    │   ├── dialog_exercise_form.xml    
    │   ├── dialog_food_form.xml 
    │   └── dialog_edit_sleep.xml       
    │
    └── values                         
        ├── colors.xml 
        ├── strings.xml                
        └── themes.xml                 
```

---

# 🛠 Tech Stack

| Komponen | Teknologi |
|-----------|-----------|
| Language | Kotlin |
| Minimum SDK | API 24 |
| Target JVM | Java 11 |
| IDE | Android Studio |
| Database | SQLite |
| UI | XML Layout |
| Navigation | Fragment Navigation |
| Build System | Gradle Kotlin DSL |
| Material Components | Material Design |
| RecyclerView | ✔ |
| Custom View | ✔ |
| SharedPreferences | ✔ |

---

# 📂 Penyimpanan Data

HealthyLife menggunakan kombinasi beberapa mekanisme penyimpanan data:

- SQLite Database
- SharedPreferences
- Dummy Data Repository (development)

---

# 📊 Visual Components

Aplikasi memiliki beberapa komponen visual yang dibuat secara khusus.

- 📈 Custom Bar Chart
- 🔵 Ring Progress Indicator
- 📊 Activity Analytics
- 📅 Daily Summary

---

# 📸 Tampilan Aplikasi

> Tambahkan screenshot aplikasi pada folder berikut:

```
docs/
│
├── home.png
├── exercise.png
├── nutrition.png
├── sleep.png
├── profile.png
└── settings.png
```

Kemudian tampilkan menggunakan Markdown:

```md
## Home

![Home](docs/home.png)

## Exercise

![Exercise](docs/exercise.png)

## Nutrition

![Nutrition](docs/nutrition.png)

## Sleep

![Sleep](docs/sleep.png)

## Profile

![Profile](docs/profile.png)

## Settings

![Settings](docs/settings.png)
```

---

# 🚀 Cara Menjalankan Project

### Clone Repository

```bash
git clone https://github.com/username/Healthy-Life_Mobile-Apps.git
```

### Buka Project

```
Android Studio
Open Existing Project
```

### Build

```
Sync Gradle
Run App
```

atau

```
Shift + F10
```

---

# 👨‍💻 Tim Pengembang

| Nama | NIM |
|------|------|
| I Gusti Bagus Narendratanaya Wiweka | 2405551007 |
| Anak Agung Narendera Sancaya | 2405551038 |
| I Made Sandika Wijaya | 2405551082 |

---

# 🎯 Roadmap

### ✅ Selesai

- Home Dashboard
- Exercise Module
- Nutrition Module
- Sleep Module
- Profile
- Settings
- SQLite Database
- Analytics Widget
- RecyclerView
- Theme Preference

### 🚧 Dalam Pengembangan

- Authentication
- Daily Reminder
- Notification
- Goal Tracking
- Achievement System
- Export Health Report
- Cloud Synchronization

---

# 📜 Lisensi

Project ini dikembangkan untuk keperluan akademik pada Mata Kuliah **Pemrograman Mobile A** Program Studi Teknologi Informasi Universitas Udayana.

---

<div align="center">

### 🌿 HealthyLife

**Healthy habits start with small daily actions.**

</div>
