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

package com.t8rin.imagetoolbox.feature.assistant.domain

import kotlinx.serialization.Serializable

@Serializable
data class AssistantScreenContext(
    val toolId: Int? = null,
    val settingKey: String? = null,
    val selectedUris: List<String> = emptyList(),
    val parameters: Map<String, String> = emptyMap()
)

@Serializable
data class AssistantRequest(
    val query: String,
    val context: AssistantScreenContext = AssistantScreenContext(),
    val language: String? = null
)

@Serializable
data class AssistantReference(
    val title: String,
    val text: String,
    val tipId: String,
    val categoryKey: String
)

@Serializable
data class AssistantTool(
    val key: String,
    val title: String,
    val subtitle: String,
    val helpTipId: String? = null,
    val filterKey: String? = null
)

@Serializable
data class AssistantSetting(
    val key: String,
    val title: String,
    val subtitle: String
)

@Serializable
data class AssistantAnswer(
    val text: String,
    val references: List<AssistantReference> = emptyList(),
    val tools: List<AssistantTool> = emptyList(),
    val settings: List<AssistantSetting> = emptyList()
)
