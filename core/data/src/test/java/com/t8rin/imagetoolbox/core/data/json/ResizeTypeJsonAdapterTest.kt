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

package com.t8rin.imagetoolbox.core.data.json

import com.t8rin.imagetoolbox.core.data.di.JsonModule
import com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfile
import com.t8rin.imagetoolbox.core.domain.image.model.ImageInfo
import com.t8rin.imagetoolbox.core.domain.image.model.ResizeType
import org.junit.Assert.assertEquals
import org.junit.Test

class ResizeTypeJsonAdapterTest {
    private val moshi = JsonModule.moshi()

    @Test
    fun blurredBackgroundSurvivesProfileRoundTrip() {
        val adapter = moshi.adapter(ImageExportProfile::class.java)
        listOf(
            ResizeType.Fit(canvasColor = null, blurRadius = 35),
            ResizeType.CenterCrop(canvasColor = null, blurRadius = 35)
        ).forEach { resizeType ->
            val profile = ImageExportProfile(
                name = "Blurred edges",
                imageInfo = ImageInfo(resizeType = resizeType)
            )

            assertEquals(profile, adapter.fromJson(adapter.toJson(profile)))
        }
    }

    @Test
    fun oldProfilesWithoutBlurFlagKeepSolidBackground() {
        val adapter = moshi.adapter(ResizeType::class.java)

        assertEquals(
            ResizeType.Fit(canvasColor = 0, blurRadius = 35),
            adapter.fromJson("""{"type":"fit","canvasColor":0,"blurRadius":35}""")
        )
        assertEquals(
            ResizeType.CenterCrop(canvasColor = 0, blurRadius = 35),
            adapter.fromJson("""{"type":"center_crop","canvasColor":0,"blurRadius":35}""")
        )
    }
}