package com.lefarmico.donetime.adapters.exercise.entity

import com.lefarmico.donetime.adapters.exercise.ExerciseMenuFactory
import com.lefarmico.lerecycle.IViewHolderFactory
import com.lefarmico.lerecycle.ItemType

data class ExerciseName(
    val name: String,
    val tags: String,
) : ItemType {
    override val type: IViewHolderFactory<ItemType> = ExerciseMenuFactory.EXERCISE
}

data class ExerciseSet(
    val setNumber: Int,
    val weights: Float,
    val reps: Int,
) : ItemType {
    override val type: IViewHolderFactory<ItemType> = ExerciseMenuFactory.SET
}

data class AddDelButtons(
    val addButtonCallback: () -> Unit,
    val deleteButtonCallback: () -> Unit,
) : ItemType {
    override val type: IViewHolderFactory<ItemType> = ExerciseMenuFactory.BUTTONS
}
