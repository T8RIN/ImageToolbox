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

package com.t8rin.imagetoolbox.feature.duplicate_finder.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.FolderMatch
import com.t8rin.imagetoolbox.core.resources.icons.ImageSearch
import com.t8rin.imagetoolbox.core.ui.utils.helper.ImageUtils.rememberHumanFileSize
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItem

@Composable
internal fun ResultSummary(
    exactGroupCount: Int,
    similarGroupCount: Int,
    reclaimableBytes: Long,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        PreferenceItem(
            title = stringResource(R.string.exact_copies),
            subtitle = exactGroupCount.takeIf { it > 0 }?.toString()
                ?: stringResource(R.string.not_found),
            startIcon = Icons.Outlined.FolderMatch,
            shape = ShapeDefaults.top,
            modifier = Modifier.fillMaxWidth()
        )
        PreferenceItem(
            title = stringResource(R.string.similar_images),
            subtitle = stringResource(
                R.string.duplicate_groups_summary,
                similarGroupCount + exactGroupCount,
                rememberHumanFileSize(reclaimableBytes)
            ),
            startIcon = Icons.Outlined.ImageSearch,
            shape = ShapeDefaults.bottom,
            modifier = Modifier.fillMaxWidth()
        )
    }
}