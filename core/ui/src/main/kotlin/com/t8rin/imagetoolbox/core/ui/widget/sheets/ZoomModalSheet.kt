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

package com.t8rin.imagetoolbox.core.ui.widget.sheets

import android.content.res.Resources
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.asDrawable
import coil3.transform.Transformation
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalSheetDragHandle
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.other.BoxAnimatedVisibility
import com.t8rin.imagetoolbox.core.ui.widget.text.AutoSizeText
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@Composable
fun ZoomModalSheet(
    data: Any?,
    visible: Boolean,
    onDismiss: () -> Unit,
    transformations: List<Transformation> = emptyList()
) {
    val sheetContent: @Composable ColumnScope.() -> Unit = {
        val zoomState = rememberZoomState(maxScale = 15f)
        var aspectRatio by remember(data) {
            mutableFloatStateOf(1f)
        }
        Column(
            modifier = Modifier.navigationBarsPadding()
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                        .container(
                            shape = ShapeDefaults.extraSmall,
                            color = MaterialTheme.colorScheme
                                .outlineVariant()
                                .copy(alpha = 0.1f),
                            resultPadding = 0.dp
                        )
                        .transparencyChecker()
                        .clipToBounds()
                        .zoomable(
                            zoomState = zoomState
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Picture(
                        model = data,
                        contentDescription = null,
                        onSuccess = {
                            aspectRatio =
                                it.result.image.asDrawable(Resources.getSystem()).safeAspectRatio
                        },
                        contentScale = ContentScale.FillBounds,
                        showTransparencyChecker = false,
                        transformations = transformations,
                        shape = RectangleShape,
                        modifier = Modifier.aspectRatio(aspectRatio)
                    )
                }
                val zoomLevel = zoomState.scale
                BoxAnimatedVisibility(
                    visible = zoomLevel > 1f,
                    modifier = Modifier
                        .padding(
                            horizontal = 24.dp,
                            vertical = 8.dp
                        )
                        .align(Alignment.TopStart),
                    enter = scaleIn() + fadeIn(),
                    exit = scaleOut() + fadeOut()
                ) {
                    Text(
                        text = stringResource(R.string.zoom) + " ${zoomLevel.roundToTwoDigits()}x",
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.scrim.copy(0.4f),
                                ShapeDefaults.circle
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White
                    )
                }
            }
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleItem(text = stringResource(R.string.zoom), icon = Icons.Rounded.ZoomIn)
                Spacer(Modifier.weight(1f))
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = onDismiss,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    AutoSizeText(stringResource(R.string.close))
                }
            }
        }
    }

    if (data != null) {
        EnhancedModalBottomSheet(
            sheetContent = sheetContent,
            visible = visible,
            onDismiss = {
                if (!it) onDismiss()
            },
            dragHandle = {
                EnhancedModalSheetDragHandle(
                    color = Color.Transparent,
                    drawStroke = false,
                    heightWhenDisabled = 20.dp
                )
            }
        )
    }
}