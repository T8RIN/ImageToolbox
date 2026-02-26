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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SignalCellularConnectedNoInternet0Bar
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Slideshow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.transformations
import coil3.toBitmap
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResources
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.ui.theme.StrongBlack
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.LocalFilterPreviewModelProvider
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shimmer
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.ToastDuration
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.utils.appContext
import kotlinx.coroutines.launch

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
    cubeLutDownloadProgress: DownloadProgress? = null,
    onCubeLutDownloadRequest: (forceUpdate: Boolean, downloadOnlyNewData: Boolean) -> Unit = { _, _ -> }
) {
    val essentials = rememberLocalEssentials()
    val previewModel = LocalFilterPreviewModelProvider.current.preview

    var isBitmapDark by remember {
        mutableStateOf(true)
    }
    var loading by remember {
        mutableStateOf(false)
    }

    var showDownloadDialog by rememberSaveable {
        mutableStateOf(false)
    }

    var downloadOnlyNewData by rememberSaveable {
        mutableStateOf(false)
    }

    var forceUpdate by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItemOverload(
        title = stringResource(filter.title),
        startIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(contentAlignment = Alignment.Center) {
                    Picture(
                        model = remember(filter, previewModel) {
                            ImageRequest.Builder(appContext)
                                .data(previewModel.data)
                                .error(R.drawable.filter_preview_source)
                                .transformations(onRequestFilterMapping(filter))
                                .diskCacheKey(filter::class.simpleName + previewModel.data.hashCode())
                                .memoryCacheKey(filter::class.simpleName + previewModel.data.hashCode())
                                .size(300, 300)
                                .build()
                        },
                        contentScale = ContentScale.Crop,
                        contentDescription = null,
                        onLoading = {
                            loading = true
                        },
                        onSuccess = {
                            loading = false
                            essentials.launch {
                                isBitmapDark =
                                    calculateBrightnessEstimate(it.result.image.toBitmap()) < 110
                            }
                        },
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
                                .clip(ShapeDefaults.circle)
                                .hapticsClickable(onClick = onOpenPreview),
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
            FilterSelectionCubeLutBottomContent(
                cubeLutRemoteResources = cubeLutRemoteResources,
                shape = shape,
                onShowDownloadDialog = { forceUpdateP, downloadOnlyNewDataP ->
                    showDownloadDialog = true
                    forceUpdate = forceUpdateP
                    downloadOnlyNewData = downloadOnlyNewDataP
                },
                onRequestFilterMapping = onRequestFilterMapping,
                onClick = onClick
            )
        }
    )

    CubeLutDownloadDialog(
        visible = showDownloadDialog,
        onDismiss = { showDownloadDialog = false },
        onDownload = {
            if (essentials.isNetworkAvailable()) {
                onCubeLutDownloadRequest(
                    forceUpdate, downloadOnlyNewData
                )
                showDownloadDialog = false
            } else {
                essentials.showToast(
                    message = essentials.getString(R.string.no_connection),
                    icon = Icons.Outlined.SignalCellularConnectedNoInternet0Bar,
                    duration = ToastDuration.Long
                )
            }
        },
        downloadOnlyNewData = downloadOnlyNewData,
        cubeLutDownloadProgress = cubeLutDownloadProgress
    )
}