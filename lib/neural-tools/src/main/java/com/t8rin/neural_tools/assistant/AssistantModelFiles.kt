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

package com.t8rin.neural_tools.assistant

import com.t8rin.neural_tools.ModelFiles

internal class AssistantModelFiles private constructor(
    val files: List<ModelFiles.Model>
) {
    val size: Long = files.sumOf(ModelFiles.Model::size)

    companion object {
        const val RUNTIME_VERSION = "0.17.0"

        private val common = listOf(
            ModelFiles.Model(
                name = "generation_config.json",
                size = 239L,
                sha256 = "2325da0f15bb848e018c5ae071b7943332e9f871d6b60e2ed22ca97d4cb993d2"
            ),
            ModelFiles.Model(
                name = "tokenizer.json",
                size = 11_422_650L,
                sha256 = "be75606093db2094d7cd20f3c2f385c212750648bd6ea4fb2bf507a6a4c55506"
            ),
            ModelFiles.Model(
                name = "tokenizer_config.json",
                size = 692L,
                sha256 = "92b2e6c7c829903e91bed89b790930f0aa78a6cc9744c5c71d7a48db015c47ac"
            )
        )

        private val lite = AssistantModelFiles(
            files = common + listOf(
                ModelFiles.Model(
                    name = "config.json",
                    size = 726L,
                    sha256 = "660db3b73d788119c04535e48cf9be5f55bc3100841a718637ae695b442f27dd"
                ),
                ModelFiles.Model(
                    name = "genai_config.json",
                    size = 1_486L,
                    sha256 = "7e6d3d247255fe94a4b76a24c81d838b325f1e99b7c9a015779e21553b739703"
                ),
                ModelFiles.Model(
                    name = "model.onnx",
                    size = 300_036L,
                    sha256 = "065babb582852ec304189a4fcb0983276541c95b1144425772756224cb638c64"
                ),
                ModelFiles.Model(
                    name = "model.onnx.data",
                    size = 393_740_288L,
                    sha256 = "eff419e6e53a1b67e137bbd3d8a248885dd27095f1a0ea349a42bc2bd72cf71a"
                )
            )
        )

        private val advanced = AssistantModelFiles(
            files = common + listOf(
                ModelFiles.Model(
                    name = "config.json",
                    size = 726L,
                    sha256 = "1ddb5b89ebc90dcb417a45c213d818577e65976454d29385c8f6140771d95197"
                ),
                ModelFiles.Model(
                    name = "genai_config.json",
                    size = 1_486L,
                    sha256 = "520ddc6a31325666de967b9ba7f472438c6aa0b566bcc3c4e7c35182c8f7ba66"
                ),
                ModelFiles.Model(
                    name = "model.onnx",
                    size = 300_435L,
                    sha256 = "a7b541ad764a6ba46af745e70f5bca2840dbd191f2cc05e5832a638b1a45f675"
                ),
                ModelFiles.Model(
                    name = "model.onnx.data",
                    size = 1_096_810_496L,
                    sha256 = "50445e40659edc6c44d11bc095325eedd6377382a7133d15c4f78a254213cbfc"
                )
            )
        )

        operator fun get(variant: AssistantModels.Variant): AssistantModelFiles = when (variant) {
            AssistantModels.Variant.Lite -> lite
            AssistantModels.Variant.Advanced -> advanced
        }
    }
}
