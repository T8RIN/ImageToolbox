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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.transformations
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.filters.domain.model.TemplateFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.toUiFilter
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.LocalFilterPreviewModelProvider
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.utils.appContext

@Composable
internal fun TemplateFilterSelectionItem(
    templateFilter: TemplateFilter,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRequestFilterMapping: (UiFilter<*>) -> Transformation,
    onInfoClick: () -> Unit,
    showPreviewImage: Boolean,
    shape: Shape,
    modifier: Modifier
) {
    val previewModel = LocalFilterPreviewModelProvider.current.preview

    PreferenceItemOverload(
        title = templateFilter.name,
        startIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterPreviewPicture(
                    model = remember(templateFilter, previewModel) {
                        ImageRequest.Builder(appContext)
                            .data(previewModel.data)
                            .error(R.drawable.filter_preview_source)
                            .transformations(
                                templateFilter.filters.map {
                                    onRequestFilterMapping(
                                        it.toUiFilter()
                                    )
                                }
                            )
                            .diskCacheKey(templateFilter.toString() + previewModel.data.hashCode())
                            .memoryCacheKey(templateFilter.toString() + previewModel.data.hashCode())
                            .size(160, 160)
                            .build()
                    },
                    canShowImage = showPreviewImage,
                    canOpenPreview = true,
                    onOpenPreview = onLongClick
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
        endIcon = {
            EnhancedIconButton(
                onClick = onInfoClick,
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
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
}
