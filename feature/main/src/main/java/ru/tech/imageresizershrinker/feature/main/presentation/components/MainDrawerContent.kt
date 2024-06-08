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

package ru.tech.imageresizershrinker.feature.main.presentation.components

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.utils.helper.ProvidesValue
import ru.tech.imageresizershrinker.core.ui.widget.modifier.animateShape
import kotlin.coroutines.cancellation.CancellationException

@Composable
internal fun MainDrawerContent(
    sideSheetState: DrawerState,
    isSheetSlideable: Boolean,
    sheetExpanded: Boolean,
    layoutDirection: LayoutDirection,
    settingsBlockContent: @Composable () -> Unit
) {
    val settingsState = LocalSettingsState.current
    val settingsBlock = remember {
        movableContentOf {
            settingsBlockContent()
        }
    }

    val configuration = LocalConfiguration.current
    val widthState by remember(sheetExpanded) {
        derivedStateOf {
            if (isSheetSlideable) {
                min(
                    configuration.screenWidthDp.dp * 0.85f,
                    DrawerDefaults.MaximumDrawerWidth
                )
            } else {
                if (sheetExpanded) configuration.screenWidthDp.dp * 0.55f
                else min(
                    configuration.screenWidthDp.dp * 0.4f,
                    DrawerDefaults.MaximumDrawerWidth
                )
            }.coerceAtLeast(1.dp)
        }
    }

    var animatedScale by remember {
        mutableFloatStateOf(1f)
    }
    var animatedOffsetX by remember {
        mutableFloatStateOf(0f)
    }
    var animatedOffsetY by remember {
        mutableFloatStateOf(0f)
    }
    var animatedShape by remember {
        mutableStateOf(
            RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 0.dp,
                bottomEnd = 24.dp,
                topEnd = 24.dp
            )
        )
    }
    var initialSwipeOffset by remember { mutableStateOf(Offset.Zero) }
    val scale by animateFloatAsState(animatedScale)
    val offsetX by animateFloatAsState(animatedOffsetX)
    val offsetY by animateFloatAsState(animatedOffsetY)
    val shape = animateShape(animatedShape)

    val clean = {
        animatedOffsetX = 0f
        animatedOffsetY = 0f
        animatedScale = 1f
        animatedShape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 0.dp,
            bottomEnd = 24.dp,
            topEnd = 24.dp
        )
        initialSwipeOffset = Offset.Zero
    }

    LaunchedEffect(sideSheetState.isOpen, isSheetSlideable) {
        if (!sideSheetState.isOpen || isSheetSlideable) {
            delay(300L)
            clean()
        }
    }

    if (sideSheetState.isOpen && isSheetSlideable) {
        PredictiveBackHandler { progress ->
            try {
                progress.collect { event ->
                    if (event.progress <= 0.05f) {
                        clean()
                        initialSwipeOffset = Offset(event.touchX, event.touchY)
                    }

                    animatedOffsetX = initialSwipeOffset.x - event.touchX
                    animatedOffsetY = event.touchY - initialSwipeOffset.y
                    animatedShape = RoundedCornerShape(24.dp)
                    animatedScale = (1f - event.progress * 2f).coerceAtLeast(0.7f)
                }
                sideSheetState.close()
            } catch (e: CancellationException) {
                clean()
            }
        }
    }

    ModalDrawerSheet(
        modifier = Modifier
            .width(animateDpAsState(targetValue = widthState).value)
            .then(
                if (isSheetSlideable) {
                    Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        }
                        .offset {
                            IntOffset(offsetX.toInt(), offsetY.toInt())
                        }
                        .offset(-((settingsState.borderWidth + 1.dp)))
                        .border(
                            settingsState.borderWidth,
                            MaterialTheme.colorScheme.outlineVariant(
                                0.3f,
                                DrawerDefaults.standardContainerColor
                            ),
                            shape
                        )
                        .clip(shape)
                } else Modifier
            ),
        drawerContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        drawerShape = if (isSheetSlideable) shape else RectangleShape,
        windowInsets = WindowInsets(0)
    ) {
        LocalLayoutDirection.ProvidesValue(layoutDirection) {
            settingsBlock()
        }
    }
}