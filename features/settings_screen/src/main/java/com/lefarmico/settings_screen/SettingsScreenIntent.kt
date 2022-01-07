package com.lefarmico.settings_screen

import com.lefarmico.core.base.BaseIntent

sealed class SettingsScreenIntent : BaseIntent {

    object SetFullDateFormatter : SettingsScreenIntent()

    object GetCurrentDateFormatter : SettingsScreenIntent()
}
