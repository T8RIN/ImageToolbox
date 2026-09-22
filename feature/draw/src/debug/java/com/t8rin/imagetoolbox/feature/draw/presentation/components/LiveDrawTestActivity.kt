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

package com.t8rin.imagetoolbox.feature.draw.presentation.components

import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.retainedComponent
import com.t8rin.imagetoolbox.core.domain.model.GradientFill
import com.t8rin.imagetoolbox.core.domain.model.GradientPalette
import com.t8rin.imagetoolbox.core.domain.model.pt
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.HelperGridParams
import com.t8rin.imagetoolbox.core.utils.initAppContext
import com.t8rin.imagetoolbox.feature.draw.domain.DrawLineStyle
import com.t8rin.imagetoolbox.feature.draw.domain.DrawMode
import com.t8rin.imagetoolbox.feature.draw.domain.DrawPathMode
import com.t8rin.imagetoolbox.feature.draw.presentation.components.utils.DrawRenderCache
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicInteger

private class LiveDrawTestState(context: ComponentContext) : ComponentContext by context {
    val cache = DrawRenderCache()
    val image = createBitmap(512, 512)
        .apply { eraseColor(android.graphics.Color.WHITE) }.asImageBitmap()
    var paths by mutableStateOf(emptyList<UiPathPaint>())
    val filterCalls = AtomicInteger()
}

class LiveDrawTestActivity : ComponentActivity() {
    private lateinit var state: LiveDrawTestState
    val image get() = state.image.asAndroidBitmap()
    val cache get() = state.cache
    val filterCalls get() = state.filterCalls.get()
    var mode: DrawMode by mutableStateOf(DrawMode.Pen)
    var gradient: GradientPalette? by mutableStateOf(null)
    var gradientLength by mutableStateOf(1f)
    var gradientMirrored by mutableStateOf(false)
    var background by mutableStateOf(Color.Transparent)
    var backgroundGradient: GradientFill? by mutableStateOf(null)
    var erasing by mutableStateOf(false)
    var softness by mutableStateOf(0.pt)
    var alpha by mutableStateOf(1f)
    var width by mutableStateOf(65.pt)
    var viewport by mutableStateOf(256.dp)
    var paths: List<UiPathPaint>
        get() = state.paths
        set(value) {
            state.paths = value
        }

    @Volatile
    var frame: Bitmap? = null

    @Volatile
    var bounds = Rect.Zero

    @Volatile
    var ready = false

    @Volatile
    var readyPaths: List<UiPathPaint>? = null

    @Volatile
    var observeFrame: ((Bitmap) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppContext()
        state = retainedComponent { LiveDrawTestState(it) }
        viewport =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 208.dp else 256.dp
        current = this
        setContent {
            val settings = SettingsState.Default.toUiState().copy(
                magnifierEnabled = false,
                drawBitmapBorder = false
            )
            CompositionLocalProvider(LocalSettingsState provides settings) {
                MaterialTheme {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val displayedPaths = paths
                        BitmapDrawer(
                            imageBitmap = state.image,
                            renderCache = state.cache,
                            paths = displayedPaths,
                            onAddPath = { paths = paths + it },
                            onRequestFiltering = { source, _ ->
                                state.filterCalls.incrementAndGet()
                                delay(80)
                                createBitmap(
                                    source.width,
                                    source.height
                                ).apply { eraseColor(android.graphics.Color.CYAN) }
                            },
                            strokeWidth = width,
                            brushSoftness = softness,
                            drawColor = Color.Red.copy(alpha = alpha),
                            gradientPalette = gradient,
                            gradientLength = gradientLength,
                            isGradientMirrored = gradientMirrored,
                            isEraserOn = erasing,
                            drawMode = mode,
                            drawPathMode = DrawPathMode.Free,
                            drawLineStyle = DrawLineStyle.None,
                            modifier = Modifier
                                .size(viewport)
                                .onGloballyPositioned { bounds = it.boundsInWindow() },
                            backgroundColor = background,
                            backgroundGradient = backgroundGradient,
                            panEnabled = false,
                            helperGridParams = HelperGridParams(),
                            onDraw = { frame = it; observeFrame?.invoke(it) },
                            onRenderReady = {
                                ready = it
                                readyPaths = displayedPaths.takeIf { _ -> it }
                            }
                        )
                    }
                }
            }
        }
    }

    companion object {
        @Volatile
        var current: LiveDrawTestActivity? = null
    }
}
