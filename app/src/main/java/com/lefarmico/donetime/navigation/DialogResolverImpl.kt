package com.lefarmico.donetime.navigation

import androidx.fragment.app.FragmentManager
import com.lefarmico.core.dialog.*
import com.lefarmico.navigation.dialog.Dialog
import com.lefarmico.navigation.dialog.DialogResolver
import javax.inject.Inject

class DialogResolverImpl @Inject constructor() : DialogResolver {

    override fun show(fragmentManager: FragmentManager, dialog: Dialog) {
        when (dialog) {
            is Dialog.CalendarPickerDialog -> LocalDatePickerDialog(
                dialog.dateParameter,
                dialog.callback
            ).show(fragmentManager, LocalDatePickerDialog.TAG_DIALOG)

            is Dialog.FieldEditorDialog -> FieldEditorDialog(
                dialog.hint,
                dialog.callback
            ).show(fragmentManager, FieldEditorDialog.TAG)

            is Dialog.SetParameterPickerDialog -> SetParameterPickerDialog(
                dialog.exerciseId,
                dialog.callback
            ).show(fragmentManager, SetParameterPickerDialog.TAG)

            is Dialog.ListItemPickerDialog -> ListItemPickerDialog(
                dialog.itemList,
                dialog.callbackPosition
            ).show(fragmentManager, ListItemPickerDialog.TAG)

            is Dialog.TimePickerDialog -> LocalTimePickerDialog(
                dialog.timeParameter,
                dialog.callback
            ).show(fragmentManager, LocalTimePickerDialog.TAG_DIALOG)
        }
    }
}
