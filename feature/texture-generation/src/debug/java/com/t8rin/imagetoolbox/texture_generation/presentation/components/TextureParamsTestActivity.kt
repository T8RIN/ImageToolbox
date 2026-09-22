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

import android.graphics.Bitmap
import android.os.Bundle
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
import coil3.size.Size
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.settings.domain.SimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalResourceManager
import com.t8rin.imagetoolbox.core.utils.initAppContext
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.withDefaultsFor
import java.lang.reflect.Proxy

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
