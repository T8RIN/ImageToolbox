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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.remove_annotations.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedChip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfAnnotationType

@Composable
internal fun PdfAnnotationTypeSelector(
    values: Set<PdfAnnotationType>,
    onValueChange: (Set<PdfAnnotationType>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .container(
                shape = ShapeDefaults.extraLarge
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(Modifier.height(4.dp))

        Text(
            text = stringResource(R.string.annotations),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterVertically
            ),
            horizontalArrangement = Arrangement.spacedBy(
                space = 8.dp,
                alignment = Alignment.CenterHorizontally
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .container(
                    shape = ShapeDefaults.default,
                    color = MaterialTheme.colorScheme.surface
                )
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            PdfAnnotationType.entries.forEach { type ->
                val selected = type in values

                EnhancedChip(
                    selected = selected,
                    onClick = {
                        onValueChange(values.toggle(type))
                    },
                    selectedColor = MaterialTheme.colorScheme.primary,
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(stringResource(type.title()))
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}

private fun PdfAnnotationType.title(): Int = when (this) {
    PdfAnnotationType.Link -> R.string.annotation_link
    PdfAnnotationType.FileAttachment -> R.string.annotation_file_attachment
    PdfAnnotationType.Line -> R.string.annotation_line
    PdfAnnotationType.Popup -> R.string.annotation_popup
    PdfAnnotationType.Stamp -> R.string.annotation_stamp
    PdfAnnotationType.SquareCircle -> R.string.annotation_shapes
    PdfAnnotationType.Text -> R.string.annotation_text
    PdfAnnotationType.TextMarkup -> R.string.annotation_text_markup
    PdfAnnotationType.Widget -> R.string.annotation_widget
    PdfAnnotationType.Markup -> R.string.annotation_markup
    PdfAnnotationType.Unknown -> R.string.annotation_unknown
}