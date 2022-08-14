package com.lefarmico.exercise_menu.viewModel

import com.lefarmico.core.base.BaseState
import com.lefarmico.core.base.BaseViewModel
import com.lefarmico.core.entity.LibraryViewData
import com.lefarmico.core.extensions.debounceImmediate
import com.lefarmico.core.extensions.observeUi
import com.lefarmico.core.validator.EmptyValidator
import com.lefarmico.core.validator.ExistedValidator
import com.lefarmico.core.validator.ValidateHandler
import com.lefarmico.domain.entity.LibraryDto
import com.lefarmico.domain.repository.LibraryRepository
import com.lefarmico.exercise_menu.*
import com.lefarmico.exercise_menu.intent.SubcategoryIntent
import com.lefarmico.exercise_menu.intent.SubcategoryIntent.*
import com.lefarmico.exercise_menu.reduceDto
import com.lefarmico.exercise_menu.state.LibraryListEvent
import com.lefarmico.exercise_menu.state.LibraryListState
import com.lefarmico.navigation.Router
import com.lefarmico.navigation.notification.Notification
import com.lefarmico.navigation.params.LibraryParams
import com.lefarmico.navigation.params.ToastBarParams
import com.lefarmico.navigation.screen.Screen
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SubcategoryViewModel @Inject constructor(
    private val repo: LibraryRepository,
    private val router: Router
) : BaseViewModel<
    SubcategoryIntent, LibraryListState, LibraryListEvent
    >() {

    private val validator = ValidateHandler()
    private val validateSubject = PublishSubject.create<String>()
    private var validateCache = listOf<String>()

    init {
        validator()
    }

    override fun triggerIntent(intent: SubcategoryIntent) {
        return when (intent) {
            is AddSubcategory -> addSubcategory(intent.title, intent.categoryId)
            is ClickItem -> goToExerciseListScreen(intent.item, intent.isFromWorkoutScreen)
            is GetSubcategories -> getSubCategories(intent.categoryId)
            is ShowToast -> showToast(intent.text)
            is Validate -> validateSubject.onNext(intent.title)
            is DeleteSubCategory -> deleteSubcategory(intent.subcategoryId, intent.categoryId)
            is EditState -> editStateAction(intent.action)
            is GetCategotyTitle -> getCategory(intent.categotyId)
        }
    }

    private fun postStateEvent(baseState: BaseState) {
        when (baseState) {
            is BaseState.Event -> mEvent.postValue(baseState as LibraryListEvent)
            is BaseState.State -> mState.value = baseState as LibraryListState
        }
    }

    private fun getCategory(categoryId: Int) {
        repo.getCategory(categoryId)
            .observeUi()
            .doOnError { postStateEvent(LibraryListEvent.ExceptionResult(it as Exception)) }
            .doAfterSuccess { dataState -> postStateEvent(dataState.reduce()) }
            .subscribe()
    }

    private fun getSubCategories(categoryId: Int) {
        repo.getSubCategories(categoryId)
            .observeUi()
            .doOnError { postStateEvent(LibraryListEvent.ExceptionResult(it as Exception)) }
            .doOnSubscribe { postStateEvent(LibraryListState.Loading) }
            .doAfterSuccess { dataState ->
                val viewState = dataState.reduceDto()
                postStateEvent(viewState)
                putToCache(viewState)
            }.subscribe()
    }

    private fun putToCache(state: BaseState) {
        if (state is LibraryListState.LibraryResult)
            try {
                validateCache = state.libraryList.map { (it as LibraryViewData.SubCategory).title }
            } catch (e: java.lang.IllegalArgumentException) {
                postStateEvent(LibraryListEvent.ExceptionResult(e))
            }
    }

    private fun validator() {
        Observable.create<String> { input -> validateSubject.subscribe { input.onNext(it) } }
            .debounceImmediate(500, TimeUnit.MILLISECONDS)
            .distinctUntilChanged()
            .observeUi()
            .doOnError { postStateEvent(LibraryListEvent.ExceptionResult(it as Exception)) }
            .doOnNext { validateField -> validate(validateField, validateCache) }
            .subscribe()
    }

    private fun validate(field: String, fieldList: List<String>) {
        validator.resetValidators()
        validator.addValidator(EmptyValidator(field))
        validator.addValidator(ExistedValidator(field, fieldList))

        val event = when (validator.validate()) {
            EmptyValidator.EmptyState -> LibraryListEvent.ValidationResult.Empty
            ExistedValidator.ExistedState -> LibraryListEvent.ValidationResult.AlreadyExist
            com.lefarmico.core.validator.ValidationState.SuccessState -> LibraryListEvent.ValidationResult.Success
        }
        mEvent.postValue(event)
    }

    private fun addSubcategory(subcategoryTitle: String, categoryId: Int) {
        val subCategory = LibraryDto.SubCategory(0, subcategoryTitle, categoryId)
        repo.addSubCategory(subCategory)
            .observeUi()
            .doOnError { postStateEvent(LibraryListEvent.ExceptionResult(it as Exception)) }
            .doOnSubscribe { postStateEvent(LibraryListState.Loading) }
            .doAfterSuccess { getSubCategories(subCategory.categoryId) }
            .subscribe()
    }

    private fun deleteSubcategory(subcategoryId: Int, categoryId: Int) {
        repo.deleteSubcategory(subcategoryId)
            .observeUi()
            .doOnError { postStateEvent(LibraryListEvent.ExceptionResult(it as Exception)) }
            .doOnSubscribe { postStateEvent(LibraryListState.Loading) }
            .doAfterSuccess { getSubCategories(categoryId) }
            .subscribe()
    }

    private fun editStateAction(action: EditState.Action) {
        val event = when (action) {
            EditState.Action.DeselectAll -> LibraryListEvent.DeselectAllWorkouts
            EditState.Action.Hide -> LibraryListEvent.HideEditState
            EditState.Action.SelectAll -> LibraryListEvent.SelectAllWorkouts
            EditState.Action.Show -> LibraryListEvent.ShowEditState
            EditState.Action.DeleteSelected -> LibraryListEvent.DeleteSelectedWorkouts
        }
        mEvent.postValue(event)
    }

    private fun goToExerciseListScreen(
        subcategory: LibraryViewData.SubCategory,
        isFromWorkoutScreen: Boolean
    ) {
        val categoryId = subcategory.categoryId
        val subcategoryId = subcategory.id
        if (isFromWorkoutScreen) {
            router.navigate(
                screen = Screen.EXERCISE_LIST_SCREEN,
                data = LibraryParams.ExerciseList(categoryId, subcategoryId, isFromWorkoutScreen)
            )
        } else {
            router.navigate(
                screen = Screen.LIBRARY_EXERCISES_LIST_SCREEN,
                data = LibraryParams.ExerciseList(categoryId, subcategoryId, isFromWorkoutScreen)
            )
        }
    }

    private fun showToast(text: String) {
        router.show(Notification.TOAST, ToastBarParams(text))
    }
}
