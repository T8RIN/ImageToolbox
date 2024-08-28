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

package ru.tech.imageresizershrinker.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Rounded.Webp: ImageVector by lazy {
    ImageVector.Builder(
        name = "Webp",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(4.115f, 14.781f)
            curveTo(3.499f, 14.781f, 3f, 14.282f, 3f, 13.666f)
            verticalLineTo(9.207f)
            horizontalLineToRelative(1.115f)
            verticalLineToRelative(4.459f)
            horizontalLineToRelative(1.115f)
            verticalLineTo(9.764f)
            horizontalLineToRelative(1.115f)
            verticalLineToRelative(3.902f)
            horizontalLineToRelative(1.115f)
            verticalLineTo(9.207f)
            horizontalLineToRelative(1.115f)
            verticalLineToRelative(4.459f)
            curveToRelative(0f, 0.616f, -0.499f, 1.115f, -1.115f, 1.115f)
            horizontalLineTo(4.115f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(9.372f, 9.202f)
            verticalLineToRelative(5.574f)
            horizontalLineToRelative(3.344f)
            verticalLineToRelative(-1.115f)
            horizontalLineToRelative(-2.23f)
            verticalLineToRelative(-1.115f)
            horizontalLineToRelative(2.23f)
            verticalLineToRelative(-1.115f)
            horizontalLineToRelative(-2.23f)
            verticalLineToRelative(-1.115f)
            horizontalLineToRelative(2.23f)
            verticalLineTo(9.202f)
            horizontalLineTo(9.372f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(16.858f, 11.158f)
            verticalLineToRelative(-0.836f)
            curveToRelative(0f, -0.616f, -0.499f, -1.115f, -1.115f, -1.115f)
            horizontalLineToRelative(-2.23f)
            verticalLineToRelative(5.574f)
            horizontalLineToRelative(2.23f)
            curveToRelative(0.616f, 0f, 1.115f, -0.499f, 1.115f, -1.115f)
            verticalLineToRelative(-0.836f)
            curveToRelative(0f, -0.446f, -0.39f, -0.836f, -0.836f, -0.836f)
            curveTo(16.468f, 11.994f, 16.858f, 11.604f, 16.858f, 11.158f)
            moveTo(15.743f, 13.666f)
            horizontalLineTo(14.628f)
            verticalLineToRelative(-1.115f)
            horizontalLineToRelative(1.115f)
            verticalLineTo(13.666f)
            moveTo(15.743f, 11.437f)
            horizontalLineTo(14.628f)
            verticalLineToRelative(-1.115f)
            horizontalLineToRelative(1.115f)
            verticalLineTo(11.437f)
            close()
        }
        path(fill = SolidColor(Color(0xFF000000))) {
            moveTo(17.656f, 9.207f)
            verticalLineToRelative(5.574f)
            horizontalLineToRelative(1.115f)
            verticalLineToRelative(-2.23f)
            horizontalLineToRelative(1.115f)
            curveTo(20.501f, 12.552f, 21f, 12.052f, 21f, 11.437f)
            verticalLineToRelative(-1.115f)
            curveToRelative(0f, -0.616f, -0.499f, -1.115f, -1.115f, -1.115f)
            horizontalLineTo(17.656f)
            moveTo(18.77f, 10.322f)
            horizontalLineToRelative(1.115f)
            verticalLineToRelative(1.115f)
            horizontalLineToRelative(-1.115f)
            verticalLineTo(10.322f)
            close()
        }
    }.build()
}