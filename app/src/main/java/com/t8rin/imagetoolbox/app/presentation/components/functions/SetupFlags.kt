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

package com.t8rin.imagetoolbox.app.presentation.components.functions

import androidx.compose.foundation.ComposeFoundationFlags
import androidx.compose.material3.ComposeMaterial3Flags
import androidx.compose.runtime.ComposeRuntimeFlags
import androidx.compose.runtime.Composer
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.tooling.ComposeStackTraceMode
import com.arkivanov.decompose.DecomposeSettings

@OptIn(ExperimentalComposeApi::class)
internal fun setupFlags() {
    ComposeRuntimeFlags.isLinkBufferComposerEnabled = true
    ComposeFoundationFlags.isPausableCompositionInPrefetchEnabled = true
    ComposeMaterial3Flags.isCheckboxStylingFixEnabled = true
    Composer.setDiagnosticStackTraceMode(ComposeStackTraceMode.GroupKeys)
    DecomposeSettings.update { it.copy(duplicateConfigurationsEnabled = true) }
}