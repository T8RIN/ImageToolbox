package ru.tech.imageresizershrinker.coreui.widget.other

import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toIntRect
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.smarttoolfactory.image.zoom.animatedZoom
import com.smarttoolfactory.image.zoom.rememberAnimatedZoomState
import com.t8rin.dynamic.theme.observeAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import my.nanihadesuka.compose.LazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import ru.tech.imageresizershrinker.coredomain.model.IntegerSize
import ru.tech.imageresizershrinker.coreui.utils.state.update
import ru.tech.imageresizershrinker.coreui.widget.image.Picture
import ru.tech.imageresizershrinker.coreui.widget.modifier.container
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalImageLoader
import ru.tech.imageresizershrinker.coreui.widget.utils.LocalWindowSizeClass
import kotlin.math.max
import kotlin.math.sqrt

enum class PdfViewerOrientation {
    Vertical, Grid
}

@Composable
fun PdfViewer(
    uriState: Uri?,
    modifier: Modifier = Modifier.fillMaxWidth(),
    selectAllToggle: MutableState<Boolean> = remember {
        mutableStateOf(false)
    },
    deselectAllToggle: MutableState<Boolean> = remember {
        mutableStateOf(false)
    },
    onGetPagesCount: (Int) -> Unit = {},
    enableSelection: Boolean = false,
    selectedPages: List<Int> = emptyList(),
    updateSelectedPages: (List<Int>) -> Unit = {},
    spacing: Dp = 8.dp,
    orientation: PdfViewerOrientation = PdfViewerOrientation.Vertical
) {
    val showError: (Throwable) -> Unit = {}

    AnimatedContent(
        targetState = uriState
    ) { uri ->
        if (uri != null) {
            val listState = rememberLazyListState()
            BoxWithConstraints(modifier = modifier.animateContentSize()) {
                val density = LocalDensity.current
                val width = with(density) { maxWidth.toPx() }.toInt()
                val height = (width * sqrt(2f)).toInt()

                val context = LocalContext.current
                val rendererScope = rememberCoroutineScope()
                val mutex = remember { Mutex() }
                val pagesSize = remember { mutableStateListOf<IntegerSize>() }
                val renderer by produceState<PdfRenderer?>(null, uri) {
                    rendererScope.launch(Dispatchers.IO) {
                        runCatching {
                            val input = context.contentResolver.openFileDescriptor(uri, "r")
                            pagesSize.clear()
                            val renderer = input?.let {
                                PdfRenderer(it)
                            }?.also {
                                onGetPagesCount(it.pageCount)
                                repeat(it.pageCount) { index ->
                                    it.openPage(index)?.use { page ->
                                        val size = IntegerSize(
                                            width = page.width,
                                            height = page.height
                                        ).flexibleResize(width, height)

                                        pagesSize.add(size)
                                    }
                                }
                            }
                            value = renderer
                        }.exceptionOrNull()?.let(showError)
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
                val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }

                val selectedItems = remember(uri) {
                    mutableStateOf(selectedPages.toSet())
                }
                LaunchedEffect(selectedItems.value) {
                    updateSelectedPages(selectedItems.value.toList())
                }
                LaunchedEffect(selectAllToggle.value) {
                    if (selectAllToggle.value) {
                        selectedItems.update {
                            List(pageCount) { it }.toSet()
                        }
                        selectAllToggle.value = false
                    }
                }
                LaunchedEffect(deselectAllToggle.value) {
                    if (deselectAllToggle.value) {
                        selectedItems.update { emptySet() }
                        deselectAllToggle.value = false
                    }
                }

                if (orientation == PdfViewerOrientation.Vertical) {
                    LazyColumnScrollbar(
                        listState = listState,
                        thumbColor = MaterialTheme.colorScheme.primary,
                        thumbSelectedColor = MaterialTheme.colorScheme.primary,
                        padding = 0.dp,
                        thickness = 10.dp,
                        selectionMode = ScrollbarSelectionMode.Full,
                        thumbShape = RoundedCornerShape(
                            topStartPercent = 100,
                            bottomStartPercent = 100
                        ),
                        hideDelayMillis = 1500,
                        indicatorContent = { index, _ ->
                            val text by remember(index, pageCount, listState) {
                                derivedStateOf {
                                    val first = listState.layoutInfo.visibleItemsInfo.firstOrNull()
                                    val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()
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
                                        shape = CircleShape,
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    )
                                    .padding(start = 6.dp, end = 6.dp, top = 2.dp, bottom = 2.dp),
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    ) {
                        val zoomState = rememberAnimatedZoomState(
                            maxZoom = 10f
                        )

                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceContainerLow)
                                .animatedZoom(animatedZoomState = zoomState),
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
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
                                val selected by remember(selectedItems.value) {
                                    derivedStateOf {
                                        selectedItems.value.contains(index)
                                    }
                                }

                                PdfPage(
                                    selected = selected,
                                    selectionEnabled = enableSelection,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .then(
                                            if (enableSelection) {
                                                Modifier.toggleable(
                                                    value = selected,
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null,
                                                    onValueChange = { value ->
                                                        if (value) {
                                                            selectedItems.update { it - index }
                                                        } else {
                                                            selectedItems.update { it + index }
                                                        }
                                                    }
                                                )
                                            } else Modifier
                                        ),
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
                } else {
                    val state = rememberLazyGridState()
                    val autoScrollSpeed: MutableState<Float> = remember { mutableFloatStateOf(0f) }
                    LaunchedEffect(autoScrollSpeed.value) {
                        if (autoScrollSpeed.value != 0f) {
                            while (isActive) {
                                state.scrollBy(autoScrollSpeed.value)
                                delay(10)
                            }
                        }
                    }
                    val configuration = LocalConfiguration.current
                    val sizeClass = LocalWindowSizeClass.current.widthSizeClass
                    val landscape by remember(
                        LocalLifecycleOwner.current.lifecycle.observeAsState().value,
                        sizeClass,
                        configuration
                    ) {
                        derivedStateOf {
                            !(configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || sizeClass == WindowWidthSizeClass.Compact)
                        }
                    }
                    if (landscape) {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(120.dp),
                            state = state,
                            modifier = Modifier
                                .fillMaxSize()
                                .dragHandler(key = uri,
                                    lazyGridState = state,
                                    isVertical = true,
                                    haptics = LocalHapticFeedback.current,
                                    selectedItems = selectedItems,
                                    autoScrollSpeed = autoScrollSpeed,
                                    autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() }),
                            verticalArrangement = Arrangement.spacedBy(
                                spacing,
                                Alignment.CenterVertically
                            ),
                            horizontalArrangement = Arrangement.spacedBy(
                                spacing,
                                Alignment.CenterHorizontally
                            ),
                            contentPadding = PaddingValues(12.dp),
                        ) {
                            items(
                                count = pageCount,
                                key = { index -> "$uri-$index" }
                            ) { index ->
                                val cacheKey = MemoryCache.Key("$uri-120-$index")
                                val selected by remember(selectedItems.value) {
                                    derivedStateOf {
                                        selectedItems.value.contains(index).also {
                                            updateSelectedPages(selectedItems.value.toList())
                                        }
                                    }
                                }

                                val size = 120.dp
                                PdfPage(
                                    selected = selected,
                                    selectionEnabled = enableSelection,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .aspectRatio(1f),
                                    index = index,
                                    renderWidth = with(density) { size.roundToPx() },
                                    renderHeight = with(density) { size.roundToPx() },
                                    mutex = mutex,
                                    renderer = renderer,
                                    cacheKey = cacheKey
                                )
                            }
                        }
                    } else {
                        LazyHorizontalGrid(
                            rows = GridCells.Adaptive(120.dp),
                            state = state,
                            modifier = Modifier
                                .fillMaxSize()
                                .dragHandler(key = uri,
                                    lazyGridState = state,
                                    isVertical = false,
                                    haptics = LocalHapticFeedback.current,
                                    selectedItems = selectedItems,
                                    autoScrollSpeed = autoScrollSpeed,
                                    autoScrollThreshold = with(LocalDensity.current) { 40.dp.toPx() }),
                            verticalArrangement = Arrangement.spacedBy(
                                spacing,
                                Alignment.CenterVertically
                            ),
                            horizontalArrangement = Arrangement.spacedBy(
                                spacing,
                                Alignment.CenterHorizontally
                            ),
                            contentPadding = PaddingValues(12.dp),
                        ) {
                            items(
                                count = pageCount,
                                key = { index -> "$uri-$index" }
                            ) { index ->

                                val cacheKey = MemoryCache.Key("$uri-120-$index")
                                val selected by remember(selectedItems.value) {
                                    derivedStateOf {
                                        selectedItems.value.contains(index).also {
                                            updateSelectedPages(selectedItems.value.toList())
                                        }
                                    }
                                }
                                PdfPage(
                                    selected = selected,
                                    selectionEnabled = enableSelection,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .aspectRatio(1f),
                                    index = index,
                                    renderWidth = with(density) { 120.dp.roundToPx() },
                                    renderHeight = with(density) { 120.dp.roundToPx() },
                                    mutex = mutex,
                                    renderer = renderer,
                                    cacheKey = cacheKey
                                )
                            }
                        }
                    }
                }
                if (pageCount == 0) {
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Loading()
                    }
                }
            }
        } else {
            Box(
                modifier = modifier.animateContentSize(),
                contentAlignment = Alignment.Center
            ) {
                Loading()
            }
        }
    }
}

@Composable
private fun PdfPage(
    selected: Boolean,
    selectionEnabled: Boolean,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier,
    index: Int,
    renderWidth: Int,
    renderHeight: Int,
    zoom: Float = 1f,
    mutex: Mutex,
    renderer: PdfRenderer?,
    cacheKey: MemoryCache.Key,
) {
    val context = LocalContext.current
    val imageLoader = LocalImageLoader.current
    val imageLoadingScope = rememberCoroutineScope()

    val cacheValue: Bitmap? = imageLoader.memoryCache?.get(cacheKey)?.bitmap

    var bitmap: Bitmap? by remember { mutableStateOf(cacheValue) }
    if (bitmap == null) {
        DisposableEffect(cacheKey, index) {
            val job = imageLoadingScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    if (!coroutineContext.isActive) return@launch
                    try {
                        renderer?.let {
                            it.openPage(index).use { page ->
                                val size = IntegerSize(
                                    width = page.width,
                                    height = page.height
                                ).flexibleResize(renderWidth, renderHeight)
                                val destinationBitmap = Bitmap.createBitmap(
                                    size.width,
                                    size.height,
                                    Bitmap.Config.ARGB_8888
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
    }

    val request = ImageRequest.Builder(context)
        .size(renderWidth, renderHeight)
        .memoryCacheKey(cacheKey)
        .data(bitmap)
        .build()

    val transition = updateTransition(selected)
    val padding by transition.animateDp { s ->
        if (s) 10.dp else 0.dp
    }
    val corners by transition.animateDp { s ->
        if (s) 16.dp else 0.dp
    }
    val bgColor = MaterialTheme.colorScheme.secondaryContainer

    val density = LocalDensity.current
    Box(
        modifier
            .clip(RoundedCornerShape(4.dp))
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
                .padding(padding)
                .clip(RoundedCornerShape(corners))
                .background(Color.White),
            shape = RectangleShape,
            contentScale = contentScale,
            showTransparencyChecker = false,
            manualImageRequest = request,
            model = bitmap
        )
        AnimatedVisibility(
            visible = selectionEnabled,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .clip(RoundedCornerShape(corners))
                    .background(Color.Black.copy(0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (index + 1).toString(),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                }
            ) { selected ->
                if (selected) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(4.dp)
                            .border(2.dp, bgColor, CircleShape)
                            .clip(CircleShape)
                            .background(bgColor)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.RadioButtonUnchecked,
                        tint = Color.White.copy(alpha = 0.7f),
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }
    }

}

private fun Modifier.dragHandler(
    key: Any?,
    isVertical: Boolean,
    lazyGridState: LazyGridState,
    haptics: HapticFeedback,
    selectedItems: MutableState<Set<Int>>,
    autoScrollSpeed: MutableState<Float>,
    autoScrollThreshold: Float
): Modifier {
    fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? {
        val find = layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }
        val itemKey = find?.key
        return itemKey?.toString()?.takeLastWhile { it != '-' }?.toIntOrNull()
    }

    return pointerInput(key) {
        detectTapGestures { offset ->
            lazyGridState.gridItemKeyAtPosition(offset)?.let { key ->
                if (selectedItems.value.contains(key)) {
                    selectedItems.update { it - key }
                } else {
                    selectedItems.update { it + key }
                }
            }
        }
    }.pointerInput(key) {
        var initialKey: Int? = null
        var currentKey: Int? = null
        detectDragGesturesAfterLongPress(
            onDragStart = { offset ->
                lazyGridState.gridItemKeyAtPosition(offset)?.let { key ->
                    if (!selectedItems.value.contains(key)) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        initialKey = key
                        currentKey = key
                        selectedItems.update { it + key }
                    }
                }
            },
            onDragCancel = {
                initialKey = null
                autoScrollSpeed.value = 0f
            },
            onDragEnd = {
                initialKey = null
                autoScrollSpeed.value = 0f
            },
            onDrag = { change, _ ->
                if (initialKey != null) {
                    val distFromBottom = if (isVertical) {
                        lazyGridState.layoutInfo.viewportSize.height - change.position.y
                    } else lazyGridState.layoutInfo.viewportSize.width - change.position.x
                    val distFromTop = if (isVertical) {
                        change.position.y
                    } else change.position.x
                    autoScrollSpeed.value = when {
                        distFromBottom < autoScrollThreshold -> autoScrollThreshold - distFromBottom
                        distFromTop < autoScrollThreshold -> -(autoScrollThreshold - distFromTop)
                        else -> 0f
                    }

                    lazyGridState.gridItemKeyAtPosition(change.position)?.let { key ->
                        if (currentKey != key) {
                            selectedItems.update {
                                it
                                    .minus(initialKey!!..currentKey!!)
                                    .minus(currentKey!!..initialKey!!)
                                    .plus(initialKey!!..key)
                                    .plus(key..initialKey!!)
                            }
                            currentKey = key
                        }
                    }
                }
            }
        )
    }
}

private fun IntegerSize.flexibleResize(w: Int, h: Int): IntegerSize {
    val max = max(w, h)
    return runCatching {
        if (width > w) {
            val aspectRatio = width.toDouble() / height.toDouble()
            val targetHeight = w / aspectRatio
            return@runCatching IntegerSize(w, targetHeight.toInt())
        }

        if (height >= width) {
            val aspectRatio = width.toDouble() / height.toDouble()
            val targetWidth = (max * aspectRatio).toInt()
            IntegerSize(targetWidth, max)
        } else {
            val aspectRatio = height.toDouble() / width.toDouble()
            val targetHeight = (max * aspectRatio).toInt()
            IntegerSize(max, targetHeight)
        }
    }.getOrNull() ?: this
}