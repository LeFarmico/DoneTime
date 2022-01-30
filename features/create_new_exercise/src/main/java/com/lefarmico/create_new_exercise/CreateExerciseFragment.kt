package com.lefarmico.create_new_exercise

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.lefarmico.core.base.BaseFragment
import com.lefarmico.create_new_exercise.CreateExerciseIntent.*
import com.lefarmico.create_new_exercise.databinding.FragmentCreateNewExerciseBinding
import com.lefarmico.navigation.params.LibraryParams
import java.lang.IllegalArgumentException

class CreateExerciseFragment : BaseFragment<
    CreateExerciseIntent, CreateExerciseState, CreateExerciseEvent,
    FragmentCreateNewExerciseBinding, CreateExerciseViewModel>(
    FragmentCreateNewExerciseBinding::inflate,
    CreateExerciseViewModel::class.java
) {

    private val textField get() = binding.exerciseEditText.text.toString()
    private val descriptionField get() = binding.descriptionEditText.text.toString()
    private val imageSourceField get() = ""
    private val subcategory get() = params.subCategoryId

    private val params: LibraryParams.NewExercise by lazy {
        arguments?.getParcelable<LibraryParams.NewExercise>(KEY_PARAMS)
            ?: throw (IllegalArgumentException("Arguments params must be not null"))
    }

    override fun setUpViews() {
        dispatchIntent(GetExercises(params.subCategoryId))
        isAddButtonActive(false)
        setUpToolbar()

        binding.apply {
            addButton.setOnClickListener {
                dispatchIntent(AddExercise(textField, descriptionField, imageSourceField, subcategory))
            }
        }
        binding.exerciseEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            takeIf { hasFocus }.run { dispatchIntent(ValidateExercise(textField)) }
        }
        binding.exerciseEditText.doOnTextChanged { text, _, _, _ ->
            dispatchIntent(ValidateExercise(text.toString()))
            isAddButtonActive(false)
        }
    }

    override fun receive(state: CreateExerciseState) {}

    override fun receive(event: CreateExerciseEvent) {
        when (event) {
            CreateExerciseEvent.ValidationAlreadyExist -> {
                setEditTextError(getString(R.string.ex_exist))
                isAddButtonActive(false)
            }
            CreateExerciseEvent.ValidationEmpty -> {
                setEditTextError(getString(R.string.empty_field))
                isAddButtonActive(false)
            }
            CreateExerciseEvent.ValidationSuccess -> {
                setEditTextError("")
                isAddButtonActive(true)
            }
            CreateExerciseEvent.ExerciseActionResult.Failure -> showError(getString(R.string.smth_went_wrong))
            CreateExerciseEvent.ExerciseActionResult.Success -> closeScreenWithToast(getString(R.string.ex_success_added))
            is CreateExerciseEvent.HardException -> closeScreenWithError(getString(R.string.smth_went_wrong))
        }
    }

    private fun setEditTextError(errorText: String) {
        binding.exerciseTitleTextView.error = errorText
    }

    private fun isAddButtonActive(isActive: Boolean) {
        if (!isActive) {
            binding.addButton.alpha = 0.5f
            binding.addButton.isClickable = false
            binding.addButton.focusable = View.NOT_FOCUSABLE
            return
        }
        binding.addButton.alpha = 1f
        binding.addButton.isClickable = true
        binding.addButton.focusable = View.FOCUSABLE
    }

    private fun setUpToolbar() {
        requireActivity().title = getString(R.string.create_new_exercise_screen)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun showError(errorText: String) {
        // TODO Send crashlytics log here
        dispatchIntent(ShowToast(errorText))
    }

    private fun closeScreenWithToast(successText: String) {
        dispatchIntent(CloseScreenWithToast(successText))
    }

    private fun closeScreenWithError(errorText: String) {
        // TODO Send crashlytics log here
        dispatchIntent(CloseScreenWithToast(errorText))
    }

    companion object {
        private const val KEY_PARAMS = "new_exercise_params"
        fun createBundle(data: Parcelable?): Bundle {
            requireNotNull(data)
            require(data is LibraryParams.NewExercise)
            return Bundle().apply { putParcelable(KEY_PARAMS, data) }
        }
    }
}
