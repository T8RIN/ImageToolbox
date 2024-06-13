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

package ru.tech.imageresizershrinker.core.ui.widget.sheets

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.t8rin.modalsheet.ModalSheet
import com.t8rin.modalsheet.ModalSheetState
import kotlinx.coroutines.delay
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.animation.ModalSheetAnimationSpec
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.animateShape
import ru.tech.imageresizershrinker.core.ui.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    cancelable: Boolean = true,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val settingsState = LocalSettingsState.current

    val autoElevation by animateDpAsState(
        if (settingsState.drawContainerShadows) 16.dp
        else 0.dp
    )
    ProvideContainerDefaults(
        color = SimpleSheetDefaults.contentContainerColor
    ) {
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
            mutableStateOf(SimpleSheetDefaults.shape)
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
            animatedShape = SimpleSheetDefaults.shape
            initialSwipeOffset = Offset.Zero
        }

        LaunchedEffect(visible) {
            if (!visible) {
                delay(300L)
                clean()
            }
        }
        ModalSheet(
            cancelable = cancelable,
            nestedScrollEnabled = nestedScrollEnabled,
            animationSpec = ModalSheetAnimationSpec,
            dragHandle = dragHandle,
            containerColor = SimpleSheetDefaults.containerColor,
            sheetModifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .offset {
                    IntOffset(offsetX.toInt(), offsetY.toInt())
                }
                .statusBarsPadding()
                .offset(y = (settingsState.borderWidth + 1.dp))
                .autoElevatedBorder(
                    shape = shape,
                    autoElevation = autoElevation
                )
                .autoElevatedBorder(
                    height = 0.dp,
                    shape = shape,
                    autoElevation = autoElevation
                )
                .clip(shape)
                .animateContentSize(),
            elevation = 0.dp,
            visible = visible,
            onVisibleChange = onDismiss,
            content = {
                if (visible) {
                    PredictiveBackHandler { progress ->
                        try {
                            progress.collect { event ->
                                if (event.progress <= 0.05f) {
                                    clean()
                                    initialSwipeOffset = Offset(event.touchX, event.touchY)
                                }

                                animatedOffsetX = event.touchX - initialSwipeOffset.x
                                animatedOffsetY = event.touchY - initialSwipeOffset.y
                                animatedShape = RoundedCornerShape(28.dp)
                                animatedScale = (1f - event.progress * 2f).coerceAtLeast(0.7f)
                            }
                            onDismiss(false)
                        } catch (e: CancellationException) {
                            clean()
                        }
                    }
                }
                sheetContent()
            }
        )
    }
}

@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean,
    cancelable: Boolean = true,
    confirmButton: @Composable RowScope.() -> Unit,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    title: @Composable () -> Unit,
    endConfirmButtonPadding: Dp = 16.dp,
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    enableBackHandler: Boolean = true,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val settingsState = LocalSettingsState.current

    val autoElevation by animateDpAsState(
        if (settingsState.drawContainerShadows) 16.dp
        else 0.dp
    )

    ProvideContainerDefaults(
        color = SimpleSheetDefaults.contentContainerColor
    ) {
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
            mutableStateOf(SimpleSheetDefaults.shape)
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
            animatedShape = SimpleSheetDefaults.shape
            initialSwipeOffset = Offset.Zero
        }

        LaunchedEffect(visible) {
            if (!visible) {
                delay(300L)
                clean()
            }
        }
        ModalSheet(
            cancelable = cancelable,
            nestedScrollEnabled = nestedScrollEnabled,
            animationSpec = ModalSheetAnimationSpec,
            dragHandle = dragHandle,
            containerColor = SimpleSheetDefaults.containerColor,
            sheetModifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .offset {
                    IntOffset(offsetX.toInt(), offsetY.toInt())
                }
                .statusBarsPadding()
                .offset(y = (settingsState.borderWidth + 1.dp))
                .autoElevatedBorder(
                    shape = shape,
                    autoElevation = autoElevation
                )
                .autoElevatedBorder(
                    height = 0.dp,
                    shape = shape,
                    autoElevation = autoElevation
                )
                .clip(shape)
                .animateContentSize(),
            elevation = 0.dp,
            visible = visible,
            onVisibleChange = onDismiss,
            content = {
                if (visible && enableBackHandler) {
                    PredictiveBackHandler { progress ->
                        try {
                            progress.collect { event ->
                                if (event.progress <= 0.05f) {
                                    clean()
                                    initialSwipeOffset = Offset(event.touchX, event.touchY)
                                }

                                animatedOffsetX = event.touchX - initialSwipeOffset.x
                                animatedOffsetY = event.touchY - initialSwipeOffset.y
                                animatedShape = RoundedCornerShape(28.dp)
                                animatedScale = (1f - event.progress * 2f).coerceAtLeast(0.7f)
                            }
                            onDismiss(false)
                        } catch (e: CancellationException) {
                            clean()
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f, false),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = sheetContent
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawHorizontalStroke(true, autoElevation = 6.dp)
                        .background(SimpleSheetDefaults.barContainerColor)
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .padding(end = endConfirmButtonPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    title()
                    Spacer(modifier = Modifier.weight(1f))
                    confirmButton()
                }
            }
        )
    }
}

