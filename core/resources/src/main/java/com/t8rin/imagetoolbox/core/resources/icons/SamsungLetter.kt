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

val Icons.Outlined.SamsungLetter: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SamsungLetter",
        defaultWidth = 512.dp,
        defaultHeight = 512.dp,
        viewportWidth = 512f,
        viewportHeight = 512f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(292.59f, 332.17f)
            curveToRelative(3.49f, 8.61f, 2.42f, 19.71f, 0.78f, 26.4f)
            curveToRelative(-2.99f, 11.81f, -11.03f, 23.91f, -34.65f, 23.91f)
            curveToRelative(-22.34f, 0f, -35.86f, -12.81f, -35.86f, -32.3f)
            lineToRelative(0f, -34.44f)
            lineToRelative(-95.48f, 0f)
            lineToRelative(-0.07f, 27.53f)
            curveToRelative(0f, 79.33f, 62.47f, 103.31f, 129.42f, 103.31f)
            curveToRelative(64.39f, 0f, 117.4f, -21.99f, 125.79f, -81.32f)
            curveToRelative(4.34f, -30.74f, 1.07f, -50.87f, -0.36f, -58.49f)
            curveToRelative(-15.01f, -74.49f, -150.12f, -96.76f, -160.16f, -138.38f)
            curveToRelative(-1.71f, -7.11f, -1.21f, -14.73f, -0.36f, -18.78f)
            curveToRelative(2.49f, -11.31f, 10.25f, -23.83f, 32.51f, -23.83f)
            curveToRelative(20.78f, 0f, 33.08f, 12.88f, 33.08f, 32.3f)
            curveToRelative(0f, 6.55f, 0f, 21.99f, 0f, 21.99f)
            lineToRelative(88.72f, 0f)
            lineToRelative(0f, -24.97f)
            curveToRelative(0f, -77.55f, -69.58f, -89.65f, -119.96f, -89.65f)
            curveToRelative(-63.32f, 0f, -115.05f, 20.92f, -124.51f, 78.83f)
            curveToRelative(-2.56f, 16.01f, -2.92f, 30.24f, 0.78f, 48.1f)
            curveToRelative(15.58f, 72.64f, 141.94f, 93.7f, 160.3f, 139.81f)
            close()
        }
    }.build()
}
