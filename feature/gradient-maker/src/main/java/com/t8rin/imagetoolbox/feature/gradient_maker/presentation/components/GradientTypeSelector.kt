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

package com.t8rin.imagetoolbox.feature.gradient_maker.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.animateContentSizeNoClip
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import com.t8rin.imagetoolbox.core.ui.widget.other.ExpandableItem
import com.t8rin.imagetoolbox.core.ui.widget.text.TitleItem
import com.t8rin.imagetoolbox.feature.gradient_maker.domain.GradientType

@Composable
fun GradientTypeSelector(
    value: GradientType,
    onValueChange: (GradientType) -> Unit,
    modifier: Modifier = Modifier,
    propertiesContent: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .container(
                shape = ShapeDefaults.extraLarge
            )
            .animateContentSizeNoClip(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EnhancedButtonGroup(
            modifier = Modifier.padding(8.dp),
            enabled = true,
            items = GradientType.entries.map { it.translatedName },
            selectedIndex = GradientType.entries.indexOf(value),
            title = stringResource(id = R.string.gradient_type),
            onIndexChange = {
                onValueChange(GradientType.entries[it])
            }
        )
        ExpandableItem(
            visibleContent = {
                TitleItem(
                    text = stringResource(id = R.string.properties),
                    icon = Icons.Outlined.Tune
                )
            },
            expandableContent = {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    propertiesContent()
                }
            },
            modifier = Modifier.padding(end = 8.dp, start = 8.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.surface
        )
    }
}

private val GradientType.translatedName: String
    @Composable
    get() = when (this) {
        GradientType.Linear -> stringResource(id = R.string.gradient_type_linear)
        GradientType.Radial -> stringResource(id = R.string.gradient_type_radial)
        GradientType.Sweep -> stringResource(id = R.string.gradient_type_sweep)
    }
