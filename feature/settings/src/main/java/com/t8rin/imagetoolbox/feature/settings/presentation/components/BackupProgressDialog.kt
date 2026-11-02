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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.t8rin.imagetoolbox.core.domain.utils.humanFileSize
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.DownloadFile
import com.t8rin.imagetoolbox.core.resources.icons.UploadFile
import com.t8rin.imagetoolbox.core.settings.domain.model.BackupProgress
import com.t8rin.imagetoolbox.core.ui.theme.ImageToolboxThemeForPreview
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedAlertDialog
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedLoadingIndicator
import kotlin.math.roundToInt

@Composable
fun BackupProgressDialog(
    visible: Boolean,
    title: Int,
    progress: BackupProgress
) {
    val fraction = progress.fraction
    val animatedProgress by animateFloatAsState(fraction ?: 0f)

    EnhancedAlertDialog(
        visible = visible,
        onDismissRequest = {},
        confirmButton = {},
        icon = {
            Icon(
                imageVector = if (title == R.string.backup_restoring) {
                    Icons.Outlined.DownloadFile
                } else {
                    Icons.Outlined.UploadFile
                },
                contentDescription = null
            )
        },
        title = { Text(stringResource(title)) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box {
                    EnhancedLoadingIndicator(
                        progress = animatedProgress,
                        text = "${(animatedProgress * 100).roundToInt()}%",
                        predicate = { animatedProgress > 0 },
                        loaderSize = 68.dp
                    )
                }
                Text(
                    text = stringResource(
                        R.string.backup_progress_bytes,
                        humanFileSize(progress.completedBytes),
                        progress.totalBytes?.let(::humanFileSize) ?: "…"
                    ),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}

@Preview(widthDp = 420, heightDp = 700)
@Composable
private fun BackupPreparingPreview() = BackupProgressPreviewContent {
    BackupProgressDialog(
        visible = true,
        title = R.string.backup_creating,
        progress = BackupProgress()
    )
}

@Preview(widthDp = 420, heightDp = 700)
@Composable
private fun BackupWritingPreview() = BackupProgressPreviewContent {
    BackupProgressDialog(
        visible = true,
        title = R.string.backup_creating,
        progress = BackupProgress(1_800_000_000L, 4_500_000_000L)
    )
}

@Preview(widthDp = 420, heightDp = 700)
@Composable
private fun BackupRestoringPreview() = BackupProgressPreviewContent {
    BackupProgressDialog(
        visible = true,
        title = R.string.backup_restoring,
        progress = BackupProgress(1_500_000_000L, 4_500_000_000L)
    )
}

@Preview(widthDp = 420, heightDp = 700)
@Composable
private fun BackupCompletePreview() = BackupProgressPreviewContent {
    BackupProgressDialog(
        visible = true,
        title = R.string.backup_creating,
        progress = BackupProgress(4_500_000_000L, 4_500_000_000L)
    )
}

@Preview(widthDp = 420, heightDp = 700)
@Composable
private fun BackupUnknownSizePreview() = BackupProgressPreviewContent {
    BackupProgressDialog(
        visible = true,
        title = R.string.backup_restoring,
        progress = BackupProgress(640_000_000L)
    )
}

@Composable
private fun BackupProgressPreviewContent(
    content: @Composable () -> Unit
) = ImageToolboxThemeForPreview(isDarkTheme = true) {
    Box(Modifier.fillMaxSize()) {
        content()
    }
}
