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

package com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.QrCode
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.model.QrType
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.controls.selection.DataSelector
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedModalBottomSheet
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.enhancedVerticalScroll
import com.t8rin.imagetoolbox.core.ui.widget.modifier.clearFocusOnTap
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.scan_qr_code.presentation.components.editor.QrEditField

@Composable
internal fun QrTypeEditSheet(
    qrType: QrType.Complex?,
    onSave: (QrType.Complex) -> Unit,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    var edited by retain(visible, qrType) {
        mutableStateOf(qrType ?: QrType.Wifi())
    }

    EnhancedModalBottomSheet(
        visible = visible,
        onDismiss = { onDismiss() },
        title = {
            TitleItem(
                text = stringResource(
                    if (qrType == null) R.string.create_barcode
                    else R.string.edit_barcode
                ),
                icon = Icons.Rounded.QrCode
            )
        },
        confirmButton = {
            EnhancedButton(
                onClick = {
                    onSave(edited.updateRaw())
                    onDismiss()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                enabled = !edited.isEmpty()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .enhancedVerticalScroll(rememberScrollState())
                .padding(16.dp)
                .clearFocusOnTap(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DataSelector(
                value = edited,
                onValueChange = {
                    edited = if (qrType != null && it::class.isInstance(qrType)) qrType else it
                },
                itemEqualityDelegate = { t, o -> t::class.isInstance(o) },
                entries = QrType.complexEntries,
                title = null,
                titleIcon = null,
                itemContentText = { stringResource(it.name) },
                itemContentIcon = { item, _ -> item.icon },
                canExpand = false
            )
            QrEditField(
                value = edited,
                onValueChange = { edited = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            )
        }
    }
}