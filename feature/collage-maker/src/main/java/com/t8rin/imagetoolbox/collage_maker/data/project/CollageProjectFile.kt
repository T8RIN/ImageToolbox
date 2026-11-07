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

package com.t8rin.imagetoolbox.collage_maker.data.project

import com.t8rin.collages.model.CollageState
import com.t8rin.imagetoolbox.collage_maker.domain.CollageParams
import com.t8rin.imagetoolbox.core.domain.image.model.ImageFormat
import com.t8rin.imagetoolbox.core.domain.image.model.Quality
import com.t8rin.imagetoolbox.core.domain.model.GradientFill

internal const val CollageProjectVersion = 1

internal data class CollageProjectFile(
    val version: Int = CollageProjectVersion,
    val current: CollageProjectSnapshotFile,
    val history: List<CollageProjectSnapshotFile> = emptyList(),
    val redoHistory: List<CollageProjectSnapshotFile> = emptyList()
)

internal data class CollageProjectSnapshotFile(
    val imageIds: List<Int>? = null,
    val layoutId: String? = null,
    val state: CollageState?,
    val aspectRatio: CollageProjectAspectRatio,
    val backgroundColor: Int,
    val backgroundGradient: GradientFill?,
    val params: CollageParams,
    val imageFormat: ImageFormat,
    val quality: Quality
)

internal data class CollageProjectAspectRatio(
    val width: Float,
    val height: Float,
    val isCustom: Boolean,
    val isSaved: Boolean
)