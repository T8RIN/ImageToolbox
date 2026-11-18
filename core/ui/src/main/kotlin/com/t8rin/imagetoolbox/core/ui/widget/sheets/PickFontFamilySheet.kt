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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.overscroll
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.withoutVisualEffect
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.model.MimeType
import com.t8rin.imagetoolbox.core.domain.model.plus
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.FileExport
import com.t8rin.imagetoolbox.core.resources.icons.FileImport
import com.t8rin.imagetoolbox.core.resources.icons.FilterAlt
import com.t8rin.imagetoolbox.core.resources.icons.FontDownload
import com.t8rin.imagetoolbox.core.resources.icons.FontFamily
import com.t8rin.imagetoolbox.core.resources.icons.FormatLineSpacing
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.settings.domain.model.DomainFontFamily
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont.Category
import com.t8rin.imagetoolbox.core.settings.domain.model.FontPackFont.Script
import com.t8rin.imagetoolbox.core.settings.presentation.model.UiFontFamily
import com.t8rin.imagetoolbox.core.settings.presentation.model.toUiFont
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalFontLibrary
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSimpleSettingsInteractor
import com.t8rin.imagetoolbox.core.ui.theme.ProvideTypography
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFileCreator
import com.t8rin.imagetoolbox.core.ui.utils.content_pickers.rememberFilePicker
import com.t8rin.imagetoolbox.core.ui.utils.helper.AppToastHost
import com.t8rin.imagetoolbox.core.ui.utils.helper.SaveResultHandler
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBadge
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.press
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.negativePadding
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.other.FontBadge
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRow
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import kotlinx.coroutines.launch
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.roundToInt

