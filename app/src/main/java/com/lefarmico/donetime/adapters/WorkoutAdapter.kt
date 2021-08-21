package com.lefarmico.donetime.adapters

import com.lefarmico.donetime.adapters.viewHolders.curWorkout.ExerciseViewHolder
import com.lefarmico.donetime.adapters.viewHolders.factories.WorkoutViewHolderFactory
import com.lefarmico.donetime.data.entities.exercise.ExerciseData
import com.lefarmico.donetime.utils.IWorkoutItemObservable
import com.lefarmico.donetime.utils.ItemObserver
import com.lefarmico.lerecycle.ItemType
import com.lefarmico.lerecycle.LeRecyclerAdapter
import com.lefarmico.lerecycle.LeRecyclerViewHolder
import com.lefarmico.lerecycle.extractValues

class WorkoutAdapter(
    private val dataSource: IWorkoutItemObservable
) : LeRecyclerAdapter(), ItemObserver {

    private val types = extractValues<WorkoutViewHolderFactory>()
    
    init {
        setItemTypes(types)
        dataSource.registerObserver(this)
    }

    override fun onBindViewHolder(holder: LeRecyclerViewHolder<ItemType>, position: Int) {
        super.onBindViewHolder(holder, position)
        when (items[position]) {
            is ExerciseData -> {
                holder as ExerciseViewHolder
                bindExerciseItem(holder, position)
            }
        }
    }

    private fun onNotActiveExerciseCallback(position: Int) {
        dataSource.setActivePosition(position)
    }

    private fun bindExerciseItem(holder: ExerciseViewHolder, position: Int) {
        val exercise = items[position] as ExerciseData
        val adapter = ExerciseAdapter(exercise)

        if (!exercise.isActive) {
            adapter.setOnClickEvent {
                onNotActiveExerciseCallback(position)
            }
        }
        holder.bindAdapter(adapter)
    }

    override fun updateData(items: MutableList<ItemType>) {
        this.items = items
    }
}
