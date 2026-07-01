package com.example.healthylife.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.healthylife.util.DateUtils

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "healthylife.db"
        private const val DATABASE_VERSION = 4

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

        // 2. Seed exercises
        val initialExercises = listOf(
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Running")
                put(KEY_EXERCISE_EMOJI, "🏃")
                put(KEY_EXERCISE_DURATION_MINUTES, 35)
                put(KEY_EXERCISE_CALORIES_BURNED, 320)
                put(KEY_EXERCISE_DATE, DateUtils.getRelativeDateString(0))
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Walking")
                put(KEY_EXERCISE_EMOJI, "🚶")
                put(KEY_EXERCISE_DURATION_MINUTES, 20)
                put(KEY_EXERCISE_CALORIES_BURNED, 90)
                put(KEY_EXERCISE_DATE, DateUtils.getRelativeDateString(0))
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Gym")
                put(KEY_EXERCISE_EMOJI, "🏋️")
                put(KEY_EXERCISE_DURATION_MINUTES, 50)
                put(KEY_EXERCISE_CALORIES_BURNED, 450)
                put(KEY_EXERCISE_DATE, DateUtils.getRelativeDateString(1))
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Yoga")
                put(KEY_EXERCISE_EMOJI, "🧘")
                put(KEY_EXERCISE_DURATION_MINUTES, 30)
                put(KEY_EXERCISE_CALORIES_BURNED, 120)
                put(KEY_EXERCISE_DATE, DateUtils.getRelativeDateString(1))
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Running")
                put(KEY_EXERCISE_EMOJI, "🏃")
                put(KEY_EXERCISE_DURATION_MINUTES, 40)
                put(KEY_EXERCISE_CALORIES_BURNED, 360)
                put(KEY_EXERCISE_DATE, DateUtils.getRelativeDateString(2))
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Cycling")
                put(KEY_EXERCISE_EMOJI, "🚴")
                put(KEY_EXERCISE_DURATION_MINUTES, 45)
                put(KEY_EXERCISE_CALORIES_BURNED, 380)
                put(KEY_EXERCISE_DATE, DateUtils.getRelativeDateString(9))
            }
        )
        for (ex in initialExercises) {
            db.insert(TABLE_EXERCISES, null, ex)
        }

        // 3. Seed foods
        fun food(
            name: String, emoji: String, calories: Int,
            carbs: Float, protein: Float, fat: Float, fiber: Float,
            mealType: String, daysAgo: Int
        ) = ContentValues().apply {
            put(KEY_FOOD_NAME, name)
            put(KEY_FOOD_EMOJI, emoji)
            put(KEY_FOOD_CALORIES, calories)
            put(KEY_FOOD_CARBS, carbs)
            put(KEY_FOOD_PROTEIN, protein)
            put(KEY_FOOD_FAT, fat)
            put(KEY_FOOD_FIBER, fiber)
            put(KEY_FOOD_MEAL_TYPE, mealType)
            put(KEY_FOOD_DATE, DateUtils.getRelativeDateString(daysAgo))
        }
        val initialFoods = listOf(
            food("Oatmeal + Pisang",  "🥣", 350, 60f, 12f, 6f,  8f, "Sarapan",         0),
            food("Susu Rendah Lemak", "🥛", 110, 12f, 8f,  2f,  0f, "Sarapan",         0),
            food("Nasi Goreng Ayam",  "🍳", 480, 55f, 22f, 14f, 3f, "Makan Siang",     0),
            food("Kopi Kenangan",     "☕", 120, 18f, 3f,  4f,  0f, "Makanan Ringan",  0),
            food("Ayam Geprek",       "🍗", 520, 40f, 35f, 20f, 4f, "Makan Malam",     0),
            food("Mie Gacoan",        "🍜", 380, 50f, 15f, 12f, 3f, "Makan Siang",     2),
            food("Buah Potong",       "🍉", 95,  22f, 1f,  0f,  4f, "Makanan Ringan",  3),
            food("Salad Sayur",       "🥗", 210, 18f, 8f,  10f, 6f, "Makan Malam",     9)
        )
        for (food in initialFoods) {
            db.insert(TABLE_FOODS, null, food)
        }

        // 4. Seed sleep records
        fun sleep(daysAgo: Int, duration: Float, quality: String) = ContentValues().apply {
            put(KEY_SLEEP_DATE, DateUtils.getRelativeDateString(daysAgo))
            put(KEY_SLEEP_BED_TIME, "")
            put(KEY_SLEEP_WAKE_TIME, "")
            put(KEY_SLEEP_DURATION_HOURS, duration)
            put(KEY_SLEEP_QUALITY, quality)
        }
        val initialSleep = listOf(
            sleep(0, 8.0f, "Baik"),
            sleep(1, 7.0f, "Cukup"),
            sleep(2, 7.0f, "Cukup"),
            sleep(3, 8.5f, "Baik"),
            sleep(4, 6.0f, "Buruk"),
            sleep(5, 8.0f, "Baik"),
            sleep(9, 7.0f, "Cukup")
        )
        for (sl in initialSleep) {
            db.insert(TABLE_SLEEP_RECORDS, null, sl)
        }
    }
}
