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

import com.t8rin.imagetoolbox.core.resources.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.DataSaverOff: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.DataSaverOff",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 22f)
            quadTo(9.925f, 22f, 8.1f, 21.212f)
            quadTo(6.275f, 20.425f, 4.925f, 19.075f)
            quadTo(3.575f, 17.725f, 2.787f, 15.9f)
            quadTo(2f, 14.075f, 2f, 12f)
            quadTo(2f, 8.75f, 3.875f, 6.15f)
            quadTo(5.75f, 3.55f, 8.85f, 2.525f)
            quadTo(9.575f, 2.275f, 10.188f, 2.7f)
            quadTo(10.8f, 3.125f, 10.8f, 3.85f)
            quadTo(10.8f, 4.35f, 10.512f, 4.762f)
            quadTo(10.225f, 5.175f, 9.775f, 5.325f)
            quadTo(7.625f, 6f, 6.313f, 7.838f)
            quadTo(5f, 9.675f, 5f, 12f)
            quadTo(5f, 14.925f, 7.037f, 16.962f)
            quadTo(9.075f, 19f, 12f, 19f)
            quadTo(13.3f, 19f, 14.512f, 18.55f)
            quadTo(15.725f, 18.1f, 16.675f, 17.25f)
            quadTo(17.05f, 16.9f, 17.587f, 16.9f)
            quadTo(18.125f, 16.9f, 18.5f, 17.25f)
            quadTo(19.075f, 17.775f, 19.1f, 18.438f)
            quadTo(19.125f, 19.1f, 18.55f, 19.6f)
            quadTo(17.2f, 20.775f, 15.538f, 21.388f)
            quadTo(13.875f, 22f, 12f, 22f)
            close()
            moveTo(19f, 12f)
            quadTo(19f, 9.7f, 17.675f, 7.863f)
            quadTo(16.35f, 6.025f, 14.2f, 5.325f)
            quadTo(13.75f, 5.175f, 13.462f, 4.762f)
            quadTo(13.175f, 4.35f, 13.175f, 3.85f)
            quadTo(13.175f, 3.125f, 13.788f, 2.7f)
            quadTo(14.4f, 2.275f, 15.125f, 2.525f)
            quadTo(18.25f, 3.575f, 20.125f, 6.175f)
            quadTo(22f, 8.775f, 22f, 12f)
            quadTo(22f, 12.45f, 21.962f, 12.913f)
            quadTo(21.925f, 13.375f, 21.825f, 13.925f)
            quadTo(21.7f, 14.65f, 21.087f, 14.962f)
            quadTo(20.475f, 15.275f, 19.75f, 15f)
            quadTo(19.275f, 14.825f, 19.013f, 14.363f)
            quadTo(18.75f, 13.9f, 18.85f, 13.4f)
            quadTo(18.925f, 12.975f, 18.962f, 12.65f)
            quadTo(19f, 12.325f, 19f, 12f)
            close()
        }
    }.build()
}
