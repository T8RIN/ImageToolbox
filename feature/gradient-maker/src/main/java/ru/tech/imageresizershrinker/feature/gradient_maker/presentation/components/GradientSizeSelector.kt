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

package ru.tech.imageresizershrinker.feature.gradient_maker.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.restrict
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextFieldColors

@Composable
fun GradientSizeSelector(
    value: IntegerSize,
    onWidthChange: (Int) -> Unit,
    onHeightChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.container(
            shape = RoundedCornerShape(24.dp)
        )
    ) {
        val (width, height) = value
        RoundedTextField(
            value = width.takeIf { it != 0 }?.toString() ?: "",
            onValueChange = {
                onWidthChange(it.restrict(8192).toIntOrNull() ?: 0)
            },
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            label = {
                Text(stringResource(R.string.width, " "))
            },
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = 8.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                    end = 4.dp
                ),
            colors = RoundedTextFieldColors(
                isError = false,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
        RoundedTextField(
            value = height.takeIf { it != 0 }?.toString() ?: "",
            onValueChange = {
                onHeightChange(it.restrict(8192).toIntOrNull() ?: 0)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            shape = RoundedCornerShape(12.dp),
            label = {
                Text(stringResource(R.string.height, " "))
            },
            modifier = Modifier
                .weight(1f)
                .padding(
                    start = 4.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                    end = 8.dp
                ),
            colors = RoundedTextFieldColors(
                isError = false,
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
    }
}