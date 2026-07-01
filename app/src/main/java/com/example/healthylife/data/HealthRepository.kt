package com.example.healthylife.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.healthylife.model.Exercise
import com.example.healthylife.model.Food
import com.example.healthylife.model.SleepRecord
import com.example.healthylife.model.User

class HealthRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    // ── USER OPERATIONS ───────────────────────────────────────────────────────

    fun getUser(id: Int = 1): User? {
        val db = dbHelper.readableDatabase
        var user: User? = null
        val selectQuery = "SELECT * FROM ${DatabaseHelper.TABLE_USERS} WHERE ${DatabaseHelper.KEY_USER_ID} = ?"
        val cursor = db.rawQuery(selectQuery, arrayOf(id.toString()))

        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_NAME)),
                age = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_AGE)),
                weight = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_WEIGHT)),
                height = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_HEIGHT)),
                targetCalories = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_TARGET_CALORIES)),
                targetSleepHours = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_TARGET_SLEEP_HOURS)),
                targetExerciseMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_TARGET_EXERCISE_MINUTES)),
                streakDays = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_USER_STREAK_DAYS))
            )
        }
        cursor.close()
        return user
    }

    fun updateUser(user: User): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.KEY_USER_NAME, user.name)
            put(DatabaseHelper.KEY_USER_AGE, user.age)
            put(DatabaseHelper.KEY_USER_WEIGHT, user.weight)
            put(DatabaseHelper.KEY_USER_HEIGHT, user.height)
            put(DatabaseHelper.KEY_USER_TARGET_CALORIES, user.targetCalories)
            put(DatabaseHelper.KEY_USER_TARGET_SLEEP_HOURS, user.targetSleepHours)
            put(DatabaseHelper.KEY_USER_TARGET_EXERCISE_MINUTES, user.targetExerciseMinutes)
            put(DatabaseHelper.KEY_USER_STREAK_DAYS, user.streakDays)
        }
        return db.update(
            DatabaseHelper.TABLE_USERS,
            values,
            "${DatabaseHelper.KEY_USER_ID} = ?",
            arrayOf(user.id.toString())
        )
    }

    // ── EXERCISE OPERATIONS ───────────────────────────────────────────────────

    fun getAllExercises(): List<Exercise> {
        val list = mutableListOf<Exercise>()
        val db = dbHelper.readableDatabase
        val selectQuery = "SELECT * FROM ${DatabaseHelper.TABLE_EXERCISES} ORDER BY ${DatabaseHelper.KEY_EXERCISE_ID} DESC"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val exercise = Exercise(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EXERCISE_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EXERCISE_NAME)),
                    emoji = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EXERCISE_EMOJI)),
                    durationMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EXERCISE_DURATION_MINUTES)),
                    caloriesBurned = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EXERCISE_CALORIES_BURNED)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_EXERCISE_DATE))
                )
                list.add(exercise)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun insertExercise(exercise: Exercise): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.KEY_EXERCISE_NAME, exercise.name)
            put(DatabaseHelper.KEY_EXERCISE_EMOJI, exercise.emoji)
            put(DatabaseHelper.KEY_EXERCISE_DURATION_MINUTES, exercise.durationMinutes)
            put(DatabaseHelper.KEY_EXERCISE_CALORIES_BURNED, exercise.caloriesBurned)
            put(DatabaseHelper.KEY_EXERCISE_DATE, exercise.date)
        }
        return db.insert(DatabaseHelper.TABLE_EXERCISES, null, values)
    }

    // ── FOOD OPERATIONS ───────────────────────────────────────────────────────

    fun getAllFoods(): List<Food> {
        val list = mutableListOf<Food>()
        val db = dbHelper.readableDatabase
        val selectQuery = "SELECT * FROM ${DatabaseHelper.TABLE_FOODS} ORDER BY ${DatabaseHelper.KEY_FOOD_ID} DESC"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val food = Food(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FOOD_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FOOD_NAME)),
                    emoji = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FOOD_EMOJI)),
                    calories = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FOOD_CALORIES)),
                    carbs = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FOOD_CARBS)),
                    protein = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FOOD_PROTEIN)),
                    fat = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FOOD_FAT)),
                    mealType = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_FOOD_MEAL_TYPE))
                )
                list.add(food)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun insertFood(food: Food): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.KEY_FOOD_NAME, food.name)
            put(DatabaseHelper.KEY_FOOD_EMOJI, food.emoji)
            put(DatabaseHelper.KEY_FOOD_CALORIES, food.calories)
            put(DatabaseHelper.KEY_FOOD_CARBS, food.carbs)
            put(DatabaseHelper.KEY_FOOD_PROTEIN, food.protein)
            put(DatabaseHelper.KEY_FOOD_FAT, food.fat)
            put(DatabaseHelper.KEY_FOOD_MEAL_TYPE, food.mealType)
        }
        return db.insert(DatabaseHelper.TABLE_FOODS, null, values)
    }

    // ── SLEEP OPERATIONS ──────────────────────────────────────────────────────

    fun getAllSleepRecords(): List<SleepRecord> {
        val list = mutableListOf<SleepRecord>()
        val db = dbHelper.readableDatabase
        val selectQuery = "SELECT * FROM ${DatabaseHelper.TABLE_SLEEP_RECORDS} ORDER BY ${DatabaseHelper.KEY_SLEEP_ID} DESC"
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val record = SleepRecord(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SLEEP_ID)),
                    date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SLEEP_DATE)),
                    bedTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SLEEP_BED_TIME)),
                    wakeTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SLEEP_WAKE_TIME)),
                    durationHours = cursor.getFloat(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SLEEP_DURATION_HOURS)),
                    quality = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_SLEEP_QUALITY))
                )
                list.add(record)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }

    fun insertSleepRecord(record: SleepRecord): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.KEY_SLEEP_DATE, record.date)
            put(DatabaseHelper.KEY_SLEEP_BED_TIME, record.bedTime)
            put(DatabaseHelper.KEY_SLEEP_WAKE_TIME, record.wakeTime)
            put(DatabaseHelper.KEY_SLEEP_DURATION_HOURS, record.durationHours)
            put(DatabaseHelper.KEY_SLEEP_QUALITY, record.quality)
        }
        return db.insert(DatabaseHelper.TABLE_SLEEP_RECORDS, null, values)
    }
}
