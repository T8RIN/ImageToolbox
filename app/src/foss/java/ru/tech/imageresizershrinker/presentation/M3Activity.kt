package ru.tech.imageresizershrinker.presentation

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.adjustFontSize
import ru.tech.imageresizershrinker.presentation.crash_screen.CrashActivity

@AndroidEntryPoint
open class M3Activity : AppCompatActivity() {

    private val settingsState = mutableStateOf(SettingsState.Default)

    fun getSettingsState(): SettingsState = settingsState.value

    override fun attachBaseContext(newBase: Context) {
        settingsState.value = runBlocking {
            EntryPointAccessors
                .fromApplication(newBase, SettingsStateEntryPoint::class.java)
                .settingsRepository
                .getSettingsState()
        }
        val newOverride = Configuration(newBase.resources?.configuration)
        settingsState.value.fontScale?.let { newOverride.fontScale = it }
        applyOverrideConfiguration(newOverride)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        adjustFontSize(settingsState.value.fontScale)
        enableEdgeToEdge()
        GlobalExceptionHandler.setAllowCollectCrashlytics(settingsState.value.allowCollectCrashlytics)
        GlobalExceptionHandler.initialize(
            applicationContext = applicationContext,
            activityToBeLaunched = CrashActivity::class.java,
        )
        lifecycleScope.launch {
            EntryPointAccessors
                .fromApplication(
                    this@M3Activity.applicationContext,
                    SettingsStateEntryPoint::class.java
                )
                .settingsRepository
                .getSettingsStateFlow()
                .collect {
                    settingsState.value = it
                }
        }
    }

    override fun recreate() {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            kotlinx.coroutines.delay(10L)
            super.recreate()
        }
    }

}