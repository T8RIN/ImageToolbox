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

package ru.tech.imageresizershrinker.feature.root.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.theme.takeColorFromScheme
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
internal fun BoxScope.SettingsOpenButton(
    isWantOpenSettings: Boolean,
    onStateChange: (Boolean) -> Unit,
    scaffoldState: BackdropScaffoldState,
    canExpandSettings: Boolean
) {
    val scope = rememberCoroutineScope()

    Surface(
        color = Color.Companion.Transparent,
        modifier = Modifier.Companion
            .align(Alignment.Companion.CenterEnd)
            .size(
                height = animateDpAsState(
                    if (isWantOpenSettings) 64.dp
                    else 104.dp
                ).value,
                width = animateDpAsState(
                    if (isWantOpenSettings) 48.dp
                    else 24.dp
                ).value
            )
            .clickable(
                indication = null,
                interactionSource = null
            ) {
                if (isWantOpenSettings) {
                    scope.launch {
                        scaffoldState.reveal()
                        onStateChange(false)
                    }
                } else {
                    onStateChange(true)
                }
            }
            .alpha(
                animateFloatAsState(
                    if (canExpandSettings) 1f
                    else 0f
                ).value
            )
    ) {
        Box {
            Box(
                modifier = Modifier.Companion
                    .align(Alignment.Companion.CenterEnd)
                    .width(
                        animateDpAsState(
                            if (isWantOpenSettings) 48.dp
                            else 4.dp
                        ).value
                    )
                    .height(64.dp)
                    .container(
                        shape = RoundedCornerShape(
                            topStart = 8.dp,
                            bottomStart = 8.dp
                        ),
                        resultPadding = 0.dp,
                        color = takeColorFromScheme {
                            tertiary.blend(primary, 0.8f)
                        }
                    ),
                contentAlignment = Alignment.Companion.Center
            ) {
                AnimatedVisibility(
                    visible = isWantOpenSettings,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut()
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = takeColorFromScheme {
                            onTertiary.blend(onPrimary, 0.8f)
                        }
                    )
                }
            }
        }
    }
}