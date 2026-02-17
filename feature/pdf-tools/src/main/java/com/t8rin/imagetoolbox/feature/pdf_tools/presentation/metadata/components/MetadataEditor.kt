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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.metadata.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.rounded.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Mop
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfMetadata

@Composable
internal fun MetadataEditor(
    value: PdfMetadata,
    onValueChange: (PdfMetadata) -> Unit,
    onReset: () -> Unit,
    deepClean: Boolean,
    onUpdateDeepClean: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.container(
            shape = ShapeDefaults.extraLarge,
            clip = false
        ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 8.dp
                )
        ) {
            Text(
                fontWeight = FontWeight.Medium,
                text = stringResource(R.string.tags),
                modifier = Modifier.weight(1f),
                fontSize = 18.sp,
            )
            Spacer(Modifier.width(16.dp))

            EnhancedButton(
                onClick = onReset,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                contentPadding = PaddingValues(
                    start = 8.dp,
                    end = 12.dp
                ),
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .height(30.dp),
            ) {
                Row {
                    Icon(
                        imageVector = Icons.Rounded.Restore,
                        contentDescription = "Restore",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        stringResource(R.string.reset)
                    )
                }
            }

        }
        PreferenceRowSwitch(
            title = stringResource(R.string.privacy_deep_clean),
            subtitle = stringResource(R.string.privacy_deep_clean_sub),
            checked = deepClean,
            onClick = onUpdateDeepClean,
            startIcon = Icons.Rounded.Mop,
            shape = ShapeDefaults.default,
            containerColor = MaterialTheme.colorScheme.surface,
            modifier = Modifier.padding(8.dp)
        )

        val previewValue = value.takeIf { !deepClean }

        Box {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .padding(8.dp)
                    .alpha(
                        animateFloatAsState(
                            if (deepClean) 0.5f else 1f
                        ).value
                    )
            ) {
                MetadataField(
                    shape = ShapeDefaults.top,
                    value = previewValue?.title.orEmpty(),
                    onValueChange = {
                        onValueChange(value.copy(title = it))
                    },
                    label = stringResource(R.string.title)
                )
                MetadataField(
                    shape = ShapeDefaults.center,
                    value = previewValue?.author.orEmpty(),
                    onValueChange = {
                        onValueChange(value.copy(author = it))
                    },
                    label = stringResource(R.string.author)
                )
                MetadataField(
                    shape = ShapeDefaults.center,
                    value = previewValue?.subject.orEmpty(),
                    onValueChange = {
                        onValueChange(value.copy(subject = it))
                    },
                    label = stringResource(R.string.subject)
                )
                MetadataField(
                    shape = ShapeDefaults.center,
                    value = previewValue?.keywords.orEmpty(),
                    onValueChange = {
                        onValueChange(value.copy(keywords = it))
                    },
                    label = stringResource(R.string.keywords)
                )
                MetadataField(
                    shape = ShapeDefaults.center,
                    value = previewValue?.creator.orEmpty(),
                    onValueChange = {
                        onValueChange(value.copy(creator = it))
                    },
                    label = stringResource(R.string.creator)
                )
                MetadataField(
                    shape = ShapeDefaults.bottom,
                    value = previewValue?.producer.orEmpty(),
                    onValueChange = {
                        onValueChange(value.copy(producer = it))
                    },
                    label = stringResource(R.string.producer)
                )
            }

            if (deepClean) {
                Surface(
                    modifier = Modifier.matchParentSize(),
                    color = Color.Transparent
                ) { }
            }
        }
    }
}

@Composable
private fun MetadataField(
    shape: Shape,
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    RoundedTextField(
        modifier = Modifier
            .container(
                shape = shape,
                color = MaterialTheme.colorScheme.surface,
                resultPadding = 8.dp
            ),
        value = value,
        endIcon = {
            if (value.isNotEmpty()) {
                EnhancedIconButton(
                    onClick = { onValueChange("") },
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = stringResource(R.string.cancel),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        singleLine = false,
        onValueChange = onValueChange,
        label = {
            Text(label)
        }
    )
}

@Composable
@EnPreview
private fun Preview() = ImageToolboxThemeForPreview(true) {
    MetadataEditor(
        value = PdfMetadata(),
        onValueChange = {},
        onReset = {},
        deepClean = false,
        onUpdateDeepClean = {},
        modifier = Modifier.padding(16.dp)
    )
}