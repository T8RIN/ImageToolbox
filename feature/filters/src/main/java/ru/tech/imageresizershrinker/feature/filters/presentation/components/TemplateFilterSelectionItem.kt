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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.Transformation
import ru.tech.imageresizershrinker.core.filters.domain.model.TemplateFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.UiFilter
import ru.tech.imageresizershrinker.core.filters.presentation.model.toUiFilter
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.modifier.shimmer
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload

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