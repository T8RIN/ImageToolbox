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

package com.t8rin.imagetoolbox.feature.media_picker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.AddPhotoAlt
import com.t8rin.imagetoolbox.core.resources.icons.Delete
import com.t8rin.imagetoolbox.core.resources.icons.Preview
import com.t8rin.imagetoolbox.core.settings.domain.model.CustomSemanticSearchCategory
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.media_picker.domain.model.SemanticSearchCategoryResult
import com.t8rin.imagetoolbox.feature.media_picker.presentation.screenLogic.MediaPickerComponent
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive

@Composable
internal fun SemanticSearchCategoryReferences(
    referenceImages: List<String>,
    onAdd: () -> Unit,
    onRemove: (String) -> Unit,
    enabled: Boolean = true
) {
    PreferenceItem(
        enabled = enabled,
        title = stringResource(R.string.gallery_search_category_references),
        subtitle = stringResource(R.string.gallery_search_category_references_description),
        startIcon = Icons.Outlined.AddPhotoAlt,
        onClick = onAdd,
        shape = ShapeDefaults.center,
        modifier = Modifier.fillMaxWidth(),
        bottomContent = if (referenceImages.isNotEmpty()) {
            {
                LazyRow(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(referenceImages, key = { it }) { uri ->
                        Box {
                            Picture(
                                model = uri,
                                modifier = Modifier.size(72.dp),
                                shape = ShapeDefaults.small,
                                showTransparencyChecker = false,
                                size = 256
                            )
                            EnhancedIconButton(
                                enabled = enabled,
                                onClick = { onRemove(uri) },
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = stringResource(
                                        R.string.gallery_search_category_remove_reference
                                    ),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else null
    )
}

@Composable
internal fun SemanticSearchCategoryPreview(
    visible: Boolean,
    categoryId: String,
    category: CustomSemanticSearchCategory?,
    threshold: Float,
    component: MediaPickerComponent
) {
    if (!visible) return

    val mediaState by component.semanticSearchPreviewMedia.collectAsStateWithLifecycle()
    val state by component.semanticSearchState.collectAsStateWithLifecycle()
    val enabled by component.isSemanticSearchEnabled.collectAsStateWithLifecycle()
    val hasQuery = category == null || category.description.isNotBlank() ||
            category.referenceImages.isNotEmpty()
    val ready = enabled && state.modelsReady && state.indexedImages > 0
    val indexRevision = if (state.isIndexing) state.processedImages / 256 else state.indexedImages
    val query = category?.copy(name = "")
    var result by remember(categoryId) { mutableStateOf<SemanticSearchCategoryResult?>(null) }
    var loading by remember(categoryId) { mutableStateOf(false) }

    LaunchedEffect(
        categoryId,
        query,
        threshold,
        ready,
        mediaState,
        indexRevision,
        state.isIndexing
    ) {
        result = null
        loading = false
        if (!ready || !hasQuery || mediaState.isLoading) return@LaunchedEffect

        loading = true
        try {
            delay(300)
            val preview = component.previewSemanticSearchCategory(
                categoryId = categoryId,
                category = query,
                threshold = threshold
            )
            currentCoroutineContext().ensureActive()
            result = preview
        } finally {
            if (currentCoroutineContext().isActive) loading = false
        }
    }

    val preview = result
    val firstPhoto = preview?.media?.firstOrNull()
    val isLoading = loading || (ready && hasQuery && mediaState.isLoading)
    TitleItem(
        text = stringResource(R.string.gallery_search_category_preview),
        subtitle = when {
            !hasQuery -> stringResource(R.string.gallery_search_category_preview_add_query)
            !ready -> stringResource(R.string.gallery_search_category_preview_prepare)
            isLoading -> stringResource(R.string.gallery_search_searching)
            preview == null -> stringResource(R.string.gallery_search_category_preview_unavailable)
            firstPhoto == null -> stringResource(
                R.string.gallery_search_category_preview_count,
                0
            ) +
                    "\n" + stringResource(R.string.gallery_search_category_preview_no_matches)

            else -> stringResource(
                R.string.gallery_search_category_preview_count,
                preview.media.size
            )
        },
        icon = Icons.Outlined.Preview,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .container(resultPadding = 16.dp),
        endContent = if (isLoading || firstPhoto != null) {
            {
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        EnhancedCircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 3.dp
                        )
                    } else {
                        Picture(
                            model = firstPhoto?.uri,
                            modifier = Modifier.size(64.dp),
                            shape = ShapeDefaults.small,
                            showTransparencyChecker = false,
                            size = 256
                        )
                    }
                }
            }
        } else null
    )
}