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

package ru.tech.imageresizershrinker.feature.watermarking.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import androidx.exifinterface.media.ExifInterface
import com.watermark.androidwm.WatermarkBuilder
import com.watermark.androidwm.bean.WatermarkImage
import com.watermark.androidwm.bean.WatermarkText
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.model.IntegerSize
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkApplier
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkParams
import ru.tech.imageresizershrinker.feature.watermarking.domain.WatermarkingType
import javax.inject.Inject

class AndroidWatermarkApplier @Inject constructor(
    @ApplicationContext private val context: Context,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>
) : WatermarkApplier<Bitmap> {

    override suspend fun applyWatermark(
        image: Bitmap,
        originalSize: Boolean,
        params: WatermarkParams
    ): Bitmap? = withContext(Dispatchers.IO) {
        val builder: WatermarkBuilder? = when (val type = params.watermarkingType) {
            is WatermarkingType.Text -> {
                WatermarkBuilder
                    .create(context, image, !originalSize)
                    .loadWatermarkText(
                        WatermarkText(type.text)
                            .setPositionX(params.positionX.toDouble())
                            .setPositionY(params.positionY.toDouble())
                            .setRotation(params.rotation.toDouble())
                            .setTextAlpha(
                                (params.alpha * 255).toInt()
                            )
                            .setTextSize(type.size.toDouble())
                            .setBackgroundColor(type.backgroundColor)
                            .setTextColor(type.color)
                            .setTextFont(type.font)
                            .setTextStyle(
                                Paint.Style.values().first { it.ordinal == type.style }
                            )
                    )
                    .setTileMode(params.isRepeated)
                    .setPorterDuffMode(
                        PorterDuff.Mode.values().first { it.ordinal == params.overlayMode }
                    )
            }

            is WatermarkingType.Image -> {
                imageGetter.getImage(
                    data = type.imageData,
                    size = IntegerSize(
                        (image.width * type.size).toInt(),
                        (image.height * type.size).toInt()
                    )
                )?.let { bitmap ->
                    WatermarkBuilder
                        .create(context, image, !originalSize)
                        .loadWatermarkImage(
                            WatermarkImage(bitmap)
                                .setPositionX(params.positionX.toDouble())
                                .setPositionY(params.positionY.toDouble())
                                .setRotation(params.rotation.toDouble())
                                .setImageAlpha(
                                    (params.alpha * 255).toInt()
                                )
                                .setSize(type.size.toDouble())
                        )
                        .setTileMode(params.isRepeated)
                        .setPorterDuffMode(
                            PorterDuff.Mode.values().first { it.ordinal == params.overlayMode }
                        )
                }
            }
        }

        builder?.watermark?.outputImage
    }

}