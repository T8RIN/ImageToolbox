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

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Bookmark
import com.t8rin.imagetoolbox.core.resources.icons.BookmarkRemove
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import com.t8rin.imagetoolbox.texture_generation.domain.model.withDefaultsFor

@Composable
internal fun TextureSearchSheet(
    visible: Boolean,
    value: TextureParams,
    empty: Bitmap,
    favoriteTextureTypes: Set<TextureFilterType>,
    previewProvider: (TextureParams) -> Transformation,
    onToggleFavorite: (TextureFilterType) -> Unit,
    onDismiss: () -> Unit,
    onValueChange: (TextureFilterType) -> Unit
) {
    var searchQuery by rememberSaveable(visible) { mutableStateOf("") }
    val textureNames = TextureFilterType.entries.associateWith {
        stringResource(it.titleRes())
    }
    val filteredTypes = remember(searchQuery, textureNames) {
        TextureFilterType.entries.filter { type ->
            textureNames.getValue(type).contains(
                other = searchQuery.trim(),
                ignoreCase = true
            )
        }
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { if (!it) onDismiss() },
        confirmButton = {},
        enableBottomContentWeight = false,
        title = {
            ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                RoundedTextField(
                    maxLines = 1,
                    hint = { Text(stringResource(R.string.search_here)) },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search,
                        autoCorrectEnabled = null
                    ),
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    startIcon = {
                        EnhancedIconButton(
                            onClick = onDismiss,
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBack,
                                contentDescription = stringResource(R.string.close),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    endIcon = {
                        AnimatedVisibility(
                            visible = searchQuery.isNotEmpty(),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            EnhancedIconButton(
                                onClick = { searchQuery = "" },
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
                    shape = ShapeDefaults.circle
                )
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(16.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            itemsIndexed(
                items = filteredTypes,
                key = { _, type -> type.name }
            ) { index, type ->
                PreferenceItemOverload(
                    title = textureNames.getValue(type),
                    onClick = { onValueChange(type) },
                    drawStartIconContainer = false,
                    startIcon = {
                        Picture(
                            model = remember(empty, type) {
                                ImageRequest.Builder(appContext)
                                    .data(empty)
                                    .memoryCacheKey(type.name)
                                    .diskCacheKey(type.name)
                                    .size(512, 512)
                                    .transformations(
                                        listOf(previewProvider(value.withDefaultsFor(type)))
                                    )
                                    .build()
                            },
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .size(48.dp)
                                .scale(1.2f)
                        )
                    },
                    endIcon = {
                        EnhancedIconButton(
                            onClick = { onToggleFavorite(type) },
                            modifier = Modifier.offset(8.dp)
                        ) {
                            val isFavorite = type in favoriteTextureTypes
                            Icon(
                                imageVector = if (isFavorite) {
                                    Icons.Rounded.BookmarkRemove
                                } else {
                                    Icons.Outlined.Bookmark
                                },
                                contentDescription = stringResource(R.string.favorite),
                                tint = takeColorFromScheme {
                                    if (isFavorite) {
                                        if (type == value.textureFilterType) onPrimaryContainer else primary
                                    } else {
                                        if (type == value.textureFilterType) {
                                            onTertiaryContainer.copy(alpha = 0.65f)
                                        } else {
                                            onSurface.copy(alpha = 0.35f)
                                        }
                                    }
                                },
                            )
                        }
                    },
                    shape = ShapeDefaults.byIndex(index, filteredTypes.size),
                    containerColor = if (type == value.textureFilterType) {
                        MaterialTheme.colorScheme.tertiaryContainer
                    } else {
                        Color.Unspecified
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}