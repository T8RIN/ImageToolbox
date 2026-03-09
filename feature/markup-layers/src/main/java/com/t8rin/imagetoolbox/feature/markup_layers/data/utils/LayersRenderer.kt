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
import android.graphics.Canvas
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.settings.domain.SettingsProvider
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.feature.markup_layers.domain.MarkupLayer
import com.t8rin.imagetoolbox.feature.markup_layers.presentation.components.LayerContent
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.min

internal class LayersRenderer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsProvider: SettingsProvider,
    dispatchersHolder: DispatchersHolder
) : DispatchersHolder by dispatchersHolder {

    private val maxTextureSize by lazy {
        min(queryGlMaxTextureSize(), 8192)
    }

    suspend fun render(
        backgroundImage: Bitmap,
        layers: List<MarkupLayer>
    ): Bitmap = withContext(uiDispatcher) {
        val settingsState = settingsProvider.getSettingsState()
        val resultBitmap = backgroundImage.copy(Bitmap.Config.ARGB_8888, true)

        if (layers.isEmpty()) return@withContext resultBitmap

        val targetWidth = resultBitmap.width
        val targetHeight = resultBitmap.height

        val firstLayer = layers.first()
        val authorWidth = firstLayer.position.currentCanvasSize.width
        val authorHeight = firstLayer.position.currentCanvasSize.height

        if (authorWidth <= 0 || authorHeight <= 0) return@withContext resultBitmap

        // Determine Tile Dimensions
        val tileWidth = min(targetWidth, maxTextureSize)
        val tileHeight = min(targetHeight, maxTextureSize)

        "Starting GPU Tile Render. Target: ${targetWidth}x${targetHeight}. Tile Size: ${tileWidth}x${tileHeight}".makeLog()

        val displayMetrics = context.resources.displayMetrics
        val uiDensity = displayMetrics.density
        val uiFontScale = context.resources.configuration.fontScale
        val uiDensityDpi = displayMetrics.densityDpi

        // Setup ImageReader for Hardware Accelerated Capturing
        val imageReader = ImageReader.newInstance(
            tileWidth,
            tileHeight,
            PixelFormat.RGBA_8888,
            2
        )

        // Setup VirtualDisplay bounded to the Tile Size
        val displayManager = context.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val virtualDisplay = displayManager.createVirtualDisplay(
            "MarkupRenderDisplay",
            tileWidth, tileHeight, uiDensityDpi,
            imageReader.surface,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
        )

        val presentation = object : Presentation(context, virtualDisplay.display) {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                window?.setBackgroundDrawableResource(android.R.color.transparent)
                (window?.decorView as? ViewGroup)?.apply {
                    clipChildren = false
                    clipToPadding = false
                }
            }
        }

        // Use Presentation context for accurate text bounds
        val composeView = ComposeView(presentation.context).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            val owner = NonUiSavedStateRegistryOwner()
            setViewTreeLifecycleOwner(owner)
            setViewTreeSavedStateRegistryOwner(owner)
            clipChildren = false
            clipToPadding = false
            layoutParams = ViewGroup.LayoutParams(tileWidth, tileHeight)
        }

        presentation.setContentView(composeView)
        presentation.show()

        // Synchronization state for the Tiling Loop
        var currentTileX by mutableStateOf(0)
        var currentTileY by mutableStateOf(0)
        var readyToCapture by mutableStateOf(false)
        val frameChannel = Channel<Bitmap>(Channel.CONFLATED)

        // Listen for hardware frames
        imageReader.setOnImageAvailableListener({ reader ->
            val img = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
            if (readyToCapture) {
                readyToCapture = false

                // Extract GPU buffer and remove hardware stride padding
                val planes = img.planes
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * tileWidth

                val paddedBitmap = Bitmap.createBitmap(
                    tileWidth + rowPadding / pixelStride,
                    tileHeight,
                    Bitmap.Config.ARGB_8888
                )
                paddedBitmap.copyPixelsFromBuffer(buffer)

                val finalBitmap = if (rowPadding > 0) {
                    Bitmap.createBitmap(paddedBitmap, 0, 0, tileWidth, tileHeight)
                } else paddedBitmap

                img.close()
                frameChannel.trySend(finalBitmap)
            } else {
                img.close()
            }
        }, Handler(Looper.getMainLooper()))

        // Build the Compose Tree
        composeView.setContent {
            val uiSettingsState = settingsState.toUiState()
            val density = Density(uiDensity, uiFontScale)

            // Wrap in MaterialTheme to fix text line-wrapping differences
            androidx.compose.material3.MaterialTheme {
                CompositionLocalProvider(
                    LocalSettingsState provides uiSettingsState,
                    LocalDensity provides density
                ) {
                    val tileWidthDp = with(density) { tileWidth.toDp() }
                    val tileHeightDp = with(density) { tileHeight.toDp() }
                    val authorWidthDp = with(density) { authorWidth.toDp() }
                    val authorHeightDp = with(density) { authorHeight.toDp() }

                    // The Viewport Box (Tile Size)
                    Box(
                        modifier = Modifier
                            .requiredSize(tileWidthDp, tileHeightDp)
                            .clipToBounds()
                    ) {
                        // The Panning Box (Shifts the content under the "scanner")
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

                            // The Scaled Author Container
                            Box(
                                modifier = Modifier
                                    .size(authorWidthDp, authorHeightDp)
                                    .graphicsLayer {
                                        scaleX = ratio
                                        scaleY = ratio
                                        transformOrigin = TransformOrigin(0f, 0f)
                                        translationX = (targetWidth - authorWidth * ratio) / 2f
                                        translationY = (targetHeight - authorHeight * ratio) / 2f
                                    }
                            ) {
                                layers.forEach { layer ->
                                    if (layer.position.isVisible) {
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
                    }

                    // Trigger capture whenever the tile offset updates
                    LaunchedEffect(currentTileX, currentTileY) {
                        delay(350) // Wait for layout/images to settle
                        "Requesting hardware capture for tile: $currentTileX, $currentTileY".makeLog()
                        readyToCapture = true
                        composeView.invalidate() // Force a frame
                    }
                }
            }
        }

        // Execute the Tile Loop
        val canvas = Canvas(resultBitmap)

        for (y in 0 until targetHeight step tileHeight) {
            for (x in 0 until targetWidth step tileWidth) {
                // Shift the compose view
                currentTileX = x
                currentTileY = y

                // Suspend and wait for the hardware frame to be processed
                val tileBitmap = frameChannel.receive()

                // Stamp the tile onto our massive result bitmap
                canvas.drawBitmap(tileBitmap, x.toFloat(), y.toFloat(), null)
                tileBitmap.recycle()
            }
        }

        // Cleanup Memory
        presentation.dismiss()
        virtualDisplay.release()
        imageReader.close()
        frameChannel.close()

        return@withContext resultBitmap
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