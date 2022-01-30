package com.lefarmico.workout

import com.lefarmico.core.base.BaseState
import java.lang.Exception

sealed class WorkoutEvent : BaseState.Event {

    object ShowEditState : WorkoutEvent()
    object HideEditState : WorkoutEvent()
    object SelectAllExercises : WorkoutEvent()
    object DeselectAllExercises : WorkoutEvent()
    object DeleteSelectedExercises : WorkoutEvent()

    data class EndWorkoutResult(val workoutId: Long) : WorkoutEvent()
    data class SetParamsDialog(val exerciseId: Int) : WorkoutEvent()
    data class ExceptionResult(val exception: Exception) : WorkoutEvent()
}
