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

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import dev.hossain.highlight.engine.HighlightTheme
import dev.hossain.highlight.engine.HljsSelectors

enum class CodePreviewTheme(val title: String) {
    Dracula("Dracula"),
    OneDark("One Dark"),
    GitHubDark("GitHub Dark"),
    TomorrowNight("Tomorrow Night"),
    AlucardDark("Alucard Dark"),
    CatppuccinMocha("Catppuccin Mocha"),
    TokyoNight("Tokyo Night"),
    Nord("Nord"),
    Monokai("Monokai"),
    GruvboxDark("Gruvbox Dark"),
    SolarizedDark("Solarized Dark"),
    Alucard("Alucard"),
    DraculaLight("Dracula Light"),
    OneLight("One Light"),
    GitHubLight("GitHub Light"),
    Tomorrow("Tomorrow"),
    CatppuccinLatte("Catppuccin Latte"),
    SolarizedLight("Solarized Light");

    private val resolvedTheme: HighlightTheme by lazy {
        when (this) {
            Dracula -> HighlightTheme.draculaDark()
            OneDark -> HighlightTheme.atomOneDark()
            GitHubDark -> HighlightTheme.githubDark()
            TomorrowNight -> HighlightTheme.tomorrowNight()
            AlucardDark -> HighlightTheme.alucardDark()
            CatppuccinMocha -> customTheme(
                name = "catppuccin-mocha",
                palette = HighlightPalette(
                    background = Color(0xFF1E1E2E),
                    foreground = Color(0xFFCDD6F4),
                    keyword = Color(0xFFCBA6F7),
                    builtIn = Color(0xFF89DCEB),
                    string = Color(0xFFA6E3A1),
                    number = Color(0xFFFAB387),
                    comment = Color(0xFF6C7086),
                    title = Color(0xFF89B4FA),
                    attribute = Color(0xFFF9E2AF),
                    meta = Color(0xFFF38BA8)
                )
            )

            TokyoNight -> customTheme(
                name = "tokyo-night",
                palette = HighlightPalette(
                    background = Color(0xFF1A1B26),
                    foreground = Color(0xFFC0CAF5),
                    keyword = Color(0xFFBB9AF7),
                    builtIn = Color(0xFF7DCFFF),
                    string = Color(0xFF9ECE6A),
                    number = Color(0xFFFF9E64),
                    comment = Color(0xFF565F89),
                    title = Color(0xFF7AA2F7),
                    attribute = Color(0xFFE0AF68),
                    meta = Color(0xFFF7768E)
                )
            )

            Nord -> customTheme(
                name = "nord",
                palette = HighlightPalette(
                    background = Color(0xFF2E3440),
                    foreground = Color(0xFFD8DEE9),
                    keyword = Color(0xFF81A1C1),
                    builtIn = Color(0xFF8FBCBB),
                    string = Color(0xFFA3BE8C),
                    number = Color(0xFFB48EAD),
                    comment = Color(0xFF616E88),
                    title = Color(0xFF88C0D0),
                    attribute = Color(0xFFEBCB8B),
                    meta = Color(0xFFD08770)
                )
            )

            Monokai -> customTheme(
                name = "monokai",
                palette = HighlightPalette(
                    background = Color(0xFF272822),
                    foreground = Color(0xFFF8F8F2),
                    keyword = Color(0xFFF92672),
                    builtIn = Color(0xFF66D9EF),
                    string = Color(0xFFE6DB74),
                    number = Color(0xFFAE81FF),
                    comment = Color(0xFF75715E),
                    title = Color(0xFFA6E22E),
                    attribute = Color(0xFFFD971F),
                    meta = Color(0xFFF92672)
                )
            )

            GruvboxDark -> customTheme(
                name = "gruvbox-dark",
                palette = HighlightPalette(
                    background = Color(0xFF282828),
                    foreground = Color(0xFFEBDBB2),
                    keyword = Color(0xFFFB4934),
                    builtIn = Color(0xFFFABD2F),
                    string = Color(0xFFB8BB26),
                    number = Color(0xFFD3869B),
                    comment = Color(0xFF928374),
                    title = Color(0xFF83A598),
                    attribute = Color(0xFFFE8019),
                    meta = Color(0xFF8EC07C)
                )
            )

            SolarizedDark -> customTheme(
                name = "solarized-dark",
                palette = HighlightPalette(
                    background = Color(0xFF002B36),
                    foreground = Color(0xFF839496),
                    keyword = Color(0xFF859900),
                    builtIn = Color(0xFFB58900),
                    string = Color(0xFF2AA198),
                    number = Color(0xFFD33682),
                    comment = Color(0xFF586E75),
                    title = Color(0xFF268BD2),
                    attribute = Color(0xFFCB4B16),
                    meta = Color(0xFF6C71C4)
                )
            )

            Alucard -> HighlightTheme.alucardLight()
            DraculaLight -> HighlightTheme.draculaLight()
            OneLight -> HighlightTheme.atomOneLight()
            GitHubLight -> HighlightTheme.githubLight()
            Tomorrow -> HighlightTheme.tomorrow()
            CatppuccinLatte -> customTheme(
                name = "catppuccin-latte",
                palette = HighlightPalette(
                    background = Color(0xFFEFF1F5),
                    foreground = Color(0xFF4C4F69),
                    keyword = Color(0xFF8839EF),
                    builtIn = Color(0xFF04A5E5),
                    string = Color(0xFF40A02B),
                    number = Color(0xFFFE640B),
                    comment = Color(0xFF9CA0B0),
                    title = Color(0xFF1E66F5),
                    attribute = Color(0xFFDF8E1D),
                    meta = Color(0xFFD20F39)
                )
            )

            SolarizedLight -> customTheme(
                name = "solarized-light",
                palette = HighlightPalette(
                    background = Color(0xFFFDF6E3),
                    foreground = Color(0xFF657B83),
                    keyword = Color(0xFF859900),
                    builtIn = Color(0xFFB58900),
                    string = Color(0xFF2AA198),
                    number = Color(0xFFD33682),
                    comment = Color(0xFF93A1A1),
                    title = Color(0xFF268BD2),
                    attribute = Color(0xFFCB4B16),
                    meta = Color(0xFF6C71C4)
                )
            )
        }
    }

