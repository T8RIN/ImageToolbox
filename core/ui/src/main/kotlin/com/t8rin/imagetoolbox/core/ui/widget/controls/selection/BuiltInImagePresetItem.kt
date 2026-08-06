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

package com.t8rin.imagetoolbox.core.ui.widget.controls.selection

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.domain.image.model.BuiltInImageExportProfile
import com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfile
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonChecked
import com.t8rin.imagetoolbox.core.resources.icons.RadioButtonUnchecked
import com.t8rin.imagetoolbox.core.ui.theme.takeColorFromScheme
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.preferences.PreferenceItemOverload

@Composable
internal fun BuiltInImagePresetItem(
    index: Int,
    profilesCount: Int,
    item: BuiltInImageExportProfile,
    selected: Boolean,
    onApplyProfile: (ImageExportProfile) -> Unit
) {
    PreferenceItemOverload(
        title = stringResource(item.titleRes),
        subtitle = item.profile.subtitle(),
        onClick = {
            onApplyProfile(item.profile)
        },
        drawStartIconContainer = false,
        modifier = Modifier.fillMaxWidth(),
        startIcon = {
            AnimatedContent(
                targetState = selected,
                modifier = Modifier.size(24.dp)
            ) { selected ->
                Icon(
                    imageVector = if (selected) {
                        Icons.Rounded.RadioButtonChecked
                    } else {
                        Icons.Rounded.RadioButtonUnchecked
                    },
                    contentDescription = null
                )
            }
        },
        shape = ShapeDefaults.byIndex(index, profilesCount),
        containerColor = takeColorFromScheme {
            if (selected) {
                primaryContainer
            } else {
                surfaceContainerLow
            }
        },
        contentColor = takeColorFromScheme {
            if (selected) {
                onPrimaryContainer
            } else {
                onSurface
            }
        }
    )
}
