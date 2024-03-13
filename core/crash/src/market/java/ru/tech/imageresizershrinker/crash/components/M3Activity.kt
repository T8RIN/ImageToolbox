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

package ru.tech.imageresizershrinker.crash.components

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.adjustFontSize
import ru.tech.imageresizershrinker.crash.CrashActivity
import ru.tech.imageresizershrinker.crash.SettingsStateEntryPoint

@AndroidEntryPoint
open class M3Activity : AppCompatActivity() {

    private val settingsState = mutableStateOf(SettingsState.Default)

    fun getSettingsState(): SettingsState = settingsState.value

    override fun attachBaseContext(newBase: Context) {
        settingsState.value = runBlocking {
            EntryPointAccessors
                .fromApplication(newBase, SettingsStateEntryPoint::class.java)
                .settingsRepository.getSettingsState()
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
        Firebase.analytics.setAnalyticsCollectionEnabled(settingsState.value.allowCollectCrashlytics)
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