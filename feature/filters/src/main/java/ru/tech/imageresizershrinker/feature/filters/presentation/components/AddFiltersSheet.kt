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

import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.Animation
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.EditRoad
import androidx.compose.material.icons.rounded.FilterHdr
import androidx.compose.material.icons.rounded.FormatColorFill
import androidx.compose.material.icons.rounded.LensBlur
import androidx.compose.material.icons.rounded.Light
import androidx.compose.material.icons.rounded.PhotoFilter
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.icons.material.Cube
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.image.SimplePicture
import ru.tech.imageresizershrinker.core.ui.widget.image.imageStickyHeader
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleDragHandle
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.Marquee
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalWindowSizeClass
import ru.tech.imageresizershrinker.core.ui.widget.utils.middleImageState
import ru.tech.imageresizershrinker.core.ui.widget.utils.rememberAvailableHeight


private object FilterHolder {
    val previewSheetData: MutableState<UiFilter<*>?> = mutableStateOf(null)
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddFiltersSheet(
    visible: MutableState<Boolean>,
    previewBitmap: Bitmap?,
    onRequestPreview: suspend (Bitmap, List<UiFilter<*>>, IntegerSize) -> Bitmap?,
    onFilterPicked: (UiFilter<*>) -> Unit,
    onFilterPickedWithParams: (UiFilter<*>) -> Unit
) {
    val scope = rememberCoroutineScope()

    var previewSheetData by FilterHolder.previewSheetData
    val showPreviewState = remember { mutableStateOf(false) }

    LaunchedEffect(previewBitmap) {
        if (previewBitmap == null) {
            previewSheetData = null
        }
    }

    val context = LocalContext.current
    val groupedFilters = remember(context) {
        UiFilter.groupedEntries(context)
    }
    val haptics = LocalHapticFeedback.current
    val pagerState = rememberPagerState(pageCount = { groupedFilters.size })

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
        filtersForSearch = groupedFilters.flatten().filter {
            context.getString(it.title).contains(
                other = searchKeyword,
                ignoreCase = true
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
                        PrimaryScrollableTabRow(
                            divider = {},
                            edgePadding = 16.dp,
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp),
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
                        targetState = filtersForSearch
                    ) { filters ->
                        if (filters.isNotEmpty()) {
                            Column(
                                Modifier
                                    .verticalScroll(rememberScrollState())
                                    .padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                filters.forEachIndexed { index, filter ->
                                    FilterSelectionItem(
                                        filter = filter,
                                        previewBitmap = previewBitmap,
                                        onLongClick = {
                                            previewSheetData = filter
                                            showPreviewState.value = true
                                        },
                                        onClick = {
                                            visible.value = false
                                            onFilterPicked(filter)
                                        },
                                        shape = ContainerShapeDefaults.shapeForIndex(
                                            index = index,
                                            size = filters.size
                                        )
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
                        outOfBoundsPageCount = 4
                    ) { page ->
                        val filters by remember(page) {
                            derivedStateOf {
                                groupedFilters[page]
                            }
                        }
                        Column(
                            Modifier
                                .verticalScroll(rememberScrollState())
                                .padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            filters.forEachIndexed { index, filter ->
                                FilterSelectionItem(
                                    filter = filter,
                                    previewBitmap = previewBitmap,
                                    onLongClick = {
                                        previewSheetData = filter
                                        showPreviewState.value = true
                                    },
                                    onClick = {
                                        visible.value = false
                                        onFilterPicked(filter)
                                    },
                                    shape = ContainerShapeDefaults.shapeForIndex(
                                        index = index,
                                        size = filters.size
                                    )
                                )
                            }
                        }
                    }
                }
            }
            AnimatedContent(
                targetState = isSearching
            ) { searching ->
                if (searching) {
                    BackHandler {
                        searchKeyword = ""
                        isSearching = false
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawHorizontalStroke(true, autoElevation = 6.dp)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                            .padding(16.dp)
                            .navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                            RoundedTextField(
                                maxLines = 1,
                                hint = { Text(stringResource(id = R.string.search_here)) },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search
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
                                            contentDescription = null,
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
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                },
                                shape = CircleShape
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawHorizontalStroke(true, autoElevation = 6.dp)
                            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp))
                            .padding(16.dp)
                            .navigationBarsPadding()
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TitleItem(
                            text = stringResource(R.string.filter),
                            icon = Icons.Rounded.PhotoFilter
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        EnhancedIconButton(
                            onClick = { isSearching = true },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null
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
        visible = visible
    )

    var transformedBitmap by remember(previewBitmap) {
        mutableStateOf(
            previewBitmap
        )
    }

    var imageState by remember { mutableStateOf(middleImageState()) }
    var loading by remember { mutableStateOf(false) }
    LaunchedEffect(previewSheetData) {
        showPreviewState.value = previewSheetData != null && previewBitmap != null
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

    val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp).copy(0.8f)
    SimpleSheet(
        dragHandle = {
            SimpleDragHandle {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = { previewSheetData = null }
                        ) {
                            Icon(Icons.Rounded.Close, null)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        MaterialTheme.colorScheme.surfaceColorAtElevation(
                            10.dp
                        )
                    ),
                    actions = {
                        EnhancedIconButton(
                            containerColor = Color.Transparent,
                            contentColor = LocalContentColor.current,
                            enableAutoShadowAndBorder = false,
                            onClick = {
                                previewSheetData?.let {
                                    onFilterPickedWithParams(it.copy(it.value!!))
                                }
                                previewSheetData = null
                                visible.value = false
                            }
                        ) {
                            Icon(Icons.Rounded.Done, null)
                        }
                    },
                    title = {
                        Marquee(edgeColor = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)) {
                            Text(
                                text = stringResource(
                                    id = previewSheetData?.title ?: R.string.app_name
                                ),
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
                val imageInside =
                    LocalConfiguration.current.orientation != Configuration.ORIENTATION_LANDSCAPE || LocalWindowSizeClass.current.widthSizeClass == WindowWidthSizeClass.Compact

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    if (!imageInside) {
                        Box(
                            Modifier
                                .container(
                                    shape = RectangleShape,
                                    color = MaterialTheme.colorScheme.surfaceContainerHighest
                                )
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
                                if (!imageInside) Modifier.weight(1f)
                                else Modifier
                            )
                            .clipToBounds()
                    ) {
                        imageStickyHeader(
                            visible = imageInside,
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
                                        .surfaceColorAtElevation(8.dp),
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    filter = it,
                                    showDragHandle = false,
                                    onRemove = { previewSheetData = null },
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
        visible = showPreviewState
    )
}

@Composable
private fun FilterSelectionItem(
    filter: UiFilter<*>,
    previewBitmap: Bitmap?,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
    shape: Shape
) {
    val haptics = LocalHapticFeedback.current
    val settingsState = LocalSettingsState.current
    PreferenceItemOverload(
        title = stringResource(filter.title),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
        icon = if (previewBitmap != null) {
            {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
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
                        Icon(Icons.Rounded.Slideshow, null)
                    }
                    Spacer(Modifier.width(16.dp))
                    Box(
                        Modifier
                            .height(36.dp)
                            .width(
                                settingsState.borderWidth.coerceAtLeast(
                                    0.25.dp
                                )
                            )
                            .background(MaterialTheme.colorScheme.outlineVariant())

                    )
                }
            }
        } else null,
        endIcon = {
            Icon(
                imageVector = Icons.Rounded.AddCircleOutline,
                contentDescription = null
            )
        },
        shape = shape,
        onLongClick = onLongClick,
        onClick = onClick
    )
}