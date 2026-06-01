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

package com.t8rin.imagetoolbox.feature.filters.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.filters.domain.ShaderPresetRepository
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderParser
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.ShaderPreset
import com.t8rin.imagetoolbox.core.filters.domain.model.shader.toItShaderJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class AndroidShaderPresetRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    jsonParser: JsonParser
) : ShaderPresetRepository {

    private val parser = ShaderParser(jsonParser)

    override fun getPresets(): Flow<List<ShaderPreset>> = dataStore.data.map { prefs ->
        val rawPresets = prefs[SHADER_PRESETS].orEmpty()
        val parsedPresets = rawPresets.mapNotNull { json ->
            parsePresetOrNull(json)?.let { preset -> json to preset }
        }

        ParsedShaderPresets(
            rawPresets = rawPresets,
            validRawPresets = parsedPresets.mapTo(mutableSetOf()) { it.first },
            presets = parsedPresets.map { it.second }
        )
    }.onEach { result ->
        if (result.rawPresets.size != result.validRawPresets.size) {
            dataStore.edit { prefs ->
                if (result.validRawPresets.isEmpty()) {
                    prefs.remove(SHADER_PRESETS)
                } else {
                    prefs[SHADER_PRESETS] = result.validRawPresets
                }
            }
        }
    }.map { result ->
        result.presets.distinctBy { it.name }.sortedBy { it.name.lowercase() }
    }.flowOn(Dispatchers.Default)

    override suspend fun savePreset(
        preset: ShaderPreset,
        replacingName: String?
    ): Result<Unit> = runCatching {
        val currentPresets = getPresets().first()
        val nextPresets = currentPresets
            .filterNot { it.name == (replacingName ?: preset.name) || it.name == preset.name }
            .plus(preset)

        dataStore.edit { prefs ->
            prefs[SHADER_PRESETS] = nextPresets.mapTo(mutableSetOf(), ShaderPreset::toItShaderJson)
        }
    }

    override suspend fun importPreset(json: String): Result<ShaderPreset> =
        parser.parse(json).onSuccess { savePreset(it).getOrThrow() }

    override suspend fun deletePreset(preset: ShaderPreset) {
        val nextPresets = getPresets().first().filterNot { it.name == preset.name }
        dataStore.edit { prefs ->
            prefs[SHADER_PRESETS] = nextPresets.mapTo(mutableSetOf(), ShaderPreset::toItShaderJson)
        }
    }

    override fun exportPreset(preset: ShaderPreset): String = preset.toItShaderJson()

    private fun parsePresetOrNull(json: String): ShaderPreset? =
        json.takeIf(String::isLikelyShaderPresetJson)
            ?.let { parser.parse(it).getOrNull() }

}

private data class ParsedShaderPresets(
    val rawPresets: Set<String>,
    val validRawPresets: Set<String>,
    val presets: List<ShaderPreset>
)

private fun String.isLikelyShaderPresetJson(): Boolean {
    val json = trim()
    return json.startsWith("{") &&
            json.endsWith("}") &&
            REQUIRED_SHADER_PRESET_FIELDS.all { field -> json.contains("\"$field\"") }
}

private val SHADER_PRESETS = stringSetPreferencesKey("SHADER_PRESETS")
private val REQUIRED_SHADER_PRESET_FIELDS = listOf("version", "name", "shader")
