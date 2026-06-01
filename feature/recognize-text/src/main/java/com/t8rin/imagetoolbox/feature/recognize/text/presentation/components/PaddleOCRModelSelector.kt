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

package com.t8rin.imagetoolbox.feature.recognize.text.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Language
import com.t8rin.imagetoolbox.core.resources.icons.MiniEdit
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedFlingBehavior
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.neural_tools.ocr.PaddleOCR

@Composable
fun PaddleOCRModelSelector(
    value: PaddleOCR.Model,
    onValueChange: (PaddleOCR.Model) -> Unit
) {
    var showSelectionSheet by remember {
        mutableStateOf(false)
    }

    PreferenceItem(
        modifier = Modifier.fillMaxWidth(),
        title = stringResource(id = R.string.language),
        subtitle = value.title,
        onClick = {
            showSelectionSheet = true
        },
        shape = ShapeDefaults.extraLarge,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        startIcon = Icons.Rounded.Language,
        endIcon = Icons.Rounded.MiniEdit
    )

    EnhancedModalBottomSheet(
        visible = showSelectionSheet,
        onDismiss = {
            showSelectionSheet = it
        },
        confirmButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = {
                    showSelectionSheet = false
                }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            TitleItem(
                text = stringResource(id = R.string.language),
                icon = Icons.Rounded.Language
            )
        }
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            flingBehavior = enhancedFlingBehavior()
        ) {
            itemsIndexed(PaddleOCR.Model.entries) { index, model ->
                PreferenceItem(
                    modifier = Modifier.fillMaxWidth(),
                    title = model.title,
                    onClick = {
                        onValueChange(model)
                    },
                    containerColor = animateColorAsState(
                        if (value == model) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surfaceContainer
                    ).value,
                    shape = ShapeDefaults.byIndex(
                        index = index,
                        size = PaddleOCR.Model.entries.size
                    )
                )
            }
        }
    }
}