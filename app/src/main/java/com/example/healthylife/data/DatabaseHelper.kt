package com.example.healthylife.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.healthylife.util.DateUtils

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "healthylife.db"
        private const val DATABASE_VERSION = 8

        // Table: users
        const val TABLE_USERS = "users"
        const val KEY_USER_ID = "id"
        const val KEY_USER_NAME = "name"
        const val KEY_USER_AGE = "age"
        const val KEY_USER_WEIGHT = "weight"
        const val KEY_USER_HEIGHT = "height"
        const val KEY_USER_TARGET_CALORIES = "target_calories"
        const val KEY_USER_TARGET_SLEEP_HOURS = "target_sleep_hours"
        const val KEY_USER_TARGET_EXERCISE_MINUTES = "target_exercise_minutes"

        // Table: exercises
        const val TABLE_EXERCISES = "exercises"
        const val KEY_EXERCISE_ID = "id"
        const val KEY_EXERCISE_NAME = "name"
        const val KEY_EXERCISE_EMOJI = "emoji"
        const val KEY_EXERCISE_DURATION_MINUTES = "duration_minutes"
        const val KEY_EXERCISE_CALORIES_BURNED = "calories_burned"
        const val KEY_EXERCISE_DATE = "date"

        // Table: foods
        const val TABLE_FOODS = "foods"
        const val KEY_FOOD_ID = "id"
        const val KEY_FOOD_NAME = "name"
        const val KEY_FOOD_EMOJI = "emoji"
        const val KEY_FOOD_CALORIES = "calories"
        const val KEY_FOOD_CARBS = "carbs"
        const val KEY_FOOD_PROTEIN = "protein"
        const val KEY_FOOD_FAT = "fat"
        const val KEY_FOOD_FIBER = "fiber"
        const val KEY_FOOD_MEAL_TYPE = "meal_type"
        const val KEY_FOOD_DATE = "date"

        // Table: sleep_records
        const val TABLE_SLEEP_RECORDS = "sleep_records"
        const val KEY_SLEEP_ID = "id"
        const val KEY_SLEEP_DATE = "date"
        const val KEY_SLEEP_BED_TIME = "bed_time"
        const val KEY_SLEEP_WAKE_TIME = "wake_time"
        const val KEY_SLEEP_DURATION_HOURS = "duration_hours"
        const val KEY_SLEEP_QUALITY = "quality"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create users table
        val createUsersTable = ("CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER PRIMARY KEY,"
                + KEY_USER_NAME + " TEXT,"
                + KEY_USER_AGE + " INTEGER,"
                + KEY_USER_WEIGHT + " REAL,"
                + KEY_USER_HEIGHT + " REAL,"
                + KEY_USER_TARGET_CALORIES + " INTEGER,"
                + KEY_USER_TARGET_SLEEP_HOURS + " REAL,"
                + KEY_USER_TARGET_EXERCISE_MINUTES + " INTEGER" + ")")
        db.execSQL(createUsersTable)

        // Create exercises table
        val createExercisesTable = ("CREATE TABLE " + TABLE_EXERCISES + "("
                + KEY_EXERCISE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_EXERCISE_NAME + " TEXT,"
                + KEY_EXERCISE_EMOJI + " TEXT,"
                + KEY_EXERCISE_DURATION_MINUTES + " INTEGER,"
                + KEY_EXERCISE_CALORIES_BURNED + " INTEGER,"
                + KEY_EXERCISE_DATE + " TEXT" + ")")
        db.execSQL(createExercisesTable)

        // Create foods table
        val createFoodsTable = ("CREATE TABLE " + TABLE_FOODS + "("
                + KEY_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_FOOD_NAME + " TEXT,"
                + KEY_FOOD_EMOJI + " TEXT,"
                + KEY_FOOD_CALORIES + " INTEGER,"
                + KEY_FOOD_CARBS + " REAL,"
                + KEY_FOOD_PROTEIN + " REAL,"
                + KEY_FOOD_FAT + " REAL,"
                + KEY_FOOD_FIBER + " REAL,"
                + KEY_FOOD_MEAL_TYPE + " TEXT,"
                + KEY_FOOD_DATE + " TEXT" + ")")
        db.execSQL(createFoodsTable)

        // Create sleep_records table
        val createSleepTable = ("CREATE TABLE " + TABLE_SLEEP_RECORDS + "("
                + KEY_SLEEP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_SLEEP_DATE + " TEXT,"
                + KEY_SLEEP_BED_TIME + " TEXT,"
                + KEY_SLEEP_WAKE_TIME + " TEXT,"
                + KEY_SLEEP_DURATION_HOURS + " REAL,"
                + KEY_SLEEP_QUALITY + " TEXT" + ")")
        db.execSQL(createSleepTable)

        // Seed initial dummy data
        seedInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FOODS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SLEEP_RECORDS")
        onCreate(db)
    }

    // How many days of history to generate dummy data for.
    private val SEED_DAYS = 60

    private fun seedInitialData(db: SQLiteDatabase) {
        // 1. Seed user
        val userValues = ContentValues().apply {
            put(KEY_USER_ID, 1)
            put(KEY_USER_NAME, "Sandika")
            put(KEY_USER_AGE, 22)
            put(KEY_USER_WEIGHT, 68.0f)
            put(KEY_USER_HEIGHT, 172.0f)
            put(KEY_USER_TARGET_CALORIES, 2000)
            put(KEY_USER_TARGET_SLEEP_HOURS, 8.0f)
            put(KEY_USER_TARGET_EXERCISE_MINUTES, 45)
        }
        db.insert(TABLE_USERS, null, userValues)

        seedExercises(db)
        seedFoods(db)
        seedSleep(db)
    }

    // 2. Seed exercises: cycles through varied exercise templates across SEED_DAYS days,
    // with an extra exercise thrown in every few days for realistic variety.
    private fun seedExercises(db: SQLiteDatabase) {
        data class ExerciseTemplate(val name: String, val emoji: String, val minMin: Int, val maxMin: Int, val calPerMin: Float)

        val templates = listOf(
            ExerciseTemplate("Running", "🏃", 20, 45, 9.0f),
            ExerciseTemplate("Walking", "🚶", 15, 40, 4.5f),
            ExerciseTemplate("Gym", "🏋️", 30, 60, 8.5f),
            ExerciseTemplate("Yoga", "🧘", 20, 45, 4.0f),
            ExerciseTemplate("Cycling", "🚴", 25, 55, 8.2f),
            ExerciseTemplate("Swimming", "🏊", 20, 45, 10.0f),
            ExerciseTemplate("Badminton", "🏸", 30, 60, 7.0f),
            ExerciseTemplate("Basketball", "🏀", 30, 60, 8.8f),
            ExerciseTemplate("Hiking", "🥾", 40, 90, 6.5f),
            ExerciseTemplate("Jump Rope", "🤸", 10, 25, 12.0f),
            ExerciseTemplate("Football", "⚽", 40, 70, 8.0f),
            ExerciseTemplate("Pilates", "🤸‍♀️", 25, 40, 4.2f)
        )

        val entries = mutableListOf<ContentValues>()
        for (day in 0 until SEED_DAYS) {
            // Skip roughly every 4th day so the history isn't a perfectly unbroken streak.
            if (day % 4 == 3) continue

            val primary = templates[day % templates.size]
            val primaryDuration = primary.minMin + (day * 7) % (primary.maxMin - primary.minMin + 1)
            val primaryCalories = (primaryDuration * primary.calPerMin).toInt()
            entries.add(ContentValues().apply {
                put(KEY_EXERCISE_NAME, primary.name)
                put(KEY_EXERCISE_EMOJI, primary.emoji)
                put(KEY_EXERCISE_DURATION_MINUTES, primaryDuration)
                put(KEY_EXERCISE_CALORIES_BURNED, primaryCalories)
                put(KEY_EXERCISE_DATE, DateUtils.getRelativeDateString(day))
            })

            // Every 3rd active day, log a second exercise session that day.
            if (day % 3 == 0) {
                val secondary = templates[(day + 5) % templates.size]
                val secondaryDuration = secondary.minMin + (day * 11) % (secondary.maxMin - secondary.minMin + 1)
                val secondaryCalories = (secondaryDuration * secondary.calPerMin).toInt()
                entries.add(ContentValues().apply {
                    put(KEY_EXERCISE_NAME, secondary.name)
                    put(KEY_EXERCISE_EMOJI, secondary.emoji)
                    put(KEY_EXERCISE_DURATION_MINUTES, secondaryDuration)
                    put(KEY_EXERCISE_CALORIES_BURNED, secondaryCalories)
                    put(KEY_EXERCISE_DATE, DateUtils.getRelativeDateString(day))
                })
            }
        }
        for (ex in entries) {
            db.insert(TABLE_EXERCISES, null, ex)
        }
    }

    // 3. Seed foods: cycles through varied Indonesian food templates for each meal type,
    // producing a full day's worth of meals (Sarapan, Makan Siang, Makan Malam, Makanan
    // Ringan) for every day in SEED_DAYS.
    private fun seedFoods(db: SQLiteDatabase) {
        data class FoodTemplate(
            val name: String, val emoji: String, val calories: Int,
            val carbs: Float, val protein: Float, val fat: Float, val fiber: Float
        )

        val breakfast = listOf(
            FoodTemplate("Oatmeal + Pisang", "🥣", 350, 60f, 12f, 6f, 8f),
            FoodTemplate("Roti Gandum + Telur", "🍞", 300, 35f, 16f, 10f, 5f),
            FoodTemplate("Bubur Ayam", "🥣", 310, 45f, 14f, 8f, 2f),
            FoodTemplate("Telur Dadar + Nasi", "🍳", 380, 50f, 15f, 12f, 2f),
            FoodTemplate("Nasi Uduk", "🍚", 400, 55f, 10f, 15f, 3f),
            FoodTemplate("Lontong Sayur", "🍲", 360, 48f, 12f, 12f, 4f),
            FoodTemplate("Nasi Kuning", "🍚", 420, 58f, 11f, 16f, 3f),
            FoodTemplate("Bubur Kacang Hijau", "🥣", 290, 50f, 10f, 6f, 6f),
            FoodTemplate("Roti Bakar Keju", "🧀", 340, 38f, 13f, 14f, 2f),
            FoodTemplate("Pancake Madu", "🥞", 320, 52f, 8f, 9f, 2f),
            FoodTemplate("Ketupat Sayur", "🍲", 390, 50f, 12f, 14f, 4f),
            FoodTemplate("Sereal + Susu", "🥛", 280, 45f, 10f, 6f, 3f),
            FoodTemplate("Nasi Kuning Ayam Suwir", "🍗", 450, 55f, 20f, 15f, 3f),
            FoodTemplate("Mie Rebus Telur", "🍜", 340, 48f, 14f, 10f, 2f),
            FoodTemplate("Bubur Manado", "🍲", 300, 42f, 10f, 8f, 6f),
            FoodTemplate("Roti Isi Cokelat", "🍫", 310, 44f, 7f, 12f, 3f)
        )
        val lunch = listOf(
            FoodTemplate("Nasi Goreng Ayam", "🍳", 480, 55f, 22f, 14f, 3f),
            FoodTemplate("Soto Ayam", "🍲", 340, 30f, 20f, 12f, 3f),
            FoodTemplate("Mie Gacoan", "🍜", 380, 50f, 15f, 12f, 3f),
            FoodTemplate("Bakso Sapi", "🍜", 350, 40f, 18f, 12f, 3f),
            FoodTemplate("Ayam Geprek", "🍗", 520, 40f, 35f, 20f, 4f),
            FoodTemplate("Nasi Padang", "🍛", 600, 65f, 30f, 25f, 4f),
            FoodTemplate("Sate Ayam", "🍢", 430, 30f, 28f, 18f, 3f),
            FoodTemplate("Rendang + Nasi", "🍛", 650, 55f, 35f, 32f, 4f),
            FoodTemplate("Ayam Penyet", "🍗", 540, 45f, 32f, 22f, 3f),
            FoodTemplate("Nasi Campur Bali", "🍛", 560, 60f, 28f, 20f, 5f),
            FoodTemplate("Mie Ayam", "🍜", 400, 52f, 18f, 13f, 3f),
            FoodTemplate("Soto Betawi", "🍲", 480, 25f, 24f, 30f, 3f),
            FoodTemplate("Nasi Bebek Goreng", "🦆", 600, 50f, 32f, 30f, 3f),
            FoodTemplate("Gudeg Ayam", "🍛", 520, 60f, 22f, 18f, 4f),
            FoodTemplate("Sop Buntut", "🍖", 490, 22f, 30f, 28f, 3f),
            FoodTemplate("Nasi Rawon", "🍛", 470, 45f, 26f, 18f, 4f),
            FoodTemplate("Pecel Lele", "🐟", 500, 42f, 28f, 22f, 4f)
        )
        val dinner = listOf(
            FoodTemplate("Gado-Gado", "🥗", 400, 35f, 15f, 20f, 8f),
            FoodTemplate("Salad Sayur", "🥗", 210, 18f, 8f, 10f, 6f),
            FoodTemplate("Tahu Tempe Goreng", "🍢", 220, 18f, 12f, 10f, 4f),
            FoodTemplate("Ikan Bakar + Nasi", "🐟", 470, 45f, 32f, 15f, 3f),
            FoodTemplate("Capcay", "🥦", 260, 25f, 12f, 8f, 6f),
            FoodTemplate("Sop Iga", "🍖", 480, 20f, 30f, 28f, 3f),
            FoodTemplate("Sayur Asem + Ikan Asin", "🍲", 320, 30f, 18f, 12f, 6f),
            FoodTemplate("Ayam Bakar + Lalapan", "🍗", 460, 35f, 30f, 18f, 4f),
            FoodTemplate("Tumis Kangkung", "🥬", 180, 15f, 6f, 9f, 5f),
            FoodTemplate("Pepes Ikan", "🐟", 350, 20f, 28f, 16f, 3f),
            FoodTemplate("Cah Tahu Brokoli", "🥦", 240, 20f, 14f, 10f, 6f),
            FoodTemplate("Semur Daging", "🍖", 420, 25f, 26f, 22f, 3f),
            FoodTemplate("Sup Sayur Bening", "🍲", 150, 18f, 6f, 3f, 5f),
            FoodTemplate("Pecel Sayur", "🥗", 300, 30f, 12f, 14f, 8f),
            FoodTemplate("Ayam Kecap + Nasi", "🍗", 480, 48f, 26f, 16f, 3f)
        )
        val snack = listOf(
            FoodTemplate("Kopi Kenangan", "☕", 120, 18f, 3f, 4f, 0f),
            FoodTemplate("Jus Alpukat", "🥑", 260, 30f, 4f, 12f, 6f),
            FoodTemplate("Es Teh Manis", "🧋", 140, 34f, 0f, 0f, 0f),
            FoodTemplate("Buah Potong", "🍉", 95, 22f, 1f, 0f, 4f),
            FoodTemplate("Pisang Goreng", "🍌", 180, 28f, 2f, 8f, 3f),
            FoodTemplate("Keripik Singkong", "🍟", 200, 25f, 2f, 10f, 2f),
            FoodTemplate("Roti Bakar Coklat", "🍫", 250, 32f, 5f, 11f, 2f),
            FoodTemplate("Martabak Manis", "🥞", 320, 45f, 6f, 14f, 2f),
            FoodTemplate("Risoles Mayo", "🥐", 210, 24f, 5f, 10f, 2f),
            FoodTemplate("Es Cendol", "🍧", 260, 48f, 2f, 8f, 1f),
            FoodTemplate("Kacang Rebus", "🥜", 170, 14f, 8f, 9f, 5f),
            FoodTemplate("Yogurt Buah", "🍓", 150, 20f, 6f, 4f, 2f),
            FoodTemplate("Klepon", "🍡", 190, 30f, 2f, 7f, 2f),
            FoodTemplate("Bakwan Sayur", "🥟", 220, 22f, 4f, 13f, 3f),
            FoodTemplate("Es Jeruk", "🍊", 110, 26f, 1f, 0f, 1f),
            FoodTemplate("Kerupuk + Sambal", "🍘", 130, 16f, 1f, 6f, 1f)
        )

        fun toValues(t: FoodTemplate, mealType: String, day: Int) = ContentValues().apply {
            put(KEY_FOOD_NAME, t.name)
            put(KEY_FOOD_EMOJI, t.emoji)
            put(KEY_FOOD_CALORIES, t.calories)
            put(KEY_FOOD_CARBS, t.carbs)
            put(KEY_FOOD_PROTEIN, t.protein)
            put(KEY_FOOD_FAT, t.fat)
            put(KEY_FOOD_FIBER, t.fiber)
            put(KEY_FOOD_MEAL_TYPE, mealType)
            put(KEY_FOOD_DATE, DateUtils.getRelativeDateString(day))
        }

        val entries = mutableListOf<ContentValues>()
        for (day in 0 until SEED_DAYS) {
            entries.add(toValues(breakfast[day % breakfast.size], "Sarapan", day))
            entries.add(toValues(lunch[(day + 2) % lunch.size], "Makan Siang", day))
            entries.add(toValues(dinner[(day + 4) % dinner.size], "Makan Malam", day))
            // Skip the snack on some days for realism (not every day has a snack).
            if (day % 5 != 4) {
                entries.add(toValues(snack[(day + 1) % snack.size], "Makanan Ringan", day))
            }
            // Second snack most days, for more variety and more items per day.
            if (day % 2 == 0) {
                entries.add(toValues(snack[(day + 6) % snack.size], "Makanan Ringan", day))
            }
            // Occasional extra light lunch item (e.g. a side dish) every 3rd day.
            if (day % 3 == 0) {
                entries.add(toValues(lunch[(day + 8) % lunch.size], "Makan Siang", day))
            }
        }
        for (food in entries) {
            db.insert(TABLE_FOODS, null, food)
        }
    }

    // 4. Seed sleep records: one entry per day for SEED_DAYS days, with duration/quality
    // cycling through a realistic weekly-ish pattern.
    private fun seedSleep(db: SQLiteDatabase) {
        data class SleepTemplate(val duration: Float, val quality: String)

        val pattern = listOf(
            SleepTemplate(8.0f, "Baik"),
            SleepTemplate(7.0f, "Cukup"),
            SleepTemplate(7.0f, "Cukup"),
            SleepTemplate(8.5f, "Baik"),
            SleepTemplate(6.0f, "Buruk"),
            SleepTemplate(8.0f, "Baik"),
            SleepTemplate(6.5f, "Cukup"),
            SleepTemplate(7.5f, "Baik"),
            SleepTemplate(5.5f, "Buruk"),
            SleepTemplate(7.0f, "Cukup")
        )

        val entries = mutableListOf<ContentValues>()
        for (day in 0 until SEED_DAYS) {
            val t = pattern[day % pattern.size]
            entries.add(ContentValues().apply {
                put(KEY_SLEEP_DATE, DateUtils.getRelativeDateString(day))
                put(KEY_SLEEP_BED_TIME, "")
                put(KEY_SLEEP_WAKE_TIME, "")
                put(KEY_SLEEP_DURATION_HOURS, t.duration)
                put(KEY_SLEEP_QUALITY, t.quality)
            })
        }
        for (sl in entries) {
            db.insert(TABLE_SLEEP_RECORDS, null, sl)
        }
    }
}