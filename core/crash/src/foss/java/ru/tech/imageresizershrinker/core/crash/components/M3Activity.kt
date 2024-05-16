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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.core.crash.CrashActivity
import ru.tech.imageresizershrinker.core.crash.SettingsStateEntryPoint
import ru.tech.imageresizershrinker.core.di.entryPoint
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.settings.presentation.model.asColorTuple
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.adjustFontSize

@AndroidEntryPoint
open class M3Activity : AppCompatActivity() {

    private val settingsState = mutableStateOf(SettingsState.Default)

    fun getSettingsState(): SettingsState = settingsState.value

    override fun attachBaseContext(newBase: Context) {
        newBase.entryPoint<SettingsStateEntryPoint> {
            runBlocking {
                settingsState.value = settingsManager.getSettingsState()
            }
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
            entryPoint<SettingsStateEntryPoint> {
                settingsManager
                    .getSettingsStateFlow()
                    .collect {
                        settingsState.value = it

                        val colorTuple = it.appColorTuple.asColorTuple()
                        DynamicColors.applyToActivitiesIfAvailable(
                            this@M3Activity.application,
                            DynamicColorsOptions.Builder()
                                .setContentBasedSource(colorTuple.primary.toArgb())
                                .build()
                        )
                    }
            }
        }
    }

    override fun recreate() {
        CoroutineScope(Dispatchers.Main.immediate).launch {
            delay(10L)
            super.recreate()
        }
    }

}