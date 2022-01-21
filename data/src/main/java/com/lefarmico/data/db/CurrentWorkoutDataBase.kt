package com.lefarmico.data.db

import com.lefarmico.data.db.entity.CurrentWorkoutData
import io.reactivex.rxjava3.core.Single
import java.lang.NullPointerException

class CurrentWorkoutDataBase {

    private val lock = Any()
    private val exerciseWithSetsList = mutableListOf<CurrentWorkoutData.ExerciseWithSets>()
    private var exerciseId = 1

    fun insertExercise(exerciseWithSets: CurrentWorkoutData.ExerciseWithSets): Single<Int> {
        return Single.create { emitter ->
            synchronized(lock) {
                val exercise = exerciseWithSets.exercise.copy(id = exerciseId)
                val withSets = exerciseWithSets.copy(exercise = exercise)
                exerciseWithSetsList.add(withSets)
                emitter.onSuccess(exerciseId++)
            }
        }
    }

    fun insertExercises(exerciseList: List<CurrentWorkoutData.ExerciseWithSets>): Single<Int> {
        return Single.create { emitter ->
            synchronized(lock) {
                val withSets = exerciseList.map { exWithSets ->
                    val exercise = exWithSets.exercise.copy(id = exerciseId++)
                    exWithSets.copy(exercise = exercise)
                }
                exerciseWithSetsList.addAll(withSets)
                emitter.onSuccess(exerciseId)
            }
        }
    }

    fun getExercises(): Single<List<CurrentWorkoutData.ExerciseWithSets>> {
        return Single.create { emitter ->
            synchronized(lock) {
                emitter.onSuccess(exerciseWithSetsList)
            }
        }
    }

    fun getExercise(exerciseId: Int): Single<CurrentWorkoutData.ExerciseWithSets> {
        return Single.create { emitter ->
            validateExercise(exerciseId) { exercise -> emitter.onSuccess(exercise) }
        }
    }

    fun getSets(exerciseId: Int): Single<List<CurrentWorkoutData.Set>> {
        return Single.create { emitter ->
            validateExercise(exerciseId) { exercise -> emitter.onSuccess(exercise.setList) }
        }
    }

    fun deleteExercise(exerciseId: Int): Single<Int> {
        return Single.create { emitter ->
            validateExercise(exerciseId) { exercise ->
                exerciseWithSetsList.remove(exercise)
                emitter.onSuccess(exerciseId)
            }
        }
    }

    fun insertSet(set: CurrentWorkoutData.Set): Single<Int> {
        return Single.create { emitter ->
            validateExercise(set.exerciseId) { exercise ->
                exercise.setList.add(set)
                emitter.onSuccess(set.id)
            }
        }
    }

    fun deleteSetFromExercise(set: CurrentWorkoutData.Set): Single<Int> {
        return Single.create { emitter ->
            validateExercise(set.exerciseId) { exercise ->
                exercise.setList.remove(set)
                emitter.onSuccess(set.id)
            }
        }
    }

    fun deleteLastSet(exerciseId: Int): Single<Int> {
        return Single.create { emitter ->
            validateExercise(exerciseId) { exercise ->
                val lastSet = exercise.setList[exercise.setList.size - 1]
                exercise.setList.remove(lastSet)
                if (exercise.setList.isEmpty()) {
                    exerciseWithSetsList.remove(exercise)
                }
                emitter.onSuccess(lastSet.id)
            }
        }
    }

    fun clearData() {
        synchronized(lock) {
            exerciseWithSetsList.clear()
        }
    }

    private fun validateExercise(
        id: Int,
        onExist: (CurrentWorkoutData.ExerciseWithSets) -> Unit,
    ) {
        synchronized(lock) {
            val exercise = exerciseWithSetsList.find { it.exercise.id == id }
            if (exercise == null) {
                throw (NullPointerException("Exercise with id = $id is not exist"))
            } else {
                onExist(exercise)
            }
        }
    }
}
