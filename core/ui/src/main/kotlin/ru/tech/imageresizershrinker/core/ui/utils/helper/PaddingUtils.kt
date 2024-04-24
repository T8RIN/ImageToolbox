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

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalLayoutDirection

@Composable
operator fun PaddingValues.plus(paddingValues: PaddingValues): PaddingValues {
    val ld = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(ld) + paddingValues.calculateStartPadding(ld),
        top = calculateTopPadding() + paddingValues.calculateTopPadding(),
        end = calculateEndPadding(ld) + paddingValues.calculateEndPadding(ld),
        bottom = calculateBottomPadding() + paddingValues.calculateBottomPadding(),
    )
}

@Composable
operator fun PaddingValues.minus(paddingValues: PaddingValues): PaddingValues {
    val ld = LocalLayoutDirection.current
    return PaddingValues(
        start = calculateStartPadding(ld) - paddingValues.calculateStartPadding(ld),
        top = calculateTopPadding() - paddingValues.calculateTopPadding(),
        end = calculateEndPadding(ld) - paddingValues.calculateEndPadding(ld),
        bottom = calculateBottomPadding() - paddingValues.calculateBottomPadding(),
    )
}