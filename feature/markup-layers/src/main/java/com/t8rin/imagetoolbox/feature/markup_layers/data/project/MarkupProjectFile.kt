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

package com.t8rin.imagetoolbox.feature.markup_layers.data.project

data class MarkupProjectFile(
    val version: Int = MarkupProjectVersion,
    val imageFormat: String?,
    val saveExif: Boolean,
    val background: BackgroundSnapshot,
    val layers: List<LayerSnapshot>,
    val lastLayers: List<LayerSnapshot>,
    val undoneLayers: List<LayerSnapshot>,
)

data class BackgroundSnapshot(
    val type: BackgroundType,
    val assetPath: String? = null,
    val width: Int? = null,
    val height: Int? = null,
    val color: Int? = null,
)

data class LayerSnapshot(
    val type: LayerSnapshotType,
    val position: PositionSnapshot,
    val text: TextSnapshot? = null,
    val picture: PictureSnapshot? = null,
)

data class PositionSnapshot(
    val scale: Float,
    val rotation: Float,
    val offsetX: Float,
    val offsetY: Float,
    val alpha: Float,
    val canvasWidth: Int,
    val canvasHeight: Int,
    val coerceToBounds: Boolean,
    val isVisible: Boolean,
)

data class TextSnapshot(
    val color: Int,
    val size: Float,
    val font: FontSnapshot?,
    val backgroundColor: Int,
    val text: String,
    val decorations: List<String>,
    val outline: OutlineSnapshot?,
    val alignment: String,
)

data class PictureSnapshot(
    val assetPath: String? = null,
    val value: String? = null,
)

data class FontSnapshot(
    val type: FontSnapshotType,
    val resourceId: Int? = null,
    val path: String? = null,
)

data class OutlineSnapshot(
    val color: Int,
    val width: Float,
)

enum class BackgroundType {
    None, Image, Color
}

enum class LayerSnapshotType {
    Text, Image, Sticker
}

enum class FontSnapshotType {
    File, Resource
}
