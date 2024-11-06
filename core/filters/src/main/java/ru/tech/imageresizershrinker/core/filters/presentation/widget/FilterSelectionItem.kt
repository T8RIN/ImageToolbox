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

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.domain.model.FileModel
import ru.tech.imageresizershrinker.core.domain.model.ImageModel
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResources
import ru.tech.imageresizershrinker.core.domain.remote.RemoteResourcesDownloadProgress
import ru.tech.imageresizershrinker.core.domain.utils.readableByteCount
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiCubeLutFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.icons.BookmarkRemove
import ru.tech.imageresizershrinker.core.ui.theme.StrongBlack
import ru.tech.imageresizershrinker.core.ui.theme.White
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isNetworkAvailable
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.toBitmap
import ru.tech.imageresizershrinker.core.ui.utils.helper.toImageModel
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.dialogs.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.core.ui.widget.other.LocalToastHostState
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastDuration
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.sheets.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
internal fun FilterSelectionItem(
    filter: UiFilter<*>,
    isFavoritePage: Boolean,
    canOpenPreview: Boolean,
    favoriteFilters: List<UiFilter<*>>,
    onLongClick: (() -> Unit)?,
    onOpenPreview: () -> Unit,
    onClick: (UiFilter<*>?) -> Unit,
    onToggleFavorite: () -> Unit,
    onRequestFilterMapping: ((UiFilter<*>) -> Transformation),
    shape: Shape,
    modifier: Modifier,
    cubeLutRemoteResources: RemoteResources? = null,
    cubeLutDownloadProgress: RemoteResourcesDownloadProgress? = null,
    onCubeLutDownloadRequest: (forceUpdate: Boolean, downloadOnlyNewData: Boolean) -> Unit = { _, _ -> },
    previewModel: ImageModel = remember { R.drawable.filter_preview_source.toImageModel() }
) {
    val haptics = LocalHapticFeedback.current

    val toastHostState = LocalToastHostState.current
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val model = remember(filter, previewModel) {
        ImageRequest.Builder(context)
            .data(previewModel.data)
            .error(R.drawable.filter_preview_source)
            .transformations(onRequestFilterMapping(filter))
            .diskCacheKey(filter::class.simpleName + previewModel.data.hashCode())
            .memoryCacheKey(filter::class.simpleName + previewModel.data.hashCode())
            .size(300, 300)
            .build()
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

    var showDownloadDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var downloadOnlyNewData by rememberSaveable {
        mutableStateOf(false)
    }

    var forceUpdate by rememberSaveable {
        mutableStateOf(false)
    }

    Column {
        PreferenceItemOverload(
            title = stringResource(filter.title),
            startIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
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
                        if (canOpenPreview) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        haptics.performHapticFeedback(
                                            HapticFeedbackType.LongPress
                                        )
                                        onOpenPreview()
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
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant())
                    )
                }
            },
            endIcon = {
                EnhancedIconButton(
                    onClick = onToggleFavorite,
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
            onClick = { onClick(null) },
            drawStartIconContainer = false,
            bottomContent = {
                cubeLutRemoteResources?.let { resources ->
                    var showSelection by rememberSaveable {
                        mutableStateOf(false)
                    }
                    Row(
                        modifier = Modifier
                            .padding(
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 8.dp
                            )
                            .container(
                                color = MaterialTheme.colorScheme.surface,
                                resultPadding = 0.dp,
                                shape = shape
                            )
                            .clickable {
                                haptics.performHapticFeedback(
                                    HapticFeedbackType.LongPress
                                )
                                if (resources.list.isEmpty()) {
                                    showDownloadDialog = true
                                } else {
                                    showSelection = true
                                }
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TitleItem(
                            text = stringResource(R.string.lut_library),
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        EnhancedIconButton(
                            onClick = {
                                showDownloadDialog = true
                                forceUpdate = true
                                downloadOnlyNewData = false
                            },
                            containerColor = if (resources.list.isEmpty()) {
                                MaterialTheme.colorScheme.secondary
                            } else Color.Transparent
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Download,
                                contentDescription = null
                            )
                        }
                        if (resources.list.isNotEmpty()) {
                            EnhancedIconButton(
                                onClick = {
                                    showDownloadDialog = true
                                    forceUpdate = true
                                    downloadOnlyNewData = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Update,
                                    contentDescription = null
                                )
                            }
                            EnhancedIconButton(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                onClick = {
                                    showSelection = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Visibility,
                                    contentDescription = "View"
                                )
                            }
                        }
                    }

                    var isSearching by rememberSaveable {
                        mutableStateOf(false)
                    }
                    var searchKeyword by rememberSaveable(isSearching) {
                        mutableStateOf("")
                    }
                    EnhancedModalBottomSheet(
                        visible = showSelection,
                        onDismiss = { showSelection = it },
                        confirmButton = {},
                        enableBottomContentWeight = false,
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
                                            text = stringResource(R.string.lut_library),
                                            icon = Icons.Rounded.TableChart
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
                                            onClick = { showSelection = false }
                                        ) {
                                            AutoSizeText(stringResource(R.string.close))
                                        }
                                        Spacer(Modifier.width(8.dp))
                                    }
                                }
                            }
                        }
                    ) {
                        val data by remember(resources.list, searchKeyword) {
                            derivedStateOf {
                                if (searchKeyword.isEmpty()) resources.list
                                else resources.list.filter {
                                    it.name.trim()
                                        .contains(searchKeyword.trim(), true)
                                }
                            }
                        }
                        AnimatedContent(data.isNotEmpty()) { haveData ->
                            if (haveData) {
                                LazyColumn(
                                    verticalArrangement = Arrangement.spacedBy(4.dp),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    data.forEachIndexed { index, (uri, name) ->
                                        item {
                                            PreferenceItemOverload(
                                                title = remember(name) {
                                                    name.removeSuffix(".cube")
                                                        .removeSuffix("_LUT")
                                                        .replace("_", " ")
                                                },
                                                drawStartIconContainer = false,
                                                startIcon = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Picture(
                                                            model = remember(uri, previewModel) {
                                                                ImageRequest.Builder(context)
                                                                    .data(previewModel.data)
                                                                    .error(R.drawable.filter_preview_source)
                                                                    .transformations(
                                                                        onRequestFilterMapping(
                                                                            UiCubeLutFilter(
                                                                                1f to FileModel(uri)
                                                                            )
                                                                        )
                                                                    )
                                                                    .diskCacheKey(uri + previewModel.data.hashCode())
                                                                    .memoryCacheKey(uri + previewModel.data.hashCode())
                                                                    .size(300, 300)
                                                                    .build()
                                                            },
                                                            shape = MaterialTheme.shapes.medium,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier
                                                                .size(48.dp)
                                                                .scale(1.2f)
                                                        )
                                                        Spacer(Modifier.width(16.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .height(36.dp)
                                                                .width(1.dp)
                                                                .background(MaterialTheme.colorScheme.outlineVariant())
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    showSelection = false
                                                    onClick(
                                                        UiCubeLutFilter(1f to FileModel(uri))
                                                    )
                                                },
                                                shape = ContainerShapeDefaults.shapeForIndex(
                                                    index = index,
                                                    size = data.size
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .animateItem()
                                            )
                                        }
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
                    }
                }
            }
        )
    }

    EnhancedAlertDialog(
        visible = showDownloadDialog,
        icon = {
            Icon(
                imageVector = Icons.Outlined.TableChart,
                contentDescription = null
            )
        },
        title = { Text(stringResource(id = R.string.cube_lut)) },
        text = {
            Text(
                stringResource(
                    if (downloadOnlyNewData) R.string.lut_library_update_sub
                    else R.string.lut_library_sub
                )
            )
        },
        onDismissRequest = {},
        confirmButton = {
            EnhancedButton(
                onClick = {
                    if (context.isNetworkAvailable()) {
                        onCubeLutDownloadRequest(
                            forceUpdate, downloadOnlyNewData
                        )
                        showDownloadDialog = false
                    } else {
                        scope.launch {
                            toastHostState.showToast(
                                message = context.getString(R.string.no_connection),
                                icon = Icons.Outlined.SignalCellularConnectedNoInternet0Bar,
                                duration = ToastDuration.Long
                            )
                        }
                    }
                }
            ) {
                Text(stringResource(R.string.download))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showDownloadDialog = false
                }
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )

    if (cubeLutDownloadProgress != null) {
        BasicAlertDialog(onDismissRequest = {}) {
            Box(
                Modifier.fillMaxSize()
            ) {
                Loading(
                    progress = cubeLutDownloadProgress.currentPercent / 100,
                    loaderSize = 64.dp
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                    ) {
                        Text(
                            text = cubeLutDownloadProgress.run { "$itemsDownloaded/$itemsCount" },
                            maxLines = 1,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            fontSize = 12.sp,
                            lineHeight = 12.sp
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = readableByteCount(cubeLutDownloadProgress.currentTotalSize),
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            fontSize = 10.sp,
                            lineHeight = 10.sp
                        )
                    }
                }
            }
        }
    }
}