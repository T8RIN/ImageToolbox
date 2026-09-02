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

import com.t8rin.imagetoolbox.core.utils.appContext
import dev.hossain.highlight.engine.HighlightTheme

@ConsistentCopyVisibility
data class CodePreviewTheme private constructor(
    val assetName: String
) {
    val title: String = assetName.toThemeTitle()

    private val resolvedTheme: HighlightTheme by lazy {
        val css = appContext.assets
            .open("$THEMES_ASSET_PATH/$assetName.min.css")
            .bufferedReader()
            .use { it.readText() }
            .resolveCssValues()

        HighlightTheme.fromCss(
            cssText = css,
            name = assetName
        )
    }

    fun highlightTheme(): HighlightTheme = resolvedTheme

    companion object {
        val Dracula = CodePreviewTheme("base16/dracula")
        val Tomorrow = CodePreviewTheme("base16/tomorrow")
        val TomorrowNight = CodePreviewTheme("base16/tomorrow-night")

        val entries: List<CodePreviewTheme> by lazy {
            val rootThemes = appContext.assets
                .list(THEMES_ASSET_PATH)
                .orEmpty()
                .filter { it.endsWith(THEME_FILE_SUFFIX) }
                .map { it.removeSuffix(THEME_FILE_SUFFIX) }
            val base16Themes = appContext.assets
                .list("$THEMES_ASSET_PATH/base16")
                .orEmpty()
                .filter { it.endsWith(THEME_FILE_SUFFIX) }
                .map { "base16/${it.removeSuffix(THEME_FILE_SUFFIX)}" }

            (rootThemes + base16Themes)
                .distinct()
                .sorted()
                .map(::CodePreviewTheme)
        }
    }
}

private const val THEMES_ASSET_PATH = "code-preview/themes"
private const val THEME_FILE_SUFFIX = ".min.css"

private val cssVariableDeclaration = Regex("""(--[\w-]+)\s*:\s*([^;}]+)""")
private val cssVariableReference = Regex("""var\((--[\w-]+)\)""")
private val cssBackgroundDeclaration = Regex("""background\s*:\s*([^;}]+)""")
private val cssHexColor = Regex("""#[\da-fA-F]{3,8}\b""")

private fun String.resolveCssValues(): String {
    val variables = cssVariableDeclaration.findAll(this).associate { match ->
        match.groupValues[1] to match.groupValues[2].trim()
    }
    val withResolvedVariables = cssVariableReference.replace(this) { match ->
        variables[match.groupValues[1]] ?: match.value
    }
    return cssBackgroundDeclaration.replace(withResolvedVariables) { match ->
        val value = match.groupValues[1]
        val fallbackColor = cssHexColor.findAll(value).lastOrNull()?.value
        fallbackColor?.let { "background:$it" } ?: match.value
    }
}

private fun String.toThemeTitle(): String =
    substringAfter('/').split('-').joinToString(" ") { word ->
        ThemeWordTitles[word] ?: word.replaceFirstChar(Char::uppercase)
    }

private val ThemeWordTitles = mapOf(
    "1c" to "1C",
    "a11y" to "A11y",
    "androidstudio" to "Android Studio",
    "codepen" to "CodePen",
    "github" to "GitHub",
    "gml" to "GML",
    "googlecode" to "Google Code",
    "ia" to "iA",
    "ir" to "IR",
    "isbl" to "ISBL",
    "nnfx" to "NNFX",
    "oceanicnext" to "Oceanic Next",
    "papercolor" to "PaperColor",
    "phd" to "PhD",
    "qtcreator" to "Qt Creator",
    "ros" to "Rosé",
    "rose" to "Rosé",
    "stackoverflow" to "Stack Overflow",
    "ui" to "UI",
    "vs" to "VS",
    "vs2015" to "VS 2015",
    "xcode" to "Xcode",
    "xt256" to "XT 256"
)
