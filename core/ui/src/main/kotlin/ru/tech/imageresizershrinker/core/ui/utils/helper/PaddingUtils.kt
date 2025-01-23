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

package ru.tech.imageresizershrinker.core.ui.utils.helper

import android.content.res.Configuration
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalLayoutDirection
import ru.tech.imageresizershrinker.core.ui.utils.provider.LocalWindowSizeClass

@Composable
operator fun PaddingValues.plus(paddingValues: PaddingValues): PaddingValues {
    val ld = LocalLayoutDirection.current
    return remember(ld, paddingValues) {
        derivedStateOf {
            PaddingValues(
                start = calculateStartPadding(ld) + paddingValues.calculateStartPadding(ld),
                top = calculateTopPadding() + paddingValues.calculateTopPadding(),
                end = calculateEndPadding(ld) + paddingValues.calculateEndPadding(ld),
                bottom = calculateBottomPadding() + paddingValues.calculateBottomPadding(),
            )
        }
    }.value
}

@Composable
operator fun PaddingValues.minus(paddingValues: PaddingValues): PaddingValues {
    val ld = LocalLayoutDirection.current
    return remember(ld, paddingValues) {
        derivedStateOf {
            PaddingValues(
                start = calculateStartPadding(ld) - paddingValues.calculateStartPadding(ld),
                top = calculateTopPadding() - paddingValues.calculateTopPadding(),
                end = calculateEndPadding(ld) - paddingValues.calculateEndPadding(ld),
                bottom = calculateBottomPadding() - paddingValues.calculateBottomPadding(),
            )
        }
    }.value
}

@Composable
fun isPortraitOrientationAsState(): State<Boolean> {
    val configuration = LocalConfiguration.current
    val sizeClass = LocalWindowSizeClass.current

    return remember(configuration, sizeClass) {
        derivedStateOf {
            configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || sizeClass.widthSizeClass == WindowWidthSizeClass.Compact
        }
    }
}

@Composable
fun isLandscapeOrientationAsState(): State<Boolean> {
    val isPortrait by isPortraitOrientationAsState()

    return remember(isPortrait) {
        derivedStateOf {
            !isPortrait
        }
    }
}