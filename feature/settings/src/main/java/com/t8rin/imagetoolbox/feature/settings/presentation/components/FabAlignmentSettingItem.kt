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

package com.t8rin.imagetoolbox.feature.settings.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ViewSidebar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.presentation.provider.LocalSettingsState
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.settings.presentation.components.additional.FabPreview

@Composable
fun FabAlignmentSettingItem(
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp),
    shape: Shape = ShapeDefaults.bottom
) {
    val settingsState = LocalSettingsState.current

    Row(
        modifier
            .height(IntrinsicSize.Max)
            .container(
                shape = shape
            )
            .animateContentSizeNoClip()
            .padding(
                start = 4.dp,
                top = 4.dp,
                bottom = 4.dp,
                end = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val derivedValue by remember(settingsState) {
            derivedStateOf {
                when (settingsState.fabAlignment) {
                    Alignment.BottomStart -> 0
                    Alignment.BottomCenter -> 1
                    else -> 2
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(end = 12.dp)
        ) {
            TitleItem(
                text = stringResource(R.string.fab_alignment),
                icon = Icons.AutoMirrored.Outlined.ViewSidebar,
                modifier = Modifier.padding(
                    start = 8.dp,
                    top = 6.dp
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            EnhancedButtonGroup(
                modifier = Modifier.padding(horizontal = 4.dp),
                itemCount = 3,
                itemContent = {
                    Text(
                        stringResource(
                            when (it) {
                                0 -> R.string.start_position
                                1 -> R.string.center_position
                                else -> R.string.end_position
                            }
                        )
                    )
                },
                onIndexChange = {
                    onValueChange(it.toFloat())
                },
                selectedIndex = derivedValue,
                inactiveButtonColor = MaterialTheme.colorScheme.surfaceContainer
            )
        }
        FabPreview(
            alignment = settingsState.fabAlignment,
            modifier = Modifier
                .width(75.dp)
                .fillMaxHeight()
        )
    }
}