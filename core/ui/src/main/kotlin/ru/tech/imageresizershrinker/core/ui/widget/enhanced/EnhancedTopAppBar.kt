/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.core.ui.widget.enhanced

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke

@Composable
fun EnhancedTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = EnhancedTopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = EnhancedTopAppBarDefaults.colors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    type: EnhancedTopAppBarType = EnhancedTopAppBarType.Normal,
    drawHorizontalStroke: Boolean = true
) {
    AnimatedContent(
        targetState = type,
        transitionSpec = { fadeIn() togetherWith fadeOut() }
    ) {
        when (it) {
            EnhancedTopAppBarType.Center -> {
                CenterAlignedTopAppBar(
                    title = title,
                    modifier = modifier.drawHorizontalStroke(
                        enabled = drawHorizontalStroke
                    ),
                    navigationIcon = navigationIcon,
                    actions = actions,
                    windowInsets = windowInsets,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }

            EnhancedTopAppBarType.Normal -> {
                TopAppBar(
                    title = title,
                    modifier = modifier.drawHorizontalStroke(
                        enabled = drawHorizontalStroke
                    ),
                    navigationIcon = navigationIcon,
                    actions = actions,
                    windowInsets = windowInsets,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }

            EnhancedTopAppBarType.Medium -> {
                MediumTopAppBar(
                    title = title,
                    modifier = modifier.drawHorizontalStroke(
                        enabled = drawHorizontalStroke
                    ),
                    navigationIcon = navigationIcon,
                    actions = actions,
                    windowInsets = windowInsets,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }

            EnhancedTopAppBarType.Large -> {
                LargeTopAppBar(
                    title = title,
                    modifier = modifier.drawHorizontalStroke(
                        enabled = drawHorizontalStroke
                    ),
                    navigationIcon = navigationIcon,
                    actions = actions,
                    windowInsets = windowInsets,
                    colors = colors,
                    scrollBehavior = scrollBehavior
                )
            }
        }
    }
}

enum class EnhancedTopAppBarType {
    Center, Normal, Medium, Large
}

object EnhancedTopAppBarDefaults {

    val windowInsets: WindowInsets
        @Composable
        get() = WindowInsets.safeDrawing
            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)

    @Composable
    fun colors(
        containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
        scrolledContainerColor: Color = Color.Unspecified,
        navigationIconContentColor: Color = Color.Unspecified,
        titleContentColor: Color = Color.Unspecified,
        actionIconContentColor: Color = Color.Unspecified,
    ): TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        scrolledContainerColor = scrolledContainerColor,
        navigationIconContentColor = navigationIconContentColor,
        titleContentColor = titleContentColor,
        actionIconContentColor = actionIconContentColor
    )

}