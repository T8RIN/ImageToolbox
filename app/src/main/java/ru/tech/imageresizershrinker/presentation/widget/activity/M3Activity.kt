package ru.tech.imageresizershrinker.presentation.widget.activity

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import ru.tech.imageresizershrinker.presentation.crash_screen.CrashActivity
import ru.tech.imageresizershrinker.presentation.utils.exception.GlobalExceptionHandler
import ru.tech.imageresizershrinker.core.android.ContextUtils.adjustFontSize

open class M3Activity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(newBase.adjustFontSize())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        adjustFontSize()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        GlobalExceptionHandler.initialize(applicationContext, CrashActivity::class.java)
    }

}