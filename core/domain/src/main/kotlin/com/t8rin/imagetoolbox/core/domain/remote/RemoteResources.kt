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

package com.t8rin.imagetoolbox.core.domain.remote

data class RemoteResources(
    val name: String,
    val list: List<RemoteResource>
) {

    companion object {
        const val CUBE_LUT = "cubelut/luts.zip"
        const val MESH_GRADIENTS = "mesh_gradient/mesh_gradients.zip"

        val CubeLutDefault = RemoteResources(
            name = CUBE_LUT,
            list = emptyList()
        )

        val MeshGradientsDefault = RemoteResources(
            name = MESH_GRADIENTS,
            list = emptyList()
        )
    }

}

data class RemoteResource(
    val uri: String,
    val name: String
)