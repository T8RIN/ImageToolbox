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

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material.icons.rounded.TableChart
import androidx.compose.material.icons.rounded.Update
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.request.ImageRequest
import coil3.request.error
import coil3.request.transformations
import coil3.transform.Transformation
import com.t8rin.imagetoolbox.core.domain.model.FileModel
import com.t8rin.imagetoolbox.core.domain.remote.RemoteResources
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiCubeLutFilter
import com.t8rin.imagetoolbox.core.filters.presentation.model.UiFilter
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.LocalFilterPreviewModelProvider
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.utils.appContext

@Composable
internal fun FilterSelectionCubeLutBottomContent(
    cubeLutRemoteResources: RemoteResources?,
    shape: Shape,
    onShowDownloadDialog: (
        forceUpdate: Boolean,
        downloadOnlyNewData: Boolean
    ) -> Unit,
    onRequestFilterMapping: ((UiFilter<*>) -> Transformation),
    onClick: (UiCubeLutFilter) -> Unit
) {
    cubeLutRemoteResources?.let { resources ->
        val previewModel = LocalFilterPreviewModelProvider.current.preview

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
                .hapticsClickable {
                    if (resources.list.isEmpty()) {
                        onShowDownloadDialog(false, false)
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
                    onShowDownloadDialog(true, false)
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
                        onShowDownloadDialog(true, true)
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
                                shape = ShapeDefaults.circle
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
                        contentPadding = PaddingValues(8.dp),
                        flingBehavior = enhancedFlingBehavior()
                    ) {
                        itemsIndexed(
                            items = data,
                            key = { _, d -> d.name + d.uri }
                        ) { index, (uri, name) ->
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
                                                ImageRequest.Builder(appContext)
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
                                shape = ShapeDefaults.byIndex(
                                    index = index,
                                    size = data.size
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .animateItem()
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
        }
    }
}