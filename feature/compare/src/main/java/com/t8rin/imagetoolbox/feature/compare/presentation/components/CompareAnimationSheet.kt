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

package com.t8rin.imagetoolbox.feature.compare.presentation.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.scale
import androidx.core.graphics.withTranslation
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Animation
import com.t8rin.imagetoolbox.core.resources.icons.Save
import com.t8rin.imagetoolbox.core.resources.icons.Share
import com.t8rin.imagetoolbox.core.ui.utils.provider.ProvideContainerDefaults
import com.t8rin.imagetoolbox.core.ui.widget.dialogs.OneTimeSaveLocationSelectionDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedSliderItem
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.compare.data.CompareAnimationRenderer
import com.t8rin.imagetoolbox.feature.compare.domain.CompareAnimationParams
import com.t8rin.imagetoolbox.feature.compare.presentation.components.model.CompareData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

@Composable
internal fun CompareAnimationSheet(
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    data: CompareData?,
    params: CompareAnimationParams,
    onParamsChange: (CompareAnimationParams) -> Unit,
    onSave: (String?) -> Unit,
    onShare: () -> Unit
) {
    val before = data?.first?.image ?: return
    val after = data.second?.image ?: return
    val beforeLabel = stringResource(R.string.compare_animation_before)
    val afterLabel = stringResource(R.string.compare_animation_after)
    var showFolderSelection by rememberSaveable(visible) { mutableStateOf(false) }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = onVisibleChange,
        title = {
            TitleItem(
                text = stringResource(R.string.compare_animation),
                icon = Icons.Rounded.Animation
            )
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { onVisibleChange(false) }
            ) {
                AutoSizeText(stringResource(R.string.close))
            }
        },
        sheetContent = {
            val previewSize = with(LocalDensity.current) { 120.dp.roundToPx() }
            val renderer by produceState<CompareAnimationRenderer?>(
                initialValue = null,
                key1 = data,
                key2 = previewSize,
                key3 = beforeLabel to afterLabel
            ) {
                value = null
                value = withContext(Dispatchers.Default) {
                    val (width, height) = CompareAnimationParams.outputSize(
                        before.width, before.height, previewSize
                    )
                    val (afterWidth, afterHeight) = CompareAnimationParams.outputSize(
                        after.width, after.height, previewSize
                    )
                    CompareAnimationRenderer(
                        before = before.scale(width, height),
                        after = after.scale(afterWidth, afterHeight),
                        width = width,
                        height = height,
                        beforeLabel = beforeLabel,
                        afterLabel = afterLabel
                    )
                }
            }
            Column(
                modifier = Modifier.enhancedVerticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .padding(
                            bottom = 8.dp,
                            start = 4.dp,
                            end = 4.dp,
                            top = 16.dp
                        )
                        .height(100.dp)
                        .width(120.dp)
                        .container(
                            shape = MaterialTheme.shapes.medium,
                            resultPadding = 0.dp
                        )
                        .transparencyChecker()
                ) {
                    val transition = rememberInfiniteTransition()
                    val time by transition.animateFloat(
                        initialValue = 0f,
                        targetValue = params.durationMillis.toFloat(),
                        animationSpec = infiniteRepeatable(
                            tween(params.durationMillis, easing = LinearEasing)
                        )
                    )
                    Canvas(Modifier.fillMaxSize()) {
                        val renderer = renderer ?: return@Canvas
                        val scale =
                            minOf(size.width / renderer.width, size.height / renderer.height)
                        drawIntoCanvas {
                            val canvas = it.nativeCanvas
                            canvas.withTranslation(
                                (size.width - renderer.width * scale) / 2f,
                                (size.height - renderer.height * scale) / 2f
                            ) {
                                canvas.scale(scale, scale)
                                canvas.clipRect(0, 0, renderer.width, renderer.height)
                                renderer.draw(canvas, params.positionAt(time.toInt()), params)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .container(
                            shape = ShapeDefaults.extraLarge,
                            resultPadding = 12.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ProvideContainerDefaults(color = MaterialTheme.colorScheme.surface) {
                        EnhancedSliderItem(
                            value = params.durationSeconds,
                            title = stringResource(R.string.compare_animation_duration),
                            valueRange = 2f..8f,
                            steps = 5,
                            valueSuffix = " s",
                            internalStateTransformation = Float::roundToInt,
                            onValueChange = {
                                onParamsChange(params.copy(durationSeconds = it.roundToInt()))
                            },
                            shape = ShapeDefaults.top
                        )
                        EnhancedButtonGroup(
                            modifier = Modifier
                                .fillMaxWidth()
                                .container(shape = ShapeDefaults.center),
                            value = params.maxSize,
                            entries = CompareAnimationParams.sizes,
                            onValueChange = { onParamsChange(params.copy(maxSize = it)) },
                            title = stringResource(R.string.resolution),
                            itemContent = { Text(it.toString()) },
                            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                            isScrollable = false
                        )
                        EnhancedButtonGroup(
                            modifier = Modifier
                                .fillMaxWidth()
                                .container(shape = ShapeDefaults.center),
                            value = params.isVertical,
                            entries = listOf(false, true),
                            onValueChange = { onParamsChange(params.copy(isVertical = it)) },
                            title = stringResource(R.string.compare_animation_direction),
                            itemContent = {
                                Text(stringResource(if (it) R.string.vertical else R.string.horizontal))
                            },
                            inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer,
                            isScrollable = false
                        )
                        PreferenceRowSwitch(
                            title = stringResource(R.string.compare_animation_labels),
                            checked = params.showLabels,
                            onClick = { onParamsChange(params.copy(showLabels = it)) },
                            shape = ShapeDefaults.bottom,
                            modifier = Modifier.fillMaxWidth(),
                            applyHorizontalPadding = false
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                PreferenceItem(
                    title = stringResource(R.string.save),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = { onSave(null) },
                    onLongClick = { showFolderSelection = true },
                    endIcon = Icons.Rounded.Save,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    shape = ShapeDefaults.top
                )
                Spacer(Modifier.height(4.dp))
                PreferenceItem(
                    title = stringResource(R.string.share),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = onShare,
                    endIcon = Icons.Rounded.Share,
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    shape = ShapeDefaults.bottom
                )
                Spacer(Modifier.height(16.dp))
            }
        }
    )
    OneTimeSaveLocationSelectionDialog(
        visible = showFolderSelection,
        onDismiss = { showFolderSelection = false },
        onSaveRequest = onSave,
        formatForFilenameSelection = ImageFormat.Gif,
        hasOriginalUri = false
    )
}