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

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ZoomIn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.transform.Transformation
import com.smarttoolfactory.colordetector.util.ColorUtil.roundToTwoDigits
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.image.Picture
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.other.BoxAnimatedVisibility
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZoomModalSheet(
    data: Any?,
    visible: MutableState<Boolean>,
    transformations: List<Transformation> = emptyList()
) {
    var showSheet by visible

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
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme
                                .outlineVariant()
                                .copy(alpha = 0.1f),
                            resultPadding = 0.dp
                        )
                        .transparencyChecker()
                        .zoomable(
                            zoomState = zoomState
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Picture(
                        model = data,
                        contentDescription = null,
                        onSuccess = {
                            aspectRatio = it.result.drawable.run {
                                intrinsicWidth / intrinsicHeight.toFloat()
                            }
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
                            .background(Color.Black.copy(0.4f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitleItem(text = stringResource(R.string.zoom), icon = Icons.Rounded.ZoomIn)
                Spacer(Modifier.weight(1f))
                EnhancedButton(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    onClick = {
                        showSheet = false
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    AutoSizeText(stringResource(R.string.close))
                }
            }
        }
    }

    if (data != null) {
        SimpleSheet(
            sheetContent = sheetContent,
            visible = visible,
            dragHandle = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    BottomSheetDefaults.DragHandle()
                }
            }
        )
    }
}