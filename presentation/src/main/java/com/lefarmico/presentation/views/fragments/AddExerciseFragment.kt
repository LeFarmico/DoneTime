package com.lefarmico.presentation.views.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.lefarmico.domain.utils.DataState
import com.lefarmico.presentation.databinding.FragmentAddExerciseBinding
import com.lefarmico.presentation.intents.AddExerciseIntent
import com.lefarmico.presentation.viewModels.AddExerciseViewModel
import com.lefarmico.presentation.views.base.BaseFragment

class AddExerciseFragment : BaseFragment<FragmentAddExerciseBinding, AddExerciseViewModel>(
    FragmentAddExerciseBinding::inflate,
    AddExerciseViewModel::class.java
) {

    private var bundleResult: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getBundleResult()
    }
    override fun setUpViews() {
        binding.addButton.setOnClickListener {
            viewModel.onTriggerEvent(
                AddExerciseIntent.AddExerciseResult(
                    getTitleField(),
                    getDescriptionField(),
                    getImageSource(),
                    getSubcategory()
                )
            )
        }
        
        viewModel.addExerciseLiveData.observe(viewLifecycleOwner) { dataState ->
            when (dataState) {
                DataState.Empty -> {
                    binding.emptyState.root.visibility = View.VISIBLE
                    binding.errorState.root.visibility = View.GONE
                    binding.loadingState.root.visibility = View.GONE
                }
                is DataState.Error -> {
                    binding.errorState.root.visibility = View.VISIBLE
                    binding.emptyState.root.visibility = View.GONE
                    binding.loadingState.root.visibility = View.GONE
                }
                DataState.Loading -> {
                    binding.loadingState.root.visibility = View.VISIBLE
                    binding.emptyState.root.visibility = View.GONE
                    binding.errorState.root.visibility = View.GONE
                }
                is DataState.Success -> {
                    Toast.makeText(requireContext(), "Exercise Added to Library", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack(
                        BACK_STACK_KEY,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    viewModel.onTriggerEvent(AddExerciseIntent.DefaultState)
                    binding.emptyState.root.visibility = View.GONE
                    binding.errorState.root.visibility = View.GONE
                    binding.loadingState.root.visibility = View.GONE
                }
            }
        }
    }

    private fun getBundleResult() {
        val bundle = this.arguments
        if (bundle != null) {
            bundleResult = bundle.getInt(KEY_NUMBER)
        }
    }

    private fun getTitleField(): String {
        return binding.exerciseEditText.text.toString()
    }
    private fun getDescriptionField(): String {
        return binding.descriptionEditText.text.toString()
    }
    private fun getImageSource(): String {
        return ""
    }
    private fun getSubcategory(): Int {
        return bundleResult
    }

    companion object {
        const val BACK_STACK_KEY = "add_ex_stack"
        const val KEY_NUMBER = "add_ex_key"
    }
}
