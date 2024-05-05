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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.AutoFixHigh
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.EditRoad
import androidx.compose.material.icons.rounded.FilterHdr
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.LensBlur
import androidx.compose.material.icons.rounded.Light
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.utils.LocalFavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.filters.presentation.utils.getFavoriteFiltersAsUiState
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.BookmarkOff
import ru.tech.imageresizershrinker.core.resources.icons.BookmarkRemove
import ru.tech.imageresizershrinker.core.resources.icons.Cube
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.StrongBlack
import ru.tech.imageresizershrinker.core.ui.theme.White
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.getStringLocalized
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.isPortraitOrientationAsState
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.SimplePicture
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBar
import ru.tech.imageresizershrinker.core.ui.widget.other.EnhancedTopAppBarType
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleDragHandle
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheetDefaults
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberImageState
import java.util.Locale


private object FilterHolder {
    val previewSheetData: MutableState<UiFilter<*>?> = mutableStateOf(null)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFiltersSheet(
    visible: MutableState<Boolean>,
    previewBitmap: Bitmap?,
    onRequestPreview: suspend (Bitmap, List<UiFilter<*>>, IntegerSize) -> Bitmap?,
    onRequestFilterMapping: ((UiFilter<*>) -> Transformation)?,
    onFilterPicked: (UiFilter<*>) -> Unit,
    onFilterPickedWithParams: (UiFilter<*>) -> Unit
) {
    val scope = rememberCoroutineScope()

    val favoriteFilters by LocalFavoriteFiltersInteractor.getFavoriteFiltersAsUiState()

    var previewSheetData by FilterHolder.previewSheetData

    LaunchedEffect(
        previewBitmap,
        previewSheetData
    ) {
        if (previewBitmap == null) {
            previewSheetData = null
        }
    }

    val context = LocalContext.current
    val groupedFilters by remember(context) {
        derivedStateOf {
            UiFilter.groupedEntries(context)
        }
    }
    val haptics = LocalHapticFeedback.current
    val pagerState = rememberPagerState(
        pageCount = { groupedFilters.size + 1 },
        initialPage = 1
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
                                        shape = RoundedCornerShape(topStart = 100f, topEnd = 100f)
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
                            ).forEachIndexed { index, (icon, title) ->
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
                                        previewBitmap = previewBitmap,
                                        favoriteFilters = favoriteFilters,
                                        onLongClick = {
                                            previewSheetData = filter
                                        },
                                        onClick = {
                                            visible.value = false
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
                                groupedFilters[page - 1]
                            }
                        }
                        if (page != 0) {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                contentPadding = PaddingValues(16.dp)
                            ) {

                                itemsIndexed(filters) { index, filter ->
                                    FilterSelectionItem(
                                        filter = filter,
                                        previewBitmap = previewBitmap,
                                        favoriteFilters = favoriteFilters,
                                        onLongClick = {
                                            previewSheetData = filter
                                        },
                                        onClick = {
                                            visible.value = false
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
                        } else {
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
                                                previewBitmap = previewBitmap,
                                                favoriteFilters = favoriteFilters,
                                                onLongClick = {
                                                    previewSheetData = filter
                                                },
                                                onClick = {
                                                    visible.value = false
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
                            onClick = { visible.value = false }
                        ) {
                            AutoSizeText(stringResource(R.string.close))
                        }
                    }
                }
            }
        },
        confirmButton = {},
        enableBottomContentWeight = false,
        visible = visible.value,
        onDismiss = {
            visible.value = it
        }
    )

    var transformedBitmap by remember(previewBitmap) {
        mutableStateOf(
            previewBitmap
        )
    }

    var imageState by rememberImageState()
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(previewSheetData) {
        if (previewBitmap != null && previewSheetData != null) {
            if (previewSheetData?.value is Unit) {
                imageState = imageState.copy(position = 2)
            }
            loading = true
            transformedBitmap = onRequestPreview(
                previewBitmap,
                listOf(previewSheetData!!),
                IntegerSize(2000, 2000)
            )
            loading = false
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
                                previewSheetData = null
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
                                previewSheetData?.let {
                                    onFilterPickedWithParams(it)
                                }
                                previewSheetData = null
                                visible.value = false
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
                                    id = previewSheetData?.title ?: R.string.app_name
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
                    SimplePicture(
                        bitmap = transformedBitmap,
                        loading = loading,
                        modifier = Modifier
                    )
                }
                val isPortrait by isPortraitOrientationAsState()

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    val isUnit = previewSheetData?.value == Unit
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
                            previewSheetData?.takeIf { it.value != Unit }?.let {
                                FilterItem(
                                    backgroundColor = MaterialTheme
                                        .colorScheme
                                        .surface,
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    filter = it,
                                    showDragHandle = false,
                                    onRemove = {
                                        previewSheetData = null
                                    },
                                    onFilterChange = { v ->
                                        previewSheetData = previewSheetData?.copy(v)
                                    }
                                )
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
            previewSheetData = null
        }
    )
}

