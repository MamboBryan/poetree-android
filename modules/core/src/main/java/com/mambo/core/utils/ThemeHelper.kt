package com.mambo.core.utils

import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {

    fun applyTheme(theme: Int) {
        AppCompatDelegate.setDefaultNightMode(theme)
    }

}
