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

package com.t8rin.imagetoolbox.feature.pdf_tools.presentation.signature.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Signature
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.theme.outlineVariant
import com.t8rin.imagetoolbox.core.ui.utils.helper.EnPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.hapticsClickable
import com.t8rin.imagetoolbox.core.ui.widget.image.Picture
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.transparencyChecker
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
fun SignatureSelector(
    savedSignatures: List<String>,
    onAdd: (Any) -> Unit,
    onSelect: (Any) -> Unit
) {
    var isDrawingVisible by remember {
        mutableStateOf(false)
    }

    PreferenceItem(
        shape = ShapeDefaults.large,
        onClick = { isDrawingVisible = true },
        title = stringResource(R.string.draw_signature),
        subtitle = stringResource(R.string.draw_signature_sub),
        startIcon = Icons.Outlined.Signature,
        endIcon = Icons.Rounded.AddCircleOutline,
        modifier = Modifier.fillMaxWidth(),
        bottomContent = if (savedSignatures.isNotEmpty()) {
            {
                val shape = ShapeDefaults.mini
                LazyRow(
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(savedSignatures) { signature ->
                        Picture(
                            model = signature,
                            modifier = Modifier
                                .size(40.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant(),
                                    shape = shape
                                )
                                .clip(shape)
                                .transparencyChecker(
                                    checkerWidth = 6.dp,
                                    checkerHeight = 6.dp
                                )
                                .background(Color.White.copy(0.4f))
                                .hapticsClickable {
                                    onSelect(signature)
                                },
                            showTransparencyChecker = false,
                            shape = RectangleShape
                        )
                    }
                }
            }
        } else null
    )

    SignatureDialog(
        visible = isDrawingVisible,
        onDismiss = { isDrawingVisible = false },
        onDone = onAdd
    )
}

@Composable
@EnPreview
private fun Preview() = ImageToolboxThemeForPreview(false) {
    SignatureSelector(
        savedSignatures = List(10) { "qwqw" },
        onSelect = {},
        onAdd = {}
    )
}