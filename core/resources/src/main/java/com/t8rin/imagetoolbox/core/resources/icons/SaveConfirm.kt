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

val Icons.Outlined.SaveConfirm: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.SaveConfirm",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 19f)
            lineToRelative(0f, -14f)
            lineToRelative(0f, 5.075f)
            lineToRelative(0f, -0.075f)
            lineToRelative(0f, 9f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 21f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(5f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            curveToRelative(0.392f, -0.392f, 0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(11.175f)
            curveToRelative(0.267f, 0f, 0.521f, 0.05f, 0.762f, 0.15f)
            reflectiveCurveToRelative(0.454f, 0.242f, 0.637f, 0.425f)
            lineToRelative(2.85f, 2.85f)
            curveToRelative(0.183f, 0.183f, 0.325f, 0.396f, 0.425f, 0.637f)
            reflectiveCurveToRelative(0.15f, 0.496f, 0.15f, 0.762f)
            verticalLineToRelative(1.55f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            reflectiveCurveToRelative(-0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            verticalLineToRelative(-1.525f)
            lineToRelative(-2.85f, -2.85f)
            horizontalLineTo(5f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(5.8f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            curveToRelative(-0.192f, 0.192f, -0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-5.8f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 10f)
            horizontalLineToRelative(7f)
            curveToRelative(0.283f, 0f, 0.521f, -0.096f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.287f, -0.429f, 0.287f, -0.712f)
            verticalLineToRelative(-2f)
            curveToRelative(0f, -0.283f, -0.096f, -0.521f, -0.287f, -0.712f)
            reflectiveCurveToRelative(-0.429f, -0.287f, -0.712f, -0.287f)
            horizontalLineToRelative(-7f)
            curveToRelative(-0.283f, 0f, -0.521f, 0.096f, -0.712f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.712f)
            verticalLineToRelative(2f)
            curveToRelative(0f, 0.283f, 0.096f, 0.521f, 0.287f, 0.712f)
            curveToRelative(0.192f, 0.192f, 0.429f, 0.287f, 0.712f, 0.287f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(11.05f, 17.85f)
            curveToRelative(-0.017f, -0.15f, -0.029f, -0.296f, -0.038f, -0.438f)
            reflectiveCurveToRelative(-0.013f, -0.287f, -0.013f, -0.438f)
            curveToRelative(0f, -0.9f, 0.167f, -1.767f, 0.5f, -2.6f)
            reflectiveCurveToRelative(0.817f, -1.575f, 1.45f, -2.225f)
            curveToRelative(-0.15f, -0.05f, -0.304f, -0.087f, -0.463f, -0.112f)
            reflectiveCurveToRelative(-0.321f, -0.038f, -0.488f, -0.038f)
            curveToRelative(-0.833f, 0f, -1.542f, 0.292f, -2.125f, 0.875f)
            reflectiveCurveToRelative(-0.875f, 1.292f, -0.875f, 2.125f)
            curveToRelative(0f, 0.65f, 0.188f, 1.237f, 0.563f, 1.763f)
            reflectiveCurveToRelative(0.871f, 0.887f, 1.487f, 1.087f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(21.537f, 13.463f)
            curveToRelative(-0.975f, -0.975f, -2.154f, -1.463f, -3.537f, -1.463f)
            curveToRelative(-1.383f, 0f, -2.563f, 0.487f, -3.537f, 1.463f)
            curveToRelative(-0.975f, 0.975f, -1.463f, 2.154f, -1.463f, 3.537f)
            reflectiveCurveToRelative(0.487f, 2.563f, 1.463f, 3.537f)
            curveToRelative(0.975f, 0.975f, 2.154f, 1.463f, 3.537f, 1.463f)
            curveToRelative(1.383f, 0f, 2.563f, -0.487f, 3.537f, -1.463f)
            curveToRelative(0.975f, -0.975f, 1.463f, -2.154f, 1.463f, -3.537f)
            reflectiveCurveToRelative(-0.487f, -2.563f, -1.463f, -3.537f)
            close()
            moveTo(20.225f, 16.325f)
            lineToRelative(-2.25f, 2.225f)
            curveToRelative(-0.2f, 0.2f, -0.433f, 0.3f, -0.7f, 0.3f)
            reflectiveCurveToRelative(-0.5f, -0.1f, -0.7f, -0.3f)
            lineToRelative(-0.8f, -0.8f)
            curveToRelative(-0.15f, -0.15f, -0.225f, -0.325f, -0.225f, -0.525f)
            curveToRelative(0f, -0.2f, 0.075f, -0.375f, 0.225f, -0.525f)
            curveToRelative(0.15f, -0.15f, 0.325f, -0.229f, 0.525f, -0.237f)
            curveToRelative(0.2f, -0.008f, 0.375f, 0.063f, 0.525f, 0.212f)
            lineToRelative(0.45f, 0.45f)
            lineToRelative(1.9f, -1.875f)
            curveToRelative(0.15f, -0.133f, 0.325f, -0.204f, 0.525f, -0.213f)
            curveToRelative(0.2f, -0.008f, 0.375f, 0.063f, 0.525f, 0.213f)
            curveToRelative(0.15f, 0.15f, 0.225f, 0.329f, 0.225f, 0.537f)
            curveToRelative(0f, 0.208f, -0.075f, 0.388f, -0.225f, 0.538f)
            close()
        }
    }.build()
}
