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

import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
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
            )
        )

        private val smallModelTokenizerConfig = ModelFiles.Model(
            name = "tokenizer_config.json",
            size = 692L,
            sha256 = "92b2e6c7c829903e91bed89b790930f0aa78a6cc9744c5c71d7a48db015c47ac"
        )

        private val lite = AssistantModelFiles(
            files = common + listOf(
                smallModelTokenizerConfig,
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
                smallModelTokenizerConfig,
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

        private val max = AssistantModelFiles(
            files = common + listOf(
                ModelFiles.Model(
                    name = "tokenizer_config.json",
                    size = 691L,
                    sha256 = "abff6e9dcb26f8db399539ab12e87267f928044c3dad1c275712c6ed1e9bf28b"
                ),
                ModelFiles.Model(
                    name = "config.json",
                    size = 728L,
                    sha256 = "f7c4eadfbbf522470667b797a3c89be2524832d2d599797248dc304fff447c30"
                ),
                ModelFiles.Model(
                    name = "genai_config.json",
                    size = 1_486L,
                    sha256 = "9e2a08f9b01fc71a811742f0e67c12d4ac18bcc98c15e8a92a1d7e17cfaf6d11"
                ),
                ModelFiles.Model(
                    name = "model.onnx",
                    size = 385_431L,
                    sha256 = "6545b844d3d024003d8236ebb6eee7a2102354512215c113fca1371edc66774f"
                ),
                ModelFiles.Model(
                    name = "model.onnx.data",
                    size = 4_373_413_888L,
                    sha256 = "041b11dd4e54f34e42a64cf59e8be3b940e3f28b08e95aa7b4111cdb2a97e59d"
                )
            )
        )

        private val visionLite = AssistantModelFiles(
            files = listOf(
                ModelFiles.Model(
                    name = "genai_config.json",
                    size = 2_866L,
                    sha256 = "639443e319c3cfc4f2df9f65f50f1cffa2dadb402bb47d4e051ce3f1e4247235"
                ),
                ModelFiles.Model(
                    name = "processor_config.json",
                    size = 1_528L,
                    sha256 = "ceb4f9c08b4fb66a7dbba921d2da1074f57ddf58f12ccaa2052b0f0cf2f27caa"
                ),
                ModelFiles.Model(
                    name = "tokenizer.json",
                    size = 19_989_339L,
                    sha256 = "a5cd9732badce41de57e6efce8302930ded1c1188c5f81feb2bd6c24c4a1941f"
                ),
                ModelFiles.Model(
                    name = "tokenizer_config.json",
                    size = 1_462L,
                    sha256 = "6bcfdefdea3da31bacc5741005cd5523a6a5bffcb5af4596f6b4641643450ee1"
                ),
                ModelFiles.Model(
                    name = "embedding.onnx",
                    size = 2_745L,
                    sha256 = "a90615fb71379f3a730d72adbf2def710b4f50b46f2470daf6bad8e8054dbdc5"
                ),
                ModelFiles.Model(
                    name = "embedding.onnx.data",
                    size = 286_064_640L,
                    sha256 = "80668d32e2bc7570fc97573263f4a21aa9c142e9d313397c79800b7846caef60"
                ),
                ModelFiles.Model(
                    name = "text.onnx",
                    size = 363_968L,
                    sha256 = "51c0df5309939ce108fe4af71088ff67cc56fa7ce8e25d7f1be3b005bf89b514"
                ),
                ModelFiles.Model(
                    name = "text.onnx.data",
                    size = 851_062_016L,
                    sha256 = "e78ac9a769e99996e9c3410a33fa68b67625f9a12297b73fb6b4389eeac29c88"
                ),
                ModelFiles.Model(
                    name = "vision.onnx",
                    size = 1_539_738L,
                    sha256 = "c7e2a3ba4cb0ff56cf721b20210ea30366bddc462a8a2ae6b12783501f6e3cff"
                ),
                ModelFiles.Model(
                    name = "vision.onnx.data",
                    size = 116_981_760L,
                    sha256 = "7ffa58330aa3907db3b0869792ebc721f752d2c86ef083d7ab113bd5af3ca966"
                )
            )
        )

        private val visionAdvanced = AssistantModelFiles(
            files = listOf(
                ModelFiles.Model(
                    name = "genai_config.json",
                    size = 2_866L,
                    sha256 = "ffa0090f4f9e4c025aafd4b9f31a13e972a8abeef451c9c95e7b5204092aced4"
                ),
                ModelFiles.Model(
                    name = "processor_config.json",
                    size = 1_528L,
                    sha256 = "ceb4f9c08b4fb66a7dbba921d2da1074f57ddf58f12ccaa2052b0f0cf2f27caa"
                ),
                ModelFiles.Model(
                    name = "tokenizer.json",
                    size = 19_989_339L,
                    sha256 = "a5cd9732badce41de57e6efce8302930ded1c1188c5f81feb2bd6c24c4a1941f"
                ),
                ModelFiles.Model(
                    name = "tokenizer_config.json",
                    size = 1_462L,
                    sha256 = "6bcfdefdea3da31bacc5741005cd5523a6a5bffcb5af4596f6b4641643450ee1"
                ),
                ModelFiles.Model(
                    name = "embedding.onnx",
                    size = 2_745L,
                    sha256 = "0158757c64317e9915e31bcab906f6a44ca47831f3c5e225c2a3b460285c9526"
                ),
                ModelFiles.Model(
                    name = "embedding.onnx.data",
                    size = 572_129_280L,
                    sha256 = "58a35460276a890fec4b4a3dd8b61b8d1e5b50d85afa1ad6881012bed34635ce"
                ),
                ModelFiles.Model(
                    name = "text.onnx",
                    size = 364_527L,
                    sha256 = "21a03b0df4859c8b178b6effd1294f13b99776f6d69224de66a39ea189fe48f1"
                ),
                ModelFiles.Model(
                    name = "text.onnx.data",
                    size = 2_123_513_088L,
                    sha256 = "f306874b0c48b773364d889453cb6051fe6dfe013c5a66c3d100ea337f39224e"
                ),
                ModelFiles.Model(
                    name = "vision.onnx",
                    size = 3_123_637L,
                    sha256 = "3b89180d42d93dd410f56b63f977635ca7e1b7448367b22b9e3cada99d5b29ec"
                ),
                ModelFiles.Model(
                    name = "vision.onnx.data",
                    size = 378_339_328L,
                    sha256 = "45740b760bc963207548c86e282064403acf486cc690cea898961d9cc85e4dba"
                )
            )
        )

        private val visionMax = AssistantModelFiles(
            files = listOf(
                ModelFiles.Model(
                    name = "genai_config.json",
                    size = 2_867L,
                    sha256 = "3973fc78773a6a00e00dd4c9d3acab5b4601d2da78e6231597af3d78a6a329fa"
                ),
                ModelFiles.Model(
                    name = "processor_config.json",
                    size = 1_528L,
                    sha256 = "ceb4f9c08b4fb66a7dbba921d2da1074f57ddf58f12ccaa2052b0f0cf2f27caa"
                ),
                ModelFiles.Model(
                    name = "tokenizer.json",
                    size = 19_989_339L,
                    sha256 = "a5cd9732badce41de57e6efce8302930ded1c1188c5f81feb2bd6c24c4a1941f"
                ),
                ModelFiles.Model(
                    name = "tokenizer_config.json",
                    size = 1_462L,
                    sha256 = "6bcfdefdea3da31bacc5741005cd5523a6a5bffcb5af4596f6b4641643450ee1"
                ),
                ModelFiles.Model(
                    name = "embedding.onnx",
                    size = 2_921L,
                    sha256 = "f37fdf74f2d6bb7e6ac570200a1fc85159a9cd419a22183801180df61af51257"
                ),
                ModelFiles.Model(
                    name = "embedding.onnx.data",
                    size = 635_699_200L,
                    sha256 = "a3b3ad6274bba3cd2ca25d8702a846debcf78cb54febd62c0ee7fa0dfe278c5b"
                ),
                ModelFiles.Model(
                    name = "text.onnx",
                    size = 486_267L,
                    sha256 = "e3e30d4da301e7460947cc3d242b109f315795ff0d50572a2a37a9a8d6fd4051"
                ),
                ModelFiles.Model(
                    name = "text.onnx.data",
                    size = 4_987_078_656L,
                    sha256 = "1f0a4c30cfaf76df520922b813e6074f007885dcb1c2926bff1a9c3c4edf6002"
                ),
                ModelFiles.Model(
                    name = "vision.onnx",
                    size = 3_531_711L,
                    sha256 = "b70ad50cd254716c4be11f754b6ddb2ff2aab8a3d221866584ef1abc34469feb"
                ),
                ModelFiles.Model(
                    name = "vision.onnx.data",
                    size = 522_387_456L,
                    sha256 = "a528f6b0cbd0143b237669de93defb92845f51332506de907e14123f14b31128"
                )
            )
        )

        operator fun get(variant: AssistantModel): AssistantModelFiles = when (variant) {
            AssistantModel.Lite -> lite
            AssistantModel.Advanced -> advanced
            AssistantModel.Max -> max
            AssistantModel.VisionLite -> visionLite
            AssistantModel.VisionAdvanced -> visionAdvanced
            AssistantModel.VisionMax -> visionMax
        }
    }
}
