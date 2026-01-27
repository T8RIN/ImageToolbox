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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedNavigationBarItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.longPress
import com.t8rin.imagetoolbox.core.ui.widget.modifier.drawHorizontalStroke
import com.t8rin.imagetoolbox.core.ui.widget.text.marquee

@Composable
internal fun MainNavigationBar(
    selectedIndex: Int,
    onValueChange: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .drawHorizontalStroke(top = true)
            .height(
                80.dp + WindowInsets.systemBars
                    .asPaddingValues()
                    .calculateBottomPadding()
            ),
    ) {
        Screen.typedEntries.forEachIndexed { index, group ->
            val selected = index == selectedIndex
            val haptics = LocalHapticFeedback.current
            EnhancedNavigationBarItem(
                modifier = Modifier.weight(1f),
                selected = selected,
                onClick = {
                    onValueChange(index)
                    haptics.longPress()
                },
                icon = {
                    AnimatedContent(
                        targetState = selected,
                        transitionSpec = {
                            fadeIn() togetherWith fadeOut()
                        }
                    ) { selected ->
                        Icon(
                            imageVector = group.icon(selected),
                            contentDescription = stringResource(group.title)
                        )
                    }
                },
                label = {
                    Text(
                        text = stringResource(group.title),
                        modifier = Modifier.marquee()
                    )
                }
            )
        }
    }
}