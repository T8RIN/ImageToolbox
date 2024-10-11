/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.crash.components

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.core.crash.CrashActivity
import ru.tech.imageresizershrinker.core.crash.SettingsStateEntryPoint
import ru.tech.imageresizershrinker.core.di.entryPoint
import ru.tech.imageresizershrinker.core.domain.model.SystemBarsVisibility
import ru.tech.imageresizershrinker.core.domain.utils.smartJob
import ru.tech.imageresizershrinker.core.settings.domain.SettingsProvider
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.adjustFontSize
import ru.tech.imageresizershrinker.core.ui.utils.state.update

@AndroidEntryPoint
abstract class M3Activity : AppCompatActivity() {

    private val windowInsetsController: WindowInsetsControllerCompat?
        get() = window?.let {
            WindowCompat.getInsetsController(it, it.decorView)
        }

    private lateinit var settingsProvider: SettingsProvider

    private val _settingsState = mutableStateOf(SettingsState.Default)
    private val settingsState: SettingsState by _settingsState

    @JvmName("getSettingsState1")
    fun getSettingsState(): SettingsState = settingsState

    override fun attachBaseContext(newBase: Context) {
        newBase.entryPoint<SettingsStateEntryPoint> {
            settingsProvider = settingsManager
            _settingsState.update {
                runBlocking {
                    settingsProvider.getSettingsState()
                }
            }
            lifecycleScope.launch {
                settingsProvider
                    .getSettingsStateFlow()
                    .collect { state ->
                        _settingsState.update { state }
                        handleSystemBarsBehavior()
                    }
            }
        }
        val newOverride = Configuration(newBase.resources?.configuration)
        settingsState.fontScale?.let { newOverride.fontScale = it }
        applyOverrideConfiguration(newOverride)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        adjustFontSize(settingsState.fontScale)
        enableEdgeToEdge()
        GlobalExceptionHandler.setAllowCollectCrashlytics(settingsState.allowCollectCrashlytics)
        GlobalExceptionHandler.initialize(
            applicationContext = applicationContext,
            activityToBeLaunched = CrashActivity::class.java,
        )
        Firebase.analytics.setAnalyticsCollectionEnabled(settingsState.allowCollectCrashlytics)

        handleSystemBarsBehavior()
    }

    private var recreationJob: Job? by smartJob()

    override fun recreate() {
        recreationJob = lifecycleScope.launch {
            delay(200L)
            super.recreate()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) handleSystemBarsBehavior()
    }

    private fun handleSystemBarsBehavior() {
        val safeController: WindowInsetsControllerCompat? = windowInsetsController

        safeController?.apply {
            when (settingsState.systemBarsVisibility) {
                SystemBarsVisibility.Auto -> {
                    val orientation = resources.configuration.orientation

                    show(STATUS_BARS)

                    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                        hide(NAV_BARS)
                    } else {
                        show(NAV_BARS)
                    }
                }

                SystemBarsVisibility.HideAll -> {
                    hide(SYSTEM_BARS)
                }

                SystemBarsVisibility.ShowAll -> {
                    show(SYSTEM_BARS)
                }

                SystemBarsVisibility.HideNavigationBar -> {
                    show(STATUS_BARS)
                    hide(NAV_BARS)
                }

                SystemBarsVisibility.HideStatusBar -> {
                    show(NAV_BARS)
                    hide(STATUS_BARS)
                }
            }

            systemBarsBehavior = if (settingsState.isSystemBarsVisibleBySwipe) {
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
        }
    }

}

private val NAV_BARS = WindowInsetsCompat.Type.navigationBars()
private val SYSTEM_BARS = WindowInsetsCompat.Type.systemBars()
private val STATUS_BARS = WindowInsetsCompat.Type.statusBars()