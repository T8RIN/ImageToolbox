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

package com.t8rin.neural_tools.tryon

import com.t8rin.neural_tools.ModelFiles

internal object MobileVtonModels {

    const val size = 5956389249L

    val files = ModelFiles(
        "mobile-vton/v1-1024",
        ModelFiles.Model(
            name = "denoiser.onnx",
            size = 3016091L,
            sha256 = "efa1774ff30859163e11217f5a04e6f10a8e2c1cbdb931ce48b42e9188656b36"
        ),
        ModelFiles.Model(
            name = "denoiser.onnx.data",
            size = 1526500416L,
            sha256 = "0d34b161086ab412633276b9185de15c51e98fdaeae21f24645466007843daa6"
        ),
        ModelFiles.Model(
            name = "embedding.onnx",
            size = 2745L,
            sha256 = "a90615fb71379f3a730d72adbf2def710b4f50b46f2470daf6bad8e8054dbdc5"
        ),
        ModelFiles.Model(
            name = "embedding.onnx.data",
            size = 286064640L,
            sha256 = "80668d32e2bc7570fc97573263f4a21aa9c142e9d313397c79800b7846caef60"
        ),
        ModelFiles.Model(
            name = "garment.onnx",
            size = 1779279L,
            sha256 = "708e395a3f8dea1cd03899c36c068279c50ebcd0b33d34ce71bebb40d1b5a2a6"
        ),
        ModelFiles.Model(
            name = "garment.onnx.data",
            size = 1068900864L,
            sha256 = "ef1a5b570f63e1d119febe63be35b0e097cfbe4349aac841775cd3a09f7d1ed4"
        ),
        ModelFiles.Model(
            name = "genai_config.json",
            size = 2866L,
            sha256 = "639443e319c3cfc4f2df9f65f50f1cffa2dadb402bb47d4e051ce3f1e4247235"
        ),
        ModelFiles.Model(
            name = "image_encoder.onnx",
            size = 127429L,
            sha256 = "bc5e0498bdff483c7048faa51369747967a546aafa4c6a637cf676fa5ebca951"
        ),
        ModelFiles.Model(
            name = "image_encoder.onnx.data",
            size = 317952000L,
            sha256 = "6d50264d5de06a99103c0312912c9064c9b418f6a86fdc7bf8f1bfaa51875fbd"
        ),
        ModelFiles.Model(
            name = "image_preprocessor.json",
            size = 436L,
            sha256 = "14e780d86fa1861f8751f868d7f45425b5feb55c38ca26f152ca5097ab30f828"
        ),
        ModelFiles.Model(
            name = "image_projection.onnx",
            size = 37596L,
            sha256 = "d74f5d8393b88b5c809a5f3b45582b1f8bb503790e4d9c7c11c335f552357491"
        ),
        ModelFiles.Model(
            name = "image_projection.onnx.data",
            size = 153495552L,
            sha256 = "ed64c1d73f05b1883e0f4005e5d1000c7f43b3f406afecbefdd1bcc0fa13dc9d"
        ),
        ModelFiles.Model(
            name = "processor_config.json",
            size = 1528L,
            sha256 = "ceb4f9c08b4fb66a7dbba921d2da1074f57ddf58f12ccaa2052b0f0cf2f27caa"
        ),
        ModelFiles.Model(
            name = "scheduler.json",
            size = 414L,
            sha256 = "6b5c70ac26b9b4197bccb241da95c5b0a67f3a6febd46780b9bda1beb8b13df3"
        ),
        ModelFiles.Model(
            name = "text.onnx",
            size = 363968L,
            sha256 = "51c0df5309939ce108fe4af71088ff67cc56fa7ce8e25d7f1be3b005bf89b514"
        ),
        ModelFiles.Model(
            name = "text.onnx.data",
            size = 851062016L,
            sha256 = "e78ac9a769e99996e9c3410a33fa68b67625f9a12297b73fb6b4389eeac29c88"
        ),
        ModelFiles.Model(
            name = "text_encoder.onnx",
            size = 198734L,
            sha256 = "ba9eef029745b909bc2f9d55fc538b4cb4c11e4dc2031facfde0bb9c1afbe72c"
        ),
        ModelFiles.Model(
            name = "text_encoder.onnx.data",
            size = 494601216L,
            sha256 = "44d0ca176a1cb818afe1edd450e052b5ef3883347b44aca89c14dc0f4d21ee73"
        ),
        ModelFiles.Model(
            name = "text_encoder_2.onnx",
            size = 568310L,
            sha256 = "0939725fd30a7155ac47fb0d6a10520bd0f91b16d93fe798bfe6b1e8482b417d"
        ),
        ModelFiles.Model(
            name = "text_encoder_2.onnx.data",
            size = 965159076L,
            sha256 = "260c8086f87bd12b4b6c09b781d71615ae6e32d51c99f2a84e6de4fd223eae46"
        ),
        ModelFiles.Model(
            name = "tokenizer.json",
            size = 19989339L,
            sha256 = "a5cd9732badce41de57e6efce8302930ded1c1188c5f81feb2bd6c24c4a1941f"
        ),
        ModelFiles.Model(
            name = "tokenizer_2_merges.txt",
            size = 524619L,
            sha256 = "9fd691f7c8039210e0fced15865466c65820d09b63988b0174bfe25de299051a"
        ),
        ModelFiles.Model(
            name = "tokenizer_2_special_tokens_map.json",
            size = 576L,
            sha256 = "c4dbb96da703fb38f10ccf0490df2fd476811c5a3e71b7e0189cffeed3224e25"
        ),
        ModelFiles.Model(
            name = "tokenizer_2_tokenizer_config.json",
            size = 856L,
            sha256 = "e70eb3e2b85a508e32bb81c32c13fc3f2b015548ca469b94627c2f32e445cba1"
        ),
        ModelFiles.Model(
            name = "tokenizer_2_vocab.json",
            size = 1059962L,
            sha256 = "e089ad92ba36837a0d31433e555c8f45fe601ab5c221d4f607ded32d9f7a4349"
        ),
        ModelFiles.Model(
            name = "tokenizer_config.json",
            size = 1462L,
            sha256 = "6bcfdefdea3da31bacc5741005cd5523a6a5bffcb5af4596f6b4641643450ee1"
        ),
        ModelFiles.Model(
            name = "tokenizer_merges.txt",
            size = 524619L,
            sha256 = "9fd691f7c8039210e0fced15865466c65820d09b63988b0174bfe25de299051a"
        ),
        ModelFiles.Model(
            name = "tokenizer_special_tokens_map.json",
            size = 588L,
            sha256 = "2cdb3b8331a60c92fc1e55a13e9fd61fd2293c5a51275fdcccd62b780052530e"
        ),
        ModelFiles.Model(
            name = "tokenizer_tokenizer_config.json",
            size = 705L,
            sha256 = "6bdcee9ccce2a16ca2b4c0c5ed00b42c50ea225f4472a8c4c1e963a2902c2881"
        ),
        ModelFiles.Model(
            name = "tokenizer_vocab.json",
            size = 1059962L,
            sha256 = "e089ad92ba36837a0d31433e555c8f45fe601ab5c221d4f607ded32d9f7a4349"
        ),
        ModelFiles.Model(
            name = "vae_decoder.onnx",
            size = 51487L,
            sha256 = "8ba2f50a75f66da644dc132a57f49294ab7b7ed44c40da886adbed9ce19180f7"
        ),
        ModelFiles.Model(
            name = "vae_decoder.onnx.data",
            size = 7610380L,
            sha256 = "65c8163998de07e8eec10edb91f377e5ae865b5de4261196a73f655f50646561"
        ),
        ModelFiles.Model(
            name = "vae_encoder.onnx",
            size = 111188L,
            sha256 = "35a9d9f10efdf9846008065604c605c99647349889949ecb52150b7656635ca1"
        ),
        ModelFiles.Model(
            name = "vae_encoder.onnx.data",
            size = 137096832L,
            sha256 = "519e30cb592f3065b5e51f498ecb427996f237ad511740a62e6e065773a3c934"
        ),
        ModelFiles.Model(
            name = "vision.onnx",
            size = 1539738L,
            sha256 = "c7e2a3ba4cb0ff56cf721b20210ea30366bddc462a8a2ae6b12783501f6e3cff"
        ),
        ModelFiles.Model(
            name = "vision.onnx.data",
            size = 116981760L,
            sha256 = "7ffa58330aa3907db3b0869792ebc721f752d2c86ef083d7ab113bd5af3ca966"
        )
    )
}