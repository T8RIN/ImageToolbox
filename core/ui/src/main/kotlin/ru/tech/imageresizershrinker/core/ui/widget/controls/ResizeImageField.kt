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

package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.image.model.ImageInfo
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField

@Composable
fun ResizeImageField(
    imageInfo: ImageInfo,
    originalSize: IntegerSize?,
    onWidthChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit,
    showWarning: Boolean = false
) {
    Column(
        modifier = Modifier
            .container(shape = RoundedCornerShape(24.dp))
            .padding(8.dp)
            .animateContentSize()
    ) {
        Row {
            RoundedTextField(
                value = imageInfo.width.takeIf { it != 0 }
                    .let { it ?: "" }
                    .toString(),
                onValueChange = { value ->
                    val maxValue = if (imageInfo.height != 0) {
                        (ImageUtils.Dimens.MAX_IMAGE_SIZE / imageInfo.height).coerceAtMost(32768)
                    } else 32768

                    onWidthChange(
                        value
                            .restrict(maxValue)
                            .toIntOrNull() ?: 0
                    )
                },
                shape = RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 6.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 6.dp
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                label = {
                    Text(
                        stringResource(
                            R.string.width,
                            originalSize?.width?.toString() ?: ""
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            RoundedTextField(
                value = imageInfo.height.takeIf { it != 0 }
                    .let { it ?: "" }
                    .toString(),
                onValueChange = { value ->
                    val maxValue = if (imageInfo.width != 0) {
                        (ImageUtils.Dimens.MAX_IMAGE_SIZE / imageInfo.width).coerceAtMost(32768)
                    } else 32768

                    onHeightChange(
                        value
                            .restrict(maxValue)
                            .toIntOrNull() ?: 0
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                shape = RoundedCornerShape(
                    topEnd = 12.dp,
                    topStart = 6.dp,
                    bottomEnd = 12.dp,
                    bottomStart = 6.dp
                ),
                label = {
                    Text(
                        stringResource(
                            R.string.height,
                            originalSize?.height?.toString()
                                ?: ""
                        )
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        OOMWarning(visible = showWarning)
    }
}