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

package ru.tech.imageresizershrinker.core.ui.widget.saver

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.Pt
import ru.tech.imageresizershrinker.core.domain.model.pt
import ru.tech.imageresizershrinker.core.settings.presentation.model.PicturePickerMode
import ru.tech.imageresizershrinker.core.ui.theme.toColor

val ColorSaver: Saver<Color, Int> = Saver(
    save = {
        it.toArgb()
    },
    restore = {
        it.toColor()
    }
)

val DpSaver: Saver<Dp, Float> = Saver(
    save = {
        it.value
    },
    restore = {
        it.dp
    }
)

val PicturePickerModeSaver: Saver<PicturePickerMode, Int> = Saver(
    save = {
        PicturePickerMode.entries.indexOf(it)
    },
    restore = {
        PicturePickerMode.entries[it]
    }
)

val PtSaver: Saver<Pt, Float> = Saver(
    save = {
        it.value
    },
    restore = {
        it.pt
    }
)

val OffsetSaver: Saver<Offset?, Any> = listSaver<Offset?, Float>(
    save = {
        listOfNotNull(it?.x, it?.y)
    },
    restore = {
        if (it.isEmpty()) null
        else Offset(it[0], it[1])
    }
)