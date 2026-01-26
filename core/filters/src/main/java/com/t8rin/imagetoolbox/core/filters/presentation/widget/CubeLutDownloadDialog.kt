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

package com.t8rin.imagetoolbox.core.filters.presentation.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.remote.DownloadProgress
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.BasicEnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButton
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator

@Composable
internal fun CubeLutDownloadDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    onDownload: () -> Unit,
    downloadOnlyNewData: Boolean,
    cubeLutDownloadProgress: DownloadProgress?
) {
    EnhancedAlertDialog(
        visible = visible,
        icon = {
            Icon(
                imageVector = Icons.Outlined.TableChart,
                contentDescription = "lut"
            )
        },
        title = { Text(stringResource(id = R.string.cube_lut)) },
        text = {
            Text(
                stringResource(
                    if (downloadOnlyNewData) R.string.lut_library_update_sub
                    else R.string.lut_library_sub
                )
            )
        },
        onDismissRequest = {},
        confirmButton = {
            EnhancedButton(
                onClick = onDownload
            ) {
                Text(stringResource(R.string.download))
            }
        },
        dismissButton = {
            EnhancedButton(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = onDismiss
            ) {
                Text(stringResource(R.string.close))
            }
        }
    )

    BasicEnhancedAlertDialog(
        onDismissRequest = {},
        visible = cubeLutDownloadProgress != null,
        modifier = Modifier.fillMaxSize()
    ) {
        EnhancedLoadingIndicator(
            progress = (cubeLutDownloadProgress?.currentPercent ?: 0f),
            loaderSize = 72.dp
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = rememberHumanFileSize(cubeLutDownloadProgress?.currentTotalSize ?: 0),
                    maxLines = 1,
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp,
                    lineHeight = 10.sp
                )
            }
        }
    }
}