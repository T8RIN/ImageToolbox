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

package com.t8rin.imagetoolbox.feature.palette_tools.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.theme.inverse
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextFieldColors


@Composable
internal fun PaletteColorNameField(
    value: String,
    onValueChange: (String) -> Unit,
    shape: Shape = ShapeDefaults.large,
    colors: TextFieldColors = RoundedTextFieldColors(
        isError = false,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant.inverse({ 0.2f })
            .copy(0.5f)
    ),
    modifier: Modifier = Modifier
) {
    var isFocused by remember {
        mutableStateOf(false)
    }
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textColor(
                enabled = true,
                isError = false,
                focused = true
            )
        ),
        modifier = modifier
            .background(
                color = colors.containerColor(
                    enabled = true,
                    isError = false,
                    focused = isFocused
                ),
                shape = shape
            )
            .border(
                width = animateDpAsState(
                    if (isFocused) 2.dp else 1.dp
                ).value,
                color = if (isFocused) {
                    colors.focusedIndicatorColor
                } else {
                    colors.unfocusedIndicatorColor
                },
                shape = shape
            )
            .onFocusChanged { isFocused = it.isFocused },
        maxLines = 3,
        cursorBrush = SolidColor(colors.focusedIndicatorColor)
    ) { inner ->
        Box(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
        ) {
            inner()
            if (value.isEmpty()) {
                Text(
                    text = stringResource(id = R.string.color_name),
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}