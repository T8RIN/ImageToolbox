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

package com.t8rin.imagetoolbox.core.ui.widget.other

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.APP_RELEASES
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Block
import com.t8rin.imagetoolbox.core.resources.icons.Github
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.Black
import com.t8rin.imagetoolbox.core.ui.theme.White
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedFloatingActionButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBar
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedTopAppBarType
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.image.SourceNotPickedWidget
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText

@Composable
fun FeatureNotAvailableContent(
    title: @Composable () -> Unit,
    onGoBack: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val linkHandler = LocalUriHandler.current
    val settingsState = LocalSettingsState.current

    Scaffold(
        topBar = {
            EnhancedTopAppBar(
                type = EnhancedTopAppBarType.Large,
                scrollBehavior = scrollBehavior,
                title = title,
                navigationIcon = {
                    EnhancedIconButton(
                        onClick = onGoBack
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
        floatingActionButtonPosition = when (settingsState.fabAlignment) {
            Alignment.BottomStart -> FabPosition.Start
            Alignment.BottomCenter -> FabPosition.Center
            else -> FabPosition.End
        },
        floatingActionButton = {
            EnhancedFloatingActionButton(
                onClick = {
                    linkHandler.openUri(APP_RELEASES)
                },
                containerColor = Black,
                contentColor = White,
                content = {
                    Spacer(Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Rounded.Github,
                        contentDescription = stringResource(R.string.restart_app)
                    )
                    Spacer(Modifier.width(16.dp))
                    AutoSizeText(
                        text = stringResource(R.string.open_github_page),
                        maxLines = 1
                    )
                    Spacer(Modifier.width(16.dp))
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .enhancedVerticalScroll(rememberScrollState())
                .fillMaxSize()
                .padding(it)
                .padding(20.dp)
        ) {
            SourceNotPickedWidget(
                onClick = {
                    linkHandler.openUri(APP_RELEASES)
                },
                text = stringResource(R.string.wallpapers_export_not_avaialbe),
                icon = Icons.TwoTone.Block,
                maxLines = Int.MAX_VALUE
            )
        }
    }
}