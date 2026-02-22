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

val Icons.Outlined.BarcodeScanner: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.BarcodeScanner",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 960f,
        viewportHeight = 960f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(40f, 840f)
            verticalLineToRelative(-200f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(120f)
            horizontalLineToRelative(120f)
            verticalLineToRelative(80f)
            lineTo(40f, 840f)
            close()
            moveTo(720f, 840f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(120f)
            verticalLineToRelative(-120f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(200f)
            lineTo(720f, 840f)
            close()
            moveTo(160f, 720f)
            verticalLineToRelative(-480f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(480f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(280f, 720f)
            verticalLineToRelative(-480f)
            horizontalLineToRelative(40f)
            verticalLineToRelative(480f)
            horizontalLineToRelative(-40f)
            close()
            moveTo(400f, 720f)
            verticalLineToRelative(-480f)
            horizontalLineToRelative(80f)
            verticalLineToRelative(480f)
            horizontalLineToRelative(-80f)
            close()
            moveTo(520f, 720f)
            verticalLineToRelative(-480f)
            horizontalLineToRelative(120f)
            verticalLineToRelative(480f)
            lineTo(520f, 720f)
            close()
            moveTo(680f, 720f)
            verticalLineToRelative(-480f)
            horizontalLineToRelative(40f)
            verticalLineToRelative(480f)
            horizontalLineToRelative(-40f)
            close()
            moveTo(760f, 720f)
            verticalLineToRelative(-480f)
            horizontalLineToRelative(40f)
            verticalLineToRelative(480f)
            horizontalLineToRelative(-40f)
            close()
            moveTo(40f, 320f)
            verticalLineToRelative(-200f)
            horizontalLineToRelative(200f)
            verticalLineToRelative(80f)
            lineTo(120f, 200f)
            verticalLineToRelative(120f)
            lineTo(40f, 320f)
            close()
            moveTo(840f, 320f)
            verticalLineToRelative(-120f)
            lineTo(720f, 200f)
            verticalLineToRelative(-80f)
            horizontalLineToRelative(200f)
            verticalLineToRelative(200f)
            horizontalLineToRelative(-80f)
            close()
        }
    }.build()
}
