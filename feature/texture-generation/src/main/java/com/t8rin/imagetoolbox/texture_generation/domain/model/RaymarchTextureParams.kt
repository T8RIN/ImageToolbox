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

package com.t8rin.imagetoolbox.texture_generation.domain.model

import com.t8rin.imagetoolbox.core.domain.model.ColorModel

data class RaymarchTextureParams(
    val environment: String? = null,
    val innerRadius: Float = 0.9f,
    val size: Float = 1f,
    val thickness: Float = 0.18f,
    val roundness: Float = 0.06f,
    val count: Int = 3,
    val rotationX: Float = 20f,
    val rotationY: Float = 30f,
    val rotationZ: Float = 0f,
    val cameraDistance: Float = 3.5f,
    val cameraYaw: Float = 0f,
    val cameraPitch: Float = 0f,
    val fieldOfView: Float = 45f,
    val metallic: Float = 0.8f,
    val roughness: Float = 0.25f,
    val transmission: Float = 0f,
    val lightAngle: Float = -35f,
    val lightElevation: Float = 50f
)

internal fun TextureFilterType.defaultRaymarchParams(): RaymarchTextureParams = when (this) {
    TextureFilterType.InfiniteSpheroids3D,
    TextureFilterType.InfiniteStructure3D,
    TextureFilterType.InfiniteCylinders3D,
    TextureFilterType.TruchetRings3D,
    TextureFilterType.TruchetPipes3D -> RaymarchTextureParams(
        size = 0.75f,
        thickness = 0.14f,
        cameraDistance = 7.2f,
        metallic = 0.7f,
        roughness = 0.22f
    )

    TextureFilterType.TorusGem3D -> RaymarchTextureParams(
        metallic = 0.2f,
        roughness = 0.08f,
        transmission = 0.65f
    )

    TextureFilterType.Sphere3D,
    TextureFilterType.EmptiedCube3D -> RaymarchTextureParams(
        metallic = 0.4f,
        roughness = 0.24f
    )

    else -> RaymarchTextureParams()
}

internal fun TextureFilterType.defaultRaymarchColor(): ColorModel = ColorModel(
    when (this) {
        TextureFilterType.Sphere3D,
        TextureFilterType.EmptiedCube3D -> 0xFFE3CFB0.toInt()

        TextureFilterType.CubeStair3D,
        TextureFilterType.Torus3D -> 0xFFE0A575.toInt()

        TextureFilterType.MobiusTorus3D,
        TextureFilterType.CubeSpheres3D,
        TextureFilterType.TruchetPipes3D -> 0xFF77B7B0.toInt()

        TextureFilterType.CubeOctahedron3D -> 0xFFC5A5C5.toInt()

        TextureFilterType.InfiniteStructure3D -> 0xFFD4B778.toInt()

        else -> 0xFFB4C9DD.toInt()
    }
)