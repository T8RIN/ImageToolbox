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

package com.t8rin.imagetoolbox.feature.pdf_tools.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.PdfCropPresetRepository
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCropPreset
import com.t8rin.imagetoolbox.feature.pdf_tools.domain.model.PdfCropPresets
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AndroidPdfCropPresetRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val jsonParser: JsonParser
) : PdfCropPresetRepository {

    override val presets: Flow<List<PdfCropPreset>> = dataStore.data.map { preferences ->
        preferences.readPresets().presets.asReversed()
    }

    override suspend fun upsert(preset: PdfCropPreset) {
        val normalized = preset.copy(name = preset.name.trim())
        if (normalized.name.isBlank() || normalized.rect.isEmpty) return

        dataStore.edit { preferences ->
            val presets = preferences.readPresets().presets.toMutableList()
            presets.removeAll { it.name.equals(normalized.name, ignoreCase = true) }
            presets.add(normalized)
            preferences.writePresets(PdfCropPresets(presets))
        }
    }

    override suspend fun delete(preset: PdfCropPreset) {
        dataStore.edit { preferences ->
            preferences.writePresets(
                PdfCropPresets(
                    preferences.readPresets().presets.filterNot {
                        it.name.equals(preset.name, ignoreCase = true)
                    }
                )
            )
        }
    }

    private fun Preferences.readPresets(): PdfCropPresets = this[PresetsKey]
        ?.let { json ->
            runCatching {
                jsonParser.fromJson<PdfCropPresets>(
                    json = json,
                    type = PdfCropPresets::class.java
                )
            }.getOrNull()
        }
        ?: PdfCropPresets()

    private fun MutablePreferences.writePresets(presets: PdfCropPresets) {
        if (presets.presets.isEmpty()) {
            remove(PresetsKey)
            return
        }

        jsonParser.toJson(
            obj = presets,
            type = PdfCropPresets::class.java
        )?.let { json ->
            this[PresetsKey] = json
        }
    }
}

private val PresetsKey = stringPreferencesKey("PDF_CROP_PRESETS")
