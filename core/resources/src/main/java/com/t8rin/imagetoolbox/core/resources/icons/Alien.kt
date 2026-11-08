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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.t8rin.imagetoolbox.core.resources.Icons

val Icons.Outlined.Alien: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.Alien",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(10.31f, 10.93f)
            curveTo(11.33f, 12.57f, 11.18f, 14.5f, 9.96f, 15.28f)
            curveTo(8.74f, 16.04f, 6.92f, 15.33f, 5.89f, 13.69f)
            curveTo(4.87f, 12.05f, 5.03f, 10.1f, 6.25f, 9.34f)
            curveTo(7.47f, 8.58f, 9.29f, 9.29f, 10.31f, 10.93f)
            moveTo(12f, 17.75f)
            curveTo(14f, 17.75f, 14.5f, 17f, 14.5f, 17f)
            curveTo(14.5f, 17f, 14f, 19f, 12f, 19f)
            curveTo(10f, 19f, 9.5f, 17.03f, 9.5f, 17f)
            curveTo(9.5f, 17f, 10f, 17.75f, 12f, 17.75f)
            moveTo(17.75f, 9.34f)
            curveTo(18.97f, 10.1f, 19.13f, 12.05f, 18.11f, 13.69f)
            curveTo(17.08f, 15.33f, 15.26f, 16.04f, 14.04f, 15.28f)
            curveTo(12.82f, 14.5f, 12.67f, 12.57f, 13.69f, 10.93f)
            curveTo(14.71f, 9.29f, 16.53f, 8.58f, 17.75f, 9.34f)
            moveTo(12f, 20f)
            curveTo(14.5f, 20f, 20f, 14.86f, 20f, 11f)
            curveTo(20f, 7.14f, 16.41f, 4f, 12f, 4f)
            curveTo(7.59f, 4f, 4f, 7.14f, 4f, 11f)
            curveTo(4f, 14.86f, 9.5f, 20f, 12f, 20f)
            moveTo(12f, 2f)
            curveTo(17.5f, 2f, 22f, 6.04f, 22f, 11f)
            curveTo(22f, 15.08f, 16.32f, 22f, 12f, 22f)
            curveTo(7.68f, 22f, 2f, 15.08f, 2f, 11f)
            curveTo(2f, 6.04f, 6.5f, 2f, 12f, 2f)
            close()
        }
    }.build()
}