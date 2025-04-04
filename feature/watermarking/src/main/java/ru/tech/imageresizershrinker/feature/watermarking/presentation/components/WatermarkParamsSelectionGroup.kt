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

package ru.tech.imageresizershrinker.feature.watermarking.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.other.ExpandableItem
import ru.tech.imageresizershrinker.core.ui.widget.text.TitleItem
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkParams
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.selectors.CommonParamsContent
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.selectors.DigitalParamsContent
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.selectors.ImageParamsContent
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.selectors.StampParamsContent
import ru.tech.imageresizershrinker.feature.watermarking.presentation.components.selectors.TextParamsContent

@Composable
fun WatermarkParamsSelectionGroup(
    value: WatermarkParams,
    onValueChange: (WatermarkParams) -> Unit,
    modifier: Modifier = Modifier
) {
    ExpandableItem(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainer,
        visibleContent = {
            TitleItem(
                text = stringResource(id = R.string.properties),
                icon = Icons.Outlined.Tune
            )
        },
        expandableContent = {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val params by rememberUpdatedState(value)

                CommonParamsContent(
                    params = params,
                    onValueChange = onValueChange
                )

                TextParamsContent(
                    params = params,
                    onValueChange = onValueChange
                )

                ImageParamsContent(
                    params = params,
                    onValueChange = onValueChange
                )

                DigitalParamsContent(
                    params = params,
                    onValueChange = onValueChange
                )

                StampParamsContent(
                    params = params,
                    onValueChange = onValueChange
                )
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}