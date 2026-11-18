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

package com.t8rin.imagetoolbox.core.games.presentation.components.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

@Immutable
data class ArcadeStyle(
    val shape: Shape,
    val background: Brush,
    val borderColor: Color,
    val playerColor: Color,
    val playerDetailColor: Color,
    val obstacleColor: Color,
    val accentColor: Color,
    val detailColor: Color,
    val textColor: Color,
    val highScoreColor: Color,
    val scoreTextStyle: TextStyle,
    val overlayColor: Color,
    val buttonColor: Color,
    val buttonContentColor: Color,
    val gameHeight: Dp,
    val hazardColor: Color = accentColor
)