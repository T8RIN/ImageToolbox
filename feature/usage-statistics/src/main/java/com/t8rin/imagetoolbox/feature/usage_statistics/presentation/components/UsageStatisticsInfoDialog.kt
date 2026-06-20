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

package com.t8rin.imagetoolbox.feature.usage_statistics.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.DeleteSweep
import com.t8rin.imagetoolbox.core.resources.icons.FinanceMode
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedIconButton

@Composable
fun UsageStatisticsInfoDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onWantReset: () -> Unit
) {
    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                EnhancedIconButton(
                    onClick = onWantReset,
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteSweep,
                        contentDescription = stringResource(R.string.reset_usage_statistics)
                    )
                }
                EnhancedButton(
                    onClick = onDismiss
                ) {
                    Text(stringResource(R.string.close))
                }
            }
        },
        icon = {
            Icon(
                imageVector = Icons.Outlined.FinanceMode,
                contentDescription = null
            )
        },
        title = {
            Text(stringResource(R.string.usage_statistics))
        },
        text = {
            Text(stringResource(R.string.usage_statistics_info))
        }
    )
}