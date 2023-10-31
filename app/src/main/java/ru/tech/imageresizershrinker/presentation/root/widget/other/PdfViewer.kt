package ru.tech.imageresizershrinker.presentation.root.widget.other

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BitmapCompat
import coil.compose.rememberAsyncImagePainter
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import my.nanihadesuka.compose.LazyColumnScrollbar
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.container
import ru.tech.imageresizershrinker.presentation.root.widget.modifier.shimmer
import ru.tech.imageresizershrinker.presentation.root.widget.utils.LocalImageLoader
import kotlin.math.max
import kotlin.math.sqrt

@Composable
fun PdfViewer(
    uriState: Uri?,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp)
) {
    AnimatedContent(
        targetState = uriState,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) { uri ->
        if (uri != null) {
            val context = LocalContext.current
            val rendererScope = rememberCoroutineScope()
            val mutex = remember { Mutex() }
            val renderer by produceState<PdfRenderer?>(null, uri) {
                rendererScope.launch(Dispatchers.IO) {
                    val input = context.contentResolver.openFileDescriptor(uri, "r")
                    value = input?.let { PdfRenderer(it) }
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

            val listState = rememberLazyListState()
            val imageLoader = LocalImageLoader.current
            val imageLoadingScope = rememberCoroutineScope()
            BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
                val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
                val height = (width * sqrt(2f)).toInt()
                val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }
                LazyColumnScrollbar(
                    listState = listState,
                    thumbColor = MaterialTheme.colorScheme.primary,
                    thumbSelectedColor = MaterialTheme.colorScheme.primary,
                    indicatorContent = { index, _ ->
                        val text by remember(index, pageCount, listState) {
                            derivedStateOf {
                                if (listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index == pageCount - 1) index + 2
                                else index + 1
                            }
                        }
                        Text(
                            text = "$text / $pageCount",
                            modifier = Modifier
                                .padding(6.dp)
                                .container(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.secondaryContainer
                                )
                                .padding(start = 6.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .animatedZoom(animatedZoomState = rememberAnimatedZoomState()),
                        verticalArrangement = verticalArrangement,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(
                            count = pageCount,
                            key = { index -> "$uri-$index" }
                        ) { index ->
                            val cacheKey = MemoryCache.Key("$uri-$index")
                            val cacheValue: Bitmap? = imageLoader.memoryCache?.get(cacheKey)?.bitmap

                            var bitmap: Bitmap? by remember { mutableStateOf(cacheValue) }
                            if (bitmap == null) {
                                DisposableEffect(uri, index) {
                                    val job = imageLoadingScope.launch(Dispatchers.IO) {
                                        mutex.withLock {
                                            if (!coroutineContext.isActive) return@launch
                                            try {
                                                renderer?.let {
                                                    it.openPage(index).use { page ->
                                                        val destinationBitmap = flexibleResize(
                                                            Bitmap.createBitmap(
                                                                page.width,
                                                                page.height,
                                                                Bitmap.Config.ARGB_8888
                                                            ),
                                                            max(width, height)
                                                        )
                                                        page.render(
                                                            destinationBitmap,
                                                            null,
                                                            null,
                                                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                                                        )
                                                        bitmap = destinationBitmap
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                //Just catch and return in case the renderer is being closed
                                                return@launch
                                            }
                                        }
                                    }
                                    onDispose {
                                        job.cancel()
                                    }
                                }
                            } else {
                                val request = ImageRequest.Builder(context)
                                    .size(width, height)
                                    .memoryCacheKey(cacheKey)
                                    .data(bitmap)
                                    .build()

                                Image(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentScale = ContentScale.Fit,
                                    painter = rememberAsyncImagePainter(request),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        } else {
            Box(
                modifier = modifier.shimmer(true)
            )
        }
    }
}

private fun flexibleResize(image: Bitmap, max: Int): Bitmap {
    return runCatching {
        if (image.height >= image.width) {
            val aspectRatio = image.width.toDouble() / image.height.toDouble()
            val targetWidth = (max * aspectRatio).toInt()
            BitmapCompat.createScaledBitmap(image, targetWidth, max, null, true)
        } else {
            val aspectRatio = image.height.toDouble() / image.width.toDouble()
            val targetHeight = (max * aspectRatio).toInt()
            BitmapCompat.createScaledBitmap(image, max, targetHeight, null, true)
        }
    }.getOrNull() ?: image
}