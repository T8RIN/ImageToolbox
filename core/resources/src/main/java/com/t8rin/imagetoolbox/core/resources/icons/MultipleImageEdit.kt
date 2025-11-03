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

package com.t8rin.imagetoolbox.core.resources.icons

import androidx.compose.material.icons.Icons
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val Icons.Outlined.MultipleImageEdit: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    Builder(
        name = "Outlined.MultipleImageEdit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 6.5f)
            curveToRelative(0f, -0.417f, 0.146f, -0.771f, 0.438f, -1.063f)
            curveToRelative(0.292f, -0.292f, 0.646f, -0.438f, 1.063f, -0.438f)
            horizontalLineToRelative(9f)
            curveToRelative(0.417f, 0f, 0.771f, 0.146f, 1.063f, 0.438f)
            reflectiveCurveToRelative(0.438f, 0.646f, 0.438f, 1.063f)
            horizontalLineTo(6f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 3.5f)
            curveToRelative(0f, -0.417f, 0.146f, -0.771f, 0.438f, -1.063f)
            reflectiveCurveToRelative(0.646f, -0.438f, 1.063f, -0.438f)
            horizontalLineToRelative(7f)
            curveToRelative(0.417f, 0f, 0.771f, 0.146f, 1.063f, 0.438f)
            reflectiveCurveToRelative(0.438f, 0.646f, 0.438f, 1.063f)
            horizontalLineTo(7f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.615f, 16.463f)
            curveToRelative(-0.062f, -0.151f, -0.148f, -0.288f, -0.257f, -0.412f)
            lineToRelative(-0.762f, -0.762f)
            curveToRelative(-0.124f, -0.124f, -0.261f, -0.216f, -0.412f, -0.278f)
            reflectiveCurveToRelative(-0.309f, -0.093f, -0.474f, -0.093f)
            curveToRelative(-0.151f, 0f, -0.302f, 0.027f, -0.453f, 0.082f)
            reflectiveCurveToRelative(-0.288f, 0.144f, -0.412f, 0.268f)
            lineToRelative(-4.552f, 4.531f)
            verticalLineToRelative(2.533f)
            horizontalLineToRelative(2.533f)
            lineToRelative(4.531f, -4.531f)
            curveToRelative(0.124f, -0.124f, 0.213f, -0.264f, 0.268f, -0.422f)
            curveToRelative(0.055f, -0.158f, 0.082f, -0.312f, 0.082f, -0.463f)
            reflectiveCurveToRelative(-0.031f, -0.302f, -0.093f, -0.453f)
            close()
            moveTo(15.311f, 21.097f)
            horizontalLineToRelative(-0.783f)
            verticalLineToRelative(-0.783f)
            lineToRelative(2.513f, -2.492f)
            lineToRelative(0.391f, 0.371f)
            lineToRelative(0.371f, 0.391f)
            lineToRelative(-2.492f, 2.513f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(18.412f, 8.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(7f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(10f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(4.801f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-4.801f)
            verticalLineToRelative(-10f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(3.642f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-3.642f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(14.93f, 15.73f)
            lineToRelative(-1.387f, -1.849f)
            lineToRelative(-1.875f, 2.5f)
            lineToRelative(-1.375f, -1.825f)
            lineToRelative(-2.125f, 2.825f)
            lineToRelative(5.111f, 0f)
            lineToRelative(1.651f, -1.651f)
            close()
        }
    }.build()
}

val Icons.TwoTone.MultipleImageEdit: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    Builder(
        name = "TwoTone.MultipleImageEdit",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(6f, 6.5f)
            curveToRelative(0f, -0.417f, 0.146f, -0.771f, 0.438f, -1.063f)
            curveToRelative(0.292f, -0.292f, 0.646f, -0.438f, 1.063f, -0.438f)
            horizontalLineToRelative(9f)
            curveToRelative(0.417f, 0f, 0.771f, 0.146f, 1.063f, 0.438f)
            reflectiveCurveToRelative(0.438f, 0.646f, 0.438f, 1.063f)
            horizontalLineTo(6f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 3.5f)
            curveToRelative(0f, -0.417f, 0.146f, -0.771f, 0.438f, -1.063f)
            reflectiveCurveToRelative(0.646f, -0.438f, 1.063f, -0.438f)
            horizontalLineToRelative(7f)
            curveToRelative(0.417f, 0f, 0.771f, 0.146f, 1.063f, 0.438f)
            reflectiveCurveToRelative(0.438f, 0.646f, 0.438f, 1.063f)
            horizontalLineTo(7f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.615f, 16.463f)
            curveToRelative(-0.062f, -0.151f, -0.148f, -0.288f, -0.257f, -0.412f)
            lineToRelative(-0.762f, -0.762f)
            curveToRelative(-0.124f, -0.124f, -0.261f, -0.216f, -0.412f, -0.278f)
            reflectiveCurveToRelative(-0.309f, -0.093f, -0.474f, -0.093f)
            curveToRelative(-0.151f, 0f, -0.302f, 0.027f, -0.453f, 0.082f)
            reflectiveCurveToRelative(-0.288f, 0.144f, -0.412f, 0.268f)
            lineToRelative(-4.552f, 4.531f)
            verticalLineToRelative(2.533f)
            horizontalLineToRelative(2.533f)
            lineToRelative(4.531f, -4.531f)
            curveToRelative(0.124f, -0.124f, 0.213f, -0.264f, 0.268f, -0.422f)
            curveToRelative(0.055f, -0.158f, 0.082f, -0.312f, 0.082f, -0.463f)
            reflectiveCurveToRelative(-0.031f, -0.302f, -0.093f, -0.453f)
            close()
            moveTo(15.311f, 21.097f)
            horizontalLineToRelative(-0.783f)
            verticalLineToRelative(-0.783f)
            lineToRelative(2.513f, -2.492f)
            lineToRelative(0.391f, 0.371f)
            lineToRelative(0.371f, 0.391f)
            lineToRelative(-2.492f, 2.513f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(18.412f, 8.588f)
            curveToRelative(-0.392f, -0.392f, -0.862f, -0.588f, -1.412f, -0.588f)
            horizontalLineTo(7f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(10f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(4.801f)
            verticalLineToRelative(-2f)
            horizontalLineToRelative(-4.801f)
            verticalLineToRelative(-10f)
            horizontalLineToRelative(10f)
            verticalLineToRelative(3.642f)
            horizontalLineToRelative(2f)
            verticalLineToRelative(-3.642f)
            curveToRelative(0f, -0.55f, -0.196f, -1.021f, -0.588f, -1.412f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(14.93f, 15.73f)
            lineToRelative(-1.387f, -1.849f)
            lineToRelative(-1.875f, 2.5f)
            lineToRelative(-1.375f, -1.825f)
            lineToRelative(-2.125f, 2.825f)
            lineToRelative(5.111f, 0f)
            lineToRelative(1.651f, -1.651f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(11.801f, 18.862f)
            lineToRelative(0.007f, 0f)
            lineToRelative(-0.005f, -0.005f)
            lineToRelative(5.195f, -5.195f)
            lineToRelative(0.002f, 0.002f)
            lineToRelative(0f, -0.002f)
            lineToRelative(0f, -3.662f)
            lineToRelative(-10f, 0f)
            lineToRelative(0f, 10f)
            lineToRelative(4.801f, 0f)
            lineToRelative(0f, -1.138f)
            close()
        }
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(13.628f, 20.412f)
            lineToRelative(4.057f, -4.057f)
            lineToRelative(1.49f, 1.49f)
            lineToRelative(-4.057f, 4.057f)
            close()
        }
    }.build()
}