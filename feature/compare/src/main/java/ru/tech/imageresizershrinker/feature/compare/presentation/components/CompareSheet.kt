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

package ru.tech.imageresizershrinker.feature.compare.presentation.components

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Compare
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.beforeafter.BeforeAfterImage
import com.smarttoolfactory.beforeafter.BeforeAfterLayout
import com.smarttoolfactory.beforeafter.OverlayStyle
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalBottomSheet
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@Composable
fun CompareSheet(
    data: Pair<Bitmap?, Bitmap?>?,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    var progress by rememberSaveable(visible) { mutableFloatStateOf(50f) }

    if (data != null) {
        EnhancedModalBottomSheet(
            sheetContent = {
                Column(
                    modifier = Modifier.navigationBarsPadding()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .container(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme
                                    .outlineVariant()
                                    .copy(alpha = 0.1f),
                                resultPadding = 0.dp
                            )
                            .transparencyChecker()
                            .clipToBounds()
                            .zoomable(rememberZoomState(maxScale = 10f)),
                        contentAlignment = Alignment.Center
                    ) {
                        data.let { (b, a) ->
                            val before = remember(data) { b?.asImageBitmap() }
                            val after = remember(data) { a?.asImageBitmap() }
                            if (before != null && after != null) {
                                BeforeAfterImage(
                                    overlayStyle = OverlayStyle(),
                                    modifier = Modifier.clip(RoundedCornerShape(4.dp)),
                                    progress = animateFloatAsState(targetValue = progress).value,
                                    onProgressChange = {
                                        progress = it
                                    },
                                    enableZoom = false,
                                    beforeImage = before,
                                    afterImage = after,
                                    beforeLabel = { },
                                    afterLabel = { }
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TitleItem(
                            text = stringResource(R.string.compare),
                            icon = Icons.Rounded.Compare
                        )
                        Spacer(Modifier.weight(1f))
                        EnhancedButton(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = onDismiss,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            AutoSizeText(stringResource(R.string.close))
                        }
                    }
                }
            },
            visible = visible,
            onDismiss = {
                if (!it) onDismiss()
            },
            dragHandle = {
                EnhancedModalSheetDragHandle(
                    color = Color.Transparent,
                    drawStroke = false
                )
            }
        )
    }
}

@Composable
fun CompareSheet(
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit,
    shape: Shape = RoundedCornerShape(4.dp)
) {
    var progress by rememberSaveable(visible) { mutableFloatStateOf(50f) }

    EnhancedModalBottomSheet(
        sheetContent = {
            Column(
                modifier = Modifier.navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .container(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme
                                .outlineVariant()
                                .copy(alpha = 0.1f),
                            resultPadding = 0.dp
                        )
                        .transparencyChecker()
                        .clipToBounds()
                        .zoomable(rememberZoomState(maxScale = 10f)),
                    contentAlignment = Alignment.Center
                ) {
                    BeforeAfterLayout(
                        overlayStyle = OverlayStyle(),
                        modifier = Modifier.clip(shape),
                        progress = animateFloatAsState(targetValue = progress).value,
                        onProgressChange = {
                            progress = it
                        },
                        enableZoom = false,
                        beforeContent = beforeContent,
                        afterContent = afterContent,
                        beforeLabel = { },
                        afterLabel = { }
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TitleItem(
                        text = stringResource(R.string.compare),
                        icon = Icons.Rounded.Compare
                    )
                    Spacer(Modifier.weight(1f))
                    EnhancedButton(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        onClick = onDismiss,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        AutoSizeText(stringResource(R.string.close))
                    }
                }
            }
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        dragHandle = {
            EnhancedModalSheetDragHandle(
                color = Color.Transparent,
                drawStroke = false
            )
        }
    )
}