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
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.t8rin.modalsheet.ModalBottomSheetValue
import com.t8rin.modalsheet.rememberModalBottomSheetState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.utils.animation.ModalSheetAnimationSpec
import ru.tech.imageresizershrinker.core.ui.utils.provider.ProvideContainerDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.autoElevatedBorder
import ru.tech.imageresizershrinker.core.ui.widget.modifier.drawHorizontalStroke
import kotlin.coroutines.cancellation.CancellationException
import com.t8rin.modalsheet.ModalSheet as RealModalSheet

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
        var predictiveBackProgress by remember {
            mutableFloatStateOf(0f)
        }
        val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)

        val clean = {
            predictiveBackProgress = 0f
        }
        val shape = SimpleSheetDefaults.shape

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
                .statusBarsPadding()
                .graphicsLayer {
                    val sheetOffset = 0f
                    val sheetHeight = size.height
                    if (!sheetOffset.isNaN() && !sheetHeight.isNaN() && sheetHeight != 0f) {
                        val progress = animatedPredictiveBackProgress
                        scaleX = calculatePredictiveBackScaleX(progress)
                        scaleY = calculatePredictiveBackScaleY(progress)
                        transformOrigin =
                            TransformOrigin(0.5f, (sheetOffset + sheetHeight) / sheetHeight)
                    }
                }
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
                                }
                                predictiveBackProgress = event.progress
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
        var predictiveBackProgress by remember {
            mutableFloatStateOf(0f)
        }
        val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)

        val clean = {
            predictiveBackProgress = 0f
        }
        val shape = SimpleSheetDefaults.shape

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
                .statusBarsPadding()
                .graphicsLayer {
                    val sheetOffset = 0f
                    val sheetHeight = size.height
                    if (!sheetOffset.isNaN() && !sheetHeight.isNaN() && sheetHeight != 0f) {
                        val progress = animatedPredictiveBackProgress
                        scaleX = calculatePredictiveBackScaleX(progress)
                        scaleY = calculatePredictiveBackScaleY(progress)
                        transformOrigin =
                            TransformOrigin(0.5f, (sheetOffset + sheetHeight) / sheetHeight)
                    }
                }
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
                                }
                                predictiveBackProgress = event.progress
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
        var predictiveBackProgress by remember {
            mutableFloatStateOf(0f)
        }
        val animatedPredictiveBackProgress by animateFloatAsState(predictiveBackProgress)

        val clean = {
            predictiveBackProgress = 0f
        }
        val shape = SimpleSheetDefaults.shape

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
                .statusBarsPadding()
                .graphicsLayer {
                    val sheetOffset = 0f
                    val sheetHeight = size.height
                    if (!sheetOffset.isNaN() && !sheetHeight.isNaN() && sheetHeight != 0f) {
                        val progress = animatedPredictiveBackProgress
                        scaleX = calculatePredictiveBackScaleX(progress)
                        scaleY = calculatePredictiveBackScaleY(progress)
                        transformOrigin =
                            TransformOrigin(0.5f, (sheetOffset + sheetHeight) / sheetHeight)
                    }
                }
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
                                }
                                predictiveBackProgress = event.progress
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
                        Row(
                            modifier = Modifier.then(
                                if (enableBottomContentWeight) {
                                    Modifier.weight(1f)
                                } else Modifier
                            )
                        ) {
                            title()
                        }
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

private fun GraphicsLayerScope.calculatePredictiveBackScaleX(progress: Float): Float {
    val width = size.width
    return if (width.isNaN() || width == 0f) {
        1f
    } else {
        (1f - progress).coerceAtLeast(0.8f)
    }
}

private fun GraphicsLayerScope.calculatePredictiveBackScaleY(progress: Float): Float {
    val height = size.height
    return if (height.isNaN() || height == 0f) {
        1f
    } else {
        (1f - progress).coerceAtLeast(0.8f)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModalSheet(
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    dragHandle: @Composable ColumnScope.() -> Unit = {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            BottomSheetDefaults.DragHandle()
        }
    },
    nestedScrollEnabled: Boolean = false,
    animationSpec: AnimationSpec<Float> = spring(),
    sheetModifier: Modifier = Modifier,
    cancelable: Boolean = true,
    skipHalfExpanded: Boolean = true,
    shape: Shape = BottomSheetDefaults.ExpandedShape,
    elevation: Dp = BottomSheetDefaults.Elevation,
    containerColor: Color = BottomSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Hold cancelable flag internally and set to true when modal sheet is dismissed via "visible" property in
    // non-cancellable modal sheet. This ensures that "confirmValueChange" will return true when sheet is set to hidden
    // state.
    val internalCancelable = remember { mutableStateOf(cancelable) }
    val sheetState = rememberModalBottomSheetState(
        skipHalfExpanded = skipHalfExpanded,
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = animationSpec,
        confirmValueChange = {
            // Intercept and disallow hide gesture / action
            if (it == ModalBottomSheetValue.Hidden && !internalCancelable.value) {
                return@rememberModalBottomSheetState false
            }
            true
        },
    )
    val scope = rememberCoroutineScope()
    var isAnimating by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(visible, cancelable) {
        if (visible) {
            internalCancelable.value = cancelable
            isAnimating = true
            scope.launch {
                sheetState.show()
                isAnimating = false
            }
        } else {
            internalCancelable.value = true
            isAnimating = true
            scope.launch {
                sheetState.hide()
                isAnimating = false
            }
        }
    }

    LaunchedEffect(sheetState.currentValue, sheetState.targetValue, sheetState.progress) {
        if (sheetState.progress == 1f && sheetState.currentValue == sheetState.targetValue) {
            val newVisible = sheetState.isVisible
            if (newVisible != visible) {
                onVisibleChange(newVisible)
            }
        }
    }

    if (!visible && sheetState.currentValue == sheetState.targetValue && !sheetState.isVisible && !isAnimating) {
        return
    }

    RealModalSheet(
        sheetState = sheetState,
        onDismiss = {
            if (cancelable) {
                onVisibleChange(false)
            }
        },
        dragHandle = dragHandle,
        nestedScrollEnabled = nestedScrollEnabled,
        sheetModifier = sheetModifier,
        modifier = modifier,
        shape = shape,
        elevation = elevation,
        containerColor = containerColor,
        contentColor = contentColor,
        scrimColor = scrimColor,
        content = content,
    )
}