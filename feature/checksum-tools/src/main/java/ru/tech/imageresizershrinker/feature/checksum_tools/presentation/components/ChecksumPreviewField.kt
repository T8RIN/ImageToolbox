/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.checksum_tools.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedIconButton
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextField
import ru.tech.imageresizershrinker.core.ui.widget.text.RoundedTextFieldColors

@Composable
internal fun ChecksumPreviewField(
    value: String,
    onCopyText: (String) -> Unit,
    label: String = stringResource(R.string.source_checksum)
) {
    RoundedTextField(
        modifier = Modifier
            .container(
                shape = MaterialTheme.shapes.large,
                resultPadding = 8.dp,
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(
                    0.2f
                )
            ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        onValueChange = {},
        singleLine = false,
        readOnly = true,
        value = value,
        endIcon = {
            AnimatedVisibility(value.isNotBlank()) {
                EnhancedIconButton(
                    onClick = {
                        onCopyText(value)
                    },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = stringResource(
                            R.string.copy
                        )
                    )
                }
            }
        },
        colors = RoundedTextFieldColors(
            isError = false,
            containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(0.3f),
            focusedIndicatorColor = MaterialTheme.colorScheme.tertiary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                0.5f
            )
        ),
        label = label
    )
}