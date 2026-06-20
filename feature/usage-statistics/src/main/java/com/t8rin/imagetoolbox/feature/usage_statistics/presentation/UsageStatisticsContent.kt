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

package com.t8rin.imagetoolbox.feature.usage_statistics.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ArrowBack
import com.t8rin.imagetoolbox.core.resources.icons.DeleteSweep
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.ResetDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.other.TopAppBarEmoji
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components.UsageStatisticsContentImpl
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components.UsageStatisticsInfoDialog
import com.t8rin.imagetoolbox.feature.usage_statistics.presentation.screenLogic.UsageStatisticsComponent

@Composable
fun UsageStatisticsContent(
    component: UsageStatisticsComponent
) {
    val state by component.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var showInfoDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showResetDialog by rememberSaveable {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            EnhancedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.usage_statistics),
                        modifier = Modifier.marquee()
                    )
                },
                navigationIcon = {
                    EnhancedIconButton(onClick = component.onGoBack) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                type = EnhancedTopAppBarType.Large,
                scrollBehavior = scrollBehavior,
                actions = {
                    TopAppBarEmoji()
                }
            )
        },
        floatingActionButton = {
            EnhancedFloatingActionButton(
                onClick = {
                    showInfoDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null
                )
            }
        }
    ) { contentPadding ->
        UsageStatisticsContentImpl(
            state = state,
            contentPadding = contentPadding + PaddingValues(bottom = 80.dp),
            onNavigate = component.onNavigate,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }

    UsageStatisticsInfoDialog(
        visible = showInfoDialog,
        onDismiss = { showInfoDialog = false },
        onWantReset = {
            showResetDialog = true
        }
    )

    ResetDialog(
        visible = showResetDialog,
        onDismiss = { showResetDialog = false },
        onReset = {
            showInfoDialog = false
            showResetDialog = false
            component.resetUsageStatistics()
        },
        title = stringResource(R.string.reset_usage_statistics),
        text = stringResource(R.string.reset_usage_statistics_sub),
        icon = Icons.Outlined.DeleteSweep
    )
}