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

val Icons.Rounded.Restore: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.Restore",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(12f, 21f)
            quadTo(8.85f, 21f, 6.425f, 19.087f)
            quadTo(4f, 17.175f, 3.275f, 14.2f)
            quadTo(3.175f, 13.825f, 3.425f, 13.512f)
            quadTo(3.675f, 13.2f, 4.1f, 13.15f)
            quadTo(4.5f, 13.1f, 4.825f, 13.3f)
            quadTo(5.15f, 13.5f, 5.275f, 13.9f)
            quadTo(5.875f, 16.15f, 7.75f, 17.575f)
            quadTo(9.625f, 19f, 12f, 19f)
            quadTo(14.925f, 19f, 16.962f, 16.962f)
            quadTo(19f, 14.925f, 19f, 12f)
            quadTo(19f, 9.075f, 16.962f, 7.037f)
            quadTo(14.925f, 5f, 12f, 5f)
            quadTo(10.275f, 5f, 8.775f, 5.8f)
            quadTo(7.275f, 6.6f, 6.25f, 8f)
            horizontalLineTo(8f)
            quadTo(8.425f, 8f, 8.712f, 8.288f)
            quadTo(9f, 8.575f, 9f, 9f)
            quadTo(9f, 9.425f, 8.712f, 9.712f)
            quadTo(8.425f, 10f, 8f, 10f)
            horizontalLineTo(4f)
            quadTo(3.575f, 10f, 3.287f, 9.712f)
            quadTo(3f, 9.425f, 3f, 9f)
            verticalLineTo(5f)
            quadTo(3f, 4.575f, 3.287f, 4.287f)
            quadTo(3.575f, 4f, 4f, 4f)
            quadTo(4.425f, 4f, 4.713f, 4.287f)
            quadTo(5f, 4.575f, 5f, 5f)
            verticalLineTo(6.35f)
            quadTo(6.275f, 4.75f, 8.113f, 3.875f)
            quadTo(9.95f, 3f, 12f, 3f)
            quadTo(13.875f, 3f, 15.512f, 3.713f)
            quadTo(17.15f, 4.425f, 18.362f, 5.637f)
            quadTo(19.575f, 6.85f, 20.288f, 8.488f)
            quadTo(21f, 10.125f, 21f, 12f)
            quadTo(21f, 13.875f, 20.288f, 15.512f)
            quadTo(19.575f, 17.15f, 18.362f, 18.362f)
            quadTo(17.15f, 19.575f, 15.512f, 20.288f)
            quadTo(13.875f, 21f, 12f, 21f)
            close()
            moveTo(13f, 11.6f)
            lineTo(15.5f, 14.1f)
            quadTo(15.775f, 14.375f, 15.775f, 14.8f)
            quadTo(15.775f, 15.225f, 15.5f, 15.5f)
            quadTo(15.225f, 15.775f, 14.8f, 15.775f)
            quadTo(14.375f, 15.775f, 14.1f, 15.5f)
            lineTo(11.3f, 12.7f)
            quadTo(11.15f, 12.55f, 11.075f, 12.363f)
            quadTo(11f, 12.175f, 11f, 11.975f)
            verticalLineTo(8f)
            quadTo(11f, 7.575f, 11.288f, 7.287f)
            quadTo(11.575f, 7f, 12f, 7f)
            quadTo(12.425f, 7f, 12.712f, 7.287f)
            quadTo(13f, 7.575f, 13f, 8f)
            close()
        }
    }.build()
}
