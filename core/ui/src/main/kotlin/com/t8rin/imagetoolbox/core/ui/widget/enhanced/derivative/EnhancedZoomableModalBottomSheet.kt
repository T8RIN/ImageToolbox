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

package com.t8rin.imagetoolbox.core.ui.widget.enhanced.derivative

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedBottomSheetDefaults
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.ZoomBadge
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun EnhancedZoomableModalBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    containerColor: Color = EnhancedBottomSheetDefaults.barContainerColor,
    confirmButton: @Composable () -> Unit = {
        EnhancedButton(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            onClick = onDismiss,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            AutoSizeText(stringResource(R.string.close))
        }
    },
    dragHandle: @Composable ColumnScope.() -> Unit = {
        EnhancedModalSheetDragHandle(
            color = containerColor,
            drawStroke = false,
            heightWhenDisabled = 20.dp
        )
    },
    title: @Composable RowScope.() -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    EnhancedModalBottomSheet(
        sheetContent = {
            val zoomState = rememberZoomState(maxScale = 20f)

            Column(
                modifier = Modifier
                    .background(containerColor)
                    .navigationBarsPadding()
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                            .clip(ShapeDefaults.extraSmall)
                            .transparencyChecker()
                            .clipToBounds()
                            .zoomable(
                                zoomState = zoomState
                            ),
                        contentAlignment = Alignment.Center,
                        content = content
                    )
                    ZoomBadge(
                        zoomLevel = zoomState.scale,
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    title()
                    Spacer(Modifier.weight(1f))
                    confirmButton()
                }
            }
        },
        visible = visible,
        onDismiss = {
            if (!it) onDismiss()
        },
        dragHandle = dragHandle
    )
}