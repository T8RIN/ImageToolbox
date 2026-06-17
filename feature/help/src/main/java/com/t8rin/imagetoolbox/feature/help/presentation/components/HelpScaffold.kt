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

package com.t8rin.imagetoolbox.feature.help.presentation.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee

@Composable
internal fun HelpScaffold(
    title: String,
    onGoBack: () -> Unit,
    isSearching: Boolean = false,
    searchKeyword: String = "",
    onSearchingChange: ((Boolean) -> Unit)? = null,
    onSearchKeywordChange: (String) -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val focus = LocalFocusManager.current
    val settingsState = LocalSettingsState.current

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            EnhancedTopAppBar(
                type = EnhancedTopAppBarType.Large,
                title = {
                    Text(
                        text = title,
                        modifier = Modifier.marquee()
                    )
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = onGoBack,
                        containerColor = Color.Transparent
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
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
            if (onSearchingChange != null) {
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
                                    onValueChange = onSearchKeywordChange,
                                    endIcon = {
                                        EnhancedIconButton(
                                            onClick = {
                                                if (searchKeyword.isNotBlank()) {
                                                    onSearchKeywordChange("")
                                                } else {
                                                    onSearchingChange(false)
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
                                onClick = { onSearchingChange(true) },
                                modifier = Modifier.align(
                                    settingsState.fabAlignment.takeIf { it != Alignment.BottomCenter }
                                        ?: Alignment.BottomEnd
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { contentPadding ->
        content(contentPadding + PaddingValues(16.dp))
    }
}

@Composable
internal fun HelpSearchEmptyContent(
    contentPadding: PaddingValues
) {
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