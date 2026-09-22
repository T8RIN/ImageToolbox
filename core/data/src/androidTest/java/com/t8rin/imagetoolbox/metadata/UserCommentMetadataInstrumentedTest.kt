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

/*
 * Regression test for WebP UserComment byte order and MIME-type stability.
 */
package com.t8rin.imagetoolbox.core.data.image

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.exif.ExifInterface
import com.t8rin.imagetoolbox.core.domain.image.clearAllAttributes
import com.t8rin.imagetoolbox.core.domain.image.get
import com.t8rin.imagetoolbox.core.domain.image.model.MetadataTag
import com.t8rin.imagetoolbox.core.domain.image.set
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class UserCommentMetadataInstrumentedTest {

    @Test
    fun webpUserCommentSurvivesFreshMetadataReadsAndSecondSave() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val file = File(context.cacheDir, "user_comment_${System.nanoTime()}.webp")

        try {
            InstrumentationRegistry.getInstrumentation().context.assets
                .open("metadata_round_trip/rich.webp").use { input ->
                file.outputStream().use(input::copyTo)
            }

            val comment = "ImageToolbox comment — Привет, 世界"

            ExifInterface(file).toMetadata().apply {
                clearAllAttributes()
                this[MetadataTag.UserComment] = comment
                // Regression coverage: this tag used to reclassify a reopened
                // WebP as DNG, exposing the mutable-mime-type byte-order bug.
                this[MetadataTag.DngVersion] = "1.4.0.0"
                saveAttributes()
            }

            val reopened = ExifInterface(file).toMetadata()
            assertEquals(comment, reopened[MetadataTag.UserComment])

            // A second save proves that the outer WebP container type stayed intact.
            reopened[MetadataTag.Model] = "ImageToolbox second save"
            reopened.saveAttributes()

            val reopenedAgain = ExifInterface(file).toMetadata()
            assertEquals(comment, reopenedAgain[MetadataTag.UserComment])
            assertEquals(
                "ImageToolbox second save",
                reopenedAgain[MetadataTag.Model]
            )

            file.inputStream().use { input ->
                val streamMetadata = ExifInterface(input).toMetadata()
                assertEquals(comment, streamMetadata[MetadataTag.UserComment])
            }
        } finally {
            file.delete()
        }
    }
}
