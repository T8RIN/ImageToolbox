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

import androidx.compose.animation.animateColorAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.Green
import ru.tech.imageresizershrinker.core.ui.theme.Red
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRow

@Composable
internal fun ChecksumResultCard(
    isCorrect: Boolean
) {
    val contentColor by animateColorAsState(
        when {
            isCorrect -> Green
            else -> Red
        }
    )
    val containerColor = contentColor.copy(0.3f)

    PreferenceRow(
        title = if (isCorrect) {
            stringResource(R.string.match)
        } else {
            stringResource(R.string.difference)
        },
        subtitle = if (isCorrect) {
            stringResource(R.string.match_sub)
        } else {
            stringResource(R.string.difference_sub)
        },
        startIcon = if (isCorrect) {
            Icons.Outlined.CheckCircle
        } else {
            Icons.Outlined.WarningAmber
        },
        contentColor = contentColor,
        color = containerColor,
        onClick = null
    )
}