@Composable
private fun FilterSelectionItem(
    filter: UiFilter<*>,
    isFavoritePage: Boolean,
    previewBitmap: Bitmap?,
    favoriteFilters: List<UiFilter<*>>,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    onRequestFilterMapping: ((UiFilter<*>) -> Transformation)?,
    shape: Shape,
    modifier: Modifier
) {
    val haptics = LocalHapticFeedback.current
    val settingsState = LocalSettingsState.current

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val model = remember(filter) {
        if (onRequestFilterMapping != null) {
            ImageRequest.Builder(context)
                .data(R.drawable.filter_preview_source)
                .error(R.drawable.filter_preview_source)
                .transformations(onRequestFilterMapping(filter))
                .diskCacheKey(filter::class.simpleName)
                .memoryCacheKey(filter::class.simpleName)
                .crossfade(true)
                .size(300, 300)
                .build()
        } else null
    }
    var isBitmapDark by remember {
        mutableStateOf(true)
    }
    var loading by remember {
        mutableStateOf(false)
    }
    val painter = rememberAsyncImagePainter(
        model = model,
        onLoading = {
            loading = true
        },
        onSuccess = {
            loading = false
            scope.launch {
                isBitmapDark = calculateBrightnessEstimate(it.result.drawable.toBitmap()) < 110
            }
        }
    )

    val interactor = LocalFavoriteFiltersInteractor.current

    PreferenceItemOverload(
        title = stringResource(filter.title),
        startIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    if (onRequestFilterMapping != null) {
                        Image(
                            painter = painter,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .scale(1.2f)
                                .clip(MaterialTheme.shapes.medium)
                                .transparencyChecker()
                                .shimmer(loading)
                        )
                    }
                    if (previewBitmap != null) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .clickable {
                                    haptics.performHapticFeedback(
                                        HapticFeedbackType.LongPress
                                    )
                                    onLongClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Slideshow,
                                contentDescription = stringResource(R.string.image_preview),
                                tint = if (isBitmapDark) StrongBlack
                                else White,
                                modifier = Modifier.scale(1.2f)
                            )
                            Icon(
                                imageVector = Icons.Rounded.Slideshow,
                                contentDescription = stringResource(R.string.image_preview),
                                tint = if (isBitmapDark) White
                                else StrongBlack
                            )
                        }
                    }
                }
                Spacer(Modifier.width(16.dp))
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .width(
                            settingsState.borderWidth.coerceAtLeast(
                                0.25.dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.outlineVariant())
                )
            }
        },
        endIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        interactor.toggleFavorite(filter)
                    }
                },
                modifier = Modifier.offset(8.dp)
            ) {
                val inFavorite by remember(favoriteFilters, filter) {
                    derivedStateOf {
                        favoriteFilters.filterIsInstance(filter::class.java).isNotEmpty()
                    }
                }
                AnimatedContent(
                    targetState = inFavorite to isFavoritePage,
                    transitionSpec = {
                        (fadeIn() + scaleIn(initialScale = 0.85f))
                            .togetherWith(fadeOut() + scaleOut(targetScale = 0.85f))
                    }
                ) { (isInFavorite, isFavPage) ->
                    val icon by remember(isInFavorite, isFavPage) {
                        derivedStateOf {
                            when {
                                isFavPage && isInFavorite -> Icons.Rounded.BookmarkRemove
                                isInFavorite -> Icons.Rounded.Bookmark
                                else -> Icons.Rounded.BookmarkBorder
                            }
                        }
                    }
                    Icon(
                        imageVector = icon,
                        contentDescription = null
                    )
                }
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        onLongClick = onLongClick,
        onClick = onClick,
        drawStartIconContainer = false
    )
}

private fun calculateBrightnessEstimate(
    bitmap: Bitmap,
    pixelSpacing: Int = 1
): Int {
    var r = 0
    var b = 0
    var g = 0
    val height = bitmap.height
    val width = bitmap.width
    var n = 0
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    var i = 0
    while (i < pixels.size) {
        val color = pixels[i]
        r += color.red
        b += color.green
        g += color.blue
        n++
        i += pixelSpacing
    }
    return (r + g + b) / (n * 3)
}