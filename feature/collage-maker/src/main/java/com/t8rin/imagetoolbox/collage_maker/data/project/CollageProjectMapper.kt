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

import com.t8rin.collages.config.CollageConstants
import com.t8rin.imagetoolbox.collage_maker.domain.CollageProject
import com.t8rin.imagetoolbox.collage_maker.domain.CollageProjectSnapshot
import com.t8rin.imagetoolbox.core.domain.model.DomainAspectRatio

internal fun CollageProject.toFile(assets: MutableMap<String, Int>): CollageProjectFile =
    CollageProjectFile(
        current = current.toFile(assets),
        history = history.map { it.toFile(assets) },
        redoHistory = redoHistory.map { it.toFile(assets) }
    )

internal fun CollageProjectFile.toProject(assets: Map<Int, String>): CollageProject {
    require(version == CollageProjectVersion)
    require(current.state != null && current.layoutId != null)
    return CollageProject(
        current = current.toSnapshot(assets),
        history = history.map { it.toSnapshot(assets) },
        redoHistory = redoHistory.map { it.toSnapshot(assets) }
    )
}

private fun CollageProjectSnapshot.toFile(
    assets: MutableMap<String, Int>
): CollageProjectSnapshotFile = CollageProjectSnapshotFile(
    imageIds = uris?.map { uri -> assets.getOrPut(uri) { assets.size } },
    layoutId = layoutId,
    state = collageState?.copy(
        images = collageState.images.map { it.copy(uri = null) }
    ),
    aspectRatio = CollageProjectAspectRatio(
        width = aspectRatio.widthProportion,
        height = aspectRatio.heightProportion,
        isCustom = aspectRatio is DomainAspectRatio.Custom,
        isSaved = (aspectRatio as? DomainAspectRatio.Custom)?.isSaved == true
    ),
    backgroundColor = backgroundColor,
    backgroundGradient = backgroundGradient,
    params = params,
    imageFormat = imageFormat,
    quality = quality
)

private fun CollageProjectSnapshotFile.toSnapshot(
    assets: Map<Int, String>
): CollageProjectSnapshot {
    val images = imageIds?.map { requireNotNull(assets[it]) }
    val count = images.orEmpty().size
    require(count <= CollageConstants.MAX_IMAGE_COUNT)
    require(layoutId == null || count > 0)
    state?.let { state ->
        require(state.layoutId == layoutId)
        require(state.design?.let { it.isValid() && it.slots.size == count } != false)
        require(state.images.map { it.index }.sorted() == (0 until count).toList())
        require(state.layoutParams.all(Float::isFinite))
        require(
            state.images.all {
                (it.matrixValues.isEmpty() || it.matrixValues.size == 9) &&
                        it.matrixValues.all(Float::isFinite) &&
                        it.viewWidth.isFinite() && it.viewWidth >= 0f &&
                        it.viewHeight.isFinite() && it.viewHeight >= 0f
            }
        )
    }
    val ratio = aspectRatio.toDomain()
    require(
        ratio == DomainAspectRatio.Free || ratio == DomainAspectRatio.Original ||
                ratio.widthProportion.isFinite() && ratio.widthProportion > 0f &&
                ratio.heightProportion.isFinite() && ratio.heightProportion > 0f
    )
    require(params.spacing in 0f..50f && params.cornerRadius in 0f..50f)
    require(params.shadowRadius in 0f..20f)
    require(params.outerPadding in 0f..50f && params.borderWidth in 0f..30f)
    require(
        params.slotStyles.size <= count && params.slotStyles.all {
            (it.cornerRadius?.let { radius -> radius in 0f..50f } != false) && it.borderWidth in 0f..30f && it.shadowRadius in 0f..20f
        }
    )
    require(params.slotBorderGradients.size <= count)
    require(params.outputScaleRatio in 0.1f..1f)

    return CollageProjectSnapshot(
        uris = images,
        layoutId = layoutId,
        collageState = state?.copy(
            images = state.images.map { it.copy(uri = images?.get(it.index)) }
        ),
        aspectRatio = ratio,
        backgroundColor = backgroundColor,
        backgroundGradient = backgroundGradient,
        params = params,
        imageFormat = imageFormat,
        quality = quality.coerceIn(imageFormat)
    )
}

private fun CollageProjectAspectRatio.toDomain(): DomainAspectRatio = when {
    isCustom -> DomainAspectRatio.Custom(width, height, isSaved)
    width == DomainAspectRatio.Free.widthProportion -> DomainAspectRatio.Free
    width == DomainAspectRatio.Original.widthProportion -> DomainAspectRatio.Original
    else -> DomainAspectRatio.Numeric(width, height)
}