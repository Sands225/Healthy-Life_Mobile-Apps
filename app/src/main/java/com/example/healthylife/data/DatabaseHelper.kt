package com.example.healthylife.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "healthylife.db"
        private const val DATABASE_VERSION = 1

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
        const val KEY_USER_STREAK_DAYS = "streak_days"

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
        const val KEY_FOOD_MEAL_TYPE = "meal_type"

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
                + KEY_USER_TARGET_EXERCISE_MINUTES + " INTEGER,"
                + KEY_USER_STREAK_DAYS + " INTEGER" + ")")
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
                + KEY_FOOD_MEAL_TYPE + " TEXT" + ")")
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
            put(KEY_USER_STREAK_DAYS, 12)
        }
        db.insert(TABLE_USERS, null, userValues)

        // 2. Seed exercises
        val initialExercises = listOf(
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Running")
                put(KEY_EXERCISE_EMOJI, "🏃")
                put(KEY_EXERCISE_DURATION_MINUTES, 35)
                put(KEY_EXERCISE_CALORIES_BURNED, 320)
                put(KEY_EXERCISE_DATE, "Hari ini")
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Walking")
                put(KEY_EXERCISE_EMOJI, "🚶")
                put(KEY_EXERCISE_DURATION_MINUTES, 20)
                put(KEY_EXERCISE_CALORIES_BURNED, 90)
                put(KEY_EXERCISE_DATE, "Hari ini")
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Gym")
                put(KEY_EXERCISE_EMOJI, "🏋️")
                put(KEY_EXERCISE_DURATION_MINUTES, 50)
                put(KEY_EXERCISE_CALORIES_BURNED, 450)
                put(KEY_EXERCISE_DATE, "Kemarin")
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Yoga")
                put(KEY_EXERCISE_EMOJI, "🧘")
                put(KEY_EXERCISE_DURATION_MINUTES, 30)
                put(KEY_EXERCISE_CALORIES_BURNED, 120)
                put(KEY_EXERCISE_DATE, "Kemarin")
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Running")
                put(KEY_EXERCISE_EMOJI, "🏃")
                put(KEY_EXERCISE_DURATION_MINUTES, 40)
                put(KEY_EXERCISE_CALORIES_BURNED, 360)
                put(KEY_EXERCISE_DATE, "2 hari lalu")
            },
            ContentValues().apply {
                put(KEY_EXERCISE_NAME, "Cycling")
                put(KEY_EXERCISE_EMOJI, "🚴")
                put(KEY_EXERCISE_DURATION_MINUTES, 45)
                put(KEY_EXERCISE_CALORIES_BURNED, 380)
                put(KEY_EXERCISE_DATE, "3 hari lalu")
            }
        )
        for (ex in initialExercises) {
            db.insert(TABLE_EXERCISES, null, ex)
        }

        // 3. Seed foods
        val initialFoods = listOf(
            ContentValues().apply {
                put(KEY_FOOD_NAME, "Oatmeal + Pisang")
                put(KEY_FOOD_EMOJI, "🥣")
                put(KEY_FOOD_CALORIES, 350)
                put(KEY_FOOD_CARBS, 60.0f)
                put(KEY_FOOD_PROTEIN, 12.0f)
                put(KEY_FOOD_FAT, 6.0f)
                put(KEY_FOOD_MEAL_TYPE, "Breakfast")
            },
            ContentValues().apply {
                put(KEY_FOOD_NAME, "Nasi Goreng Ayam")
                put(KEY_FOOD_EMOJI, "🍳")
                put(KEY_FOOD_CALORIES, 480)
                put(KEY_FOOD_CARBS, 55.0f)
                put(KEY_FOOD_PROTEIN, 22.0f)
                put(KEY_FOOD_FAT, 14.0f)
                put(KEY_FOOD_MEAL_TYPE, "Lunch")
            },
            ContentValues().apply {
                put(KEY_FOOD_NAME, "Mie Gacoan")
                put(KEY_FOOD_EMOJI, "🍜")
                put(KEY_FOOD_CALORIES, 380)
                put(KEY_FOOD_CARBS, 50.0f)
                put(KEY_FOOD_PROTEIN, 15.0f)
                put(KEY_FOOD_FAT, 12.0f)
                put(KEY_FOOD_MEAL_TYPE, "Lunch")
            },
            ContentValues().apply {
                put(KEY_FOOD_NAME, "Ayam Geprek")
                put(KEY_FOOD_EMOJI, "🍗")
                put(KEY_FOOD_CALORIES, 520)
                put(KEY_FOOD_CARBS, 40.0f)
                put(KEY_FOOD_PROTEIN, 35.0f)
                put(KEY_FOOD_FAT, 20.0f)
                put(KEY_FOOD_MEAL_TYPE, "Dinner")
            },
            ContentValues().apply {
                put(KEY_FOOD_NAME, "Kopi Kenangan")
                put(KEY_FOOD_EMOJI, "☕")
                put(KEY_FOOD_CALORIES, 120)
                put(KEY_FOOD_CARBS, 18.0f)
                put(KEY_FOOD_PROTEIN, 3.0f)
                put(KEY_FOOD_FAT, 4.0f)
                put(KEY_FOOD_MEAL_TYPE, "Snack")
            },
            ContentValues().apply {
                put(KEY_FOOD_NAME, "Buah Potong")
                put(KEY_FOOD_EMOJI, "🍉")
                put(KEY_FOOD_CALORIES, 95)
                put(KEY_FOOD_CARBS, 22.0f)
                put(KEY_FOOD_PROTEIN, 1.0f)
                put(KEY_FOOD_FAT, 0.0f)
                put(KEY_FOOD_MEAL_TYPE, "Snack")
            },
            ContentValues().apply {
                put(KEY_FOOD_NAME, "Salad Sayur")
                put(KEY_FOOD_EMOJI, "🥗")
                put(KEY_FOOD_CALORIES, 210)
                put(KEY_FOOD_CARBS, 18.0f)
                put(KEY_FOOD_PROTEIN, 8.0f)
                put(KEY_FOOD_FAT, 10.0f)
                put(KEY_FOOD_MEAL_TYPE, "Dinner")
            },
            ContentValues().apply {
                put(KEY_FOOD_NAME, "Susu Rendah Lemak")
                put(KEY_FOOD_EMOJI, "🥛")
                put(KEY_FOOD_CALORIES, 110)
                put(KEY_FOOD_CARBS, 12.0f)
                put(KEY_FOOD_PROTEIN, 8.0f)
                put(KEY_FOOD_FAT, 2.0f)
                put(KEY_FOOD_MEAL_TYPE, "Breakfast")
            }
        )
        for (food in initialFoods) {
            db.insert(TABLE_FOODS, null, food)
        }

        // 4. Seed sleep records
        val initialSleep = listOf(
            ContentValues().apply {
                put(KEY_SLEEP_DATE, "Hari ini")
                put(KEY_SLEEP_BED_TIME, "22:00")
                put(KEY_SLEEP_WAKE_TIME, "06:00")
                put(KEY_SLEEP_DURATION_HOURS, 8.0f)
                put(KEY_SLEEP_QUALITY, "Excellent")
            },
            ContentValues().apply {
                put(KEY_SLEEP_DATE, "Kemarin")
                put(KEY_SLEEP_BED_TIME, "23:30")
                put(KEY_SLEEP_WAKE_TIME, "06:30")
                put(KEY_SLEEP_DURATION_HOURS, 7.0f)
                put(KEY_SLEEP_QUALITY, "Normal")
            },
            ContentValues().apply {
                put(KEY_SLEEP_DATE, "2 hari lalu")
                put(KEY_SLEEP_BED_TIME, "22:45")
                put(KEY_SLEEP_WAKE_TIME, "05:45")
                put(KEY_SLEEP_DURATION_HOURS, 7.0f)
                put(KEY_SLEEP_QUALITY, "Normal")
            },
            ContentValues().apply {
                put(KEY_SLEEP_DATE, "3 hari lalu")
                put(KEY_SLEEP_BED_TIME, "21:30")
                put(KEY_SLEEP_WAKE_TIME, "06:00")
                put(KEY_SLEEP_DURATION_HOURS, 8.5f)
                put(KEY_SLEEP_QUALITY, "Excellent")
            },
            ContentValues().apply {
                put(KEY_SLEEP_DATE, "4 hari lalu")
                put(KEY_SLEEP_BED_TIME, "00:00")
                put(KEY_SLEEP_WAKE_TIME, "06:00")
                put(KEY_SLEEP_DURATION_HOURS, 6.0f)
                put(KEY_SLEEP_QUALITY, "Poor")
            },
            ContentValues().apply {
                put(KEY_SLEEP_DATE, "5 hari lalu")
                put(KEY_SLEEP_BED_TIME, "22:15")
                put(KEY_SLEEP_WAKE_TIME, "06:15")
                put(KEY_SLEEP_DURATION_HOURS, 8.0f)
                put(KEY_SLEEP_QUALITY, "Excellent")
            },
            ContentValues().apply {
                put(KEY_SLEEP_DATE, "6 hari lalu")
                put(KEY_SLEEP_BED_TIME, "23:00")
                put(KEY_SLEEP_WAKE_TIME, "06:00")
                put(KEY_SLEEP_DURATION_HOURS, 7.0f)
                put(KEY_SLEEP_QUALITY, "Normal")
            }
        )
        for (sl in initialSleep) {
            db.insert(TABLE_SLEEP_RECORDS, null, sl)
        }
    }
}
