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

package com.t8rin.imagetoolbox.feature.erase_background.data.backend

import android.graphics.Bitmap
import com.t8rin.imagetoolbox.feature.erase_background.domain.AutoBackgroundRemoverBackend
import com.t8rin.imagetoolbox.feature.erase_background.domain.model.BgModelType
import com.t8rin.neural_tools.bgremover.BgRemover

internal class GenericBackgroundRemoverBackend(
    private val modelType: BgModelType
) : AutoBackgroundRemoverBackend<Bitmap> {

    override suspend fun performBackgroundRemove(
        image: Bitmap
    ): Result<Bitmap> = runCatching {
        BgRemover.removeBackground(
            image = image,
            type = when (modelType) {
                BgModelType.MlKit,
                BgModelType.U2NetP -> BgRemover.Type.U2NetP

                BgModelType.U2Net -> BgRemover.Type.U2Net
                BgModelType.RMBG -> BgRemover.Type.RMBG1_4
                BgModelType.InSPyReNet -> BgRemover.Type.InSPyReNet
                BgModelType.BiRefNetTiny -> BgRemover.Type.BiRefNetTiny
                BgModelType.ISNet -> BgRemover.Type.ISNet
            }
        )!!
    }

}