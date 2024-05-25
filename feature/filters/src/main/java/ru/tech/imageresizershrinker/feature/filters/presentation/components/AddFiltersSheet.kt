/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.filters.presentation.components

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.ExtensionOff
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.EditRoad
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material.icons.rounded.FilterHdr
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.LensBlur
import androidx.compose.material.icons.rounded.Light
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.transform.Transformation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.toUiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.utils.LocalFavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.presentation.utils.getFavoriteFiltersAsUiState
import ru.tech.imageresizershrinker.core.filters.presentation.utils.getTemplateFiltersAsUiState
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.BookmarkOff
import ru.tech.imageresizershrinker.core.resources.icons.Cube
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getStringLocalized
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.safeAspectRatio
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.toCoil
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.SimplePicture
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleDragHandle
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.ScopedViewModelContainer
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberImageState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
private class AddFiltersSheetViewModel @Inject constructor(
    private val filterProvider: FilterProvider<Bitmap>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val dispatchersHolder: DispatchersHolder
) : ViewModel(), DispatchersHolder by dispatchersHolder {
    private val _previewData: MutableState<List<UiFilter<*>>?> = mutableStateOf(null)
    val previewData by _previewData

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap by _previewBitmap

    private val _isPreviewLoading: MutableState<Boolean> = mutableStateOf(false)
    val isPreviewLoading by _isPreviewLoading

    fun setPreviewData(data: UiFilter<*>?) {
        _previewData.update { data?.let { listOf(it) } }
    }

    fun setPreviewData(data: List<Filter<Bitmap, *>>) {
        _previewData.update { data.map { it.toUiFilter() } }
    }

    fun filterToTransformation(
        filter: UiFilter<*>
    ): Transformation = filterProvider.filterToTransformation(filter).toCoil()

    fun updatePreview(previewBitmap: Bitmap) {
        viewModelScope.launch {
            _isPreviewLoading.update { true }
            _previewBitmap.update {
                imageTransformer.transform(
                    image = previewBitmap,
                    transformations = previewData?.map {
                        filterProvider.filterToTransformation(it)
                    } ?: emptyList(),
                    size = IntegerSize(2000, 2000)
                )
            }
            _isPreviewLoading.update { false }
        }
    }

    fun removeFilterAtIndex(index: Int) {
        _previewData.update {
            it?.toMutableList()?.apply {
                removeAt(index)
            }
        }
    }

    fun <T : Any> updateFilter(
        value: T,
        index: Int
    ) {
        val list = (previewData ?: emptyList()).toMutableList()
        runCatching {
            list[index] = list[index].copy(value)
            _previewData.update { list }
        }.onFailure {
            list[index] = list[index].newInstance()
            _previewData.update { list }
        }
    }

    fun shareImage(
        bitmap: Bitmap,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            shareProvider.shareImage(
                imageInfo = ImageInfo(
                    width = bitmap.width,
                    height = bitmap.height,
                    imageFormat = ImageFormat.Png.Lossless
                ),
                image = bitmap,
                onComplete = onComplete
            )
        }
    }

    fun saveImage(
        bitmap: Bitmap,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        viewModelScope.launch {
            val imageInfo = ImageInfo(
                width = bitmap.width,
                height = bitmap.height,
                imageFormat = ImageFormat.Png.Lossless
            )
            onComplete(
                fileController.save(
                    saveTarget = ImageSaveTarget<ExifInterface>(
                        imageInfo = imageInfo,
                        originalUri = "",
                        sequenceNumber = null,
                        data = imageCompressor.compress(
                            image = bitmap,
                            imageFormat = imageInfo.imageFormat,
                            quality = Quality.Base()
                        )
                    ),
                    keepOriginalMetadata = true
                )
            )
        }
    }

    fun saveContentTo(
        content: String,
        fileUri: Uri,
        onComplete: (Throwable?) -> Unit
    ) {
        viewModelScope.launch(ioDispatcher) {
            runCatching {
                fileController.writeBytes(
                    fileUri.toString(),
                    onError = {
                        onComplete(it)
                        return@writeBytes
                    }
                ) {
                    it.writeBytes(content.toByteArray())
                }
            }.exceptionOrNull().let(onComplete)
        }
    }

    fun shareContent(
        content: String,
        templateFilter: TemplateFilter<Bitmap>,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            shareProvider.shareData(
                writeData = { it.writeBytes(content.toByteArray()) },
                filename = createTemplateFilename(templateFilter),
                onComplete = onComplete
            )
        }
    }

    fun createTemplateFilename(templateFilter: TemplateFilter<Bitmap>): String {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date())
        return "template(${templateFilter.name})$timeStamp.imtbx_template"
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFiltersSheet(
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    canAddTemplates: Boolean = true,
    previewBitmap: Bitmap?,
    onFilterPicked: (UiFilter<*>) -> Unit,
    onFilterPickedWithParams: (UiFilter<*>) -> Unit
) {
    ScopedViewModelContainer<AddFiltersSheetViewModel> { disposable ->
        val viewModel = this

        val onRequestFilterMapping = viewModel::filterToTransformation

        val scope = rememberCoroutineScope()
        val confettiHostState = LocalConfettiHostState.current
        val showConfetti: () -> Unit = {
            scope.launch {
                confettiHostState.showConfetti()
            }
        }
        val toastHostState = LocalToastHostState.current

        val favoriteFilters by LocalFavoriteFiltersInteractor.getFavoriteFiltersAsUiState()
        val templateFilters by LocalFavoriteFiltersInteractor.getTemplateFiltersAsUiState()

        val context = LocalContext.current
        val groupedFilters by remember(context) {
            derivedStateOf {
                UiFilter.groupedEntries(context)
            }
        }
        val haptics = LocalHapticFeedback.current
        val pagerState = rememberPagerState(
            pageCount = { groupedFilters.size + 1 },
            initialPage = 2
        )

        var isSearching by rememberSaveable {
            mutableStateOf(false)
        }
        var searchKeyword by rememberSaveable {
            mutableStateOf("")
        }
        var filtersForSearch by remember {
            mutableStateOf(
                groupedFilters.flatten().sortedBy { context.getString(it.title) }
            )
        }
        LaunchedEffect(searchKeyword) {
            delay(400L) // Debounce calculations
            if (searchKeyword.isEmpty()) {
                filtersForSearch = groupedFilters.flatten().sortedBy { context.getString(it.title) }
                return@LaunchedEffect
            }

            filtersForSearch = groupedFilters.flatten().filter {
                context.getString(it.title).contains(
                    other = searchKeyword,
                    ignoreCase = true
                ).or(
                    context.getStringLocalized(
                        it.title, Locale.ENGLISH
                    ).contains(
                        other = searchKeyword,
                        ignoreCase = true
                    )
                )
            }.sortedBy { context.getString(it.title) }
        }

        val previewSheetData = viewModel.previewData

        SimpleSheet(
            dragHandle = {
                SimpleDragHandle {
                    AnimatedVisibility(visible = !isSearching) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            ScrollableTabRow(
                                divider = {},
                                edgePadding = 16.dp,
                                containerColor = SimpleSheetDefaults.barContainerColor,
                                selectedTabIndex = pagerState.currentPage,
                                indicator = { tabPositions ->
                                    if (pagerState.currentPage < tabPositions.size) {
                                        val width by animateDpAsState(targetValue = tabPositions[pagerState.currentPage].contentWidth)
                                        TabRowDefaults.PrimaryIndicator(
                                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                            width = width,
                                            height = 4.dp,
                                            shape = RoundedCornerShape(
                                                topStart = 100f,
                                                topEnd = 100f
                                            )
                                        )
                                    }
                                }
                            ) {
                                listOf(
                                    Icons.Rounded.Bookmark to stringResource(id = R.string.favorite),
                                    Icons.Rounded.Speed to stringResource(id = R.string.simple_effects),
                                    Icons.Rounded.FormatColorFill to stringResource(id = R.string.color),
                                    Icons.Rounded.Light to stringResource(R.string.light_aka_illumination),
                                    Icons.Rounded.FilterHdr to stringResource(R.string.effect),
                                    Icons.Rounded.LensBlur to stringResource(R.string.blur),
                                    Icons.Rounded.Cube to stringResource(R.string.pixelation),
                                    Icons.Rounded.Animation to stringResource(R.string.distortion),
                                    Icons.Rounded.EditRoad to stringResource(R.string.dithering)
                                ).let {
                                    if (canAddTemplates) listOf(
                                        Icons.Rounded.Extension to stringResource(
                                            id = R.string.template
                                        )
                                    ) + it
                                    else it
                                }.forEachIndexed { index, (icon, title) ->
                                    val selected = pagerState.currentPage == index
                                    val color by animateColorAsState(
                                        if (selected) {
                                            MaterialTheme.colorScheme.primary
                                        } else MaterialTheme.colorScheme.onSurface
                                    )
                                    Tab(
                                        unselectedContentColor = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .clip(CircleShape),
                                        selected = selected,
                                        onClick = {
                                            haptics.performHapticFeedback(
                                                HapticFeedbackType.LongPress
                                            )
                                            scope.launch {
                                                pagerState.animateScrollToPage(index)
                                            }
                                        },
                                        icon = {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = null,
                                                tint = color
                                            )
                                        },
                                        text = {
                                            Text(
                                                text = title,
                                                color = color
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            },
            sheetContent = {
                disposable()

                AnimatedContent(
                    modifier = Modifier.weight(1f, false),
                    targetState = isSearching
                ) { isSearching ->
                    if (isSearching) {
                        AnimatedContent(
                            targetState = filtersForSearch.isNotEmpty()
                        ) { isNotEmpty ->
                            if (isNotEmpty) {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    modifier = Modifier.animateContentSize(),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    itemsIndexed(filtersForSearch) { index, filter ->
                                        FilterSelectionItem(
                                            filter = filter,
                                            isFavoritePage = false,
                                            canOpenPreview = previewBitmap != null,
                                            favoriteFilters = favoriteFilters,
                                            onLongClick = {
                                                viewModel.setPreviewData(filter)
                                            },
                                            onClick = {
                                                onVisibleChange(false)
                                                onFilterPicked(filter)
                                            },
                                            onRequestFilterMapping = onRequestFilterMapping,
                                            shape = ContainerShapeDefaults.shapeForIndex(
                                                index = index,
                                                size = filtersForSearch.size
                                            ),
                                            modifier = Modifier.animateItem()
                                        )
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .fillMaxHeight(0.5f),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = stringResource(R.string.nothing_found_by_search),
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(
                                            start = 24.dp,
                                            end = 24.dp,
                                            top = 8.dp,
                                            bottom = 8.dp
                                        )
                                    )
                                    Icon(
                                        imageVector = Icons.Rounded.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .weight(2f)
                                            .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                                            .fillMaxSize()
                                    )
                                    Spacer(Modifier.weight(1f))
                                }
                            }
                        }
                    } else {
                        HorizontalPager(
                            state = pagerState,
                            beyondViewportPageCount = 2
                        ) { page ->
                            val filters by remember(page) {
                                derivedStateOf {
                                    groupedFilters[page - if (canAddTemplates) 2 else 1]
                                }
                            }
                            val templatesContent = @Composable {
                                AnimatedContent(
                                    targetState = templateFilters.isEmpty()
                                ) { noTemplates ->
                                    if (noTemplates) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(0.5f),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Spacer(Modifier.weight(1f))
                                            Text(
                                                text = stringResource(R.string.no_template_filters),
                                                fontSize = 18.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(
                                                    start = 24.dp,
                                                    end = 24.dp,
                                                    top = 8.dp,
                                                    bottom = 8.dp
                                                )
                                            )
                                            Icon(
                                                imageVector = Icons.Outlined.ExtensionOff,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                                                    .fillMaxSize()
                                            )
                                            FilterTemplateAddingGroup()
                                            Spacer(Modifier.weight(1f))
                                        }
                                    } else {
                                        LazyColumn(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            contentPadding = PaddingValues(16.dp)
                                        ) {
                                            itemsIndexed(templateFilters) { index, templateFilter ->
                                                var showFilterTemplateInfoSheet by rememberSaveable {
                                                    mutableStateOf(false)
                                                }
                                                TemplateFilterSelectionItem(
                                                    templateFilter = templateFilter,
                                                    onClick = {
                                                        onVisibleChange(false)
                                                        templateFilter.filters.forEach {
                                                            onFilterPickedWithParams(it.toUiFilter())
                                                        }
                                                    },
                                                    onLongClick = {
                                                        viewModel.setPreviewData(templateFilter.filters)
                                                    },
                                                    onInfoClick = {
                                                        showFilterTemplateInfoSheet = true
                                                    },
                                                    onRequestFilterMapping = onRequestFilterMapping,
                                                    shape = ContainerShapeDefaults.shapeForIndex(
                                                        index = index,
                                                        size = templateFilters.size
                                                    ),
                                                    modifier = Modifier.animateItem()
                                                )
                                                FilterTemplateInfoSheet(
                                                    visible = showFilterTemplateInfoSheet,
                                                    onDismiss = {
                                                        showFilterTemplateInfoSheet = it
                                                    },
                                                    templateFilter = templateFilter,
                                                    onRequestFilterMapping = onRequestFilterMapping,
                                                    onShareImage = {
                                                        viewModel.shareImage(it, showConfetti)
                                                    },
                                                    onSaveImage = {
                                                        viewModel.saveImage(it) { saveResult ->
                                                            context.parseSaveResult(
                                                                saveResult = saveResult,
                                                                onSuccess = showConfetti,
                                                                toastHostState = toastHostState,
                                                                scope = scope
                                                            )
                                                        }
                                                    },
                                                    onSaveFile = { fileUri, content ->
                                                        viewModel.saveContentTo(
                                                            content = content,
                                                            fileUri = fileUri
                                                        ) { throwable ->
                                                            if (throwable != null) {
                                                                scope.launch {
                                                                    toastHostState.showError(
                                                                        context,
                                                                        throwable
                                                                    )
                                                                }
                                                            } else {
                                                                scope.launch {
                                                                    confettiHostState.showConfetti()
                                                                }
                                                                scope.launch {
                                                                    toastHostState.showToast(
                                                                        context.getString(
                                                                            R.string.saved_to_without_filename,
                                                                            ""
                                                                        ),
                                                                        Icons.Rounded.Save
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    },
                                                    onRequestTemplateFilename = {
                                                        viewModel.createTemplateFilename(
                                                            templateFilter
                                                        )
                                                    },
                                                    onShareFile = { content ->
                                                        viewModel.shareContent(
                                                            content = content,
                                                            templateFilter = templateFilter,
                                                            onComplete = showConfetti
                                                        )
                                                    }
                                                )
                                            }
                                            item {
                                                FilterTemplateAddingGroup()
                                            }
                                        }
                                    }
                                }
                            }
                            val favoritesContent = @Composable {
                                AnimatedContent(
                                    targetState = favoriteFilters.isEmpty()
                                ) { noFav ->
                                    if (noFav) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(0.5f),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Spacer(Modifier.weight(1f))
                                            Text(
                                                text = stringResource(R.string.no_favorite_filters),
                                                fontSize = 18.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.padding(
                                                    start = 24.dp,
                                                    end = 24.dp,
                                                    top = 8.dp,
                                                    bottom = 8.dp
                                                )
                                            )
                                            Icon(
                                                imageVector = Icons.Outlined.BookmarkOff,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .weight(2f)
                                                    .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                                                    .fillMaxSize()
                                            )
                                            Spacer(Modifier.weight(1f))
                                        }
                                    } else {
                                        LazyColumn(
                                            verticalArrangement = Arrangement.spacedBy(4.dp),
                                            contentPadding = PaddingValues(16.dp)
                                        ) {
                                            itemsIndexed(favoriteFilters) { index, filter ->
                                                FilterSelectionItem(
                                                    filter = filter,
                                                    isFavoritePage = true,
                                                    canOpenPreview = previewBitmap != null,
                                                    favoriteFilters = favoriteFilters,
                                                    onLongClick = {
                                                        viewModel.setPreviewData(filter)
                                                    },
                                                    onClick = {
                                                        onVisibleChange(false)
                                                        onFilterPicked(filter)
                                                    },
                                                    onRequestFilterMapping = onRequestFilterMapping,
                                                    shape = ContainerShapeDefaults.shapeForIndex(
                                                        index = index,
                                                        size = favoriteFilters.size
                                                    ),
                                                    modifier = Modifier.animateItem()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            val otherContent = @Composable {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    contentPadding = PaddingValues(16.dp)
                                ) {
                                    itemsIndexed(filters) { index, filter ->
                                        FilterSelectionItem(
                                            filter = filter,
                                            canOpenPreview = previewBitmap != null,
                                            favoriteFilters = favoriteFilters,
                                            onLongClick = {
                                                viewModel.setPreviewData(filter)
                                            },
                                            onClick = {
                                                onVisibleChange(false)
                                                onFilterPicked(filter)
                                            },
                                            onRequestFilterMapping = onRequestFilterMapping,
                                            shape = ContainerShapeDefaults.shapeForIndex(
                                                index = index,
                                                size = filters.size
                                            ),
                                            isFavoritePage = false,
                                            modifier = Modifier.animateItem()
                                        )
                                    }
                                }
                            }

                            if (canAddTemplates) {
                                when (page) {
                                    0 -> templatesContent()

                                    1 -> favoritesContent()

                                    else -> otherContent()
                                }
                            } else {
                                when (page) {
                                    0 -> favoritesContent()

                                    else -> otherContent()
                                }
                            }
                        }
                    }
                }
            },
            title = {
                AnimatedContent(
                    targetState = isSearching
                ) { searching ->
                    if (searching) {
                        BackHandler {
                            searchKeyword = ""
                            isSearching = false
                        }
                        ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                            RoundedTextField(
                                maxLines = 1,
                                hint = { Text(stringResource(id = R.string.search_here)) },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search,
                                    autoCorrectEnabled = null
                                ),
                                value = searchKeyword,
                                onValueChange = {
                                    searchKeyword = it
                                },
                                startIcon = {
                                    EnhancedIconButton(
                                        containerColor = Color.Transparent,
                                        contentColor = LocalContentColor.current,
                                        enableAutoShadowAndBorder = false,
                                        onClick = {
                                            searchKeyword = ""
                                            isSearching = false
                                        },
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                            contentDescription = stringResource(R.string.exit),
                                            tint = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                },
                                endIcon = {
                                    AnimatedVisibility(
                                        visible = searchKeyword.isNotEmpty(),
                                        enter = fadeIn() + scaleIn(),
                                        exit = fadeOut() + scaleOut()
                                    ) {
                                        EnhancedIconButton(
                                            containerColor = Color.Transparent,
                                            contentColor = LocalContentColor.current,
                                            enableAutoShadowAndBorder = false,
                                            onClick = {
                                                searchKeyword = ""
                                            },
                                            modifier = Modifier.padding(end = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = stringResource(R.string.close),
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                },
                                shape = CircleShape
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TitleItem(
                                text = stringResource(R.string.filter),
                                icon = Icons.Rounded.AutoFixHigh
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            EnhancedIconButton(
                                onClick = { isSearching = true },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = stringResource(R.string.search_here)
                                )
                            }
                            EnhancedButton(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                onClick = { onVisibleChange(false) }
                            ) {
                                AutoSizeText(stringResource(R.string.close))
                            }
                        }
                    }
                }
            },
            confirmButton = {},
            enableBottomContentWeight = false,
            visible = visible,
            onDismiss = onVisibleChange
        )

        var imageState by rememberImageState()
        LaunchedEffect(previewSheetData) {
            if (previewBitmap != null && previewSheetData != null) {
                if (previewSheetData.size == 1 && previewSheetData.firstOrNull()?.value is Unit) {
                    imageState = imageState.copy(position = 2)
                }
                viewModel.updatePreview(previewBitmap)
            }
        }

        val backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
        SimpleSheet(
            dragHandle = {
                SimpleDragHandle {
                    EnhancedTopAppBar(
                        type = EnhancedTopAppBarType.Center,
                        modifier = Modifier,
                        navigationIcon = {
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = {
                                    viewModel.setPreviewData(null)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = stringResource(R.string.close)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = SimpleSheetDefaults.barContainerColor
                        ),
                        actions = {
                            EnhancedIconButton(
                                containerColor = Color.Transparent,
                                contentColor = LocalContentColor.current,
                                enableAutoShadowAndBorder = false,
                                onClick = {
                                    previewSheetData?.forEach(onFilterPickedWithParams)
                                    viewModel.setPreviewData(null)
                                    onVisibleChange(false)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Done,
                                    contentDescription = "Done"
                                )
                            }
                        },
                        title = {
                            Marquee {
                                Text(
                                    text = stringResource(
                                        id = previewSheetData?.let {
                                            if (it.size == 1) it.first().title
                                            else R.string.filter_preview
                                        } ?: R.string.filter_preview
                                    )
                                )
                            }
                        }
                    )
                }
            },
            sheetContent = {
                DisposableEffect(Unit) {
                    onDispose {
                        imageState = imageState.copy(position = 2)
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val imageBlock = @Composable {
                        AnimatedContent(
                            targetState = viewModel.previewBitmap == null,
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { isNull ->
                            if (isNull) {
                                Box(
                                    modifier = if (viewModel.previewBitmap == null) {
                                        Modifier
                                            .aspectRatio(
                                                previewBitmap?.safeAspectRatio ?: (1 / 2f)
                                            )
                                            .clip(RoundedCornerShape(8.dp))
                                            .shimmer(true)
                                    } else Modifier
                                )
                            } else {
                                SimplePicture(
                                    bitmap = viewModel.previewBitmap,
                                    loading = viewModel.isPreviewLoading,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                    val isPortrait by isPortraitOrientationAsState()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        val isUnit =
                            previewSheetData?.size == 1 && previewSheetData.firstOrNull()?.value is Unit
                        if (!isPortrait) {
                            Box(
                                modifier = Modifier
                                    .container(RectangleShape)
                                    .weight(1.2f)
                                    .padding(20.dp)
                            ) {
                                Box(Modifier.align(Alignment.Center)) {
                                    imageBlock()
                                }
                            }
                        }

                        val internalHeight = rememberAvailableHeight(imageState = imageState)
                        LazyColumn(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .then(
                                    if (!isPortrait && !isUnit) Modifier.weight(1f)
                                    else Modifier
                                )
                                .clipToBounds()
                        ) {
                            imageStickyHeader(
                                visible = isPortrait,
                                imageState = imageState,
                                internalHeight = internalHeight,
                                onStateChange = { imageState = it },
                                imageBlock = imageBlock,
                                backgroundColor = backgroundColor
                            )
                            item {
                                previewSheetData?.takeIf { !isUnit }?.let { list ->
                                    list.forEachIndexed { index, filter ->
                                        FilterItem(
                                            backgroundColor = MaterialTheme
                                                .colorScheme
                                                .surface,
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            filter = filter,
                                            showDragHandle = false,
                                            onRemove = {
                                                if (list.size == 1) {
                                                    viewModel.setPreviewData(null)
                                                } else viewModel.removeFilterAtIndex(index)
                                            },
                                            onFilterChange = { value ->
                                                viewModel.updateFilter(value, index)
                                            }
                                        )
                                        if (index != list.lastIndex) {
                                            Spacer(Modifier.height(8.dp))
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                                Spacer(
                                    Modifier.height(
                                        WindowInsets
                                            .navigationBars
                                            .asPaddingValues()
                                            .calculateBottomPadding()
                                    )
                                )
                            }
                        }
                    }
                }
            },
            visible = previewData != null,
            onDismiss = {
                if (!it) {
                    viewModel.setPreviewData(null)
                }
            }
        )
    }
}