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

package ru.tech.imageresizershrinker.core.filters.presentation.widget

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material.icons.rounded.TableChart
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
import androidx.compose.material3.contentColorFor
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import coil.transform.Transformation
import com.arkivanov.decompose.ComponentContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageCompressor
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.image.ImageTransformer
import ru.tech.imageresizershrinker.core.domain.image.ShareProvider
import ru.tech.imageresizershrinker.core.domain.image.model.ImageFormat
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.image.model.Quality
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResources
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResourcesDownloadProgress
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResourcesStore
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.domain.saving.model.ImageSaveTarget
import ru.tech.imageresizershrinker.core.domain.saving.model.SaveResult
import ru.tech.imageresizershrinker.core.domain.utils.ListUtils.filterIsNotInstance
import ru.tech.imageresizershrinker.core.filters.domain.FavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.domain.FilterProvider
import ru.tech.imageresizershrinker.core.filters.domain.model.Filter
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiCubeLutFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.toUiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.utils.collectAsUiState
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.BookmarkOff
import ru.tech.imageresizershrinker.core.resources.icons.Cube
import ru.tech.imageresizershrinker.core.ui.utils.BaseComponent
import ru.tech.imageresizershrinker.core.ui.utils.confetti.LocalConfettiHostState
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getStringLocalized
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.safeAspectRatio
import ru.tech.imageresizershrinker.core.ui.utils.helper.asClip
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseFileSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.parseSaveResult
import ru.tech.imageresizershrinker.core.ui.utils.helper.toCoil
import ru.tech.imageresizershrinker.core.ui.utils.helper.toImageModel
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.ShareButton
import ru.tech.imageresizershrinker.core.ui.widget.controls.selection.ImageSelector
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.image.SimplePicture
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.showError
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleDragHandle
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.text.marquee
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberForeverLazyListState
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberImageState
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddFiltersSheet(
    component: AddFiltersSheetComponent,
    filterTemplateCreationSheetComponent: FilterTemplateCreationSheetComponent,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    previewBitmap: Bitmap?,
    onFilterPicked: (UiFilter<*>) -> Unit,
    onFilterPickedWithParams: (UiFilter<*>) -> Unit,
    canAddTemplates: Boolean = true
) {
    val context = LocalContext.current
    val groupedFilters by remember(context, canAddTemplates) {
        derivedStateOf {
            UiFilter.groupedEntries(context).let { lists ->
                if (canAddTemplates) lists
                else lists.map {
                    it.filterIsNotInstance(
                        Filter.PaletteTransfer::class,
                        Filter.LUT512x512::class,
                        Filter.PaletteTransferVariant::class,
                        Filter.CubeLut::class
                    )
                }
            }
        }
    }
    val haptics = LocalHapticFeedback.current
    val pagerState = rememberPagerState(
        pageCount = { groupedFilters.size + if (canAddTemplates) 2 else 1 },
        initialPage = 2
    )

    val onRequestFilterMapping = component::filterToTransformation

    val previewModel = component.previewModel

    val scope = rememberCoroutineScope()
    val confettiHostState = LocalConfettiHostState.current
    val showConfetti: () -> Unit = {
        scope.launch {
            confettiHostState.showConfetti()
        }
    }
    val toastHostState = LocalToastHostState.current

    val favoriteFilters by component.favoritesFlow.collectAsUiState()
    val templateFilters by component.templatesFlow.collectAsUiState()

    val tabs by remember(canAddTemplates) {
        derivedStateOf {
            listOf(
                Icons.Rounded.Bookmark to context.getString(R.string.favorite),
                Icons.Rounded.Speed to context.getString(R.string.simple_effects),
                Icons.Rounded.FormatColorFill to context.getString(R.string.color),
                Icons.Rounded.TableChart to context.getString(R.string.lut),
                Icons.Rounded.Light to context.getString(R.string.light_aka_illumination),
                Icons.Rounded.FilterHdr to context.getString(R.string.effect),
                Icons.Rounded.LensBlur to context.getString(R.string.blur),
                Icons.Rounded.Cube to context.getString(R.string.pixelation),
                Icons.Rounded.Animation to context.getString(R.string.distortion),
                Icons.Rounded.EditRoad to context.getString(R.string.dithering)
            ).let {
                if (canAddTemplates) listOf(
                    Icons.Rounded.Extension to context.getString(R.string.template)
                ) + it
                else it
            }
        }
    }

    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }
    var searchKeyword by rememberSaveable(isSearching) {
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

    val previewSheetData = component.previewData

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
                            tabs.forEachIndexed { index, (icon, title) ->
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
            DisposableEffect(Unit) {
                onDispose {
                    component.resetState()
                }
            }

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
                                state = rememberForeverLazyListState("sheet"),
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
                                            component.setPreviewData(filter)
                                        },
                                        onOpenPreview = {
                                            component.setPreviewData(filter)
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
                                        onToggleFavorite = {
                                            component.toggleFavorite(filter)
                                        },
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
                                                bottom = 16.dp
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
                                        FilterTemplateAddingGroup(
                                            onAddTemplateFilterFromString = component::addTemplateFilterFromString,
                                            onAddTemplateFilterFromUri = component::addTemplateFilterFromUri,
                                            component = filterTemplateCreationSheetComponent
                                        )
                                        Spacer(Modifier.weight(1f))
                                    }
                                } else {
                                    LazyColumn(
                                        state = rememberForeverLazyListState("templates"),
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
                                                    component.setPreviewData(templateFilter.filters)
                                                },
                                                onInfoClick = {
                                                    showFilterTemplateInfoSheet = true
                                                },
                                                onRequestFilterMapping = onRequestFilterMapping,
                                                shape = ContainerShapeDefaults.shapeForIndex(
                                                    index = index,
                                                    size = templateFilters.size
                                                ),
                                                modifier = Modifier.animateItem(),
                                                previewModel = previewModel
                                            )
                                            FilterTemplateInfoSheet(
                                                visible = showFilterTemplateInfoSheet,
                                                onDismiss = {
                                                    showFilterTemplateInfoSheet = it
                                                },
                                                templateFilter = templateFilter,
                                                onRequestFilterMapping = onRequestFilterMapping,
                                                onShareImage = {
                                                    component.shareImage(it, showConfetti)
                                                },
                                                onSaveImage = {
                                                    component.saveImage(it) { saveResult ->
                                                        context.parseSaveResult(
                                                            saveResult = saveResult,
                                                            onSuccess = showConfetti,
                                                            toastHostState = toastHostState,
                                                            scope = scope
                                                        )
                                                    }
                                                },
                                                onSaveFile = { fileUri, content ->
                                                    component.saveContentTo(
                                                        content = content,
                                                        fileUri = fileUri
                                                    ) { result ->
                                                        context.parseFileSaveResult(
                                                            saveResult = result,
                                                            onSuccess = {
                                                                confettiHostState.showConfetti()
                                                            },
                                                            toastHostState = toastHostState,
                                                            scope = scope
                                                        )
                                                    }
                                                },
                                                onConvertTemplateFilterToString = component::convertTemplateFilterToString,
                                                onRemoveTemplateFilter = component::removeTemplateFilter,
                                                onRequestTemplateFilename = {
                                                    component.createTemplateFilename(
                                                        templateFilter
                                                    )
                                                },
                                                onShareFile = { content ->
                                                    component.shareContent(
                                                        content = content,
                                                        filename = component.createTemplateFilename(
                                                            templateFilter
                                                        ),
                                                        onComplete = showConfetti
                                                    )
                                                },
                                                previewModel = previewModel,
                                                component = filterTemplateCreationSheetComponent
                                            )
                                        }
                                        item {
                                            FilterTemplateAddingGroup(
                                                onAddTemplateFilterFromString = component::addTemplateFilterFromString,
                                                onAddTemplateFilterFromUri = component::addTemplateFilterFromUri,
                                                component = filterTemplateCreationSheetComponent
                                            )
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
                                    val data = remember {
                                        mutableStateOf(favoriteFilters)
                                    }
                                    val listState = rememberLazyListState()
                                    val state = rememberReorderableLazyListState(
                                        lazyListState = listState,
                                        onMove = { from, to ->
                                            data.value = data.value.toMutableList().apply {
                                                add(to.index, removeAt(from.index))
                                            }
                                        }
                                    )
                                    LaunchedEffect(favoriteFilters) {
                                        if (data.value.size != favoriteFilters.size) {
                                            data.value = favoriteFilters
                                        }
                                    }
                                    LazyColumn(
                                        state = listState,
                                        modifier = Modifier.fillMaxHeight(),
                                        verticalArrangement = Arrangement.spacedBy(
                                            space = 4.dp,
                                            alignment = Alignment.CenterVertically
                                        ),
                                        contentPadding = PaddingValues(16.dp)
                                    ) {
                                        itemsIndexed(
                                            items = data.value,
                                            key = { _, f -> f.hashCode() }
                                        ) { index, filter ->
                                            ReorderableItem(
                                                state = state,
                                                key = filter.hashCode()
                                            ) { isDragging ->
                                                FilterSelectionItem(
                                                    filter = filter,
                                                    isFavoritePage = true,
                                                    canOpenPreview = previewBitmap != null,
                                                    favoriteFilters = favoriteFilters,
                                                    onLongClick = null,
                                                    onOpenPreview = {
                                                        component.setPreviewData(filter)
                                                    },
                                                    onClick = { custom ->
                                                        onVisibleChange(false)
                                                        if (custom != null) {
                                                            onFilterPickedWithParams(custom)
                                                        } else {
                                                            onFilterPicked(filter)
                                                        }
                                                    },
                                                    onRequestFilterMapping = onRequestFilterMapping,
                                                    shape = ContainerShapeDefaults.shapeForIndex(
                                                        index = index,
                                                        size = favoriteFilters.size
                                                    ),
                                                    onToggleFavorite = {
                                                        component.toggleFavorite(filter)
                                                    },
                                                    modifier = Modifier
                                                        .longPressDraggableHandle {
                                                            component.reorderFavoriteFilters(
                                                                data.value
                                                            )
                                                        }
                                                        .scale(
                                                            animateFloatAsState(
                                                                if (isDragging) 1.05f
                                                                else 1f
                                                            ).value
                                                        ),
                                                    cubeLutRemoteResources = if (filter is UiCubeLutFilter) {
                                                        component.cubeLutRemoteResources
                                                    } else null,
                                                    cubeLutDownloadProgress = if (filter is UiCubeLutFilter) {
                                                        component.cubeLutDownloadProgress
                                                    } else null,
                                                    onCubeLutDownloadRequest = { forceUpdate, downloadOnlyNewData ->
                                                        component.updateCubeLuts(
                                                            startDownloadIfNeeded = true,
                                                            forceUpdate = forceUpdate,
                                                            onFailure = {
                                                                scope.launch {
                                                                    toastHostState.showError(
                                                                        context,
                                                                        it
                                                                    )
                                                                }
                                                            },
                                                            downloadOnlyNewData = downloadOnlyNewData
                                                        )
                                                    },
                                                    previewModel = previewModel
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        val otherContent = @Composable {
                            LazyColumn(
                                state = rememberForeverLazyListState("sheet$page"),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {
                                if (tabs[page].first == Icons.Rounded.Speed) {
                                    item {
                                        Row(
                                            modifier = Modifier
                                                .padding(bottom = 8.dp)
                                                .height(intrinsicSize = IntrinsicSize.Max)
                                        ) {
                                            ImageSelector(
                                                value = previewModel.data,
                                                onValueChange = {
                                                    component.setFilterPreviewModel(it.toString())
                                                },
                                                title = stringResource(R.string.filter_preview_image),
                                                subtitle = stringResource(R.string.filter_preview_image_sub),
                                                contentScale = ContentScale.Crop,
                                                color = Color.Unspecified,
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight(),
                                                shape = RoundedCornerShape(
                                                    topEnd = 4.dp,
                                                    topStart = 16.dp,
                                                    bottomEnd = 4.dp,
                                                    bottomStart = 16.dp
                                                )
                                            )
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .padding(start = 4.dp),
                                                verticalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                repeat(2) { index ->
                                                    val shape = if (index == 0) {
                                                        RoundedCornerShape(
                                                            topEnd = 16.dp,
                                                            topStart = 4.dp,
                                                            bottomEnd = 4.dp,
                                                            bottomStart = 4.dp
                                                        )
                                                    } else {
                                                        RoundedCornerShape(
                                                            topEnd = 4.dp,
                                                            topStart = 4.dp,
                                                            bottomEnd = 16.dp,
                                                            bottomStart = 4.dp
                                                        )
                                                    }
                                                    val containerColor by animateColorAsState(
                                                        if (previewModel.data == R.drawable.filter_preview_source && index == 0) {
                                                            MaterialTheme.colorScheme.secondary
                                                        } else if (previewModel.data == R.drawable.filter_preview_source_3 && index == 1) {
                                                            MaterialTheme.colorScheme.secondary
                                                        } else {
                                                            MaterialTheme.colorScheme.secondaryContainer
                                                        }
                                                    )
                                                    Box(
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clip(shape)
                                                            .clickable {
                                                                component.setFilterPreviewModel(
                                                                    index.toString()
                                                                )
                                                            }
                                                            .container(
                                                                color = containerColor,
                                                                shape = shape,
                                                                resultPadding = 0.dp
                                                            )
                                                            .padding(horizontal = 12.dp),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        AutoSizeText(
                                                            text = (index + 1).toString(),
                                                            color = contentColorFor(
                                                                containerColor
                                                            )
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (tabs[page].first == Icons.Rounded.TableChart) {
                                    item {
                                        PreferenceItemOverload(
                                            title = stringResource(R.string.save_empty_lut),
                                            subtitle = stringResource(R.string.save_empty_lut_sub),
                                            shape = ContainerShapeDefaults.defaultShape,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 8.dp),
                                            endIcon = {
                                                Column(
                                                    verticalArrangement = Arrangement.Center,
                                                    horizontalAlignment = Alignment.CenterHorizontally
                                                ) {
                                                    Picture(
                                                        model = R.drawable.lookup,
                                                        contentScale = ContentScale.Crop,
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(48.dp)
                                                            .scale(1.1f)
                                                            .clip(MaterialTheme.shapes.extraSmall),
                                                        shape = MaterialTheme.shapes.extraSmall
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    var showFolderSelection by rememberSaveable {
                                                        mutableStateOf(false)
                                                    }
                                                    val saveNeutralLut: (String?) -> Unit = {
                                                        component.saveNeutralLut(it) { saveResult ->
                                                            context.parseSaveResult(
                                                                saveResult = saveResult,
                                                                onSuccess = showConfetti,
                                                                toastHostState = toastHostState,
                                                                scope = scope
                                                            )
                                                        }
                                                    }
                                                    Row {
                                                        ShareButton(
                                                            onShare = {
                                                                component.shareNeutralLut(
                                                                    showConfetti
                                                                )
                                                            },
                                                            onCopy = { manager ->
                                                                component.cacheNeutralLut { uri ->
                                                                    manager.setClip(
                                                                        uri.asClip(context)
                                                                    )
                                                                    showConfetti()
                                                                }
                                                            }
                                                        )
                                                        EnhancedIconButton(
                                                            onClick = {
                                                                saveNeutralLut(null)
                                                            },
                                                            onLongClick = {
                                                                showFolderSelection = true
                                                            }
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Rounded.Save,
                                                                contentDescription = stringResource(
                                                                    R.string.save
                                                                )
                                                            )
                                                        }

                                                        if (showFolderSelection) {
                                                            OneTimeSaveLocationSelectionDialog(
                                                                onDismiss = {
                                                                    showFolderSelection = false
                                                                },
                                                                onSaveRequest = saveNeutralLut
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    }
                                }
                                itemsIndexed(filters) { index, filter ->
                                    FilterSelectionItem(
                                        filter = filter,
                                        canOpenPreview = previewBitmap != null,
                                        favoriteFilters = favoriteFilters,
                                        onLongClick = {
                                            component.setPreviewData(filter)
                                        },
                                        onOpenPreview = {
                                            component.setPreviewData(filter)
                                        },
                                        onClick = { custom ->
                                            onVisibleChange(false)
                                            if (custom != null) {
                                                onFilterPickedWithParams(custom)
                                            } else {
                                                onFilterPicked(filter)
                                            }
                                        },
                                        onRequestFilterMapping = onRequestFilterMapping,
                                        shape = ContainerShapeDefaults.shapeForIndex(
                                            index = index,
                                            size = filters.size
                                        ),
                                        onToggleFavorite = {
                                            component.toggleFavorite(filter)
                                        },
                                        isFavoritePage = false,
                                        modifier = Modifier.animateItem(),
                                        cubeLutRemoteResources = if (filter is UiCubeLutFilter) {
                                            component.cubeLutRemoteResources
                                        } else null,
                                        cubeLutDownloadProgress = if (filter is UiCubeLutFilter) {
                                            component.cubeLutDownloadProgress
                                        } else null,
                                        onCubeLutDownloadRequest = { forceUpdate, downloadOnlyNewData ->
                                            component.updateCubeLuts(
                                                startDownloadIfNeeded = true,
                                                forceUpdate = forceUpdate,
                                                onFailure = {
                                                    scope.launch {
                                                        toastHostState.showError(context, it)
                                                    }
                                                },
                                                downloadOnlyNewData = downloadOnlyNewData
                                            )
                                        },
                                        previewModel = previewModel
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
                        Spacer(Modifier.width(8.dp))
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
            component.updatePreview(previewBitmap)
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
                                component.setPreviewData(null)
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
                                previewSheetData?.forEach { filter ->
                                    onFilterPickedWithParams(
                                        filter.copy(filter.value!!).also {
                                            it.isVisible = true
                                        }
                                    )
                                }
                                component.setPreviewData(null)
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
                        Text(
                            text = stringResource(
                                id = previewSheetData?.let {
                                    if (it.size == 1) it.first().title
                                    else R.string.filter_preview
                                } ?: R.string.filter_preview
                            ),
                            modifier = Modifier.marquee()
                        )
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
                        targetState = component.previewBitmap == null,
                        transitionSpec = { fadeIn() togetherWith fadeOut() }
                    ) { isNull ->
                        if (isNull) {
                            Box(
                                modifier = if (component.previewBitmap == null) {
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
                                bitmap = component.previewBitmap,
                                loading = component.isImageLoading,
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
                                                component.setPreviewData(null)
                                            } else component.removeFilterAtIndex(index)
                                        },
                                        onFilterChange = { value ->
                                            component.updateFilter(value, index)
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
        visible = previewSheetData != null,
        onDismiss = {
            if (!it) {
                component.setPreviewData(null)
            }
        }
    )
}

class AddFiltersSheetComponent @AssistedInject internal constructor(
    @Assisted componentContext: ComponentContext,
    private val filterProvider: FilterProvider<Bitmap>,
    private val imageTransformer: ImageTransformer<Bitmap>,
    private val shareProvider: ShareProvider<Bitmap>,
    private val fileController: FileController,
    private val imageCompressor: ImageCompressor<Bitmap>,
    private val favoriteInteractor: FavoriteFiltersInteractor,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val remoteResourcesStore: RemoteResourcesStore,
    dispatchersHolder: DispatchersHolder
) : BaseComponent(dispatchersHolder, componentContext) {

    private val _previewModel: MutableState<ImageModel> = mutableStateOf(
        R.drawable.filter_preview_source.toImageModel()
    )
    val previewModel: ImageModel by _previewModel

    private val _previewData: MutableState<List<UiFilter<*>>?> = mutableStateOf(null)
    val previewData by _previewData

    private val _previewBitmap: MutableState<Bitmap?> = mutableStateOf(null)
    val previewBitmap by _previewBitmap

    private val _cubeLutRemoteResources: MutableState<RemoteResources> =
        mutableStateOf(RemoteResources.CubeLutDefault)
    val cubeLutRemoteResources by _cubeLutRemoteResources

    private val _cubeLutDownloadProgress: MutableState<RemoteResourcesDownloadProgress?> =
        mutableStateOf(null)
    val cubeLutDownloadProgress by _cubeLutDownloadProgress

    init {
        updateCubeLuts(
            startDownloadIfNeeded = false,
            forceUpdate = false,
            onFailure = {},
            downloadOnlyNewData = false
        )
        favoriteInteractor
            .getFilterPreviewModel().onEach { data ->
                _previewModel.update { data }
            }.launchIn(componentScope)
    }

    fun setFilterPreviewModel(uri: String) {
        componentScope.launch {
            favoriteInteractor.setFilterPreviewModel(uri)
        }
    }

    fun updateCubeLuts(
        startDownloadIfNeeded: Boolean,
        forceUpdate: Boolean,
        onFailure: (Throwable) -> Unit,
        downloadOnlyNewData: Boolean = false
    ) {
        componentScope.launch {
            remoteResourcesStore.getResources(
                name = RemoteResources.CUBE_LUT,
                forceUpdate = forceUpdate,
                onDownloadRequest = { name ->
                    if (startDownloadIfNeeded) {
                        remoteResourcesStore.downloadResources(
                            name = name,
                            onProgress = { progress ->
                                _cubeLutDownloadProgress.update { progress }
                            },
                            onFailure = onFailure,
                            downloadOnlyNewData = downloadOnlyNewData
                        )
                    } else null
                }
            )?.let { data ->
                _cubeLutRemoteResources.update { data }
            }
            _cubeLutDownloadProgress.update { null }
        }
    }

    fun setPreviewData(data: UiFilter<*>?) {
        _previewData.update {
            data?.let { filter ->
                listOf(
                    filter.copy(filter.value!!).also { it.isVisible = true }
                )
            }
        }
    }

    fun setPreviewData(data: List<Filter<*>>) {
        _previewData.update { data.map { it.toUiFilter() } }
    }

    fun filterToTransformation(
        filter: UiFilter<*>
    ): Transformation = filterProvider.filterToTransformation(filter).toCoil()

    fun updatePreview(previewBitmap: Bitmap) {
        debouncedImageCalculation {
            _previewBitmap.update {
                imageTransformer.transform(
                    image = previewBitmap,
                    transformations = previewData?.map {
                        filterProvider.filterToTransformation(it)
                    } ?: emptyList(),
                    size = IntegerSize(2000, 2000)
                )
            }
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
        componentScope.launch {
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
        componentScope.launch {
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
        onResult: (SaveResult) -> Unit
    ) {
        componentScope.launch(ioDispatcher) {
            fileController.writeBytes(
                uri = fileUri.toString(),
                block = { it.writeBytes(content.toByteArray()) }
            ).also(onResult).onSuccess(::registerSave)
        }
    }

    fun shareContent(
        content: String,
        filename: String,
        onComplete: () -> Unit
    ) {
        componentScope.launch {
            shareProvider.shareData(
                writeData = { it.writeBytes(content.toByteArray()) },
                filename = filename,
                onComplete = onComplete
            )
        }
    }

    fun createTemplateFilename(templateFilter: TemplateFilter): String {
        val timeStamp = SimpleDateFormat(
            "yyyy-MM-dd_HH-mm-ss",
            Locale.getDefault()
        ).format(Date())
        return "template(${templateFilter.name})$timeStamp.imtbx_template"
    }

    fun reorderFavoriteFilters(value: List<UiFilter<*>>) {
        componentScope.launch {
            favoriteInteractor.reorderFavoriteFilters(value)
        }
    }

    val favoritesFlow: Flow<List<Filter<*>>>
        get() = favoriteInteractor.getFavoriteFilters()

    val templatesFlow: Flow<List<TemplateFilter>>
        get() = favoriteInteractor.getTemplateFilters()

    fun toggleFavorite(filter: UiFilter<*>) {
        componentScope.launch {
            favoriteInteractor.toggleFavorite(filter)
        }
    }

    fun removeTemplateFilter(templateFilter: TemplateFilter) {
        componentScope.launch {
            favoriteInteractor.removeTemplateFilter(templateFilter)
        }
    }

    suspend fun convertTemplateFilterToString(
        templateFilter: TemplateFilter
    ): String = favoriteInteractor.convertTemplateFilterToString(templateFilter)

    fun addTemplateFilterFromString(
        string: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onError: suspend () -> Unit
    ) {
        componentScope.launch {
            favoriteInteractor.addTemplateFilterFromString(
                string = string,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun addTemplateFilterFromUri(
        uri: String,
        onSuccess: suspend (filterName: String, filtersCount: Int) -> Unit,
        onError: suspend () -> Unit
    ) {
        componentScope.launch {
            favoriteInteractor.addTemplateFilterFromUri(
                uri = uri,
                onSuccess = onSuccess,
                onError = onError
            )
        }
    }

    fun cacheNeutralLut(onComplete: (Uri) -> Unit) {
        componentScope.launch {
            imageGetter.getImage(R.drawable.lookup)?.let {
                shareProvider.cacheImage(
                    image = it,
                    imageInfo = ImageInfo(
                        width = 512,
                        height = 512,
                        imageFormat = ImageFormat.Png.Lossless
                    )
                )?.let { uri ->
                    onComplete(uri.toUri())
                }
            }
        }
    }

    fun shareNeutralLut(onComplete: () -> Unit) {
        componentScope.launch {
            imageGetter.getImage(R.drawable.lookup)?.let {
                shareProvider.shareImage(
                    image = it,
                    imageInfo = ImageInfo(
                        width = 512,
                        height = 512,
                        imageFormat = ImageFormat.Png.Lossless
                    ),
                    onComplete = onComplete
                )
            }
        }
    }

    fun saveNeutralLut(
        oneTimeSaveLocationUri: String? = null,
        onComplete: (result: SaveResult) -> Unit,
    ) {
        componentScope.launch {
            imageGetter.getImage(R.drawable.lookup)?.let { bitmap ->
                val imageInfo = ImageInfo(
                    width = 512,
                    height = 512,
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
                        keepOriginalMetadata = false,
                        oneTimeSaveLocationUri = oneTimeSaveLocationUri
                    )
                )
            }
        }
    }

    override fun resetState() {
        _previewData.update { null }
        _previewBitmap.update { null }
        cancelImageLoading()
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(
            componentContext: ComponentContext
        ): AddFiltersSheetComponent
    }

}