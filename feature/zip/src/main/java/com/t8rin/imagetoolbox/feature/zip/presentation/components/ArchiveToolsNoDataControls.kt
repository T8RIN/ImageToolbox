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

package com.t8rin.imagetoolbox.feature.zip.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FolderZip
import com.t8rin.imagetoolbox.core.resources.icons.Unarchive
import com.t8rin.imagetoolbox.core.ui.utils.helper.isPortraitOrientationAsState
import com.t8rin.imagetoolbox.core.ui.widget.modifier.withModifier
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
internal fun ArchiveToolsNoDataControls(
    onArchive: () -> Unit,
    onExtract: () -> Unit
) {
    val isPortrait by isPortraitOrientationAsState()
    val archivePreference = @Composable {
        PreferenceItem(
            title = stringResource(R.string.archive),
            subtitle = stringResource(R.string.archive_sub),
            startIcon = Icons.Outlined.FolderZip,
            modifier = Modifier.fillMaxWidth(),
            onClick = onArchive
        )
    }
    val extractPreference = @Composable {
        PreferenceItem(
            title = stringResource(R.string.extract),
            subtitle = stringResource(R.string.extract_sub),
            startIcon = Icons.Outlined.Unarchive,
            modifier = Modifier.fillMaxWidth(),
            onClick = onExtract
        )
    }

    if (isPortrait) {
        Column(Modifier.padding(12.dp)) {
            archivePreference()
            Spacer(Modifier.height(12.dp))
            extractPreference()
        }
    } else {
        Row(Modifier.padding(12.dp)) {
            archivePreference.withModifier(Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            extractPreference.withModifier(Modifier.weight(1f))
        }
    }
}
