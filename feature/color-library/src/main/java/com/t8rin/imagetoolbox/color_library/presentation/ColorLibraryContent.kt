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

package com.t8rin.imagetoolbox.color_library.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smarttoolfactory.colordetector.util.ColorUtil
import com.t8rin.imagetoolbox.color_library.presentation.components.ColorWithNameItem
import com.t8rin.imagetoolbox.color_library.presentation.screenLogic.ColorLibraryComponent
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.provider.rememberLocalEssentials
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.isKeyboardVisibleAsState
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee

@Composable
fun ColorLibraryContent(
    component: ColorLibraryComponent
) {
    val isKeyboardVisible by isKeyboardVisibleAsState()
    val essentials = rememberLocalEssentials()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val colors = component.colors
    val searchKeyword = component.searchKeyword
    val favoriteColors = component.favoriteColors
    val settingsState = LocalSettingsState.current

    val copyColor: (Color) -> Unit = { color ->
        essentials.copyToClipboard(
            text = if (color.alpha == 1f) {
                ColorUtil.colorToHex(color)
            } else {
                ColorUtil.colorToHexAlpha(color)
            }.uppercase(),
            message = R.string.color_copied
        )
    }

    val focus = LocalFocusManager.current

    Scaffold(
        modifier = Modifier
            .clearFocusOnTap()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            EnhancedTopAppBar(
                type = EnhancedTopAppBarType.Large,
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.color_library),
                        modifier = Modifier.marquee()
                    )
                },
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = component.onGoBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.exit)
                        )
                    }
                },
                actions = {
                    TopAppBarEmoji()
                }
            )
        },
        bottomBar = {
            var isSearching by rememberSaveable {
                mutableStateOf(false)
            }
            val insets = WindowInsets.navigationBars.union(
                WindowInsets.displayCutout.only(
                    WindowInsetsSides.Horizontal
                )
            )

            AnimatedContent(
                targetState = isSearching,
                modifier = Modifier.fillMaxWidth()
            ) { isSearch ->
                if (isSearch) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawHorizontalStroke(true)
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer
                            )
                            .pointerInput(Unit) { detectTapGestures { } }
                            .windowInsetsPadding(insets)
                            .padding(16.dp)
                    ) {
                        ProvideTextStyle(value = MaterialTheme.typography.bodyLarge) {
                            RoundedTextField(
                                maxLines = 1,
                                hint = {
                                    Text(stringResource(id = R.string.search_here))
                                },
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Search,
                                    autoCorrectEnabled = null
                                ),
                                value = searchKeyword,
                                onValueChange = component::updateSearch,
                                endIcon = {
                                    EnhancedIconButton(
                                        onClick = {
                                            if (searchKeyword.isNotBlank()) {
                                                component.clearSearch()
                                            } else {
                                                isSearching = false
                                                focus.clearFocus()
                                            }
                                        },
                                        modifier = Modifier.padding(end = 4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.Close,
                                            contentDescription = stringResource(R.string.close),
                                            tint = MaterialTheme.colorScheme.onSurface.copy(
                                                if (it) 1f else 0.5f
                                            )
                                        )
                                    }
                                },
                                shape = ShapeDefaults.circle
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .windowInsetsPadding(insets)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        EnhancedFloatingActionButton(
                            onClick = { isSearching = true },
                            modifier = Modifier.align(
                                settingsState.fabAlignment.takeIf { it != Alignment.BottomCenter }
                                    ?: Alignment.BottomEnd
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = null
                            )
                        }
                    }
                }
            }
        }
    ) { contentPadding ->
        AnimatedContent(
            targetState = colors.isNotEmpty(),
            modifier = Modifier.fillMaxSize()
        ) { isNotEmpty ->
            if (isNotEmpty) {
                val reverseLayout = searchKeyword.isNotEmpty() && isKeyboardVisible

                LazyVerticalGrid(
                    contentPadding = contentPadding + PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(
                        space = 4.dp,
                        alignment = if (reverseLayout) {
                            Alignment.Bottom
                        } else {
                            Alignment.Top
                        }
                    ),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    flingBehavior = enhancedFlingBehavior(),
                    columns = GridCells.Adaptive(150.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = colors,
                        key = { it.toString() }
                    ) { colorWithName ->
                        ColorWithNameItem(
                            isFavorite = colorWithName.name in favoriteColors,
                            colorWithName = colorWithName,
                            onToggleFavorite = { component.toggleFavoriteColor(colorWithName) },
                            onCopy = { copyColor(colorWithName.color) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .padding(contentPadding)
                        .fillMaxSize(),
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