@Composable
fun PickFontFamilySheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    onFontSelected: (UiFontFamily) -> Unit,
    selectedFont: UiFontFamily = LocalSettingsState.current.font
) {
    val settings = LocalSettingsState.current
    val interactor = LocalSimpleSettingsInteractor.current
    val scope = rememberCoroutineScope()
    val exportFontsLauncher = rememberFileCreator(
        mimeType = MimeType.Zip,
        onSuccess = { uri ->
            scope.launch {
                runSuspendCatching {
                    SaveResultHandler.parseFileSaveResult(interactor.exportCustomFonts(uri.toString()))
                }.onFailure(AppToastHost::showFailureToast)
            }
        }
    )
    val onRemoveFont: (UiFontFamily.Custom) -> Unit = { font ->
        scope.launch {
            interactor.removeCustomFont(font.asDomain() as DomainFontFamily.Custom)
            if (selectedFont == font) onFontSelected(UiFontFamily.System)
        }
    }
    val haptics = LocalHapticFeedback.current
    val fonts = UiFontFamily.entries
    val customFonts = UiFontFamily.customEntries
    val packFonts = LocalFontLibrary.current?.fonts?.collectAsState()?.value.orEmpty()
    val metadata = remember(packFonts) { packFonts.associateBy { it.filePath } }
    var data by remember { mutableStateOf(fonts) }
    LaunchedEffect(fonts) {
        data = fonts
    }
    var dragStartOrder by remember { mutableStateOf(fonts) }
    var openCount by rememberSaveable { mutableIntStateOf(0) }
    var wasVisible by rememberSaveable { mutableStateOf(visible) }
    LaunchedEffect(visible) {
        if (visible && !wasVisible) openCount++
        wasVisible = visible
    }
    var searching by rememberSaveable(openCount) { mutableStateOf(false) }
    var search by rememberSaveable(openCount) { mutableStateOf("") }
    val defaultPreview = stringResource(R.string.alphabet_and_numbers)
    var previewText by rememberSaveable { mutableStateOf(defaultPreview) }
    var arranging by rememberSaveable(openCount) { mutableStateOf(false) }
    var expandedGroups by rememberSaveable(openCount) {
        mutableStateOf(emptyMap<String, String?>())
    }
    var showFilters by rememberSaveable(openCount) { mutableStateOf(false) }
    var categories by rememberSaveable(openCount) { mutableStateOf(emptyList<Category>()) }
    var scripts by rememberSaveable(openCount) { mutableStateOf(emptyList<Script>()) }
    var favoritesOnly by rememberSaveable(openCount) { mutableStateOf(false) }
    val canFilter = settings.fontPackEnabled && packFonts.isNotEmpty()
    val keyword = search.trim()
    val filtering =
        canFilter && (keyword.isNotEmpty() || categories.isNotEmpty() || scripts.isNotEmpty() || favoritesOnly)
    val filteredFonts = remember(
        data,
        filtering,
        keyword,
        categories,
        scripts,
        favoritesOnly,
        settings.favoriteFonts,
        metadata
    ) {
        if (!filtering) data else data.filter { font ->
            if (!font.name.orEmpty().contains(keyword, ignoreCase = true)) return@filter false
            if (favoritesOnly && font.key !in settings.favoriteFonts) return@filter false
            val entry = metadata[font.key] ?: return@filter !favoritesOnly ||
                    (categories.isEmpty() && scripts.isEmpty())
            (categories.isEmpty() || entry.category in categories) &&
                    scripts.all { (entry.coverage[it] ?: 0f) >= 100f }
        }
    }
    val browseState = rememberLazyListState()
    val density = LocalDensity.current
    val previewHeight = with(density) { 144.dp.roundToPx() }
    val collapsingGroups = remember(openCount) {
        mutableStateMapOf<String, FontGroupCollapse>()
    }
    val searchState = rememberLazyListState()
    val orderState = rememberLazyListState()

    LaunchedEffect(search) {
        searchState.scrollToItem(0)
    }

    LaunchedEffect(keyword.takeIf { canFilter }, categories, scripts, favoritesOnly) {
        browseState.scrollToItem(0)
    }

    fun animateGroup(key: String, headerKey: String, entries: List<UiFontFamily>) {
        val layout = browseState.layoutInfo
        val header = layout.visibleItemsInfo.firstOrNull { it.key == headerKey }
        val collapse = collapsingGroups[key] ?: run {
            val rows = layout.visibleItemsInfo.filter { row ->
                header != null && row.index > header.index && row.index <= header.index + entries.size
            }
            val first = rows.firstOrNull()
            if (first == null) {
                expandedGroups = expandedGroups - key
                return
            }
            if (header != null && first.index > header.index + 1) {
                browseState.requestScrollToItem(header.index + 1, -first.offset)
            }
            FontGroupCollapse(rows.map { it.key }.toSet()).also { collapsingGroups[key] = it }
        }
        val closing = collapse.progress.targetValue != 0f
        scope.launch {
            collapse.progress.animateTo(if (closing) 0f else 1f, tween(220))
            if (closing) {
                expandedGroups = expandedGroups - key
                withFrameNanos { }
            } else {
                val visible = browseState.layoutInfo.visibleItemsInfo
                val group = visible.firstOrNull { it.key == headerKey }
                val anchor = visible.firstOrNull {
                    it.index == browseState.firstVisibleItemIndex && it.key in collapse.visibleKeys
                }
                if (group != null && anchor != null) {
                    val index = entries.indexOfFirst { it.key == anchor.key }
                    if (index >= 0) {
                        browseState.requestScrollToItem(group.index + 1 + index, -anchor.offset)
                    }
                }
            }
            collapsingGroups.remove(key)
        }
    }

    val reorderable = rememberReorderableLazyListState(orderState) { from, to ->
        val fromIndex = data.indexOfFirst { it.key == from.key }
        val toIndex = data.indexOfFirst { it.key == to.key }
        if (fromIndex > 0 && toIndex > 0 && fromIndex != toIndex &&
            (from.key in settings.favoriteFonts) == (to.key in settings.favoriteFonts)
        ) {
            haptics.press()
            data = data.toMutableList().apply { add(toIndex, removeAt(fromIndex)) }
        }
    }
    EnhancedModalBottomSheet(
        nestedScrollEnabled = !arranging && settings.sheetNestedScrollEnabled,
        enableBottomContentWeight = true,
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        sheetContent = {
            BackHandler(enabled = arranging) { arranging = false }
            AnimatedVisibility(visible = !arranging) {
                RoundedTextField(
                    value = previewText,
                    onValueChange = { previewText = it },
                    label = stringResource(R.string.text),
                    singleLine = false,
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .container(
                            shape = ShapeDefaults.large,
                            resultPadding = 8.dp
                        )
                )
            }
            AnimatedContent(
                targetState = arranging to searching,
                transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = false) },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSizeNoClip()
            ) { (isArranging, isSearching) ->
                val listState = when {
                    isArranging -> orderState
                    isSearching -> searchState
                    else -> browseState
                }
                val itemSpacing = if (listState == browseState) 8.dp else 0.dp
                val itemPadding = Modifier.padding(bottom = itemSpacing)
                val animateItems = listState != browseState || collapsingGroups.isEmpty()
                val containerColor = EnhancedBottomSheetDefaults.containerColor
                val topPadding = if (isArranging) 0.dp else 8.dp
                val overscrollEffect = rememberOverscrollEffect()
                val scrollOverscrollEffect = remember(overscrollEffect) {
                    overscrollEffect?.withoutVisualEffect()
                }
                val pinnedKey = remember(listState) {
                    derivedStateOf { listState.layoutInfo.pinnedFontGroup()?.key }
                }.value
                val fade = remember(listState, pinnedKey) { Animatable(0f) }
                LaunchedEffect(fade) { fade.animateTo(1f, tween(150)) }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .overscroll(overscrollEffect)
                        .padding(top = topPadding)
                        .drawWithContent {
                            drawContent()
                            if (!isArranging && listState.canScrollBackward) {
                                val layout = listState.layoutInfo
                                val viewportStart = layout.viewportStartOffset
                                val pinnedHeader = layout.pinnedFontGroup()
                                val top = pinnedHeader?.let {
                                    (it.offset - viewportStart + it.size - itemSpacing.toPx())
                                        .coerceAtLeast(0f)
                                } ?: 0f
                                val colors = listOf(containerColor, Color.Transparent)
                                val clippedHeaderHeight =
                                    pinnedHeader?.let { viewportStart - it.offset } ?: 0
                                if (clippedHeaderHeight > 0 && top > 0f) {
                                    val length =
                                        minOf(clippedHeaderHeight.toFloat(), topPadding.toPx())
                                    drawRect(
                                        brush = Brush.verticalGradient(colors, endY = length),
                                        size = Size(size.width, minOf(top, length))
                                    )
                                }
                                val nextHeaderTop = layout.visibleItemsInfo.firstOrNull {
                                    it.contentType == "group" && it.offset > viewportStart
                                }?.let { (it.offset - viewportStart).toFloat() }
                                val length = if (pinnedHeader == null) 16.dp.toPx() else {
                                    val progress =
                                        if (pinnedHeader.key == pinnedKey) fade.value else 0f
                                    12.dp.toPx() + 4.dp.toPx() * progress
                                }
                                val bottom = minOf(
                                    top + length,
                                    size.height,
                                    nextHeaderTop ?: size.height
                                )
                                if (bottom > top) drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = colors,
                                        startY = top,
                                        endY = bottom
                                    ),
                                    topLeft = Offset(0f, top),
                                    size = Size(size.width, bottom - top)
                                )
                            }
                        },
                    userScrollEnabled = !isArranging || !reorderable.isAnyItemDragging,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        top = if (isArranging) 16.dp else 8.dp,
                        end = 16.dp,
                        bottom = 16.dp - itemSpacing
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp - itemSpacing),
                    flingBehavior = enhancedFlingBehavior(),
                    overscrollEffect = scrollOverscrollEffect
                ) {
                    if (!isArranging && !isSearching) {
                        item(key = "font_actions") {
                            FontImportExportButtons(
                                modifier = itemPadding,
                                canExport = customFonts.isNotEmpty() || packFonts.isNotEmpty(),
                                onAddFont = { uri ->
                                    scope.launch {
                                        runSuspendCatching {
                                            val imported =
                                                interactor.importCustomFonts(uri.toString())
                                            if (imported.isNotEmpty()) {
                                                imported.singleOrNull()
                                                    ?.let { onFontSelected(it.toUiFont()) }
                                                AppToastHost.showConfetti()
                                            } else AppToastHost.showFailureToast(R.string.wrong_font)
                                        }.onFailure(AppToastHost::showFailureToast)
                                    }
                                },
                                onExportFonts = {
                                    exportFontsLauncher.make("FONTS_EXPORT_${timestamp()}.zip")
                                }
                            )
                        }
                        item(key = "font_pack") {
                            FontPackCard(
                                modifier = itemPadding.then(
                                    if (animateItems) Modifier.animateItem() else Modifier
                                ),
                                onDisabled = {
                                    search = ""
                                    if (
                                        selectedFont is UiFontFamily.Custom
                                        && metadata.containsKey(selectedFont.filePath)
                                    ) {
                                        onFontSelected(UiFontFamily.System)
                                    }
                                }
                            )
                        }
                    }
                    fun fontItems(
                        entries: List<UiFontFamily>,
                        showSource: Boolean = false,
                        collapse: FontGroupCollapse? = null
                    ) {
                        items(items = entries, key = { it.key }, contentType = { "font" }) { font ->
                            if (isArranging && font != UiFontFamily.System) {
                                ReorderableItem(reorderable, key = font.key) { dragging ->
                                    FontItem(
                                        font = font,
                                        selected = false,
                                        onFontSelected = onFontSelected,
                                        onRemoveFont = null,
                                        arranging = true,
                                        reorderBadge = {
                                            val category = metadata[font.key]?.category
                                            val (label, icon) = when {
                                                font.key in settings.favoriteFonts ->
                                                    R.string.favorite to Icons.Outlined.Bookmark

                                                category != null -> category.titleRes to category.icon
                                                font is UiFontFamily.Custom ->
                                                    R.string.font_external to Icons.Outlined.FileImport

                                                else -> R.string.font_built_in to Icons.Outlined.FontDownload
                                            }
                                            ProvideTypography(settings.font) {
                                                FontBadge(
                                                    label = stringResource(label),
                                                    icon = icon,
                                                    modifier = Modifier.padding(top = 4.dp),
                                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                                )
                                            }
                                        },
                                        modifier = Modifier
                                            .scale(animateFloatAsState(if (dragging) 1.03f else 1f).value)
                                            .longPressDraggableHandle(
                                                onDragStarted = {
                                                    dragStartOrder = data
                                                    haptics.longPress()
                                                },
                                                onDragStopped = {
                                                    if (data != dragStartOrder) {
                                                        scope.launch { interactor.setFontOrder(data.map { it.key }) }
                                                    }
                                                }
                                            )
                                    )
                                }
                            } else {
                                FontItem(
                                    font = font,
                                    selected = !isArranging && font == selectedFont,
                                    onFontSelected = onFontSelected,
                                    onRemoveFont = onRemoveFont.takeIf { !isArranging && font in customFonts },
                                    coverage = metadata[font.key]?.coverage.orEmpty(),
                                    favorite = font.key in settings.favoriteFonts,
                                    source = if (showSource) {
                                        stringResource(
                                            when (font) {
                                                in customFonts -> R.string.font_external
                                                is UiFontFamily.Custom -> R.string.font_downloaded
                                                else -> R.string.font_built_in
                                            }
                                        )
                                    } else null,
                                    onToggleFavorite = if (font == UiFontFamily.System) null else {
                                        { scope.launch { interactor.toggleFavoriteFont(font.key) } }
                                    },
                                    previewText = previewText,
                                    arranging = isArranging,
                                    modifier = when {
                                        collapse != null -> Modifier
                                            .clipToBounds()
                                            .layout { measurable, constraints ->
                                                val placeable = measurable.measure(constraints)
                                                val height =
                                                    (placeable.height * collapse.progress.value).roundToInt()
                                                layout(placeable.width, height) {
                                                    placeable.placeRelative(0, 0)
                                                }
                                            }
                                            .graphicsLayer { alpha = collapse.progress.value }

                                        animateItems -> Modifier.animateItem()
                                        else -> Modifier
                                    }.then(itemPadding)
                                )
                            }
                        }
                    }

                    fun fontGroup(
                        key: String,
                        title: Int,
                        entries: List<UiFontFamily>,
                        icon: ImageVector
                    ) {
                        if (entries.isEmpty()) return
                        val headerKey = "font_group_$key"
                        val expanded = key in expandedGroups
                        val collapse = collapsingGroups[key]
                        val selected = selectedFont in entries

                        val firstIndex = if (expanded) {
                            val firstKey = expandedGroups[key]
                            entries.indexOfFirst { it.key == firstKey }
                        } else -1

                        val groupEntries = if (firstIndex > 0) {
                            entries.toMutableList().apply { add(0, removeAt(firstIndex)) }
                        } else entries

                        stickyHeader(key = headerKey, contentType = "group") {
                            val placement = if (animateItems) {
                                Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                            } else Modifier
                            FontGroupHeader(
                                title = stringResource(title),
                                icon = icon,
                                count = entries.size,
                                expanded = expanded && collapse?.progress?.targetValue != 0f,
                                selected = selected,
                                modifier = placement
                                    .then(itemPadding)
                                    .negativePadding(horizontal = 16.dp)
                                    .background(containerColor)
                                    .padding(horizontal = 16.dp),
                                onClick = {
                                    if (expanded) {
                                        animateGroup(key, headerKey, groupEntries)
                                    } else {
                                        val selectedKey = selectedFont.key.takeIf { selected }
                                        expandedGroups = expandedGroups + (key to selectedKey)
                                        val layout = browseState.layoutInfo
                                        val header =
                                            layout.visibleItemsInfo.firstOrNull { it.key == headerKey }
                                        if (header != null) {
                                            val spaceBelow =
                                                layout.viewportEndOffset - header.offset - header.size
                                            val viewportSpace = layout.viewportEndOffset -
                                                    layout.viewportStartOffset - header.size
                                            val scroll =
                                                minOf(previewHeight, viewportSpace) - spaceBelow
                                            if (scroll > 0) scope.launch {
                                                browseState.animateScrollBy(scroll.toFloat())
                                            }
                                        }
                                    }
                                }
                            )
                        }
                        if (expanded) {
                            fontItems(
                                if (collapse == null) groupEntries
                                else groupEntries.filter { it.key in collapse.visibleKeys },
                                showSource = key == "favorites",
                                collapse = collapse
                            )
                        }
                    }
                    when {
                        isArranging -> {
                            fontItems(data.filterNot { it == UiFontFamily.System })
                        }

                        isSearching || canFilter && (keyword.isNotEmpty() || favoritesOnly) -> {
                            val results = if (isSearching) data.filter {
                                it.name.orEmpty().contains(keyword, ignoreCase = true)
                            } else filteredFonts
                            if (results.isEmpty()) item(key = "empty_search") {
                                FontSearchEmptyState(itemPadding.animateItem())
                            }
                            fontItems(results, showSource = canFilter && favoritesOnly)
                        }

                        else -> {
                            fontItems(listOf(UiFontFamily.System))
                            val favorites = filteredFonts.filter {
                                it != UiFontFamily.System && it.key in settings.favoriteFonts
                            }
                            fontGroup(
                                "favorites",
                                R.string.favorite,
                                favorites,
                                Icons.Outlined.Bookmark
                            )
                            val imported =
                                filteredFonts.filter { it in customFonts && it.key !in settings.favoriteFonts }
                            fontGroup(
                                "imported",
                                R.string.imported_fonts,
                                imported,
                                Icons.Outlined.FileImport
                            )
                            fontGroup(
                                key = "preinstalled",
                                title = R.string.fonts_preinstalled,
                                icon = Icons.Outlined.FontDownload,
                                entries = filteredFonts.filter {
                                    it != UiFontFamily.System && it !is UiFontFamily.Custom &&
                                            it.key !in settings.favoriteFonts
                                }
                            )
                            if (filtering && filteredFonts.none { it.key in metadata }) {
                                item(key = "empty_filter") {
                                    FontSearchEmptyState(
                                        itemPadding.then(if (animateItems) Modifier.animateItem() else Modifier)
                                    )
                                }
                            }
                            Category.entries.forEach { category ->
                                fontGroup(
                                    key = category.name,
                                    title = category.titleRes,
                                    icon = category.icon,
                                    entries = filteredFonts.filter {
                                        it != UiFontFamily.System && it.key !in settings.favoriteFonts &&
                                                metadata[it.key]?.category == category
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        title = {
            AnimatedContent(targetState = searching) { isSearching ->
                if (isSearching) {
                    BackHandler {
                        search = ""
                        searching = false
                    }
                    RoundedTextField(
                        value = search,
                        onValueChange = { search = it },
                        hint = { Text(stringResource(R.string.search_here)) },
                        maxLines = 1,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        shape = ShapeDefaults.circle,
                        startIcon = {
                            EnhancedIconButton(
                                onClick = {
                                    search = ""
                                    searching = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        },
                        endIcon = {
                            if (search.isNotEmpty()) {
                                EnhancedIconButton(onClick = { search = "" }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Close,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TitleItem(
                            icon = Icons.Rounded.FontFamily,
                            text = stringResource(R.string.font)
                        )
                        Spacer(Modifier.weight(1f))
                        EnhancedIconButton(
                            onClick = { arranging = !arranging },
                            containerColor = if (arranging) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
                            contentColor = if (arranging) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSecondaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FormatLineSpacing,
                                contentDescription = null
                            )
                        }
                        AnimatedVisibility(visible = !arranging) {
                            EnhancedIconButton(
                                onClick = {
                                    if (canFilter) showFilters = true else searching = true
                                },
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                AnimatedContent(
                                    targetState = canFilter,
                                    transitionSpec = { fadeIn() togetherWith fadeOut() }
                                ) { showFilter ->
                                    Box {
                                        Icon(
                                            imageVector = if (showFilter) Icons.Rounded.FilterAlt
                                            else Icons.Outlined.Search,
                                            contentDescription = null
                                        )
                                        BoxAnimatedVisibility(
                                            visible = showFilter && filtering,
                                            modifier = Modifier
                                                .size(6.dp)
                                                .align(Alignment.TopEnd)
                                        ) {
                                            EnhancedBadge()
                                        }
                                    }
                                }
                            }
                        }
                        EnhancedButton(
                            onClick = { if (arranging) arranging = false else onDismiss() },
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            AnimatedContent(targetState = arranging) { isArranging ->
                                AutoSizeText(stringResource(if (isArranging) R.string.confirm else R.string.close))
                            }
                        }
                    }
                }
            }
        }
    )
    if (canFilter) {
        FontFilterSheet(
            visible = visible && showFilters,
            onDismiss = { showFilters = false },
            categories = categories,
            onCategoriesChange = { categories = it },
            scripts = scripts,
            onScriptsChange = { scripts = it },
            favoritesOnly = favoritesOnly,
            onFavoritesOnlyChange = { favoritesOnly = it },
            keyword = search,
            onKeywordChange = { search = it }
        )
    }
}

private class FontGroupCollapse(
    val visibleKeys: Set<Any>,
    val progress: Animatable<Float, AnimationVector1D> = Animatable(1f)
)

private fun LazyListLayoutInfo.pinnedFontGroup() = visibleItemsInfo.firstOrNull {
    it.contentType == "group" && it.offset <= viewportStartOffset
}

@Composable
private fun FontImportExportButtons(
    modifier: Modifier,
    canExport: Boolean,
    onAddFont: (Uri) -> Unit,
    onExportFonts: () -> Unit
) {
    val picker = rememberFilePicker(
        mimeType = MimeType.Font + MimeType.Zip,
        onSuccess = onAddFont
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .container(shape = ShapeDefaults.extraLarge, resultPadding = 8.dp)
            .height(IntrinsicSize.Max)
    ) {
        PreferenceRow(
            title = stringResource(R.string.import_word),
            onClick = picker::pickFile,
            shape = ShapeDefaults.start,
            titleFontStyle = PreferenceItemDefaults.TitleFontStyleCentered,
            startIcon = Icons.Rounded.FileImport,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface
        )
        PreferenceRow(
            title = stringResource(R.string.export),
            onClick = onExportFonts,
            shape = ShapeDefaults.end,
            enabled = canExport,
            startIcon = Icons.Rounded.FileExport,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            color = MaterialTheme.colorScheme.surface,
            titleFontStyle = PreferenceItemDefaults.TitleFontStyleCentered
        )
    }
}

@Composable
private fun FontGroupHeader(
    title: String,
    icon: ImageVector,
    count: Int,
    expanded: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)
    PreferenceItemOverload(
        title = title,
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        startIcon = { Icon(imageVector = icon, contentDescription = null) },
        endIcon = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedBadge(
                    containerColor = takeColorFromScheme {
                        if (selected) secondary else secondaryContainer
                    },
                    contentColor = takeColorFromScheme {
                        if (selected) onSecondary else onSecondaryContainer
                    }
                ) {
                    Text(
                        text = count.toString(),
                        style = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )
                }
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    )
}

@Composable
private fun FontSearchEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .container(
                shape = ShapeDefaults.large,
                resultPadding = 0.dp,
                color = EnhancedBottomSheetDefaults.contentContainerColor
            )
            .height(256.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.nothing_found_by_search),
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp)
        )
        Icon(
            imageVector = Icons.Outlined.SearchOff,
            contentDescription = null,
            modifier = Modifier
                .weight(2f)
                .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                .fillMaxSize()
        )
    }
}
