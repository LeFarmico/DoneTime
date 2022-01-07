package com.lefarmico.create_new_exercise

import com.lefarmico.core.base.BaseViewModel
import com.lefarmico.core.extensions.observeUi
import com.lefarmico.core.validator.EmptyValidator
import com.lefarmico.core.validator.ExistedValidator
import com.lefarmico.core.validator.ValidateHandler
import com.lefarmico.core.validator.ValidationState
import com.lefarmico.create_new_exercise.CreateExerciseIntent.*
import com.lefarmico.domain.entity.LibraryDto
import com.lefarmico.domain.repository.LibraryRepository
import com.lefarmico.navigation.Router
import com.lefarmico.navigation.notification.Notification
import com.lefarmico.navigation.params.ToastBarParams
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CreateExerciseViewModel @Inject constructor() : BaseViewModel<
    CreateExerciseIntent, CreateExerciseAction, CreateExerciseState, CreateExerciseEvent
    >() {

    @Inject lateinit var repo: LibraryRepository
    @Inject lateinit var router: Router

    private val validator = ValidateHandler()
    private val validateSubject = PublishSubject.create<String>()
    private var validateCache = listOf<String>()

    init {
        validator()
    }

    private fun getExistedExercises(subcategoryId: Int) {
        repo.getExercises(subcategoryId)
            .observeUi()
            .doOnSuccess { dataState -> putToCache(dataState.reduce()) }
            .subscribe()
    }

    private fun addExercise(title: String, description: String, imageRes: String, subcategoryId: Int) {
        val exercise = LibraryDto.Exercise(
            title = title,
            description = description,
            imageRes = imageRes,
            subCategoryId = subcategoryId
        )
        repo.addExercise(exercise)
            .observeUi()
            .doAfterSuccess { router.back() }
            .subscribe()
    }

    private fun putToCache(exerciseTitles: List<String>) {
        validateCache = exerciseTitles
    }

    private fun validator() {
        Observable.create<String> { input -> validateSubject.subscribe { input.onNext(it) } }
            .debounce(1, TimeUnit.SECONDS)
            .distinctUntilChanged()
            .observeUi()
            .doOnNext { validateField -> validate(validateField, validateCache) }
            .subscribe()
    }

    private fun validate(field: String, fieldList: List<String>) {
        validator.resetValidators()
        validator.addValidator(EmptyValidator(field))
        validator.addValidator(ExistedValidator(field, fieldList))

        val event = when (validator.validate()) {
            EmptyValidator.EmptyState -> CreateExerciseEvent.ValidationEmpty
            ExistedValidator.ExistedState -> CreateExerciseEvent.ValidationAlreadyExist
            ValidationState.SuccessState -> CreateExerciseEvent.ValidationSuccess
        }
        mEvent.postValue(event)
    }

    private fun showToast(text: String) {
        router.show(Notification.TOAST, ToastBarParams(text))
    }

    private fun back() {
        router.back()
    }

    override fun triggerAction(action: CreateExerciseAction) {
        when (action) {
            CreateExerciseAction.Back -> back()
            is CreateExerciseAction.ShowToast -> showToast(action.text)
            is CreateExerciseAction.ValidateExercise -> validateSubject.onNext(action.title)
            is CreateExerciseAction.GetExercises -> getExistedExercises(action.subcategoryId)
            is CreateExerciseAction.AddExercise -> {
                action.apply { addExercise(title, description, imageRes, subcategoryId) }
            }
        }
    }

    override fun intentToAction(intent: CreateExerciseIntent): CreateExerciseAction {
        return when (intent) {
            Back -> CreateExerciseAction.Back
            is ShowToast ->
                CreateExerciseAction
                    .ShowToast(intent.text)

            is ValidateExercise ->
                CreateExerciseAction
                    .ValidateExercise(intent.title)

            is AddExercise ->
                CreateExerciseAction
                    .AddExercise(intent.title, intent.description, intent.imageRes, intent.subcategoryId)

            is GetExercises ->
                CreateExerciseAction
                    .GetExercises(intent.subcategoryId)
        }
    }
}