@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    cancelable: Boolean = true,
    confirmButton: (@Composable RowScope.() -> Unit)? = null,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    title: (@Composable RowScope.() -> Unit)? = null,
    visible: Boolean,
    onDismiss: (Boolean) -> Unit,
    enableBackHandler: Boolean = true,
    enableBottomContentWeight: Boolean = true,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val settingsState = LocalSettingsState.current
    val autoElevation by animateDpAsState(
        if (settingsState.drawContainerShadows) 16.dp
        else 0.dp
    )

    ProvideContainerDefaults(
        color = SimpleSheetDefaults.contentContainerColor
    ) {
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
            mutableStateOf(SimpleSheetDefaults.shape)
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
            animatedShape = SimpleSheetDefaults.shape
            initialSwipeOffset = Offset.Zero
        }

        LaunchedEffect(visible) {
            if (!visible) {
                delay(300L)
                clean()
            }
        }
        ModalSheet(
            cancelable = cancelable,
            nestedScrollEnabled = nestedScrollEnabled,
            animationSpec = ModalSheetAnimationSpec,
            dragHandle = dragHandle,
            containerColor = SimpleSheetDefaults.containerColor,
            sheetModifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .offset {
                    IntOffset(offsetX.toInt(), offsetY.toInt())
                }
                .statusBarsPadding()
                .offset(y = (settingsState.borderWidth + 1.dp))
                .autoElevatedBorder(
                    shape = shape,
                    autoElevation = autoElevation
                )
                .autoElevatedBorder(
                    height = 0.dp,
                    shape = shape,
                    autoElevation = autoElevation
                )
                .clip(shape)
                .animateContentSize(),
            elevation = 0.dp,
            visible = visible,
            onVisibleChange = onDismiss,
            content = {
                if (visible && enableBackHandler) {
                    PredictiveBackHandler { progress ->
                        try {
                            progress.collect { event ->
                                if (event.progress <= 0.05f) {
                                    clean()
                                    initialSwipeOffset = Offset(event.touchX, event.touchY)
                                }

                                animatedOffsetX = event.touchX - initialSwipeOffset.x
                                animatedOffsetY = event.touchY - initialSwipeOffset.y
                                animatedShape = RoundedCornerShape(28.dp)
                                animatedScale = (1f - event.progress * 2f).coerceAtLeast(0.7f)
                            }
                            onDismiss(false)
                        } catch (e: CancellationException) {
                            clean()
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f, false),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = sheetContent
                )
                if (confirmButton != null && title != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawHorizontalStroke(true, autoElevation = 6.dp)
                            .background(SimpleSheetDefaults.barContainerColor)
                            .navigationBarsPadding()
                            .padding(16.dp)
                            .then(
                                if (enableBottomContentWeight) Modifier.padding(end = 16.dp)
                                else Modifier
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        title()
                        if (enableBottomContentWeight) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        confirmButton()
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleSheet(
    nestedScrollEnabled: Boolean = false,
    sheetState: ModalSheetState,
    confirmButton: (@Composable RowScope.() -> Unit)? = null,
    dragHandle: @Composable ColumnScope.() -> Unit = { SimpleDragHandle() },
    title: (@Composable () -> Unit)? = null,
    onDismiss: () -> Unit,
    sheetContent: @Composable ColumnScope.() -> Unit,
) {
    val settingsState = LocalSettingsState.current
    val autoElevation by animateDpAsState(
        if (settingsState.drawContainerShadows) 16.dp
        else 0.dp
    )

    ProvideContainerDefaults(
        color = SimpleSheetDefaults.contentContainerColor
    ) {
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
            mutableStateOf(SimpleSheetDefaults.shape)
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
            animatedShape = SimpleSheetDefaults.shape
            initialSwipeOffset = Offset.Zero
        }

        LaunchedEffect(sheetState.isVisible) {
            if (!sheetState.isVisible) {
                delay(300L)
                clean()
            }
        }
        ModalSheet(
            sheetState = sheetState,
            nestedScrollEnabled = nestedScrollEnabled,
            dragHandle = dragHandle,
            containerColor = SimpleSheetDefaults.containerColor,
            sheetModifier = Modifier
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .offset {
                    IntOffset(offsetX.toInt(), offsetY.toInt())
                }
                .statusBarsPadding()
                .offset(y = (settingsState.borderWidth + 1.dp))
                .autoElevatedBorder(
                    shape = shape,
                    autoElevation = autoElevation
                )
                .autoElevatedBorder(
                    height = 0.dp,
                    shape = shape,
                    autoElevation = autoElevation
                )
                .clip(shape)
                .animateContentSize(),
            elevation = 0.dp,
            onDismiss = onDismiss,
            content = {
                if (sheetState.isVisible) {
                    PredictiveBackHandler { progress ->
                        try {
                            progress.collect { event ->
                                if (event.progress <= 0.05f) {
                                    clean()
                                    initialSwipeOffset = Offset(event.touchX, event.touchY)
                                }

                                animatedOffsetX = event.touchX - initialSwipeOffset.x
                                animatedOffsetY = event.touchY - initialSwipeOffset.y
                                animatedShape = shape
                                animatedScale = (1f - event.progress * 2f).coerceAtLeast(0.7f)
                            }
                            sheetState.hide()
                        } catch (e: CancellationException) {
                            clean()
                        }
                    }
                }
                Column(
                    modifier = Modifier.weight(1f, false),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    content = sheetContent
                )
                if (confirmButton != null && title != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawHorizontalStroke(true, autoElevation = 6.dp)
                            .background(SimpleSheetDefaults.barContainerColor)
                            .navigationBarsPadding()
                            .padding(16.dp)
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        title()
                        Spacer(modifier = Modifier.weight(1f))
                        confirmButton()
                    }
                }
            }
        )
    }
}

object SimpleSheetDefaults {

    val shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)

    val barContainerColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceContainerHigh

    val containerColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceContainerLow

    val contentContainerColor: Color
        @Composable
        get() = MaterialTheme.colorScheme.surfaceContainer

}