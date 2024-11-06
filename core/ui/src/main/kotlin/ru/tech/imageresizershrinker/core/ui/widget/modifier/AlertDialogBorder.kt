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

package ru.tech.imageresizershrinker.core.ui.widget.modifier

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.settings.presentation.provider.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant

fun Modifier.alertDialogBorder() = this.composed {
    Modifier
        .autoElevatedBorder(
            color = MaterialTheme.colorScheme.outlineVariant(
                luminance = 0.15f,
                onTopOf = MaterialTheme.colorScheme.surfaceContainerHigh
            ),
            shape = AlertDialogDefaults.shape,
            autoElevation = animateDpAsState(
                if (LocalSettingsState.current.drawContainerShadows) 16.dp
                else 0.dp
            ).value
        )
}