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

package com.t8rin.imagetoolbox.feature.watermarking.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.Info
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton

@Composable
internal fun WatermarkPlaceholderInfoButton() {
    var showInfoDialog by rememberSaveable {
        mutableStateOf(false)
    }

    EnhancedIconButton(
        onClick = {
            showInfoDialog = true
        }
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null
        )
    }

    EnhancedAlertDialog(
        visible = showInfoDialog,
        onDismissRequest = { showInfoDialog = false },
        confirmButton = {
            EnhancedButton(
                onClick = { showInfoDialog = false }
            ) {
                Text(stringResource(R.string.close))
            }
        },
        title = {
            Text(stringResource(R.string.watermark_filename_placeholder))
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = null
            )
        },
        text = {
            Text(stringResource(R.string.watermark_filename_placeholder_sub))
        }
    )
}