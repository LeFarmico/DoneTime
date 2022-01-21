package com.lefarmico.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.lefarmico.data.db.entity.WorkoutRecordsData
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.time.LocalDate

@Dao
interface WorkoutRecordsDao {

    /* workout_records table */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWorkout(workout: WorkoutRecordsData.Workout): Single<Long>

    @Query("SELECT * FROM workout_records")
    fun getWorkoutRecords(): Observable<List<WorkoutRecordsData.Workout>>

    @Query("SELECT * FROM workout_records WHERE workout_id = :workoutId")
    fun getWorkout(workoutId: Int): Observable<WorkoutRecordsData.Workout>

    @Update
    fun updateWorkout(workout: WorkoutRecordsData.Workout): Int

    @Delete
    fun deleteWorkout(workout: WorkoutRecordsData.Workout): Int

    /* exercise_records table */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercise(exercise: WorkoutRecordsData.Exercise): Long

    @Query("SELECT * FROM exercise_records WHERE workout_id = :workoutId")
    fun getExerciseRecords(workoutId: Int): Observable<List<WorkoutRecordsData.Exercise>>

    @Query("SELECT * FROM exercise_records WHERE exercise_id = :exerciseId")
    fun getExercise(exerciseId: Int): Observable<WorkoutRecordsData.Exercise>

    @Update
    fun updateExercise(exercise: WorkoutRecordsData.Exercise): Int

    @Delete
    fun deleteExercise(exercise: WorkoutRecordsData.Exercise): Int

    @Query("DELETE FROM exercise_records WHERE workout_id = :workoutId ")
    fun deleteExercises(workoutId: Int)

    /* set_records table */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSet(set: WorkoutRecordsData.Set): Long

    @Query("SELECT * FROM set_records WHERE exercise_id = :exerciseId")
    fun getSetRecords(exerciseId: Int): Observable<List<WorkoutRecordsData.Set>>

    @Query("SELECT * FROM set_records WHERE set_id = :setId")
    fun getSet(setId: Int): Observable<WorkoutRecordsData.Set>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSets(setList: List<WorkoutRecordsData.Set>)

    @Update
    fun updateSet(set: WorkoutRecordsData.Set): Int

    @Update
    fun updateSets(setList: List<WorkoutRecordsData.Set>)

    @Delete
    fun deleteSet(set: WorkoutRecordsData.Set): Int

    /* cross queries */
    @Transaction
    @Query("SELECT * FROM workout_records WHERE workout_id = :workoutId")
    fun getWorkoutWithExerciseAnsSets(workoutId: Int): Single<WorkoutRecordsData.WorkoutWithExercisesAndSets>

    @Transaction
    @Query("SELECT * FROM workout_records")
    fun getWorkoutsWithExerciseAnsSets(): Single<List<WorkoutRecordsData.WorkoutWithExercisesAndSets>>

    @Transaction
    @Query("SELECT * FROM workout_records WHERE date = :date")
    fun getWorkoutsWithExerciseAnsSetsByDate(
        date: LocalDate
    ): Single<List<WorkoutRecordsData.WorkoutWithExercisesAndSets>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertExercises(exerciseList: List<WorkoutRecordsData.Exercise>): List<Long>

    @Query("SELECT date FROM workout_records WHERE date BETWEEN :from AND :to")
    fun getWorkoutDateByTime(from: LocalDate, to: LocalDate): Single<List<LocalDate>>
}
