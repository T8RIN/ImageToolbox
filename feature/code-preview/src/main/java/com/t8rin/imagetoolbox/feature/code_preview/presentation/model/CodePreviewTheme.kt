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

package com.t8rin.imagetoolbox.feature.code_preview.presentation.model

import dev.hossain.highlight.engine.HighlightTheme

enum class CodePreviewTheme(val title: String) {
    Dracula("Dracula"),
    OneDark("One Dark"),
    GitHubDark("GitHub Dark"),
    TomorrowNight("Tomorrow Night"),
    Alucard("Alucard"),
    OneLight("One Light"),
    GitHubLight("GitHub Light"),
    Tomorrow("Tomorrow");

    fun highlightTheme(): HighlightTheme = when (this) {
        Dracula -> HighlightTheme.draculaDark()
        OneDark -> HighlightTheme.atomOneDark()
        GitHubDark -> HighlightTheme.githubDark()
        TomorrowNight -> HighlightTheme.tomorrowNight()
        Alucard -> HighlightTheme.alucardLight()
        OneLight -> HighlightTheme.atomOneLight()
        GitHubLight -> HighlightTheme.githubLight()
        Tomorrow -> HighlightTheme.tomorrow()
    }
}
