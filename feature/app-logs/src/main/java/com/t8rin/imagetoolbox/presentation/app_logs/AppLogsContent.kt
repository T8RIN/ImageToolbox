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

package com.t8rin.imagetoolbox.presentation.app_logs

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.t8rin.imagetoolbox.core.resources.icons.Share
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.utils.animation.springySpec
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCancellableCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButtonType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.modifier.AutoCircleShape
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.presentation.app_logs.components.LogLineItem
import com.t8rin.imagetoolbox.presentation.app_logs.screenLogic.AppLogsComponent
import kotlinx.coroutines.delay
import my.nanihadesuka.compose.InternalLazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
fun AppLogsContent(
    component: AppLogsComponent
) {
    val lineCount = component.linesCount
    val searchQuery = component.searchQuery
    val isSendingLogs = component.isSendingLogs
    var showSearch by rememberSaveable { mutableStateOf(false) }

    BackHandler(enabled = showSearch) {
        component.updateSearchQuery("")
        showSearch = false
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clearFocusOnTap(),
        topBar = {
            EnhancedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_logs),
                        modifier = Modifier.marquee()
                    )
                },
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = {
                            if (showSearch) {
                                component.updateSearchQuery("")
                                showSearch = false
                            } else {
                                component.onGoBack()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    TopAppBarEmoji()
                }
            )
        },
        bottomBar = {
            val insets = WindowInsets.navigationBars.union(
                WindowInsets.displayCutout.only(
                    WindowInsetsSides.Horizontal
                )
            )

            AnimatedContent(
                targetState = showSearch,
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
                        val focus = LocalFocusManager.current

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
                                value = searchQuery,
                                onValueChange = component::updateSearchQuery,
                                endIcon = {
                                    AnimatedVisibility(component.isSearchLoading) {
                                        EnhancedCancellableCircularProgressIndicator(
                                            progress = { 0f },
                                            onCancel = null,
                                            modifier = Modifier
                                                .padding(end = 8.dp)
                                                .size(24.dp),
                                            trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                                            strokeWidth = 3.dp,
                                        )
                                    }
                                    AnimatedVisibility(!component.isSearchLoading) {
                                        EnhancedIconButton(
                                            onClick = {
                                                if (component.searchQuery.isNotBlank()) {
                                                    component.updateSearchQuery("")
                                                } else {
                                                    showSearch = false
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
                                    }
                                },
                                shape = ShapeDefaults.circle
                            )
                        }
                    }
                } else {
                    val settingsState = LocalSettingsState.current

                    Box(
                        modifier = Modifier
                            .windowInsetsPadding(insets)
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.align(
                                settingsState.fabAlignment.takeIf { it != Alignment.BottomCenter }
                                    ?: Alignment.BottomEnd
                            )
                        ) {
                            val progressAnimatable =
                                remember { Animatable(if (isSendingLogs) 1f else 0f) }
                            val progress = progressAnimatable.value

                            LaunchedEffect(isSendingLogs) {
                                delay(400)
                                if (isSendingLogs) {
                                    progressAnimatable.animateTo(
                                        targetValue = 1f,
                                        animationSpec = springySpec()
                                    )
                                } else {
                                    progressAnimatable.animateTo(
                                        targetValue = 0f,
                                        animationSpec = tween(200)
                                    )
                                }
                            }

                            EnhancedFloatingActionButton(
                                onClick = component::shareLogs,
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                type = EnhancedFloatingActionButtonType.SecondaryHorizontal
                            ) {
                                if (progress > 0f) {
                                    EnhancedCircularProgressIndicator(
                                        modifier = Modifier.size(24.dp * progress),
                                        trackColor = MaterialTheme.colorScheme.primary.copy(0.2f),
                                        strokeWidth = 3.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Outlined.Share,
                                        contentDescription = null
                                    )
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            EnhancedFloatingActionButton(
                                onClick = { showSearch = true }
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
        val state = rememberPullToRefreshState()

        PullToRefreshBox(
            isRefreshing = component.isRefreshing && lineCount > 0,
            onRefresh = component::refreshLogs,
            state = state,
            modifier = Modifier.fillMaxSize(),
            indicator = {
                Indicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(contentPadding),
                    isRefreshing = component.isRefreshing && lineCount > 0,
                    state = state,
                    color = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            },
            contentAlignment = Alignment.Center
        ) {
            val noMatches = lineCount == 0 && !component.isRefreshing && !component.isSearchLoading
            val isLoading = lineCount <= 0 && component.isRefreshing

            AnimatedContent(
                targetState = isLoading to noMatches,
                modifier = Modifier.fillMaxSize()
            ) { (isLoading, noMatches) ->
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        EnhancedLoadingIndicator()
                    }
                } else if (noMatches) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(contentPadding),
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
                                .sizeIn(maxHeight = 140.dp, maxWidth = 140.dp)
                                .fillMaxSize()
                        )
                        Spacer(Modifier.weight(1f))
                    }
                } else {
                    val listState = rememberLazyListState()

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = listState,
                        contentPadding = contentPadding + PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(
                            count = lineCount,
                            key = component::lineKey
                        ) { index ->
                            component.lineAtOrNull(index)?.let { line ->
                                LogLineItem(
                                    line = line,
                                    query = searchQuery
                                )
                            }
                        }
                    }

                    InternalLazyColumnScrollbar(
                        state = listState,
                        settings = ScrollbarSettings(
                            thumbUnselectedColor = MaterialTheme.colorScheme.secondary,
                            thumbSelectedColor = MaterialTheme.colorScheme.primary,
                            scrollbarPadding = 0.dp,
                            thumbThickness = 10.dp,
                            selectionMode = ScrollbarSelectionMode.Full,
                            thumbShape = AutoCircleShape(),
                            hideDelayMillis = 1500
                        ),
                        indicatorContent = { _, _ ->
                            Spacer(
                                Modifier
                                    .width(64.dp)
                                    .height(28.dp)
                            )
                        },
                        modifier = Modifier.padding(contentPadding)
                    )
                }
            }
        }
    }
}