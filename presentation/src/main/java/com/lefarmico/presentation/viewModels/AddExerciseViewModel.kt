package com.lefarmico.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import com.lefarmico.domain.entity.LibraryDto
import com.lefarmico.domain.repository.LibraryRepository
import com.lefarmico.domain.utils.DataState
import com.lefarmico.presentation.intents.AddExerciseIntent
import com.lefarmico.presentation.views.base.BaseViewModel
import javax.inject.Inject

class AddExerciseViewModel @Inject constructor() : BaseViewModel<AddExerciseIntent>() {
    
    @Inject lateinit var repo: LibraryRepository

    val addExerciseLiveData = MutableLiveData<DataState<Long>>()

    private fun addNewExercise(title: String, description: String, imageRes: String, subcategoryId: Int) {
        val exercise = LibraryDto.Exercise(
            title = title,
            description = description,
            imageRes = imageRes,
            subCategoryId = subcategoryId
        )
        repo.addExercise(exercise)
            .subscribe { dataState ->
                when (dataState) {
                    is DataState.Success -> {
                        addExerciseLiveData.postValue(DataState.Success(1))
                    }
                    else -> {
                        addExerciseLiveData.postValue(dataState)
                    }
                }
            }
    }

    private fun setDefaultState() {
        addExerciseLiveData.postValue(DataState.Empty)
    }

    override fun onTriggerEvent(eventType: AddExerciseIntent) {
        when (eventType) {
            is AddExerciseIntent.AddExerciseResult -> {
                addNewExercise(
                    eventType.title,
                    eventType.description,
                    eventType.imageRes,
                    eventType.subcategoryId
                )
            }
            AddExerciseIntent.DefaultState -> setDefaultState()
        }
    }
}
