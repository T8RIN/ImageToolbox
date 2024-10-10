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

package ru.tech.imageresizershrinker.core.ui.widget.controls.selection

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRow
import ru.tech.imageresizershrinker.core.ui.widget.color_picker.ColorSelectionRowDefaults

@Composable
fun BackgroundColorSelector(
    value: Color,
    onValueChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.background_color),
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                fontWeight = FontWeight.Medium,
                text = title,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        ColorSelectionRow(
            defaultColors = defaultColorList,
            allowAlpha = true,
            contentPadding = PaddingValues(horizontal = 12.dp),
            value = value,
            onValueChange = onValueChange
        )
    }
}

private val defaultColorList by lazy {
    listOf(
        Color(0xFFFFFFFF),
        Color(0xFF768484),
        Color(0xFF333333),
        Color(0xFF000000),
    ).plus(
        ColorSelectionRowDefaults.colorList.reversed().drop(4)
    )
}