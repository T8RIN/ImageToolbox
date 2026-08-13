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

package com.t8rin.imagetoolbox.feature.main.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.PreviewFocusFix
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.preferences.RecentToolsCard
import kotlin.random.Random

@Composable
internal fun LastUsedToolsCard(
    tools: List<UiLastUsedTool>,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val screens = remember(tools) {
        tools.map { it.screen }
    }

    RecentToolsCard(
        tools = screens,
        onNavigate = onNavigate,
        modifier = modifier,
        onHistoryClick = { onNavigate(Screen.UsageStatistics) }
    )
}

@Composable
private fun PreviewContent() {
    PreviewFocusFix()

    CompositionLocalProvider(
        LocalSettingsState provides LocalSettingsState.current.copy(
            drawContainerShadows = false
        )
    ) {
        LastUsedToolsCard(
            tools = Screen.entries.take(2).map { screen ->
                UiLastUsedTool(
                    screen = screen,
                    openCount = Random.nextInt(1, 100),
                )
            },
            onNavigate = {}
        )
    }
}

@Preview
@Composable
private fun Preview() = ImageToolboxThemeForPreview(true, Color.Green) {
    PreviewContent()
}

@Preview
@Composable
private fun Preview1() = ImageToolboxThemeForPreview(false, Color.Green) {
    PreviewContent()
}