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

package com.t8rin.imagetoolbox.feature.assistant

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.resources.getHelpString
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

@RunWith(AndroidJUnit4::class)
class HelpResourcesInstrumentedTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun selectsTranslationsAndFallsBackToEnglish() {
        listOf(
            "ru" to "Выберите правильный инструмент",
            "en" to "Choose the right tool",
            "ar" to "اختر الأداة المناسبة",
            "zh-TW" to "選擇正確的工具",
            "zu" to "Choose the right tool",
            "ru" to "Выберите правильный инструмент"
        ).forEach { (language, title) ->
            assertEquals(
                language,
                title,
                localizedContext(language).getHelpString(R.string.help_tip_choose_tool_title)
            )
        }
    }

    @Test
    fun resolvesHelpAndFilterDescriptionsForEveryTranslation() {
        val languages = listOf(
            "en", "ar", "be", "bn", "ca", "cs", "da", "de", "es", "et", "fa", "fi",
            "fil", "fr", "hi", "hu", "id", "it", "he", "ja", "kk", "ko", "lt", "nl",
            "pl", "pt-BR", "ro", "ru", "sk", "sr", "sv", "ta", "te", "th", "tr",
            "uk", "vi", "zh-CN", "zh-TW"
        )
        val expectedKeys =
            localizedContext("en").resources.openRawResource(R.raw.help_texts).bufferedReader()
                .use {
                    JSONObject(it.readText()).keys().asSequence().toSet()
                }
        languages.forEach { language ->
            val localizedContext = localizedContext(language)
            val resources = localizedContext.resources
            val strings = resources.openRawResource(R.raw.help_texts).bufferedReader().use {
                JSONObject(it.readText())
            }
            assertEquals(language, expectedKeys, strings.keys().asSequence().toSet())
            listOf(
                R.string.help_tip_choose_tool_title,
                R.string.help_tip_choose_tool_description,
                R.string.help_tip_choose_tool_step_1,
                R.string.filter_help_aces_filmic_tone_mapping
            ).forEach { id ->
                val key = resources.getString(id)
                assertEquals(language, strings.getString(key), localizedContext.getHelpString(id))
                assertTrue(language, localizedContext.getHelpString(id).isNotBlank())
            }
            assertEquals(
                resources.getString(R.string.close),
                localizedContext.getHelpString(R.string.close)
            )
        }
    }

    @Test
    fun preservesFormattingOfRegularStrings() {
        listOf("en", "ru").forEach { language ->
            val localizedContext = localizedContext(language)
            val resources = localizedContext.resources
            assertEquals(
                resources.getString(R.string.download_description, "12 MB", "Tesseract"),
                localizedContext.getHelpString(R.string.download_description, "12 MB", "Tesseract")
            )
        }
    }

    @Test
    fun packagesHelpAsCompressedRawResources() {
        ZipFile(context.applicationInfo.sourceDir).use { apk ->
            listOf("en", "ru", "ar", "zh-TW").forEach { language ->
                val value = TypedValue().also {
                    localizedContext(language).resources.getValue(R.raw.help_texts, it, true)
                }
                val entry = checkNotNull(apk.getEntry(value.string.toString()))
                assertEquals(language, ZipEntry.DEFLATED, entry.method)
                assertTrue(language, entry.compressedSize < entry.size)
            }
        }
    }

    private fun localizedContext(language: String): Context = context.createConfigurationContext(
        Configuration(context.resources.configuration).apply {
            setLocale(Locale.forLanguageTag(language))
        }
    )
}