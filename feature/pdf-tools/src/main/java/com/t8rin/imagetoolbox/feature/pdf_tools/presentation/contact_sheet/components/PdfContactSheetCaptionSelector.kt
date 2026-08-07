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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.contact_sheet.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.utils.ListUtils.toggle
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Title
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceRowSwitch
import com.t8rin.imagetoolbox.core.ui.widget.text.RoundedTextField
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfContactSheetCaptionField

@Composable
internal fun PdfContactSheetCaptionSelector(
    fields: Set<PdfContactSheetCaptionField>,
    customText: String,
    onFieldsChange: (Set<PdfContactSheetCaptionField>) -> Unit,
    onCustomTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val defaultFields = PdfContactSheetCaptionField.defaultFields

    var lastSelectedFields by remember {
        mutableStateOf(fields.ifEmpty { defaultFields })
    }
    LaunchedEffect(fields) {
        if (fields.isNotEmpty()) lastSelectedFields = fields
    }

    val entries = remember {
        PdfContactSheetCaptionField.entries.map { setOf(it) }
    }

    PreferenceRowSwitch(
        title = stringResource(R.string.image_captions),
        subtitle = stringResource(R.string.image_captions_sub),
        checked = fields.isNotEmpty(),
        startIcon = Icons.Rounded.Title,
        onClick = { enabled ->
            onFieldsChange(
                if (enabled) lastSelectedFields.ifEmpty { defaultFields }
                else emptySet()
            )
        },
        modifier = modifier.fillMaxWidth(),
        shape = ShapeDefaults.large,
        additionalContent = {
            AnimatedVisibility(
                visible = fields.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .container(
                            color = MaterialTheme.colorScheme.surface
                        )
                ) {
                    DataSelector(
                        value = fields,
                        onValueChange = { entry ->
                            entry.firstOrNull()?.let { field ->
                                val updatedFields = fields.toggle(field)
                                if (updatedFields.isNotEmpty()) lastSelectedFields =
                                    updatedFields
                                onFieldsChange(updatedFields)
                            }
                        },
                        entries = entries,
                        title = null,
                        titleIcon = null,
                        itemContentText = { entry ->
                            entry.firstOrNull()?.let { stringResource(it.title()) }
                        },
                        itemEqualityDelegate = { selectedFields, entry ->
                            entry.any(selectedFields::contains)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        selectedItemColor = MaterialTheme.colorScheme.secondaryContainer,
                        canExpand = false,
                        key = { it.first().name },
                        behaveAsContainer = false
                    )
                    AnimatedVisibility(PdfContactSheetCaptionField.CustomText in fields) {
                        RoundedTextField(
                            value = customText,
                            onValueChange = onCustomTextChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            label = stringResource(R.string.caption_custom_text),
                            hint = stringResource(R.string.caption_custom_text_hint),
                            singleLine = false,
                            shape = ShapeDefaults.default
                        )
                    }
                }
            }
        }
    )
}

private fun PdfContactSheetCaptionField.title(): Int = when (this) {
    PdfContactSheetCaptionField.FileName -> R.string.filename
    PdfContactSheetCaptionField.FileNameWithoutExtension -> R.string.caption_file_name_without_extension
    PdfContactSheetCaptionField.SequenceNumber -> R.string.caption_sequence_number
    PdfContactSheetCaptionField.ImageDimensions -> R.string.caption_image_dimensions
    PdfContactSheetCaptionField.FileSize -> R.string.file_size
    PdfContactSheetCaptionField.DateTaken -> R.string.caption_date_taken
    PdfContactSheetCaptionField.CameraModel -> R.string.caption_camera_model
    PdfContactSheetCaptionField.Lens -> R.string.caption_lens
    PdfContactSheetCaptionField.ExposureSettings -> R.string.caption_exposure_settings
    PdfContactSheetCaptionField.ParentFolder -> R.string.caption_parent_folder
    PdfContactSheetCaptionField.CustomText -> R.string.caption_custom_text
}
