package com.lefarmico.presentation.viewModels

import androidx.lifecycle.MutableLiveData
import com.lefarmico.data.repository.LibraryRepositoryImpl
import com.lefarmico.domain.entity.LibraryDto
import com.lefarmico.domain.utils.DataState
import com.lefarmico.presentation.intents.CategoryListIntent
import com.lefarmico.presentation.views.base.BaseViewModel
import java.lang.IllegalArgumentException
import javax.inject.Inject

class CategoryListViewModel : BaseViewModel<CategoryListIntent>() {

    val categoriesLiveData = MutableLiveData<DataState<List<LibraryDto.Category>>>()
    
    @Inject
    lateinit var repo: LibraryRepositoryImpl

    private fun getCategories() {
        repo.getCategories()
            .subscribe { dataState ->
                categoriesLiveData.postValue(dataState)
            }
    }

    private fun addNewCategory(categoryTitle: String) {
        if (categoryTitle != "") {
            val category = LibraryDto.Category(
                title = categoryTitle
            )
            repo.addCategory(category)
                .subscribe { dataState ->
                    when (dataState) {
                        is DataState.Error -> {
                            categoriesLiveData.postValue(dataState)
                        }
                        DataState.Loading -> {
                            categoriesLiveData.postValue(DataState.Loading)
                        }
                        is DataState.Success -> {
                            getCategories()
                        }
                        else -> {}
                    }
                }
        } else {
            categoriesLiveData.postValue(DataState.Error(IllegalArgumentException()))
        }
    }

    override fun onTriggerEvent(eventType: CategoryListIntent) {
        when (eventType) {
            is CategoryListIntent.AddCategory -> addNewCategory(eventType.categoryTitle)
            CategoryListIntent.GetCategories -> getCategories()
        }
    }
}
