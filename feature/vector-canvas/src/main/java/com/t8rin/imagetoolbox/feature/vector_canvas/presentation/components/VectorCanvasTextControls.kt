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

@file:Suppress("FunctionNaming", "PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.EditAlt
import com.t8rin.imagetoolbox.core.resources.icons.FormatAlignCenter
import com.t8rin.imagetoolbox.core.resources.icons.FormatAlignLeft
import com.t8rin.imagetoolbox.core.resources.icons.FormatAlignRight
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.feature.draw.presentation.components.DrawColorSelector
import com.t8rin.imagetoolbox.feature.draw.presentation.components.LineWidthSelector
import com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components.model.VectorCanvasSelection
import io.ak1.drawbox.domain.model.TextAlignment
import io.ak1.drawbox.presentation.viewmodel.DrawBoxController

@Composable
internal fun VectorCanvasTextControls(
    selection: VectorCanvasSelection,
    controller: DrawBoxController,
    onEditText: () -> Unit,
    modifier: Modifier = Modifier
) {
    DrawColorSelector(
        value = selection.strokeColor,
        onValueChange = {
            if (selection.text != null) {
                controller.setSelectionColor(it)
            } else {
                controller.setColor(it)
            }
        },
        titleText = stringResource(R.string.text_color),
        modifier = modifier
    )
    LineWidthSelector(
        value = selection.fontSize,
        title = stringResource(R.string.font_size),
        valueRange = 8f..96f,
        onValueChange = {
            if (selection.text != null) {
                controller.setSelectionFontSize(it)
            } else {
                controller.setFontSize(it)
            }
        },
        modifier = modifier
    )
    TextAlignmentSelector(
        value = selection.textAlignment,
        onValueChange = {
            if (selection.text != null) {
                controller.setSelectionTextAlignment(it)
            } else {
                controller.setTextAlignment(it)
            }
        },
        modifier = modifier
    )
    FontFamilySelector(
        value = selection.fontFamilyKey,
        onValueChange = {
            if (selection.text != null) {
                controller.setSelectionFontFamily(it)
            } else {
                controller.setFontFamily(it)
            }
        },
        modifier = modifier
    )
    if (selection.text != null) {
        PreferenceItem(
            title = stringResource(R.string.vector_canvas_edit_text),
            startIcon = Icons.Rounded.EditAlt,
            onClick = onEditText,
            modifier = modifier,
            shape = ShapeDefaults.extraLarge
        )
    }
}

@Composable
private fun TextAlignmentSelector(
    value: TextAlignment,
    onValueChange: (TextAlignment) -> Unit,
    modifier: Modifier
) {
    EnhancedButtonGroup(
        modifier = modifier.container(ShapeDefaults.extraLarge),
        entries = TextAlignment.entries,
        value = value,
        itemContent = { alignment ->
            Icon(
                imageVector = when (alignment) {
                    TextAlignment.LEFT -> Icons.Rounded.FormatAlignLeft
                    TextAlignment.CENTER -> Icons.Rounded.FormatAlignCenter
                    TextAlignment.RIGHT -> Icons.Rounded.FormatAlignRight
                },
                contentDescription = alignment.name
            )
        },
        title = stringResource(R.string.alignment),
        onValueChange = onValueChange
    )
}

@Composable
private fun FontFamilySelector(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier
) {
    val families = listOf("sans", "serif", "mono")
    EnhancedButtonGroup(
        modifier = modifier.container(ShapeDefaults.extraLarge),
        entries = families,
        value = value.takeIf { it in families } ?: families.first(),
        itemContent = { family ->
            Text(
                stringResource(
                    when (family) {
                        "serif" -> R.string.vector_canvas_font_serif
                        "mono" -> R.string.vector_canvas_font_mono
                        else -> R.string.vector_canvas_font_sans
                    }
                )
            )
        },
        title = stringResource(R.string.font),
        onValueChange = onValueChange
    )
}
