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

package com.t8rin.imagetoolbox.feature.markup_layers.data.utils

import android.app.Presentation
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES20
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.core.content.getSystemService
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.t8rin.imagetoolbox.core.data.image.utils.drawBitmap
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.helper.ContextUtils.density
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.LayerContent
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.min

internal class LayersRenderer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    private val settingsState: SettingsState get() = settingsProvider.settingsState.value

    private val maxTextureSize by lazy {
        min(queryGlMaxTextureSize(), 8192)
    }

    suspend fun render(
        backgroundImage: Bitmap,
        layers: List<MarkupLayer>
    ): Bitmap = coroutineScope {
        val resultBitmap = backgroundImage.copy(Bitmap.Config.ARGB_8888, true)

        if (layers.isEmpty()) return@coroutineScope resultBitmap

        val targetWidth = resultBitmap.width
        val targetHeight = resultBitmap.height

        val firstLayer = layers.first()
        val authorWidth = firstLayer.position.currentCanvasSize.width
        val authorHeight = firstLayer.position.currentCanvasSize.height

        if (authorWidth <= 0 || authorHeight <= 0) return@coroutineScope resultBitmap

        val tileWidth = min(targetWidth, maxTextureSize)
        val tileHeight = min(targetHeight, maxTextureSize)

        "Starting GPU Tile Render. Target: ${targetWidth}x${targetHeight}. Tile Size: ${tileWidth}x${tileHeight}".makeLog()

        val imageReader = ImageReader.newInstance(
            tileWidth,
            tileHeight,
            PixelFormat.RGBA_8888,
            2
        )

        val displayManager =
            context.getSystemService<DisplayManager>() ?: return@coroutineScope resultBitmap
        val virtualDisplay = displayManager.createVirtualDisplay(
            "MarkupRenderDisplay",
            tileWidth,
            tileHeight,
            context.resources.displayMetrics.densityDpi,
            imageReader.surface,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
        )

        val presentation = withContext(uiDispatcher) {
            object : Presentation(context, virtualDisplay.display) {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    window?.apply {
                        setBackgroundDrawableResource(android.R.color.transparent)
                        (decorView as? ViewGroup)?.apply {
                            clipChildren = false
                            clipToPadding = false
                        }
                    }
                }
            }
        }

        val composeView = withContext(uiDispatcher) {
            presentation.run {
                ComposeView(context).apply {
                    setBackgroundColor(Color.TRANSPARENT)
                    NonUiSavedStateRegistryOwner().apply {
                        setViewTreeLifecycleOwner(this)
                        setViewTreeSavedStateRegistryOwner(this)
                    }
                    clipChildren = false
                    clipToPadding = false
                    layoutParams = ViewGroup.LayoutParams(tileWidth, tileHeight)
                    setContentView(this)
                    show()
                }
            }
        }

        var currentTileX by mutableIntStateOf(0)
        var currentTileY by mutableIntStateOf(0)
        var readyToCapture by mutableStateOf(false)
        val frameChannel = Channel<Bitmap>(Channel.CONFLATED)

        withContext(uiDispatcher) {
            imageReader.setOnImageAvailableListener({ reader ->
                reader.acquireLatestImage()?.use { image ->
                    if (readyToCapture) {
                        readyToCapture = false

                        val planes = image.planes
                        val buffer = planes[0].buffer
                        val pixelStride = planes[0].pixelStride
                        val rowStride = planes[0].rowStride
                        val rowPadding = rowStride - pixelStride * tileWidth

                        frameChannel.trySend(
                            createBitmap(
                                width = tileWidth + rowPadding / pixelStride,
                                height = tileHeight
                            ).apply {
                                copyPixelsFromBuffer(buffer)
                            }.let { padded ->
                                if (rowPadding > 0) {
                                    Bitmap.createBitmap(padded, 0, 0, tileWidth, tileHeight)
                                } else {
                                    padded
                                }
                            }
                        )
                    }
                } ?: return@setOnImageAvailableListener

            }, Handler(Looper.getMainLooper()))
        }

        composeView.setContent {
            MaterialTheme {
                with(context.density) {
                    CompositionLocalProvider(
                        LocalSettingsState provides settingsState.toUiState(),
                        LocalDensity provides this
                    ) {
                        val authorWidthDp = authorWidth.toDp()
                        val authorHeightDp = authorHeight.toDp()

                        Box(
                            modifier = Modifier
                                .requiredSize(
                                    width = tileWidth.toDp(),
                                    height = tileHeight.toDp()
                                )
                                .clipToBounds()
                        ) {
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    translationX = -currentTileX.toFloat()
                                    translationY = -currentTileY.toFloat()
                                }
                            ) {
                                val ratio = min(
                                    targetWidth.toFloat() / authorWidth,
                                    targetHeight.toFloat() / authorHeight
                                )

                                Box(
                                    modifier = Modifier
                                        .size(authorWidthDp, authorHeightDp)
                                        .graphicsLayer {
                                            scaleX = ratio
                                            scaleY = ratio
                                            transformOrigin = TransformOrigin(0f, 0f)
                                            translationX = (targetWidth - authorWidth * ratio) / 2f
                                            translationY =
                                                (targetHeight - authorHeight * ratio) / 2f
                                        }
                                ) {
                                    layers.forEach { layer ->
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.Center)
                                                .graphicsLayer {
                                                    scaleX = layer.position.scale
                                                    scaleY = layer.position.scale
                                                    rotationZ = layer.position.rotation
                                                    translationX = layer.position.offsetX
                                                    translationY = layer.position.offsetY
                                                    alpha = layer.position.alpha
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            LayerContent(
                                                modifier = Modifier.sizeIn(
                                                    maxWidth = authorWidthDp / 2f,
                                                    maxHeight = authorHeightDp / 2f
                                                ),
                                                type = layer.type,
                                                textFullSize = minOf(authorWidth, authorHeight)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        LaunchedEffect(currentTileX, currentTileY) {
                            delay(350)
                            "Requesting hardware capture for tile: $currentTileX, $currentTileY".makeLog()
                            readyToCapture = true
                            composeView.invalidate()
                        }
                    }
                }
            }
        }

        resultBitmap.applyCanvas {
            for (y in 0 until targetHeight step tileHeight) {
                for (x in 0 until targetWidth step tileWidth) {
                    currentTileX = x
                    currentTileY = y

                    frameChannel.receive().apply {
                        drawBitmap(
                            bitmap = this,
                            left = x.toFloat(),
                            top = y.toFloat()
                        )
                        recycle()
                    }
                }
            }
        }

        presentation.dismiss()
        virtualDisplay.release()
        imageReader.close()
        frameChannel.close()

        resultBitmap
    }

    private fun queryGlMaxTextureSize(): Int {
        return try {
            val display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            val version = IntArray(2)
            EGL14.eglInitialize(display, version, 0, version, 1)

            val configAttributes = intArrayOf(
                EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
                EGL14.EGL_LEVEL, 0,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_NONE
            )
            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfig = IntArray(1)
            EGL14.eglChooseConfig(display, configAttributes, 0, configs, 0, 1, numConfig, 0)

            if (numConfig[0] == 0) return 4096

            val config = configs[0]

            val surfaceAttributes = intArrayOf(
                EGL14.EGL_WIDTH, 1,
                EGL14.EGL_HEIGHT, 1,
                EGL14.EGL_NONE
            )
            val surface = EGL14.eglCreatePbufferSurface(display, config, surfaceAttributes, 0)

            val contextAttributes = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
            )
            val context =
                EGL14.eglCreateContext(display, config, EGL14.EGL_NO_CONTEXT, contextAttributes, 0)

            EGL14.eglMakeCurrent(display, surface, surface, context)
            val maxSize = IntArray(1)
            GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0)

            EGL14.eglMakeCurrent(
                display,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT
            )
            EGL14.eglDestroySurface(display, surface)
            EGL14.eglDestroyContext(display, context)
            EGL14.eglTerminate(display)

            val limit = maxSize[0]
            "Device GL_MAX_TEXTURE_SIZE is $limit".makeLog()

            if (limit > 0) limit else 4096
        } catch (t: Throwable) {
            "Failed to query GL max texture size, falling back to 4096, ${t.message}".makeLog()
            4096
        }
    }

}