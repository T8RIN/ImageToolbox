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

package ru.tech.imageresizershrinker.core.data.image

import android.graphics.Bitmap
import androidx.core.graphics.BitmapCompat
import com.awxkee.jxlcoder.scale.BitmapScaleMode
import com.awxkee.jxlcoder.scale.BitmapScaler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.domain.image.ImageScaler
import ru.tech.imageresizershrinker.core.domain.repository.SettingsRepository
import javax.inject.Inject
import kotlin.math.roundToInt

class AndroidImageScaler @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ImageScaler<Bitmap> {

    override suspend fun scaleImage(
        image: Bitmap,
        width: Int,
        height: Int,
        imageScaleMode: ImageScaleMode
    ): Bitmap {
        if (width == image.width && height == image.height) return image

        val mode = imageScaleMode.takeIf {
            it != ImageScaleMode.NotPresent
        } ?: settingsRepository.getSettingsState().defaultImageScaleMode

        return mode.takeIf { it != ImageScaleMode.Default }?.let {
            BitmapScaler.scale(
                bitmap = image,
                dstWidth = width,
                dstHeight = height,
                scaleMode = BitmapScaleMode.entries.first { e -> e.ordinal == it.value }
            )
        } ?: BitmapCompat.createScaledBitmap(
            image,
            width,
            height,
            null,
            true
        )
    }

    override suspend fun scaleUntilCanShow(
        image: Bitmap?
    ): Bitmap? = withContext(Dispatchers.IO) {
        if (image == null) return@withContext null

        var (height, width) = image.run { height to width }

        var iterations = 0
        while (!canShow(size = height * width * 4)) {
            height = (height * 0.85f).roundToInt()
            width = (width * 0.85f).roundToInt()
            iterations++
        }

        return@withContext if (iterations == 0) image
        else scaleImage(
            image = image,
            height = height,
            width = width,
            imageScaleMode = ImageScaleMode.Bicubic
        )
    }

    private fun canShow(size: Int): Boolean {
        return size < 3096 * 3096 * 3
    }

}