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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.AccessTime: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.AccessTime",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(13f, 11.6f)
            verticalLineTo(8f)
            quadTo(13f, 7.575f, 12.712f, 7.287f)
            quadTo(12.425f, 7f, 12f, 7f)
            quadTo(11.575f, 7f, 11.288f, 7.287f)
            quadTo(11f, 7.575f, 11f, 8f)
            verticalLineTo(11.975f)
            quadTo(11f, 12.175f, 11.075f, 12.363f)
            quadTo(11.15f, 12.55f, 11.3f, 12.7f)
            lineTo(14.6f, 16f)
            quadTo(14.875f, 16.275f, 15.3f, 16.275f)
            quadTo(15.725f, 16.275f, 16f, 16f)
            quadTo(16.275f, 15.725f, 16.275f, 15.3f)
            quadTo(16.275f, 14.875f, 16f, 14.6f)
            close()
            moveTo(12f, 22f)
            quadTo(9.925f, 22f, 8.1f, 21.212f)
            quadTo(6.275f, 20.425f, 4.925f, 19.075f)
            quadTo(3.575f, 17.725f, 2.787f, 15.9f)
            quadTo(2f, 14.075f, 2f, 12f)
            quadTo(2f, 9.925f, 2.787f, 8.1f)
            quadTo(3.575f, 6.275f, 4.925f, 4.925f)
            quadTo(6.275f, 3.575f, 8.1f, 2.787f)
            quadTo(9.925f, 2f, 12f, 2f)
            quadTo(14.075f, 2f, 15.9f, 2.787f)
            quadTo(17.725f, 3.575f, 19.075f, 4.925f)
            quadTo(20.425f, 6.275f, 21.212f, 8.1f)
            quadTo(22f, 9.925f, 22f, 12f)
            quadTo(22f, 14.075f, 21.212f, 15.9f)
            quadTo(20.425f, 17.725f, 19.075f, 19.075f)
            quadTo(17.725f, 20.425f, 15.9f, 21.212f)
            quadTo(14.075f, 22f, 12f, 22f)
            close()
            moveTo(12f, 12f)
            quadTo(12f, 12f, 12f, 12f)
            quadTo(12f, 12f, 12f, 12f)
            quadTo(12f, 12f, 12f, 12f)
            quadTo(12f, 12f, 12f, 12f)
            quadTo(12f, 12f, 12f, 12f)
            quadTo(12f, 12f, 12f, 12f)
            quadTo(12f, 12f, 12f, 12f)
            quadTo(12f, 12f, 12f, 12f)
            close()
            moveTo(12f, 20f)
            quadTo(15.325f, 20f, 17.663f, 17.663f)
            quadTo(20f, 15.325f, 20f, 12f)
            quadTo(20f, 8.675f, 17.663f, 6.338f)
            quadTo(15.325f, 4f, 12f, 4f)
            quadTo(8.675f, 4f, 6.338f, 6.338f)
            quadTo(4f, 8.675f, 4f, 12f)
            quadTo(4f, 15.325f, 6.338f, 17.663f)
            quadTo(8.675f, 20f, 12f, 20f)
            close()
        }
    }.build()
}
