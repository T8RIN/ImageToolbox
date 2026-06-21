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

import android.content.Context
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.domain.model.ExtraDataType
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Album
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.Extension
import com.t8rin.imagetoolbox.core.resources.icons.File
import com.t8rin.imagetoolbox.core.resources.icons.GifBox
import com.t8rin.imagetoolbox.core.resources.icons.Image
import com.t8rin.imagetoolbox.core.resources.icons.KeyboardArrowDown
import com.t8rin.imagetoolbox.core.resources.icons.LayersSearchOutline
import com.t8rin.imagetoolbox.core.resources.icons.Pdf
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.resources.icons.SettingsBackupRestore
import com.t8rin.imagetoolbox.core.resources.icons.TextFields
import com.t8rin.imagetoolbox.core.resources.icons.Visibility
import com.t8rin.imagetoolbox.core.resources.icons.VisibilityOff
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.utils.navigation.matchesSearchQuery
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.UrisCarousel
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.shapeByInteraction
import com.t8rin.imagetoolbox.core.ui.widget.preferences.ScreenPreference
import com.t8rin.imagetoolbox.core.ui.widget.sheets.ProcessImagesSheetTitle.Companion.processImagesSheetTitle
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.core.ui.widget.utils.screenList
import com.t8rin.imagetoolbox.core.utils.appContext

@Composable
fun ProcessImagesPreferenceSheet(
    uris: List<Uri>,
    extraDataType: ExtraDataType? = null,
    visible: Boolean,
    onDismiss: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    var isSearching by rememberSaveable {
        mutableStateOf(false)
    }
    var searchKeyword by rememberSaveable(isSearching) {
        mutableStateOf("")
    }

    val (rawScreenList, hiddenScreenList) = uris.screenList(extraDataType).value
    val sheetTitle = remember(extraDataType, uris.size) {
        appContext.processImagesSheetTitle(
            extraDataType = extraDataType,
            uriCount = uris.size
        )
    }

    val urisCorrespondingScreens by remember(hiddenScreenList, rawScreenList, searchKeyword) {
        derivedStateOf {
            if (searchKeyword.isNotBlank()) {
                (rawScreenList + hiddenScreenList).filter {
                    it.matchesSearchQuery(searchKeyword)
                }
            } else {
                rawScreenList
            }
        }
    }

    EnhancedModalBottomSheet(
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
                                        imageVector = Icons.Rounded.ArrowBack,
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
                            text = sheetTitle.title,
                            icon = sheetTitle.icon
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        EnhancedIconButton(
                            onClick = { isSearching = true },
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LayersSearchOutline,
                                contentDescription = stringResource(R.string.search_here)
                            )
                        }
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = onDismiss
                        ) {
                            AutoSizeText(stringResource(R.string.close))
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        },
        sheetContent = {
            AnimatedContent(
                targetState = urisCorrespondingScreens.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) { isNotEmpty ->
                if (isNotEmpty) {
                    var showHidden by rememberSaveable {
                        mutableStateOf(false)
                    }

                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(250.dp),
                        contentPadding = PaddingValues(16.dp),
                        verticalItemSpacing = 8.dp,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        flingBehavior = enhancedFlingBehavior()
                    ) {
                        if (extraDataType == null || extraDataType == ExtraDataType.Gif || extraDataType == ExtraDataType.Pdf) {
                            item(
                                span = StaggeredGridItemSpan.FullLine
                            ) {
                                UrisCarousel(uris)
                            }
                        }
                        items(
                            items = urisCorrespondingScreens,
                            key = { it.toString() }
                        ) { screen ->
                            ScreenPreference(
                                screen = screen,
                                navigate = {
                                    onNavigate(screen)
                                    onDismiss()
                                }
                            )
                        }

                        if (hiddenScreenList.isNotEmpty() && searchKeyword.isBlank()) {
                            item(
                                span = StaggeredGridItemSpan.FullLine
                            ) {
                                val interactionSource = remember { MutableInteractionSource() }

                                TitleItem(
                                    modifier = Modifier
                                        .padding(
                                            vertical = animateDpAsState(
                                                if (showHidden) 8.dp
                                                else 0.dp
                                            ).value
                                        )
                                        .container(
                                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
                                            resultPadding = 0.dp,
                                            shape = shapeByInteraction(
                                                shape = ShapeDefaults.default,
                                                pressedShape = ShapeDefaults.pressed,
                                                interactionSource = interactionSource
                                            )
                                        )
                                        .hapticsClickable(
                                            interactionSource = interactionSource,
                                            indication = LocalIndication.current
                                        ) {
                                            showHidden = !showHidden
                                        }
                                        .padding(16.dp),
                                    text = stringResource(R.string.hidden_tools),
                                    icon = if (showHidden) {
                                        Icons.Rounded.Visibility
                                    } else {
                                        Icons.Rounded.VisibilityOff
                                    },
                                    endContent = {
                                        Icon(
                                            imageVector = Icons.Rounded.KeyboardArrowDown,
                                            contentDescription = null,
                                            modifier = Modifier.rotate(
                                                animateFloatAsState(
                                                    if (showHidden) 180f
                                                    else 0f
                                                ).value
                                            )
                                        )
                                    }
                                )
                            }

                            if (showHidden) {
                                items(
                                    items = hiddenScreenList,
                                    key = { it.toString() }
                                ) { screen ->
                                    ScreenPreference(
                                        screen = screen,
                                        navigate = {
                                            onNavigate(screen)
                                            onDismiss()
                                        }
                                    )
                                }
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
                            imageVector = Icons.Outlined.SearchOff,
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
        },
        confirmButton = {},
        enableBottomContentWeight = false,
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        }
    )
}

private data class ProcessImagesSheetTitle(
    val title: String,
    val icon: ImageVector
) {
    companion object {
        fun Context.processImagesSheetTitle(
            extraDataType: ExtraDataType?,
            uriCount: Int
        ): ProcessImagesSheetTitle = when (extraDataType) {
            null -> if (uriCount > 1) {
                ProcessImagesSheetTitle(
                    title = getString(R.string.images, uriCount),
                    icon = Icons.Rounded.Image
                )
            } else {
                ProcessImagesSheetTitle(
                    title = getString(R.string.image),
                    icon = Icons.Rounded.Image
                )
            }

            ExtraDataType.Audio -> ProcessImagesSheetTitle(
                title = getString(R.string.audio),
                icon = Icons.Rounded.Album
            )

            is ExtraDataType.Backup -> ProcessImagesSheetTitle(
                title = getString(R.string.backup),
                icon = Icons.Rounded.SettingsBackupRestore
            )

            ExtraDataType.File -> ProcessImagesSheetTitle(
                title = getString(R.string.file),
                icon = Icons.Rounded.File
            )

            ExtraDataType.Gif -> ProcessImagesSheetTitle(
                title = "GIF",
                icon = Icons.Rounded.GifBox
            )

            ExtraDataType.Pdf -> ProcessImagesSheetTitle(
                title = "PDF",
                icon = Icons.Rounded.Pdf
            )

            is ExtraDataType.Template -> ProcessImagesSheetTitle(
                title = getString(R.string.template),
                icon = Icons.Rounded.Extension
            )

            is ExtraDataType.Text -> ProcessImagesSheetTitle(
                title = getString(R.string.text),
                icon = Icons.Rounded.TextFields
            )
        }
    }
}

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true) {
    ProcessImagesPreferenceSheet(
        uris = listOf("fff".toUri()),
        visible = true,
        extraDataType = null,
        onDismiss = {},
        onNavigate = {}
    )
}