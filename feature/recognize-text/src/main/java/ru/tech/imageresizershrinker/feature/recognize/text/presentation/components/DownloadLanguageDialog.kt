/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.recognize.text.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.isNetworkAvailable
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.BasicEnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedAlertDialog
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedButton
import ru.tech.imageresizershrinker.core.ui.widget.other.LoadingIndicator
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText

@Composable
fun DownloadLanguageDialog(
    downloadDialogData: List<UiDownloadData>,
    onDownloadRequest: (List<UiDownloadData>) -> Unit,
    downloadProgress: Float,
    dataRemaining: String,
    onNoConnection: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var downloadStarted by rememberSaveable(downloadDialogData) {
        mutableStateOf(false)
    }

    EnhancedAlertDialog(
        visible = !downloadStarted,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Download,
                contentDescription = null
            )
        },
        title = { Text(stringResource(id = R.string.no_data)) },
        text = {
            Text(
                stringResource(
                    id = R.string.download_description,
                    downloadDialogData.firstOrNull()?.type?.displayName ?: "",
                    downloadDialogData.joinToString(separator = ", ") { it.localizedName }
                )
            )
        },
        onDismissRequest = {},
        confirmButton = {
            EnhancedButton(
                onClick = {
                    if (context.isNetworkAvailable()) {
                        downloadDialogData.let { downloadData ->
                            onDownloadRequest(downloadData)
                            downloadStarted = true
                        }
                    } else onNoConnection()
                }
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
        visible = downloadStarted,
        modifier = Modifier.fillMaxSize()
    ) {
        LoadingIndicator(
            progress = downloadProgress / 100,
            loaderSize = 64.dp
        ) {
            AutoSizeText(
                text = dataRemaining,
                maxLines = 1,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(it * 0.8f),
                textAlign = TextAlign.Center,
                style = LocalTextStyle.current.copy(
                    fontSize = 12.sp,
                    lineHeight = 12.sp
                )
            )
        }
    }
}