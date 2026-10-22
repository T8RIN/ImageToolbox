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

package com.t8rin.imagetoolbox.feature.settings.data.keys

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import com.t8rin.imagetoolbox.core.domain.json.JsonParser
import com.t8rin.imagetoolbox.core.domain.model.AssistantModel
import com.t8rin.imagetoolbox.core.settings.domain.model.SettingsState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.lang.reflect.Type

class AssistantSettingsTest {

    @Test
    fun missingPreferencesEnableAssistantWithIncompleteSetupAndSelectLite() {
        val settings = emptyPreferences().settings()

        assertTrue(settings.isAiAssistantEnabled)
        assertFalse(settings.isAiAssistantSetupComplete)
        assertEquals(AssistantModel.Lite, settings.aiAssistantModel)
    }

    @Test
    fun storedModelsAndCompletedSetupAreRestored() {
        AssistantModel.entries.forEach { model ->
            val settings = mutablePreferencesOf(
                AI_ASSISTANT_ENABLED to true,
                AI_ASSISTANT_SETUP_COMPLETE to true,
                AI_ASSISTANT_MODEL_TYPE to model.ordinal
            ).settings()

            assertTrue(settings.isAiAssistantEnabled)
            assertTrue(settings.isAiAssistantSetupComplete)
            assertEquals(model, settings.aiAssistantModel)
        }
    }

    @Test
    fun existingModelOrdinalsRemainCompatible() {
        listOf(
            AssistantModel.Lite,
            AssistantModel.Advanced,
            AssistantModel.Max,
            AssistantModel.VisionLite,
            AssistantModel.VisionAdvanced
        )
            .forEachIndexed { ordinal, model ->
                assertEquals(
                    model,
                    mutablePreferencesOf(AI_ASSISTANT_MODEL_TYPE to ordinal).settings().aiAssistantModel
                )
            }
    }

    @Test
    fun disabledAssistantPreservesCompletedSetupAndModelSelection() {
        val preferences = mutablePreferencesOf(
            AI_ASSISTANT_ENABLED to true,
            AI_ASSISTANT_SETUP_COMPLETE to true,
            AI_ASSISTANT_MODEL_TYPE to AssistantModel.Advanced.ordinal
        )
        preferences[AI_ASSISTANT_ENABLED] = false

        val settings = preferences.settings()

        assertFalse(settings.isAiAssistantEnabled)
        assertTrue(settings.isAiAssistantSetupComplete)
        assertEquals(AssistantModel.Advanced, settings.aiAssistantModel)
    }

    @Test
    fun storedIncompleteSetupIsRestored() {
        val settings = mutablePreferencesOf(
            AI_ASSISTANT_SETUP_COMPLETE to false
        ).settings()

        assertTrue(settings.isAiAssistantEnabled)
        assertFalse(settings.isAiAssistantSetupComplete)
    }

    private fun Preferences.settings() = toSettingsState(
        default = SettingsState.Default,
        jsonParser = object : JsonParser {
            override fun <T> toJson(obj: T, type: Type): String? = null

            override fun <T> fromJson(json: String, type: Type): T? = null
        }
    )
}
