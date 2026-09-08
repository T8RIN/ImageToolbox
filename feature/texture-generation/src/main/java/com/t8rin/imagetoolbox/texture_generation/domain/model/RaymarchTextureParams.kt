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