    fun highlightTheme(): HighlightTheme = resolvedTheme
}

private data class HighlightPalette(
    val background: Color,
    val foreground: Color,
    val keyword: Color,
    val builtIn: Color,
    val string: Color,
    val number: Color,
    val comment: Color,
    val title: Color,
    val attribute: Color,
    val meta: Color
)

private fun customTheme(
    name: String,
    palette: HighlightPalette
): HighlightTheme = HighlightTheme.fromColorMap(
    name = name,
    colorMap = buildMap {
        putColor(
            palette.keyword,
            HljsSelectors.KEYWORD,
            HljsSelectors.LITERAL,
            HljsSelectors.SYMBOL,
            HljsSelectors.BULLET,
            HljsSelectors.TEMPLATE_TAG
        )
        putColor(
            palette.builtIn,
            HljsSelectors.BUILT_IN,
            HljsSelectors.TYPE,
            HljsSelectors.TAG,
            HljsSelectors.NAME,
            HljsSelectors.SELECTOR_TAG
        )
        putColor(
            palette.string,
            HljsSelectors.STRING,
            HljsSelectors.CHAR,
            HljsSelectors.CHAR_ESCAPE,
            HljsSelectors.REGEXP,
            HljsSelectors.META_STRING,
            HljsSelectors.ADDITION
        )
        putColor(
            palette.number,
            HljsSelectors.NUMBER,
            HljsSelectors.VARIABLE_CONSTANT,
            HljsSelectors.DELETION
        )
        putColor(
            palette.comment,
            HljsSelectors.COMMENT,
            HljsSelectors.QUOTE,
            HljsSelectors.DOCTAG
        )
        putColor(
            palette.title,
            HljsSelectors.TITLE,
            HljsSelectors.TITLE_CLASS,
            HljsSelectors.TITLE_CLASS_INHERITED,
            HljsSelectors.TITLE_FUNCTION,
            HljsSelectors.TITLE_FUNCTION_INVOKE,
            HljsSelectors.SECTION,
            HljsSelectors.SELECTOR_ID,
            HljsSelectors.SELECTOR_CLASS
        )
        putColor(
            palette.attribute,
            HljsSelectors.ATTR,
            HljsSelectors.ATTRIBUTE,
            HljsSelectors.PROPERTY,
            HljsSelectors.VARIABLE,
            HljsSelectors.VARIABLE_LANGUAGE,
            HljsSelectors.PARAMS,
            HljsSelectors.TEMPLATE_VARIABLE
        )
        putColor(
            palette.meta,
            HljsSelectors.META,
            HljsSelectors.META_KEYWORD,
            HljsSelectors.META_PROMPT,
            HljsSelectors.ATRULE,
            HljsSelectors.LINK
        )
    },
    backgroundColor = palette.background,
    defaultTextColor = palette.foreground
)

private fun MutableMap<String, SpanStyle>.putColor(
    color: Color,
    vararg selectors: String
) {
    selectors.forEach { selector ->
        put(selector, SpanStyle(color = color))
    }
}
