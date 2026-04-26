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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components.viewer

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.Image
import coil3.asImage
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.flexibleResize
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.core.utils.makeLog
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.PdfRenderer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import kotlin.math.sqrt

@Composable
internal fun LegacyPdfViewer(
    uri: Uri,
    spacing: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(start = 20.dp, end = 20.dp),
    modifier: Modifier = Modifier
) {
    val showError: (Throwable) -> Unit = {
        it.makeLog("PdfViewer")
        AppToastHost.showFailureToast(it)
    }

    val listState = rememberLazyListState()
    BoxWithConstraints(modifier = modifier.animateContentSizeNoClip()) {
        val density = LocalDensity.current
        val width = with(density) { this@BoxWithConstraints.maxWidth.toPx() }.toInt()
        val height = (width * sqrt(2f)).toInt()

        val rendererScope = rememberCoroutineScope()
        val mutex = remember { Mutex() }
        val pagesSize = remember { mutableStateListOf<IntegerSize>() }
        val renderer by produceState<PdfRenderer?>(null, uri) {
            rendererScope.launch(Dispatchers.IO) {
                runCatching {
                    mutex.withLock {
                        pagesSize.clear()
                        val renderer = PdfRenderer(
                            uri = uri.toString(),
                            onFailure = showError
                        )?.also {
                            repeat(it.pageCount) { index ->
                                it.openPage(index).let { page ->
                                    val size = IntegerSize(
                                        width = page.width,
                                        height = page.height
                                    ).flexibleResize(width, height)

                                    pagesSize.add(size)
                                }
                            }
                        }
                        value = renderer
                    }
                }.onFailure(showError)
            }
            awaitDispose {
                val currentRenderer = value
                rendererScope.launch(Dispatchers.IO) {
                    mutex.withLock {
                        currentRenderer?.close()
                    }
                }
            }
        }
        val pageCount by remember(renderer) {
            derivedStateOf {
                renderer?.pageCount ?: 0
            }
        }

        LazyColumnScrollbar(
            state = listState,
            settings = ScrollbarSettings(
                thumbUnselectedColor = MaterialTheme.colorScheme.primary,
                thumbSelectedColor = MaterialTheme.colorScheme.primary,
                scrollbarPadding = 0.dp,
                thumbThickness = 10.dp,
                selectionMode = ScrollbarSelectionMode.Full,
                thumbShape = AutoCornersShape(
                    topStartPercent = 100,
                    bottomStartPercent = 100
                ),
                hideDelayMillis = 1500
            ),
            indicatorContent = { index, _ ->
                val text by remember(index, pageCount, listState) {
                    derivedStateOf {
                        val first =
                            listState.layoutInfo.visibleItemsInfo.firstOrNull()
                        val last =
                            listState.layoutInfo.visibleItemsInfo.lastOrNull()
                        first?.takeIf {
                            it.index == 0
                        }?.let { 1 } ?: ((last?.index ?: index) + 1)
                    }
                }
                Text(
                    text = "$text / $pageCount",
                    modifier = Modifier
                        .padding(6.dp)
                        .container(
                            shape = ShapeDefaults.circle,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        )
                        .padding(
                            start = 6.dp,
                            end = 6.dp,
                            top = 2.dp,
                            bottom = 2.dp
                        ),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    .clipToBounds()
                    .zoomable(
                        rememberZoomState(10f)
                    ),
                contentPadding = contentPadding,
                horizontalAlignment = Alignment.CenterHorizontally,
                flingBehavior = enhancedFlingBehavior()
            ) {
                items(
                    count = pageCount,
                    key = { index -> "$uri-$index" }
                ) { index ->
                    if (index == 0) {
                        Spacer(Modifier.height(16.dp))
                    } else Spacer(Modifier.height(spacing))

                    val cacheKey =
                        MemoryCache.Key("$uri-${pagesSize[index]}-$index")

                    PdfPage(
                        contentScale = ContentScale.Fit,
                        renderWidth = pagesSize[index].width,
                        renderHeight = pagesSize[index].height,
                        index = index,
                        mutex = mutex,
                        renderer = renderer,
                        cacheKey = cacheKey
                    )
                    if (index == pageCount - 1) {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }

        if (pageCount == 0) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EnhancedLoadingIndicator()
            }
        }
    }
}

@Composable
private fun PdfPage(
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier,
    index: Int,
    renderWidth: Int,
    renderHeight: Int,
    zoom: Float = 1f,
    mutex: Mutex,
    renderer: PdfRenderer?,
    cacheKey: MemoryCache.Key,
) {
    val imageLoadingScope = rememberCoroutineScope()

    val cacheValue: Image? = appContext.imageLoader.memoryCache?.get(cacheKey)?.image

    var bitmap: Image? by remember { mutableStateOf(cacheValue) }
    if (bitmap == null) {
        DisposableEffect(cacheKey, index) {
            val job = imageLoadingScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    if (!coroutineContext.isActive) return@launch
                    try {
                        renderer?.let {
                            it.openPage(index).let { page ->
                                val originalWidth = page.width
                                val originalHeight = page.height

                                val targetSize = IntegerSize(
                                    width = originalWidth,
                                    height = originalHeight
                                ).flexibleResize(
                                    w = renderWidth,
                                    h = renderHeight
                                )

                                val scaleX = targetSize.width / originalWidth.toFloat()
                                val scaleY = targetSize.height / originalHeight.toFloat()
                                val scale = minOf(scaleX, scaleY) * 1.2f

                                bitmap = renderer.renderImage(
                                    index,
                                    scale.coerceAtMost(3f).makeLog("PdfDecoder, scale")
                                ).asImage()
                            }
                        }
                    } catch (_: Throwable) {
                        //Just catch and return in case the renderer is being closed
                        return@launch
                    }
                }
            }
            onDispose {
                job.cancel()
            }
        }
    }

    val request = remember(renderWidth, renderHeight, bitmap) {
        ImageRequest.Builder(appContext)
            .size(renderWidth, renderHeight)
            .memoryCacheKey(cacheKey)
            .data(bitmap?.toBitmap())
            .build()
    }

    val bgColor = MaterialTheme.colorScheme.secondaryContainer

    val density = LocalDensity.current
    Box(
        modifier
            .clip(ShapeDefaults.extraSmall)
            .background(bgColor)
    ) {
        Picture(
            modifier = Modifier
                .then(
                    if (contentScale == ContentScale.Crop) Modifier.matchParentSize()
                    else Modifier
                )
                .width(with(density) { renderWidth.toDp() * zoom })
                .aspectRatio(renderWidth / renderHeight.toFloat())
                .background(Color.White),
            shape = RectangleShape,
            contentScale = contentScale,
            showTransparencyChecker = false,
            model = request
        )
    }

}