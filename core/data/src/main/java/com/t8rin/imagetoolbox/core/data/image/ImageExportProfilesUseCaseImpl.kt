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
import com.t8rin.imagetoolbox.core.domain.image.ImageExportProfilesUseCase
import com.t8rin.imagetoolbox.core.domain.image.ShareProvider
import com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfile
import com.t8rin.imagetoolbox.core.domain.image.model.ImageExportProfiles
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.domain.saving.FileController
import com.t8rin.imagetoolbox.core.domain.utils.runSuspendCatching
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class ImageExportProfilesUseCaseImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val fileController: FileController,
    private val jsonParser: JsonParser,
    private val shareProvider: ShareProvider
) : ImageExportProfilesUseCase {

    override val profiles: Flow<List<ImageExportProfile>> = dataStore.data.map { preferences ->
        preferences.readPresets().presets.asReversed()
    }

    override suspend fun upsert(profile: ImageExportProfile) {
        val normalized = profile.copy(name = profile.name.trim())
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
            preferences.writePresets(ImageExportProfiles(list))
        }
    }

    override suspend fun delete(profile: ImageExportProfile) {
        dataStore.edit { preferences ->
            val current = preferences.readPresets()

            preferences.writePresets(
                current.copy(
                    presets = current.presets.filterNot {
                        it.name.equals(profile.name, ignoreCase = true)
                    }
                )
            )
        }
    }

    override suspend fun export(
        profile: ImageExportProfile,
        uri: String
    ) {
        profile.toJson()?.let { json ->
            fileController.writeBytes(uri) {
                it.writeBytes(json.encodeToByteArray())
            }
        }
    }

    override suspend fun share(profile: ImageExportProfile) {
        profile.toJson()?.let { json ->
            shareProvider.shareData(
                filename = profile.fileName(),
                writeData = {
                    it.writeBytes(json.encodeToByteArray())
                }
            )
        }
    }

    override suspend fun importProfile(uri: String) {
        runSuspendCatching {
            fileController.readBytes(uri).decodeToString()
        }.mapCatching { json ->
            jsonParser.fromJson<ImageExportProfile>(
                json = json,
                type = ImageExportProfile::class.java
            )
        }.getOrNull()?.let { preset ->
            upsert(preset)
        }
    }

    private fun Preferences.readPresets(): ImageExportProfiles {
        val json = this[PresetsKey]

        return json?.let {
            jsonParser.fromJson<ImageExportProfiles>(
                json = it,
                type = ImageExportProfiles::class.java
            )
        } ?: ImageExportProfiles()
    }

    private fun MutablePreferences.writePresets(
        presets: ImageExportProfiles
    ) {
        if (presets.presets.isEmpty()) {
            remove(PresetsKey)
            return
        }

        jsonParser.toJson(
            obj = presets,
            type = ImageExportProfiles::class.java
        )?.let { json ->
            this[PresetsKey] = json
        }
    }

    private fun ImageExportProfile.toJson(): String? = jsonParser.toJson(
        obj = this,
        type = ImageExportProfile::class.java
    )

    private fun ImageExportProfile.fileName(): String = "${name.safePresetFileName()}.itpreset"

    private fun String.safePresetFileName(): String = trim()
        .replace(Regex("""[^\w.-]+"""), "_")
        .trim('_')
        .ifBlank { "preset" }

}

private val PresetsKey = stringPreferencesKey("PRESETS_KEY")
