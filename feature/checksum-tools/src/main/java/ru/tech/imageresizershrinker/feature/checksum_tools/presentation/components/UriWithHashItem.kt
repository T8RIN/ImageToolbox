/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.checksum_tools.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilePresent
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.resources.shapes.CloverShape
import ru.tech.imageresizershrinker.core.ui.utils.helper.ContextUtils.rememberFilename
import ru.tech.imageresizershrinker.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceItemOverload

@Composable
internal fun UriWithHashItem(
    uriWithHash: UriWithHash,
    onCopyText: (String) -> Unit
) {
    val (uri, checksum) = uriWithHash

    val filename = rememberFilename(uri) ?: stringResource(R.string.filename)

    val fileSize = rememberHumanFileSize(uri)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PreferenceItemOverload(
            title = filename,
            subtitle = fileSize,
            startIcon = {
                Icon(
                    imageVector = Icons.Outlined.FilePresent,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CloverShape)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer.copy(
                                0.5f
                            )
                        )
                        .padding(8.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            drawStartIconContainer = false
        )

        ChecksumPreviewField(
            value = checksum,
            onCopyText = onCopyText
        )
    }
}