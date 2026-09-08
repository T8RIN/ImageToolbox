/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2026 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.texture_generation.presentation.components

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import coil3.size.Size
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.settings.domain.SimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalResourceManager
import java.lang.reflect.Proxy
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.utils.initAppContext
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.withDefaultsFor
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

class TextureParamsTestActivity : ComponentActivity() {
    var params by mutableStateOf(TextureParams.Default.withDefaultsFor(TextureFilterType.HatchPattern))

    @Volatile
    var displayedType: TextureFilterType? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppContext()
        val settingsInteractor = Proxy.newProxyInstance(
            SimpleSettingsInteractor::class.java.classLoader,
            arrayOf(SimpleSettingsInteractor::class.java)
        ) { _, method, _ -> error("Unexpected settings change: ${method.name}") } as SimpleSettingsInteractor
        val resourceManager = object : ResourceManager {
            override fun getString(resId: Int) = this@TextureParamsTestActivity.getString(resId)
            override fun getString(resId: Int, vararg formatArgs: Any) =
                this@TextureParamsTestActivity.getString(resId, *formatArgs)

            override fun getStringLocalized(resId: Int, language: String) = getString(resId)
            override fun getStringLocalized(resId: Int, language: String, vararg formatArgs: Any) =
                getString(resId, *formatArgs)
        }
        setContent {
            CompositionLocalProvider(
                LocalSettingsState provides SettingsState.Default.toUiState(),
                LocalResourceManager provides resourceManager,
                LocalSimpleSettingsInteractor provides settingsInteractor
            ) {
                MaterialTheme {
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        TextureParamsSelection(
                            value = params,
                            onValueChange = { params = it },
                            previewProvider = { IdentityPreview },
                            favoriteTextureTypes = emptySet(),
                            onToggleFavorite = {}
                        )
                    }
                    SideEffect { displayedType = params.textureFilterType }
                }
            }
        }
    }

    private object IdentityPreview : Transformation() {
        override val cacheKey = "TextureParamsTest"
        override suspend fun transform(input: Bitmap, size: Size): Bitmap = input
    }
}

@RunWith(AndroidJUnit4::class)
class TextureParamsSelectionTest {
    @Test
    fun switchingBetweenPatternsAndOtherTexturesKeepsAnimatedPanelsConsistent() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val activity = instrumentation.startActivitySync(
            Intent(instrumentation.targetContext, TextureParamsTestActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ) as TextureParamsTestActivity
        try {
            val types = listOf(
                TextureFilterType.HatchPattern,
                TextureFilterType.BrushedMetal,
                TextureFilterType.RosettePattern,
                TextureFilterType.Sphere3D,
                TextureFilterType.CrossStitchPattern,
                TextureFilterType.Brick,
                TextureFilterType.HatchPattern
            )
            for (type in types) {
                instrumentation.runOnMainSync {
                    activity.params = activity.params.withDefaultsFor(type)
                }
                val deadline = SystemClock.uptimeMillis() + 5000
                while (activity.displayedType != type && SystemClock.uptimeMillis() < deadline) {
                    SystemClock.sleep(16)
                }
                assertEquals(type, activity.displayedType)
                SystemClock.sleep(100)
            }
            SystemClock.sleep(500)
            instrumentation.waitForIdleSync()
            assertEquals(TextureFilterType.HatchPattern, activity.displayedType)
        } finally {
            instrumentation.runOnMainSync { activity.finish() }
        }
    }
}