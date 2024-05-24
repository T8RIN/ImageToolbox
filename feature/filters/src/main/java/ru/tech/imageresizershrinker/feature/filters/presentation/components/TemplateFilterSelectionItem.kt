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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.toUiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.utils.LocalFavoriteFiltersInteractor
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
internal fun TemplateFilterSelectionItem(
    templateFilter: TemplateFilter<Bitmap>,
    onClick: () -> Unit,
    onRequestFilterMapping: ((UiFilter<*>) -> Transformation)?,
    shape: Shape,
    modifier: Modifier
) {
    val settingsState = LocalSettingsState.current

    val context = LocalContext.current
    val model = remember(templateFilter) {
        if (onRequestFilterMapping != null) {
            ImageRequest.Builder(context)
                .data(R.drawable.filter_preview_source)
                .error(R.drawable.filter_preview_source)
                .transformations(templateFilter.filters.map { onRequestFilterMapping(it.toUiFilter()) })
                .diskCacheKey(templateFilter.toString())
                .memoryCacheKey(templateFilter.toString())
                .crossfade(true)
                .size(300, 300)
                .build()
        } else null
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
        }
    )

    var showFilterTemplateInfoSheet by rememberSaveable {
        mutableStateOf(false)
    }

    PreferenceItemOverload(
        title = templateFilter.name,
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
                    showFilterTemplateInfoSheet = true
                },
                modifier = Modifier.offset(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        shape = shape,
        onClick = onClick,
        drawStartIconContainer = false
    )

    FilterTemplateInfoSheet(
        visible = showFilterTemplateInfoSheet,
        onDismiss = { showFilterTemplateInfoSheet = it },
        templateFilter = templateFilter,
        onRequestFilterMapping = onRequestFilterMapping
    )
}

@Composable
internal fun FilterTemplateInfoSheet(
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    templateFilter: TemplateFilter<Bitmap>,
    onRequestFilterMapping: ((UiFilter<*>) -> Transformation)?
) {
    SimpleSheet(
        visible = visible,
        onDismiss = onDismiss,
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    onDismiss(false)
                }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.template_filter),
                icon = Icons.Outlined.Extension
            )
        }
    ) {
        val context = LocalContext.current
        val model = remember(templateFilter) {
            if (onRequestFilterMapping != null) {
                ImageRequest.Builder(context)
                    .data(R.drawable.filter_preview_source)
                    .error(R.drawable.filter_preview_source)
                    .transformations(templateFilter.filters.map { onRequestFilterMapping(it.toUiFilter()) })
                    .diskCacheKey(templateFilter.toString())
                    .memoryCacheKey(templateFilter.toString())
                    .crossfade(true)
                    .size(300, 300)
                    .build()
            } else null
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
            }
        )
        var filterContent by rememberSaveable(templateFilter) {
            mutableStateOf("")
        }
        val interactor = LocalFavoriteFiltersInteractor.current
        LaunchedEffect(filterContent) {
            filterContent = interactor.convertTemplateFilterToString(templateFilter)
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                if (onRequestFilterMapping != null) {
                    Spacer(modifier = Modifier.height(36.dp))
                }
                BoxWithConstraints(
                    modifier = Modifier.then(
                        if (onRequestFilterMapping != null) {
                            Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        } else Modifier
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        QrCode(
                            content = filterContent,
                            modifier = Modifier
                                .then(
                                    if (onRequestFilterMapping != null) {
                                        Modifier.padding(top = 36.dp, bottom = 16.dp)
                                    } else Modifier
                                )
                                .size(
                                    min(
                                        min(
                                            this@BoxWithConstraints.maxWidth,
                                            this@BoxWithConstraints.maxHeight
                                        ), 300.dp
                                    )
                                )
                        )

                        Text(
                            text = templateFilter.name,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    if (onRequestFilterMapping != null) {
                        Image(
                            painter = painter,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-48).dp)
                                .size(64.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .transparencyChecker()
                                .shimmer(loading)
                        )
                    }
                }
            }
        }
    }
}
