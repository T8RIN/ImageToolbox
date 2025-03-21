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

package ru.tech.imageresizershrinker.core.crash.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.EnhancedFloatingActionButton
import ru.tech.imageresizershrinker.core.ui.widget.text.AutoSizeText

@Composable
internal fun CrashBottomButtons(
    modifier: Modifier,
    onCopy: () -> Unit,
    onRestartApp: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .navigationBarsPadding()
            .displayCutoutPadding()
    ) {
        EnhancedFloatingActionButton(
            modifier = Modifier
                .weight(1f, false),
            onClick = onRestartApp,
            content = {
                Spacer(Modifier.width(16.dp))
                Icon(
                    imageVector = Icons.Rounded.RestartAlt,
                    contentDescription = stringResource(R.string.restart_app)
                )
                Spacer(Modifier.width(16.dp))
                AutoSizeText(
                    text = stringResource(R.string.restart_app),
                    maxLines = 1
                )
                Spacer(Modifier.width(16.dp))
            }
        )
        Spacer(Modifier.width(8.dp))
        EnhancedFloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            onClick = onCopy
        ) {
            Icon(
                imageVector = Icons.Rounded.ContentCopy,
                contentDescription = stringResource(R.string.copy)
            )
        }
    }
}