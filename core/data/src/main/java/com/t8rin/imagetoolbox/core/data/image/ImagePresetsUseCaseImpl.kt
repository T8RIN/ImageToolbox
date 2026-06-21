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

package com.t8rin.imagetoolbox.core.data.image

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.t8rin.imagetoolbox.core.domain.image.ImagePresetsUseCase
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImagePreset
import com.t8rin.imagetoolbox.core.domain.image.model.ImagePresets
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ImagePresetsUseCaseImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val fileController: FileController,
    private val jsonParser: JsonParser,
    private val shareProvider: ShareProvider
) : ImagePresetsUseCase {

    override val presets: Flow<List<ImagePreset>> = dataStore.data.map { preferences ->
        preferences.readPresets().presets
    }

    override suspend fun upsert(preset: ImagePreset) {
        val normalized = preset.copy(name = preset.name.trim())
        if (normalized.name.isBlank()) return

        dataStore.edit { preferences ->
            val list = preferences.readPresets().presets.toMutableList()
            val index = list.indexOfFirst {
                it.name.equals(normalized.name, ignoreCase = true)
            }
            if (index >= 0) {
                list[index] = normalized.copy(name = list[index].name)
            } else {
                list.add(normalized)
            }
            preferences.writePresets(ImagePresets(list))
        }
    }

    override suspend fun delete(preset: ImagePreset) {
        dataStore.edit { preferences ->
            val current = preferences.readPresets()

            preferences.writePresets(
                current.copy(
                    presets = current.presets.filterNot {
                        it.name.equals(preset.name, ignoreCase = true)
                    }
                )
            )
        }
    }

    override suspend fun export(preset: ImagePreset) {
        jsonParser.toJson(
            obj = preset,
            type = ImagePreset::class.java
        )?.let { json ->
            shareProvider.shareData(
                filename = "${preset.name.safePresetFileName()}.itpreset",
                writeData = {
                    it.writeBytes(json.encodeToByteArray())
                }
            )
        }
    }

    override suspend fun importPreset(uri: String) {
        runSuspendCatching {
            fileController.readBytes(uri).decodeToString()
        }.mapCatching { json ->
            jsonParser.fromJson<ImagePreset>(
                json = json,
                type = ImagePreset::class.java
            )
        }.getOrNull()?.let { preset ->
            upsert(preset)
        }
    }

    private fun Preferences.readPresets(): ImagePresets {
        val json = this[PresetsKey]

        return json?.let {
            jsonParser.fromJson<ImagePresets>(
                json = it,
                type = ImagePresets::class.java
            )
        } ?: ImagePresets()
    }

    private fun MutablePreferences.writePresets(
        presets: ImagePresets
    ) {
        if (presets.presets.isEmpty()) {
            remove(PresetsKey)
            return
        }

        jsonParser.toJson(
            obj = presets,
            type = ImagePresets::class.java
        )?.let { json ->
            this[PresetsKey] = json
        }
    }

    private fun String.safePresetFileName(): String = trim()
        .replace(Regex("""[^\w.-]+"""), "_")
        .trim('_')
        .ifBlank { "preset" }

}

private val PresetsKey = stringPreferencesKey("PRESETS_KEY")