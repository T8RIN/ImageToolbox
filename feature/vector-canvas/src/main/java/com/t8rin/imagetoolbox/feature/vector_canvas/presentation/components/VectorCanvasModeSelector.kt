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

@file:Suppress("FunctionNaming", "PackageName", "PackageNaming")

package com.t8rin.imagetoolbox.feature.vector_canvas.presentation.components

import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.t8rin.imagetoolbox.core.resources.Icons
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.icons.ChangeHistory
import com.t8rin.imagetoolbox.core.resources.icons.Circle
import com.t8rin.imagetoolbox.core.resources.icons.Draw
import com.t8rin.imagetoolbox.core.resources.icons.Line
import com.t8rin.imagetoolbox.core.resources.icons.LineArrow
import com.t8rin.imagetoolbox.core.resources.icons.Rectangle
import com.t8rin.imagetoolbox.core.resources.icons.SelectAll
import com.t8rin.imagetoolbox.core.resources.icons.TextFormat
import com.t8rin.imagetoolbox.core.ui.widget.enhanced.EnhancedButtonGroup
import com.t8rin.imagetoolbox.core.ui.widget.modifier.ShapeDefaults
import com.t8rin.imagetoolbox.core.ui.widget.modifier.container
import io.ak1.drawbox.domain.model.Mode

@Composable
internal fun VectorCanvasModeSelector(
    value: Mode,
    onValueChange: (Mode) -> Unit,
    modifier: Modifier = Modifier
) {
    val tools = remember {
        listOf(
            VectorCanvasTool(Mode.SELECT, R.string.vector_canvas_select, Icons.Outlined.SelectAll),
            VectorCanvasTool(Mode.PEN, R.string.pen, Icons.Rounded.Draw),
            VectorCanvasTool(Mode.TEXT, R.string.text, Icons.Rounded.TextFormat),
            VectorCanvasTool(Mode.LINE, R.string.line, Icons.Rounded.Line),
            VectorCanvasTool(Mode.RECTANGLE, R.string.rect, Icons.Outlined.Rectangle),
            VectorCanvasTool(Mode.CIRCLE, R.string.vector_canvas_circle, Icons.Outlined.Circle),
            VectorCanvasTool(Mode.TRIANGLE, R.string.triangle, Icons.Outlined.ChangeHistory),
            VectorCanvasTool(Mode.ARROW, R.string.arrow, Icons.Rounded.LineArrow)
        )
    }

    EnhancedButtonGroup(
        modifier = modifier.container(ShapeDefaults.extraLarge),
        itemCount = tools.size,
        selectedIndex = tools.indexOfFirst { it.mode == value },
        itemContent = { index ->
            val tool = tools[index]
            Icon(
                imageVector = tool.icon,
                contentDescription = stringResource(tool.title)
            )
        },
        title = {
            Text(stringResource(R.string.draw_mode))
        },
        onIndexChange = { onValueChange(tools[it].mode) }
    )
}

private data class VectorCanvasTool(
    val mode: Mode,
    @StringRes val title: Int,
    val icon: ImageVector
)
