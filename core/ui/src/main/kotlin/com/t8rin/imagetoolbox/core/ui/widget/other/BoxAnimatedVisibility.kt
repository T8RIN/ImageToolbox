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

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp

@Composable
fun BoxAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    enter: EnterTransition = fadeIn() + expandIn(),
    exit: ExitTransition = shrinkOut() + fadeOut(),
    label: String = "AnimatedVisibility",
    keepContentOnCollapse: Boolean = false,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    if (!keepContentOnCollapse) {
        androidx.compose.animation.AnimatedVisibility(
            visible,
            modifier,
            enter,
            exit,
            label,
            content
        )
        return
    }

    val currentContent by rememberUpdatedState(content)
    val movableContent = remember {
        movableContentOf<AnimatedVisibilityScope> { scope -> currentContent(scope) }
    }
    val visibleState = remember { MutableTransitionState(visible) }
    visibleState.targetState = visible

    androidx.compose.animation.AnimatedVisibility(
        visible = true,
        modifier = modifier,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        val scope = this
        androidx.compose.animation.AnimatedVisibility(
            visibleState = visibleState,
            enter = enter,
            exit = exit,
            label = label
        ) {
            movableContent(this)
        }
        if (!visible && visibleState.isIdle) {
            Box(Modifier
                .height(0.dp)
                .clipToBounds()) {
                movableContent(scope)
            }
        }
    }
}