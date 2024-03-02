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

@file:Suppress("KotlinConstantConditions")

package ru.tech.imageresizershrinker.feature.main.presentation.components.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.settings.presentation.LocalSettingsState
import ru.tech.imageresizershrinker.core.ui.shapes.MaterialStarShape
import ru.tech.imageresizershrinker.core.ui.theme.blend
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.modifier.ContainerShapeDefaults
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.modifier.pulsate
import ru.tech.imageresizershrinker.core.ui.widget.preferences.PreferenceRow

@Composable
fun CurrentVersionCodeSettingItem(
    updateAvailable: Boolean,
    onClick: () -> Unit,
    shape: Shape = ContainerShapeDefaults.topShape,
    modifier: Modifier = Modifier.padding(horizontal = 8.dp)
) {
    val settingsState = LocalSettingsState.current
    PreferenceRow(
        shape = shape,
        modifier = Modifier
            .pulsate(
                enabled = updateAvailable,
                range = 0.98f..1.02f
            )
            .then(modifier),
        title = stringResource(R.string.version),
        subtitle = remember {
            "${BuildConfig.VERSION_NAME}${if (BuildConfig.FLAVOR == "foss") "-foss" else ""} (${BuildConfig.VERSION_CODE})"
        },
        startIcon = Icons.Outlined.Verified,
        endContent = {
            Icon(
                painter = painterResource(R.drawable.ic_launcher_monochrome),
                contentDescription = null,
                tint = animateColorAsState(
                    if (settingsState.isNightMode) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.primary.blend(Color.Black)
                    }
                ).value,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
                    .size(64.dp)
                    .container(
                        resultPadding = 0.dp,
                        color = animateColorAsState(
                            if (settingsState.isNightMode) {
                                MaterialTheme.colorScheme.secondaryContainer.blend(
                                    color = Color.Black,
                                    fraction = 0.3f
                                )
                            } else {
                                MaterialTheme.colorScheme.primaryContainer
                            }
                        ).value,
                        borderColor = MaterialTheme.colorScheme.outlineVariant(),
                        shape = MaterialStarShape
                    )
                    .scale(1.25f)
            )
        },
        onClick = onClick
    )
}