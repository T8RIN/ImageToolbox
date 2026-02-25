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

@file:Suppress("COMPOSE_APPLIER_CALL_MISMATCH")

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.root.components

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresExtension
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.lifecycleScope
import androidx.pdf.viewer.fragment.PdfViewerFragment
import coil3.Image
import coil3.asImage
import coil3.imageLoader
import coil3.memory.MemoryCache
import coil3.request.ImageRequest
import coil3.toBitmap
import com.t8rin.imagetoolbox.core.domain.model.IntegerSize
import com.t8rin.imagetoolbox.core.domain.model.flexibleResize
import com.t8rin.imagetoolbox.core.domain.utils.safeCast
import com.t8rin.imagetoolbox.core.ui.utils.ComposeActivity
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.utils.state.update
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.PasswordRequestDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCornersShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.dragHandler
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.PdfRenderer
import com.t8rin.imagetoolbox.feature.pdf_tools.data.utils.canUseNewPdf
import com.t8rin.logger.makeLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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
fun PdfViewer(
    uriState: Uri?,
    onForceClearType: () -> Unit,
    modifier: Modifier,
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
    onGetCorrectPassword: (String?) -> Unit = {},
    orientation: PdfViewerOrientation = PdfViewerOrientation.Vertical,
    contentPadding: PaddingValues = PaddingValues(start = 20.dp, end = 20.dp)
) {
    val essentials = rememberLocalEssentials()

    var showPasswordRequestDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var pdfPassword by rememberSaveable {
        mutableStateOf<String?>(null)
    }

    val showError: (Throwable) -> Unit = {
        it.makeLog("PdfViewer")
        if (it is SecurityException) {
            showPasswordRequestDialog = true
        } else {
            essentials.showFailureToast(it)
        }
    }

    val loading = @Composable {
        Box(
            modifier = modifier
                .fillMaxSize()
                .animateContentSizeNoClip(),
            contentAlignment = Alignment.Center
        ) {
            EnhancedLoadingIndicator()
        }
    }

    AnimatedContent(
        targetState = uriState
    ) { uri ->
        if (uri != null) {
            if (canUseNewPdf() && orientation == PdfViewerOrientation.Vertical) {
                var fragmentReference by remember {
                    mutableStateOf<PdfViewerDelegate?>(null)
                }
                val loadingState = fragmentReference?.loadingState?.collectAsState()?.value


                LaunchedEffect(fragmentReference) {
                    fragmentReference?.apply {
                        PdfViewerDelegate.searchToggle.collect {
                            @Suppress("RestrictedApi")
                            isTextSearchActive = !isTextSearchActive
                        }
                    }
                }

                AndroidFragment<PdfViewerDelegate>(
                    arguments = Bundle().apply {
                        putParcelable("documentUri", uri)
                    },
                    modifier = modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface),
                    onUpdate = {
                        fragmentReference = it
                    }
                )

                if (loadingState == true) loading()

            } else {
                val listState = rememberLazyListState()
                BoxWithConstraints(modifier = modifier.animateContentSizeNoClip()) {
                    val density = LocalDensity.current
                    val width = with(density) { this@BoxWithConstraints.maxWidth.toPx() }.toInt()
                    val height = (width * sqrt(2f)).toInt()

                    val rendererScope = rememberCoroutineScope()
                    val mutex = remember { Mutex() }
                    val pagesSize = remember { mutableStateListOf<IntegerSize>() }
                    val renderer by produceState<PdfRenderer?>(null, uri, pdfPassword) {
                        rendererScope.launch(Dispatchers.IO) {
                            runCatching {
                                mutex.withLock {
                                    pagesSize.clear()
                                    val renderer = PdfRenderer(
                                        uri = uri.toString(),
                                        password = pdfPassword,
                                        onFailure = showError,
                                        onPasswordRequest = { showPasswordRequestDialog = true }
                                    )?.also {
                                        onGetCorrectPassword(pdfPassword)
                                        onGetPagesCount(it.pageCount)
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

                    val key by remember(uri, selectedPages) {
                        derivedStateOf {
                            uri to selectedPages
                        }
                    }
                    val selectedItems by remember(key) {
                        mutableStateOf(selectedPages.toSet())
                    }
                    val privateSelectedItems = remember {
                        mutableStateOf(selectedItems)
                    }

                    LaunchedEffect(selectedPages, selectedItems) {
                        if (selectedPages != selectedItems) {
                            privateSelectedItems.value = selectedItems
                        }
                    }

                    LaunchedEffect(privateSelectedItems.value) {
                        updateSelectedPages(privateSelectedItems.value.toList())
                    }

                    LaunchedEffect(selectAllToggle.value) {
                        if (selectAllToggle.value) {
                            privateSelectedItems.update {
                                List(pageCount) { it }.toSet()
                            }
                            selectAllToggle.value = false
                        }
                    }
                    LaunchedEffect(deselectAllToggle.value) {
                        if (deselectAllToggle.value) {
                            privateSelectedItems.update { emptySet() }
                            deselectAllToggle.value = false
                        }
                    }

                    if (orientation == PdfViewerOrientation.Vertical) {
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
                                    val selected by remember(privateSelectedItems.value) {
                                        derivedStateOf {
                                            privateSelectedItems.value.contains(index)
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
                                                                privateSelectedItems.update { it - index }
                                                            } else {
                                                                privateSelectedItems.update { it + index }
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
                        val isPortrait by isPortraitOrientationAsState()

                        if (isPortrait) {
                            LazyHorizontalGrid(
                                rows = GridCells.Adaptive(120.dp),
                                state = state,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .dragHandler(
                                        key = null,
                                        lazyGridState = state,
                                        isVertical = false,
                                        selectedItems = privateSelectedItems
                                    ),
                                verticalArrangement = Arrangement.spacedBy(
                                    space = spacing,
                                    alignment = Alignment.CenterVertically
                                ),
                                horizontalArrangement = Arrangement.spacedBy(
                                    space = spacing,
                                    alignment = Alignment.CenterHorizontally
                                ),
                                contentPadding = PaddingValues(12.dp),
                                flingBehavior = enhancedFlingBehavior()
                            ) {
                                items(
                                    count = pageCount,
                                    key = { index -> "$uri-$index" }
                                ) { index ->
                                    val cacheKey = MemoryCache.Key("$uri-120-$index")
                                    val selected by remember(privateSelectedItems.value) {
                                        derivedStateOf {
                                            privateSelectedItems.value.contains(index).also {
                                                updateSelectedPages(privateSelectedItems.value.toList())
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
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Adaptive(120.dp),
                                state = state,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .dragHandler(
                                        key = null,
                                        lazyGridState = state,
                                        isVertical = true,
                                        selectedItems = privateSelectedItems
                                    ),
                                verticalArrangement = Arrangement.spacedBy(
                                    spacing,
                                    Alignment.CenterVertically
                                ),
                                horizontalArrangement = Arrangement.spacedBy(
                                    spacing,
                                    Alignment.CenterHorizontally
                                ),
                                contentPadding = PaddingValues(12.dp),
                                flingBehavior = enhancedFlingBehavior()
                            ) {
                                items(
                                    count = pageCount,
                                    key = { index -> "$uri-$index" }
                                ) { index ->
                                    val cacheKey = MemoryCache.Key("$uri-120-$index")
                                    val selected by remember(privateSelectedItems.value) {
                                        derivedStateOf {
                                            privateSelectedItems.value.contains(index).also {
                                                updateSelectedPages(privateSelectedItems.value.toList())
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
        } else {
            loading()
        }
    }


    PasswordRequestDialog(
        isVisible = showPasswordRequestDialog,
        onDismiss = {
            showPasswordRequestDialog = false
            onForceClearType()
        },
        onFillPassword = {
            showPasswordRequestDialog = false
            pdfPassword = it
        }
    )
}

enum class PdfViewerOrientation {
    Vertical, Grid
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
internal class PdfViewerDelegate : PdfViewerFragment() {
    private val _loadingState = MutableStateFlow<Boolean?>(true)
    val loadingState: StateFlow<Boolean?> = _loadingState

    override fun onLoadDocumentSuccess() {
        super.onLoadDocumentSuccess()
        _loadingState.value = false
    }

    override fun onLoadDocumentError(error: Throwable) {
        super.onLoadDocumentError(error)
        _loadingState.value = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().safeCast<ComposeActivity>()?.let { activity ->
            activity.applyDynamicColors()
            lifecycleScope.launch {
                activity.applyGlobalNightMode()
            }
        }


        return super.onCreateView(inflater, container, savedInstanceState)
    }

    companion object {
        private val _searchToggle: Channel<Unit> = Channel(Channel.BUFFERED)
        val searchToggle: Flow<Unit> = _searchToggle.receiveAsFlow()

        fun toggleSearch() {
            _searchToggle.trySend(Unit)
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
                                val scale = minOf(scaleX, scaleY)

                                bitmap = renderer.renderImage(
                                    index,
                                    scale.coerceAtMost(2f).makeLog("PdfDecoder, scale")
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
                .padding(padding)
                .clip(AutoCornersShape(corners))
                .background(Color.White),
            shape = RectangleShape,
            contentScale = contentScale,
            showTransparencyChecker = false,
            model = request
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
                    .clip(AutoCornersShape(corners))
                    .background(MaterialTheme.colorScheme.scrim.copy(0.32f)),
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
                            .border(2.dp, bgColor, ShapeDefaults.circle)
                            .clip(ShapeDefaults.circle)
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