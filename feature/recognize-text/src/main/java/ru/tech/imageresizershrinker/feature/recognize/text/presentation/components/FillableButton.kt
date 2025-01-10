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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.widget.enhanced.hapticsClickable
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@Composable
internal fun FillableButton(
    modifier: Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .container(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(0.5f),
                shape = ButtonDefaults.shape,
                resultPadding = 0.dp
            )
            .hapticsClickable(onClick = onClick)
            .padding(ButtonDefaults.ContentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            content()
        }
    }
}