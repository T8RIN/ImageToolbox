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

package com.t8rin.imagetoolbox.texture_generation.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.icons.Brick
import com.t8rin.imagetoolbox.core.resources.icons.Build
import com.t8rin.imagetoolbox.core.resources.icons.FilterAlt
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedDropdownMenu
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.withDefaultsFor

@Composable
fun TextureParamsSelection(
    value: TextureParams,
    onValueChange: (TextureParams) -> Unit,
    previewProvider: (TextureParams) -> Transformation,
    favoriteTextureTypes: Set<TextureFilterType>,
    onToggleFavorite: (TextureFilterType) -> Unit
) {
    val empty = remember { createBitmap(1, 1) }

    var category by rememberSaveable { mutableStateOf(TextureCategory.All) }
    var showSearchSheet by rememberSaveable { mutableStateOf(false) }

    val categoryEntries = remember(favoriteTextureTypes) {
        TextureCategory.entries.filterNot {
            it == TextureCategory.Favorites && favoriteTextureTypes.isEmpty()
        }
    }

    LaunchedEffect(category, categoryEntries) {
        if (category !in categoryEntries) category = TextureCategory.All
    }

    val filteredTypes = remember(
        category,
        favoriteTextureTypes
    ) {
        TextureFilterType.entries.filter { type ->
            when (category) {
                TextureCategory.All -> true
                TextureCategory.Favorites -> type in favoriteTextureTypes
                else -> type.category == category
            }
        }
    }

    Column(
        modifier = Modifier.container(
            shape = ShapeDefaults.large,
            resultPadding = 12.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleItem(
            text = stringResource(R.string.params),
            icon = Icons.Rounded.Build,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DataSelector(
                value = value.textureFilterType,
                onValueChange = {
                    onValueChange(value.withDefaultsFor(it))
                },
                entries = filteredTypes,
                title = stringResource(R.string.texture_type),
                titleIcon = Icons.TwoTone.Brick,
                badgeContent = {
                    Text(filteredTypes.size.toString())
                },
                beforeExpandAction = {
                    Box {
                        var showCategoryPicker by remember {
                            mutableStateOf(false)
                        }

                        EnhancedIconButton(
                            onClick = { showCategoryPicker = true },
                            containerColor = if (category != TextureCategory.All) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.secondaryContainer.copy(0.2f)
                            },
                            contentColor = if (category != TextureCategory.All) {
                                MaterialTheme.colorScheme.onSecondary
                            } else {
                                MaterialTheme.colorScheme.onSecondaryContainer
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.FilterAlt,
                                contentDescription = stringResource(R.string.filter)
                            )
                        }

                        EnhancedDropdownMenu(
                            expanded = showCategoryPicker,
                            onDismissRequest = { showCategoryPicker = false },
                            shape = ShapeDefaults.large
                        ) {
                            Column(
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .padding(horizontal = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                categoryEntries.forEachIndexed { index, item ->
                                    EnhancedButton(
                                        modifier = Modifier.fillMaxWidth(),
                                        onClick = {
                                            showCategoryPicker = false
                                            category = item
                                        },
                                        shape = ShapeDefaults.byIndex(index, categoryEntries.size),
                                        containerColor = if (item == category) {
                                            MaterialTheme.colorScheme.secondary
                                        } else {
                                            MaterialTheme.colorScheme.surfaceContainerHigh
                                        }
                                    ) {
                                        Text(stringResource(item.titleRes))
                                    }
                                }
                            }
                        }
                    }
                    EnhancedIconButton(
                        onClick = { showSearchSheet = true },
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.2f),
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = stringResource(R.string.search_here)
                        )
                    }
                },
                key = { it.name },
                itemContentText = { type ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Picture(
                            model = remember(empty, type) {
                                ImageRequest.Builder(appContext)
                                    .data(empty)
                                    .memoryCacheKey(type.name)
                                    .diskCacheKey(type.name)
                                    .size(512, 512)
                                    .transformations(
                                        listOf(
                                            previewProvider(
                                                value.withDefaultsFor(
                                                    type
                                                )
                                            )
                                        )
                                    )
                                    .build()
                            },
                            modifier = Modifier
                                .size(28.dp)
                                .offset(x = (-2).dp),
                            shape = ShapeDefaults.extraSmall
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(stringResource(type.titleRes()))

                        EnhancedIconButton(
                            onClick = { onToggleFavorite(type) },
                            containerColor = Color.Transparent,
                            modifier = Modifier
                                .size(30.dp)
                                .offset(x = 8.dp)
                        ) {
                            AnimatedContent(
                                targetState = type in favoriteTextureTypes
                            ) { isFavorite ->
                                Icon(
                                    imageVector = if (isFavorite) {
                                        Icons.Rounded.BookmarkRemove
                                    } else {
                                        Icons.Outlined.Bookmark
                                    },
                                    contentDescription = stringResource(R.string.favorite),
                                    tint = takeColorFromScheme {
                                        if (isFavorite) {
                                            if (type == value.textureFilterType) onPrimary else primary
                                        } else {
                                            if (type == value.textureFilterType) {
                                                onTertiary.copy(alpha = 0.65f)
                                            } else {
                                                onSurface.copy(alpha = 0.35f)
                                            }
                                        }
                                    },
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    null
                },
                chipHeight = 46.dp,
                minSpanCount = 2,
                spanCount = 4,
                containerColor = MaterialTheme.colorScheme.surface,
                shape = ShapeDefaults.default
            )

            AnimatedContent(
                targetState = value.textureFilterType,
                modifier = Modifier.fillMaxWidth()
            ) { textureFilterType ->
                when (textureFilterType) {
                    TextureFilterType.BrushedMetal -> {
                        BrushedMetalParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.Caustics -> {
                        CausticsParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.Cellular -> {
                        CellularParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.Check -> {
                        CheckParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.FBM -> {
                        FbmParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.Marble -> {
                        MarbleParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.Plasma -> {
                        PlasmaParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.Quilt -> {
                        QuiltParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.Wood -> {
                        WoodParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.OrganicFibers -> {
                        OrganicFibersParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.GmicReactionDiffusion -> {
                        ReactionDiffusionParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    TextureFilterType.Truchet -> {
                        TruchetParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }

                    else -> {
                        FastNoiseParams(
                            value = value,
                            onValueChange = onValueChange
                        )
                    }
                }
            }
        }
    }

    TextureSearchSheet(
        visible = showSearchSheet,
        value = value,
        empty = empty,
        favoriteTextureTypes = favoriteTextureTypes,
        previewProvider = previewProvider,
        onToggleFavorite = onToggleFavorite,
        onDismiss = { showSearchSheet = false },
        onValueChange = {
            onValueChange(value.withDefaultsFor(it))
            showSearchSheet = false
        }
    )
}
