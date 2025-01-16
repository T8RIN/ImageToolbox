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

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.ui.utils.helper.isLandscapeOrientationAsState

fun Modifier.navBarsPaddingOnlyIfTheyAtTheEnd(enabled: Boolean = true) = this.composed {
    if (WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() == 0.dp && enabled) {
        Modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
    } else Modifier
}

fun Modifier.navBarsLandscapePadding(enabled: Boolean = true) = this.composed {
    if (isLandscapeOrientationAsState().value && enabled) {
        Modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
    } else Modifier
}

fun Modifier.navBarsPaddingOnlyIfTheyAtTheBottom(enabled: Boolean = true) = this.composed {
    if (WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() != 0.dp && enabled) {
        Modifier
            .navigationBarsPadding()
            .displayCutoutPadding()
    } else Modifier
}