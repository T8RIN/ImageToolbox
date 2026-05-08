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

package com.t8rin.imagetoolbox.feature.settings.presentation.app_logs

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.Close
import com.t8rin.imagetoolbox.core.resources.icons.Search
import com.t8rin.imagetoolbox.core.resources.icons.SearchOff
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedCancellableCircularProgressIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.other.SearchBar
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.settings.presentation.app_logs.components.LogLineItem
import com.t8rin.imagetoolbox.feature.settings.presentation.app_logs.screenLogic.AppLogsComponent

@Composable
fun AppLogsContent(
    component: AppLogsComponent
) {
    val lineCount = component.linesCount
    val searchQuery = component.searchQuery
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
                    AnimatedContent(
                        targetState = showSearch
                    ) { searching ->
                        if (!searching) {
                            Text(
                                text = stringResource(R.string.app_logs),
                                modifier = Modifier.marquee()
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.weight(1f, false)) {
                                    SearchBar(
                                        searchString = searchQuery,
                                        onValueChange = component::updateSearchQuery
                                    )
                                }
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
                            }
                        }
                    }
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
                    AnimatedContent(
                        targetState = showSearch
                    ) { searching ->
                        EnhancedIconButton(
                            onClick = {
                                if (searching) {
                                    component.updateSearchQuery("")
                                    showSearch = false
                                } else {
                                    showSearch = true
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (searching) Icons.Rounded.Close else Icons.Outlined.Search,
                                contentDescription = stringResource(
                                    if (searching) R.string.close else R.string.search_here
                                )
                            )
                        }
                    }
                }
            )
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
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
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
                                    query = searchQuery,
                                    component = component
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}