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

package com.t8rin.imagetoolbox.core.domain.image.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BuiltInImageExportProfileTest {

    private val entries = BuiltInImageExportProfile.entries

    @Test
    fun idsAndNamesAreUnique() {
        assertEquals(entries.size, entries.map { it.id }.distinct().size)
        assertEquals(entries.size, entries.map { it.profile.name }.distinct().size)
    }

    @Test
    fun instagramProfilesUseSupportedFeedDimensions() {
        val profiles = entries
            .filter {
                it.id in setOf(
                    "instagram_square",
                    "instagram_portrait",
                    "instagram_landscape"
                )
            }
            .map { it.profile }

        assertTrue(profiles.isNotEmpty())
        profiles.forEach { profile ->
            assertEquals(1080, profile.imageInfo.width)
            assertTrue(profile.imageInfo.resizeType is ResizeType.CenterCrop)
            assertEquals(ImageFormat.Jpg, profile.imageInfo.imageFormat)

            val aspectRatio = profile.imageInfo.width.toFloat() / profile.imageInfo.height
            assertTrue(aspectRatio in (3f / 4f)..1.91f)
        }
    }

    @Test
    fun webProfilesKeepAspectRatio() {
        entries
            .filter { it.id.startsWith("web_") }
            .forEach { entry ->
                assertTrue(entry.profile.imageInfo.resizeType is ResizeType.Flexible)
                assertEquals(ImageFormat.Webp.Lossy, entry.profile.imageInfo.imageFormat)
            }
    }

    @Test
    fun socialProfilesUpscaleOnlyWhenCropNeedsToFillCanvas() {
        entries
            .filter { it.platform != BuiltInImageExportProfile.Platform.Web }
            .filter { it.id != "telegram_sticker" }
            .filter { it.profile.imageInfo.resizeType is ResizeType.CenterCrop }
            .forEach { entry ->
                val resizeType = entry.profile.imageInfo.resizeType as ResizeType.CenterCrop
                assertTrue(resizeType.upscaleToFitCanvas)
            }
    }

    @Test
    fun containsExpectedNumberOfBuiltInProfiles() {
        assertEquals(61, entries.size)
    }

    @Test
    fun telegramStickerMatchesTelegramPreset() {
        val profile = entries.first { it.id == "telegram_sticker" }.profile

        assertEquals(512, profile.imageInfo.width)
        assertEquals(512, profile.imageInfo.height)
        assertEquals(Preset.Telegram, profile.preset)
        assertEquals(ImageFormat.Png.Lossless, profile.imageInfo.imageFormat)
    }

    @Test
    fun popularPlatformsHaveBuiltInProfiles() {
        assertTrue(
            entries.map { it.platform }.containsAll(BuiltInImageExportProfile.Platform.entries)
        )
    }
}
