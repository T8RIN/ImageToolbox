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

package ru.tech.imageresizershrinker.core.ui.widget.utils

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.modalsheet.FullscreenPopup
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalScreenSize
import ru.tech.imageresizershrinker.core.ui.utils.provider.rememberCurrentLifecycleEvent
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedTopAppBarDefaults
import ru.tech.imageresizershrinker.core.ui.widget.image.ImageHeaderState

@Composable
fun rememberAvailableHeight(
    imageState: ImageHeaderState,
    expanded: Boolean = false
): Dp {
    val fullHeight = rememberFullHeight()

    return animateDpAsState(
        targetValue = fullHeight.times(
            when {
                expanded || imageState.position == 4 -> 1f
                imageState.position == 3 -> 0.7f
                imageState.position == 2 -> 0.5f
                imageState.position == 1 -> 0.35f
                else -> 0.2f
            }
        )
    ).value
}

@Composable
fun rememberFullHeight(): Dp {
    val screenSize = LocalScreenSize.current
    val currentLifecycleEvent = rememberCurrentLifecycleEvent()
    var fullHeight by remember(screenSize, currentLifecycleEvent) { mutableStateOf(0.dp) }

    val density = LocalDensity.current

    if (fullHeight == 0.dp) {
        FullscreenPopup {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    title = { Text(" ") },
                    colors = EnhancedTopAppBarDefaults.colors(Color.Transparent)
                )
                Spacer(
                    Modifier
                        .weight(1f)
                        .onSizeChanged {
                            with(density) {
                                fullHeight = it.height.toDp()
                            }
                        }
                )
                BottomAppBar(
                    containerColor = Color.Transparent,
                    floatingActionButton = {
                        Spacer(Modifier.height(56.dp))
                    },
                    actions = {}
                )
            }
        }
    }

    return fullHeight
}

fun ImageHeaderState.isExpanded() = this.position == 4 && isBlocked

fun middleImageState() = ImageHeaderState()

@Composable
fun rememberImageState(): MutableState<ImageHeaderState> {
    val settingsState = LocalSettingsState.current
    return remember(settingsState.generatePreviews) {
        mutableStateOf(
            if (settingsState.generatePreviews) middleImageState()
            else ImageHeaderState(0)
        )
    }
}