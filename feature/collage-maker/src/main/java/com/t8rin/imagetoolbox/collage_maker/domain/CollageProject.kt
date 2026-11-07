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

package com.t8rin.imagetoolbox.collage_maker.domain

import com.t8rin.collages.model.CollageState
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio
import com.t8rin.imagetoolbox.core.domain.model.GradientFill

internal data class CollageProject(
    val current: CollageProjectSnapshot,
    val history: List<CollageProjectSnapshot>,
    val redoHistory: List<CollageProjectSnapshot>
)

internal data class CollageProjectSnapshot(
    val uris: List<String>?,
    val layoutId: String?,
    val collageState: CollageState?,
    val aspectRatio: DomainAspectRatio,
    val backgroundColor: Int,
    val backgroundGradient: GradientFill?,
    val params: CollageParams,
    val imageFormat: ImageFormat,
    val quality: Quality
)