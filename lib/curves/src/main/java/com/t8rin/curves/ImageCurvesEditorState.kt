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

@file:Suppress("unused")

package com.t8rin.curves

import android.graphics.PointF
import com.t8rin.curves.view.PhotoFilterCurvesControl.CurvesToolValue
import com.t8rin.curves.view.PhotoFilterCurvesControl.CurvesValue
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter

@ConsistentCopyVisibility
data class ImageCurvesEditorState internal constructor(
    internal val curvesToolValue: CurvesToolValue
) {
    constructor(controlPoints: List<List<Float>>) : this(CurvesToolValue()) {
        initControlPoints(controlPoints)
    }

    fun copy(
        controlPoints: List<List<Float>>
    ): ImageCurvesEditorState = ImageCurvesEditorState(controlPoints).also {
        it.curvesToolValue.activeEditorType = curvesToolValue.activeEditorType
        it.curvesToolValue.activeType = curvesToolValue.activeType
    }

    internal fun snapshot(): ImageCurvesEditorState = ImageCurvesEditorState(
        curvesToolValue.copy()
    )

    internal fun buildFilter(): GPUImageFilter = buildCurvesFilter(curvesToolValue)

    fun isDefault(): Boolean = curvesToolValue.allCurves.all { it.isDefault }

    /**
     * Serialized curve points. Legacy five-value curves contain only Y coordinates.
     * New curves are stored as flattened X/Y pairs so points can be placed anywhere.
     */
    val controlPoints: List<List<Float>>
        get() = curvesToolValue.allCurves.map { curve ->
            curve.points.flatMap { point -> listOf(point.x, point.y) }
        }

    private fun initControlPoints(controlPoints: List<List<Float>>) {
        val curves = curvesToolValue.allCurves
        curves.forEachIndexed { index, curve ->
            controlPoints.getOrNull(index)?.let { points ->
                curve.setPoints(points)
            }
        }
    }

    private fun CurvesValue.setPoints(points: List<Float>) {
        val parsedPoints = if (points.size == LegacyPointCount) {
            points.mapIndexed { index, y ->
                PointF(index / (LegacyPointCount - 1f), y)
            }
        } else {
            points.chunked(2)
                .mapNotNull { pair ->
                    if (pair.size == 2) PointF(pair[0], pair[1]) else null
                }
        }
        replacePoints(parsedPoints)
    }

    companion object {
        private const val LegacyPointCount = 5

        val Default: ImageCurvesEditorState
            get() = ImageCurvesEditorState(CurvesToolValue())
    }
}

fun GPUImageToneCurveFilter(
    controlPoints: List<List<Float>>
): GPUImageFilter = GPUImageToneCurveFilter(
    state = ImageCurvesEditorState(controlPoints)
)

fun GPUImageToneCurveFilter(
    state: ImageCurvesEditorState
): GPUImageFilter = state.buildFilter()
