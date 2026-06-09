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

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Compare
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.safeAspectRatio
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.derivative.EnhancedZoomableModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.compare.presentation.components.beforeafter.BeforeAfterLayout

@Composable
fun CompareSheet(
    data: Pair<Bitmap?, Bitmap?>?,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    var progress by rememberSaveable(visible) { mutableFloatStateOf(50f) }

    if (data != null) {
        EnhancedZoomableModalBottomSheet(
            visible = visible,
            onDismiss = onDismiss,
            title = {
                TitleItem(
                    text = stringResource(R.string.compare),
                    icon = Icons.Outlined.Compare
                )
            }
        ) {
            data.let { (b, a) ->
                val before = remember(data) { b?.asImageBitmap() }
                val after = remember(data) { a?.asImageBitmap() }
                if (before != null && after != null) {
                    BeforeAfterLayout(
                        progress = animateFloatAsState(targetValue = progress).value,
                        onProgressChange = {
                            progress = it
                        },
                        beforeContent = {
                            Picture(
                                model = before,
                                modifier = Modifier.aspectRatio(before.safeAspectRatio)
                            )
                        },
                        afterContent = {
                            Picture(
                                model = after,
                                modifier = Modifier.aspectRatio(after.safeAspectRatio)
                            )
                        },
                        beforeLabel = { },
                        afterLabel = { }
                    )
                }
            }
        }
    }
}

@Composable
fun CompareSheet(
    beforeContent: @Composable () -> Unit,
    afterContent: @Composable () -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    var progress by rememberSaveable(visible) { mutableFloatStateOf(50f) }

    EnhancedZoomableModalBottomSheet(
        visible = visible,
        onDismiss = onDismiss,
        title = {
            TitleItem(
                text = stringResource(R.string.compare),
                icon = Icons.Outlined.Compare
            )
        }
    ) {
        BeforeAfterLayout(
            progress = animateFloatAsState(targetValue = progress).value,
            onProgressChange = {
                progress = it
            },
            beforeContent = beforeContent,
            afterContent = afterContent,
            beforeLabel = { },
            afterLabel = { }
        )
    }
}