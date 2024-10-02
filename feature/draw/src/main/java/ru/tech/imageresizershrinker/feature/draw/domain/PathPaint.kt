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

package ru.tech.imageresizershrinker.feature.draw.domain

import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.core.domain.model.Pt

interface PathPaint<Path, Color> {
    operator fun component1() = path
    operator fun component2() = strokeWidth
    operator fun component3() = brushSoftness
    operator fun component4() = drawColor
    operator fun component5() = isErasing
    operator fun component6() = drawMode
    operator fun component7() = canvasSize
    operator fun component8() = drawPathMode
    operator fun component9() = drawLineStyle


    val path: Path
    val strokeWidth: Pt
    val brushSoftness: Pt
    val drawColor: Color
    val isErasing: Boolean
    val drawMode: DrawMode
    val canvasSize: IntegerSize
    val drawPathMode: DrawPathMode
    val drawLineStyle: DrawLineStyle
}