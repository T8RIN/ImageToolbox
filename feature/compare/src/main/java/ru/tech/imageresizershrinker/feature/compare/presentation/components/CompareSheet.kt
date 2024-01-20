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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.smarttoolfactory.beforeafter.BeforeAfterImage
import com.smarttoolfactory.beforeafter.OverlayStyle
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.buttons.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.transparencyChecker
import ru.tech.imageresizershrinker.core.ui.widget.sheets.SimpleSheet
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareSheet(
    data: Pair<Bitmap?, Bitmap?>?,
    visible: MutableState<Boolean>
) {
    var progress by rememberSaveable { mutableFloatStateOf(50f) }

    val settingsState = LocalSettingsState.current
    var showSheet by visible

    if (data != null) {
        SimpleSheet(
            sheetContent = {
                Column(
                    Modifier.navigationBarsPadding()
                ) {
                    data.let { (b, a) ->
                        val before = remember(data) { b?.asImageBitmap() }
                        val after = remember(data) { a?.asImageBitmap() }
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                )
                                .border(
                                    settingsState.borderWidth,
                                    MaterialTheme.colorScheme.outlineVariant(),
                                    RoundedCornerShape(4.dp)
                                )
                                .background(
                                    MaterialTheme.colorScheme
                                        .outlineVariant()
                                        .copy(alpha = 0.1f),
                                    RoundedCornerShape(4.dp)
                                )
                                .weight(1f, false)
                                .clip(RoundedCornerShape(4.dp))
                                .zoomable(rememberZoomState(maxScale = 10f))
                        ) {
                            if (before != null && after != null) {
                                BeforeAfterImage(
                                    overlayStyle = OverlayStyle(),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .align(Alignment.Center)
                                        .transparencyChecker(),
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
                            onClick = {
                                showSheet = false
                            },
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            AutoSizeText(stringResource(R.string.close))
                        }
                    }
                }
            },
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