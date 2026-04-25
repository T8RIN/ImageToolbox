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

val Icons.Outlined.AddPhotoAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Outlined.AddPhotoAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(5f, 21f)
            curveToRelative(-0.55f, 0f, -1.021f, -0.196f, -1.413f, -0.587f)
            curveToRelative(-0.392f, -0.392f, -0.587f, -0.863f, -0.587f, -1.413f)
            verticalLineTo(5f)
            curveToRelative(0f, -0.55f, 0.196f, -1.021f, 0.587f, -1.413f)
            curveToRelative(0.392f, -0.392f, 0.863f, -0.587f, 1.413f, -0.587f)
            horizontalLineToRelative(7f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            curveToRelative(0.192f, 0.192f, 0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            curveToRelative(-0.192f, 0.192f, -0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-7f)
            verticalLineToRelative(14f)
            horizontalLineToRelative(14f)
            verticalLineToRelative(-7f)
            curveToRelative(0f, -0.283f, 0.096f, -0.521f, 0.287f, -0.712f)
            curveToRelative(0.192f, -0.192f, 0.429f, -0.287f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.521f, 0.096f, 0.712f, 0.287f)
            curveToRelative(0.192f, 0.192f, 0.287f, 0.429f, 0.287f, 0.712f)
            verticalLineToRelative(7f)
            curveToRelative(0f, 0.55f, -0.196f, 1.021f, -0.587f, 1.413f)
            curveToRelative(-0.392f, 0.392f, -0.863f, 0.587f, -1.413f, 0.587f)
            horizontalLineTo(5f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(17f, 7f)
            horizontalLineToRelative(-1f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(1f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.283f, 0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            verticalLineToRelative(1f)
            horizontalLineToRelative(1f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-1f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            reflectiveCurveToRelative(-0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            verticalLineToRelative(-1f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(7f, 17f)
            horizontalLineToRelative(10f)
            curveToRelative(0.2f, 0f, 0.35f, -0.092f, 0.45f, -0.275f)
            reflectiveCurveToRelative(0.083f, -0.358f, -0.05f, -0.525f)
            lineToRelative(-2.75f, -3.675f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-2.6f, 3.475f)
            lineToRelative(-1.85f, -2.475f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-2f, 2.675f)
            curveToRelative(-0.133f, 0.167f, -0.15f, 0.342f, -0.05f, 0.525f)
            reflectiveCurveToRelative(0.25f, 0.275f, 0.45f, 0.275f)
            close()
        }
    }.build()
}

val Icons.Rounded.AddPhotoAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "Rounded.AddPhotoAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(17.288f, 8.712f)
            curveToRelative(-0.192f, -0.192f, -0.287f, -0.429f, -0.287f, -0.712f)
            verticalLineToRelative(-1f)
            horizontalLineToRelative(-1f)
            curveToRelative(-0.283f, 0f, -0.521f, -0.096f, -0.712f, -0.287f)
            reflectiveCurveToRelative(-0.287f, -0.429f, -0.287f, -0.712f)
            reflectiveCurveToRelative(0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            horizontalLineToRelative(1f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.283f, 0.096f, -0.521f, 0.287f, -0.712f)
            reflectiveCurveToRelative(0.429f, -0.287f, 0.712f, -0.287f)
            reflectiveCurveToRelative(0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            verticalLineToRelative(1f)
            horizontalLineToRelative(1f)
            curveToRelative(0.283f, 0f, 0.521f, 0.096f, 0.712f, 0.287f)
            reflectiveCurveToRelative(0.287f, 0.429f, 0.287f, 0.712f)
            reflectiveCurveToRelative(-0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            horizontalLineToRelative(-1f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.283f, -0.096f, 0.521f, -0.287f, 0.712f)
            reflectiveCurveToRelative(-0.429f, 0.287f, -0.712f, 0.287f)
            reflectiveCurveToRelative(-0.521f, -0.096f, -0.712f, -0.287f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.563f, 10.8f)
            curveToRelative(-0.292f, -0.167f, -0.604f, -0.192f, -0.938f, -0.075f)
            curveToRelative(-0.267f, 0.083f, -0.533f, 0.15f, -0.8f, 0.2f)
            reflectiveCurveToRelative(-0.542f, 0.075f, -0.825f, 0.075f)
            curveToRelative(-1.383f, 0f, -2.563f, -0.487f, -3.537f, -1.463f)
            curveToRelative(-0.975f, -0.975f, -1.463f, -2.154f, -1.463f, -3.537f)
            curveToRelative(0f, -0.283f, 0.025f, -0.558f, 0.075f, -0.825f)
            reflectiveCurveToRelative(0.117f, -0.533f, 0.2f, -0.8f)
            curveToRelative(0.133f, -0.333f, 0.112f, -0.646f, -0.063f, -0.938f)
            curveToRelative(-0.175f, -0.292f, -0.429f, -0.438f, -0.763f, -0.438f)
            horizontalLineToRelative(-7.45f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(14f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineToRelative(-7.45f)
            curveToRelative(0f, -0.333f, -0.146f, -0.583f, -0.438f, -0.75f)
            close()
            moveTo(17.45f, 16.725f)
            curveToRelative(-0.1f, 0.183f, -0.25f, 0.275f, -0.45f, 0.275f)
            horizontalLineTo(7f)
            curveToRelative(-0.2f, 0f, -0.35f, -0.092f, -0.45f, -0.275f)
            curveToRelative(-0.1f, -0.183f, -0.083f, -0.358f, 0.05f, -0.525f)
            lineToRelative(2f, -2.675f)
            curveToRelative(0.1f, -0.133f, 0.233f, -0.2f, 0.4f, -0.2f)
            reflectiveCurveToRelative(0.3f, 0.067f, 0.4f, 0.2f)
            lineToRelative(1.85f, 2.475f)
            lineToRelative(2.6f, -3.475f)
            curveToRelative(0.1f, -0.133f, 0.233f, -0.2f, 0.4f, -0.2f)
            reflectiveCurveToRelative(0.3f, 0.067f, 0.4f, 0.2f)
            lineToRelative(2.75f, 3.675f)
            curveToRelative(0.133f, 0.167f, 0.15f, 0.342f, 0.05f, 0.525f)
            close()
        }
    }.build()
}

val Icons.TwoTone.AddPhotoAlt: ImageVector by lazy(LazyThreadSafetyMode.NONE) {
    ImageVector.Builder(
        name = "TwoTone.AddPhotoAlt",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = SolidColor(Color.Black),
            fillAlpha = 0.3f,
            strokeAlpha = 0.3f
        ) {
            moveTo(19.604f, 10.739f)
            curveToRelative(-0.267f, 0.083f, -0.533f, 0.15f, -0.8f, 0.2f)
            reflectiveCurveToRelative(-0.542f, 0.075f, -0.825f, 0.075f)
            curveToRelative(-1.383f, 0f, -2.563f, -0.487f, -3.537f, -1.463f)
            curveToRelative(-0.975f, -0.975f, -1.463f, -2.154f, -1.463f, -3.537f)
            curveToRelative(0f, -0.283f, 0.025f, -0.558f, 0.075f, -0.825f)
            reflectiveCurveToRelative(0.117f, -0.533f, 0.2f, -0.8f)
            curveToRelative(0.027f, -0.067f, 0.043f, -0.133f, 0.057f, -0.198f)
            horizontalLineTo(5f)
            verticalLineToRelative(14.809f)
            horizontalLineToRelative(14.82f)
            verticalLineToRelative(-8.291f)
            curveToRelative(-0.072f, 0.012f, -0.142f, 0.005f, -0.216f, 0.031f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(16f, 7f)
            horizontalLineToRelative(1f)
            verticalLineToRelative(1f)
            curveToRelative(0f, 0.283f, 0.096f, 0.521f, 0.287f, 0.713f)
            curveToRelative(0.192f, 0.192f, 0.429f, 0.287f, 0.713f, 0.287f)
            reflectiveCurveToRelative(0.521f, -0.096f, 0.713f, -0.287f)
            curveToRelative(0.192f, -0.192f, 0.287f, -0.429f, 0.287f, -0.713f)
            verticalLineToRelative(-1f)
            horizontalLineToRelative(1f)
            curveToRelative(0.283f, 0f, 0.521f, -0.096f, 0.713f, -0.287f)
            curveToRelative(0.192f, -0.192f, 0.287f, -0.429f, 0.287f, -0.713f)
            reflectiveCurveToRelative(-0.096f, -0.521f, -0.287f, -0.713f)
            curveToRelative(-0.192f, -0.192f, -0.429f, -0.287f, -0.713f, -0.287f)
            horizontalLineToRelative(-1f)
            verticalLineToRelative(-1f)
            curveToRelative(0f, -0.283f, -0.096f, -0.521f, -0.287f, -0.713f)
            curveToRelative(-0.192f, -0.192f, -0.429f, -0.287f, -0.713f, -0.287f)
            reflectiveCurveToRelative(-0.521f, 0.096f, -0.713f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.713f)
            verticalLineToRelative(1f)
            horizontalLineToRelative(-1f)
            curveToRelative(-0.283f, 0f, -0.521f, 0.096f, -0.713f, 0.287f)
            curveToRelative(-0.192f, 0.192f, -0.287f, 0.429f, -0.287f, 0.713f)
            reflectiveCurveToRelative(0.096f, 0.521f, 0.287f, 0.713f)
            curveToRelative(0.192f, 0.192f, 0.429f, 0.287f, 0.713f, 0.287f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(9.4f, 13.525f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-2f, 2.675f)
            curveToRelative(-0.133f, 0.167f, -0.15f, 0.342f, -0.05f, 0.525f)
            curveToRelative(0.1f, 0.183f, 0.25f, 0.275f, 0.45f, 0.275f)
            horizontalLineToRelative(10f)
            curveToRelative(0.2f, 0f, 0.35f, -0.092f, 0.45f, -0.275f)
            curveToRelative(0.1f, -0.183f, 0.083f, -0.358f, -0.05f, -0.525f)
            lineToRelative(-2.75f, -3.675f)
            curveToRelative(-0.1f, -0.133f, -0.233f, -0.2f, -0.4f, -0.2f)
            reflectiveCurveToRelative(-0.3f, 0.067f, -0.4f, 0.2f)
            lineToRelative(-2.6f, 3.475f)
            lineToRelative(-1.85f, -2.475f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(20.992f, 11.66f)
            curveToRelative(0f, -0.552f, -0.448f, -1f, -1f, -1f)
            curveToRelative(-0.552f, 0f, -1f, 0.448f, -1f, 1f)
            verticalLineToRelative(0.908f)
            curveToRelative(0f, 0.014f, 0.007f, 0.025f, 0.008f, 0.039f)
            verticalLineToRelative(6.394f)
            horizontalLineTo(5f)
            verticalLineTo(5f)
            horizontalLineToRelative(7.343f)
            curveToRelative(0.552f, 0f, 1f, -0.448f, 1f, -1f)
            reflectiveCurveToRelative(-0.448f, -1f, -1f, -1f)
            horizontalLineToRelative(-7.343f)
            curveToRelative(-0.55f, 0f, -1.021f, 0.196f, -1.412f, 0.588f)
            reflectiveCurveToRelative(-0.588f, 0.862f, -0.588f, 1.412f)
            verticalLineToRelative(14f)
            curveToRelative(0f, 0.55f, 0.196f, 1.021f, 0.588f, 1.412f)
            reflectiveCurveToRelative(0.862f, 0.588f, 1.412f, 0.588f)
            horizontalLineToRelative(14f)
            curveToRelative(0.55f, 0f, 1.021f, -0.196f, 1.412f, -0.588f)
            reflectiveCurveToRelative(0.588f, -0.862f, 0.588f, -1.412f)
            verticalLineToRelative(-7f)
            curveToRelative(0f, -0.016f, -0.007f, -0.028f, -0.008f, -0.043f)
            verticalLineToRelative(-0.298f)
            close()
        }
    }.build()
}