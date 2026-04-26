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

val Icons.Outlined.Place: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Place",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(12f, 19.35f)
            quadTo(15.05f, 16.55f, 16.525f, 14.262f)
            quadTo(18f, 11.975f, 18f, 10.2f)
            quadTo(18f, 7.475f, 16.263f, 5.738f)
            quadTo(14.525f, 4f, 12f, 4f)
            quadTo(9.475f, 4f, 7.738f, 5.738f)
            quadTo(6f, 7.475f, 6f, 10.2f)
            quadTo(6f, 11.975f, 7.475f, 14.262f)
            quadTo(8.95f, 16.55f, 12f, 19.35f)
            close()
            moveTo(10.675f, 20.825f)
            quadTo(9.05f, 19.325f, 7.8f, 17.9f)
            quadTo(6.55f, 16.475f, 5.713f, 15.137f)
            quadTo(4.875f, 13.8f, 4.438f, 12.563f)
            quadTo(4f, 11.325f, 4f, 10.2f)
            quadTo(4f, 6.45f, 6.412f, 4.225f)
            quadTo(8.825f, 2f, 12f, 2f)
            quadTo(15.175f, 2f, 17.587f, 4.225f)
            quadTo(20f, 6.45f, 20f, 10.2f)
            quadTo(20f, 11.325f, 19.563f, 12.563f)
            quadTo(19.125f, 13.8f, 18.288f, 15.137f)
            quadTo(17.45f, 16.475f, 16.2f, 17.9f)
            quadTo(14.95f, 19.325f, 13.325f, 20.825f)
            quadTo(13.05f, 21.075f, 12.7f, 21.2f)
            quadTo(12.35f, 21.325f, 12f, 21.325f)
            quadTo(11.65f, 21.325f, 11.3f, 21.2f)
            quadTo(10.95f, 21.075f, 10.675f, 20.825f)
            close()
            moveTo(12f, 10f)
            quadTo(12f, 10f, 12f, 10f)
            quadTo(12f, 10f, 12f, 10f)
            quadTo(12f, 10f, 12f, 10f)
            quadTo(12f, 10f, 12f, 10f)
            quadTo(12f, 10f, 12f, 10f)
            quadTo(12f, 10f, 12f, 10f)
            quadTo(12f, 10f, 12f, 10f)
            quadTo(12f, 10f, 12f, 10f)
            close()
            moveTo(12f, 12f)
            quadTo(12.825f, 12f, 13.413f, 11.413f)
            quadTo(14f, 10.825f, 14f, 10f)
            quadTo(14f, 9.175f, 13.413f, 8.587f)
            quadTo(12.825f, 8f, 12f, 8f)
            quadTo(11.175f, 8f, 10.587f, 8.587f)
            quadTo(10f, 9.175f, 10f, 10f)
            quadTo(10f, 10.825f, 10.587f, 11.413f)
            quadTo(11.175f, 12f, 12f, 12f)
            close()
        }
    }.build()
}
