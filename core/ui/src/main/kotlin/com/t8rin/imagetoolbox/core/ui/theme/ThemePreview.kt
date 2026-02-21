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

package com.t8rin.imagetoolbox.core.ui.theme

import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.animation.core.snap
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.createBitmap
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.request.ImageRequest
import coil3.size.pxOrElse
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.DynamicTheme
import com.t8rin.dynamic.theme.rememberDynamicThemeState
import com.t8rin.imagetoolbox.core.domain.model.ColorModel
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.settings.domain.SimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.settings.domain.model.OneTimeSaveLocation
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.domain.model.ShapeType
import com.t8rin.imagetoolbox.core.settings.presentation.model.defaultColorTuple
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.getStringLocalized
import com.t8rin.imagetoolbox.core.ui.utils.provider.LocalResourceManager
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.initAppContext
import java.util.Locale
import kotlin.math.max

@Composable
fun ImageToolboxThemeForPreview(
    isDarkTheme: Boolean,
    keyColor: Color? = defaultColorTuple.primary,
    shapesType: ShapeType = ShapeType.Rounded(),
    content: @Composable () -> Unit
) {
    LocalContext.current.applicationContext.initAppContext()

    FakeLoader(
        ColorTuple(keyColor ?: Color.Transparent)
    ) {
        DynamicTheme(
            state = rememberDynamicThemeState(
                initialColorTuple = ColorTuple(keyColor ?: Color.Transparent)
            ),
            dynamicColor = keyColor == null,
            isDarkTheme = isDarkTheme,
            defaultColorTuple = ColorTuple(keyColor ?: Color.Transparent),
            colorAnimationSpec = snap(),
            content = {
                CompositionLocalProvider(
                    LocalSettingsState provides SettingsState.Default.toUiState().copy(
                        shapesType = shapesType
                    ),
                    LocalSimpleSettingsInteractor provides FakeSettings,
                    LocalResourceManager provides FakeRes
                ) {
                    MaterialTheme(
                        motionScheme = CustomMotionScheme,
                        colorScheme = modifiedColorScheme(),
                        shapes = modifiedShapes(),
                        content = {
                            Surface {
                                content()
                            }
                        }
                    )
                }
            }
        )
    }
}

@Composable
private fun FakeLoader(
    tuple: ColorTuple,
    content: @Composable () -> Unit
) {
    DynamicTheme(
        state = rememberDynamicThemeState(tuple),
        isDarkTheme = false,
        defaultColorTuple = tuple
    ) {
        val colorScheme = MaterialTheme.colorScheme

        val fakeLoader = remember(colorScheme) {
            AsyncImagePreviewHandler(
                image = { request: ImageRequest ->
                    val size = request.sizeResolver.size()

                    val width = size.width.pxOrElse { 800 }.coerceAtLeast(600) - 200
                    val height = size.height.pxOrElse { 800 }.coerceAtLeast(600)

                    val bitmap = createBitmap(width, height)
                    val canvas = Canvas(bitmap)

                    val base = LinearGradient(
                        0f,
                        0f,
                        width.toFloat(),
                        height.toFloat(),
                        intArrayOf(
                            colorScheme.primary.toArgb(),
                            colorScheme.tertiary.toArgb(),
                            colorScheme.secondary.toArgb(),
                            colorScheme.error.toArgb(),
                        ),
                        floatArrayOf(0f, 0.3f, 0.7f, 1f),
                        Shader.TileMode.CLAMP
                    )

                    canvas.drawRect(
                        0f,
                        0f,
                        width.toFloat(),
                        height.toFloat(),
                        Paint(Paint.ANTI_ALIAS_FLAG).apply {
                            shader = base
                        }
                    )

                    val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        color = colorScheme.primaryContainer.toArgb()
                        maskFilter = BlurMaskFilter(height * 0.08f, BlurMaskFilter.Blur.NORMAL)
                    }

                    canvas.drawOval(
                        width * 0.1f,
                        height * 0.2f,
                        width * 0.7f,
                        height * 0.8f,
                        blurPaint
                    )

                    canvas.drawOval(
                        width * 0.5f,
                        height * 0.1f,
                        width * 0.95f,
                        height * 0.6f,
                        blurPaint
                    )

                    val noisePaint = Paint().apply {
                        color = colorScheme.tertiaryContainer.copy(0.5f).toArgb()
                    }

                    repeat((width * height) / 6) {
                        val x = (Math.random() * width).toFloat()
                        val y = (Math.random() * height).toFloat()
                        canvas.drawPoint(x, y, noisePaint)
                    }

                    val vignette = RadialGradient(
                        width / 2f,
                        height / 2f,
                        max(width, height) * 0.9f,
                        intArrayOf(
                            Color.Transparent.toArgb(),
                            colorScheme.tertiaryContainer.copy(0.5f).toArgb(),
                            Color.Transparent.toArgb(),
                        ),
                        floatArrayOf(0.6f, 0.85f, 1f),
                        Shader.TileMode.CLAMP
                    )

                    canvas.drawRect(
                        0f,
                        0f,
                        width.toFloat(),
                        height.toFloat(),
                        Paint().apply { shader = vignette }
                    )

                    bitmap.asImage()
                }
            )
        }

        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides fakeLoader
        ) {
            content()
        }
    }
}

private val FakeSettings = object : SimpleSettingsInteractor {
    override suspend fun toggleMagnifierEnabled() = Unit
    override suspend fun setOneTimeSaveLocations(value: List<OneTimeSaveLocation>) =
        Unit

    override suspend fun toggleRecentColor(
        color: ColorModel,
        forceExclude: Boolean
    ) = Unit

    override suspend fun toggleFavoriteColor(
        color: ColorModel,
        forceExclude: Boolean
    ) = Unit

    override fun isInstalledFromPlayStore(): Boolean = false

    override suspend fun toggleSettingsGroupVisibility(
        key: Int,
        value: Boolean
    ) = Unit

    override suspend fun clearRecentColors() = Unit

    override suspend fun updateFavoriteColors(colors: List<ColorModel>) = Unit

    override suspend fun setBackgroundColorForNoAlphaFormats(color: ColorModel) =
        Unit

    override suspend fun toggleCustomAsciiGradient(gradient: String) = Unit

    override suspend fun toggleOverwriteFiles() = Unit

    override suspend fun setSpotHealMode(mode: Int) = Unit
    override suspend fun setBorderWidth(width: Float) = Unit
}

private val FakeRes = object : ResourceManager {
    override fun getString(resId: Int): String = appContext.getString(resId)

    override fun getString(
        resId: Int,
        vararg formatArgs: Any
    ): String = appContext.getString(resId, formatArgs)

    override fun getStringLocalized(
        resId: Int,
        language: String
    ): String =
        appContext.getStringLocalized(resId, Locale.forLanguageTag(language))

    override fun getStringLocalized(
        resId: Int,
        language: String,
        vararg formatArgs: Any
    ): String =
        appContext.getStringLocalized(resId, Locale.forLanguageTag(language))

}