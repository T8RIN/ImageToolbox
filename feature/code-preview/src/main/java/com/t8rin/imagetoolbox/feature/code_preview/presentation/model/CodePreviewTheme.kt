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
    MaterialOcean("Material Ocean"),
    NightOwl("Night Owl"),
    AyuMirage("Ayu Mirage"),
    Cobalt("Cobalt"),
    RosePine("Rosé Pine"),
    EverforestDark("Everforest Dark"),
    GitHubDimmed("GitHub Dimmed"),
    VitesseDark("Vitesse Dark"),
    KanagawaWave("Kanagawa Wave"),
    Moonlight("Moonlight"),
    Horizon("Horizon"),
    Palenight("Palenight"),
    Synthwave84("SynthWave '84"),
    ShadesOfPurple("Shades of Purple"),
    CatppuccinMacchiato("Catppuccin Macchiato"),
    CatppuccinFrappe("Catppuccin Frappé"),
    Poimandres("Poimandres"),
    Andromeda("Andromeda"),
    AuraDark("Aura Dark"),
    Cyberpunk("Cyberpunk"),
    Alucard("Alucard"),
    DraculaLight("Dracula Light"),
    OneLight("One Light"),
    GitHubLight("GitHub Light"),
    Tomorrow("Tomorrow"),
    CatppuccinLatte("Catppuccin Latte"),
    SolarizedLight("Solarized Light"),
    AyuLight("Ayu Light"),
    GruvboxLight("Gruvbox Light"),
    RosePineDawn("Rosé Pine Dawn"),
    EverforestLight("Everforest Light"),
    KanagawaLotus("Kanagawa Lotus"),
    TokyoNightDay("Tokyo Night Day"),
    VitesseLight("Vitesse Light"),
    NordLight("Nord Light");

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

            MaterialOcean -> customTheme(
                name = "material-ocean",
                palette = HighlightPalette(
                    background = Color(0xFF0F111A),
                    foreground = Color(0xFF8F93A2),
                    keyword = Color(0xFFC792EA),
                    builtIn = Color(0xFF89DDFF),
                    string = Color(0xFFC3E88D),
                    number = Color(0xFFF78C6C),
                    comment = Color(0xFF546E7A),
                    title = Color(0xFF82AAFF),
                    attribute = Color(0xFFFFCB6B),
                    meta = Color(0xFFFF5370)
                )
            )

            NightOwl -> customTheme(
                name = "night-owl",
                palette = HighlightPalette(
                    background = Color(0xFF011627),
                    foreground = Color(0xFFD6DEEB),
                    keyword = Color(0xFFC792EA),
                    builtIn = Color(0xFF82AAFF),
                    string = Color(0xFFECC48D),
                    number = Color(0xFFF78C6C),
                    comment = Color(0xFF637777),
                    title = Color(0xFF82AAFF),
                    attribute = Color(0xFFADDB67),
                    meta = Color(0xFFEF5350)
                )
            )

            AyuMirage -> customTheme(
                name = "ayu-mirage",
                palette = HighlightPalette(
                    background = Color(0xFF1F2430),
                    foreground = Color(0xFFCCCAC2),
                    keyword = Color(0xFFFFAD66),
                    builtIn = Color(0xFF73D0FF),
                    string = Color(0xFFD5FF80),
                    number = Color(0xFFD4BFFF),
                    comment = Color(0xFF707A8C),
                    title = Color(0xFF73D0FF),
                    attribute = Color(0xFFFFC44C),
                    meta = Color(0xFFF28779)
                )
            )

            Cobalt -> customTheme(
                name = "cobalt",
                palette = HighlightPalette(
                    background = Color(0xFF002240),
                    foreground = Color(0xFFFFFFFF),
                    keyword = Color(0xFFFF9D00),
                    builtIn = Color(0xFFFF628C),
                    string = Color(0xFF3AD900),
                    number = Color(0xFFFF628C),
                    comment = Color(0xFF0088FF),
                    title = Color(0xFF80FCFF),
                    attribute = Color(0xFFFFE898),
                    meta = Color(0xFFFF9D00)
                )
            )

            RosePine -> customTheme(
                name = "rose-pine",
                palette = HighlightPalette(
                    background = Color(0xFF191724),
                    foreground = Color(0xFFE0DEF4),
                    keyword = Color(0xFFC4A7E7),
                    builtIn = Color(0xFF9CCFD8),
                    string = Color(0xFFF6C177),
                    number = Color(0xFFEB6F92),
                    comment = Color(0xFF6E6A86),
                    title = Color(0xFF31748F),
                    attribute = Color(0xFFEBCB8B),
                    meta = Color(0xFFEBBCBA)
                )
            )

            EverforestDark -> customTheme(
                name = "everforest-dark",
                palette = HighlightPalette(
                    background = Color(0xFF2D353B),
                    foreground = Color(0xFFD3C6AA),
                    keyword = Color(0xFFE67E80),
                    builtIn = Color(0xFF7FBBB3),
                    string = Color(0xFFA7C080),
                    number = Color(0xFFD699B6),
                    comment = Color(0xFF859289),
                    title = Color(0xFF83C092),
                    attribute = Color(0xFFDBBC7F),
                    meta = Color(0xFFE69875)
                )
            )

            GitHubDimmed -> customTheme(
                name = "github-dimmed",
                palette = HighlightPalette(
                    background = Color(0xFF22272E),
                    foreground = Color(0xFFADBAC7),
                    keyword = Color(0xFFF47067),
                    builtIn = Color(0xFF6CB6FF),
                    string = Color(0xFF96D0FF),
                    number = Color(0xFF6CB6FF),
                    comment = Color(0xFF768390),
                    title = Color(0xFFDCBDFB),
                    attribute = Color(0xFFF69D50),
                    meta = Color(0xFF8DDB8C)
                )
            )

            VitesseDark -> customTheme(
                name = "vitesse-dark",
                palette = HighlightPalette(
                    background = Color(0xFF121212),
                    foreground = Color(0xFFDBD7CA),
                    keyword = Color(0xFF4D9375),
                    builtIn = Color(0xFF5DA994),
                    string = Color(0xFFC98A7D),
                    number = Color(0xFFB8A965),
                    comment = Color(0xFF758575),
                    title = Color(0xFF80A665),
                    attribute = Color(0xFFD9739F),
                    meta = Color(0xFFBD976A)
                )
            )

            KanagawaWave -> customTheme(
                name = "kanagawa-wave",
                palette = HighlightPalette(
                    background = Color(0xFF1F1F28),
                    foreground = Color(0xFFDCD7BA),
                    keyword = Color(0xFF957FB8),
                    builtIn = Color(0xFF7E9CD8),
                    string = Color(0xFF98BB6C),
                    number = Color(0xFFD27E99),
                    comment = Color(0xFF727169),
                    title = Color(0xFF7FB4CA),
                    attribute = Color(0xFFE6C384),
                    meta = Color(0xFFFF5D62)
                )
            )

            Moonlight -> customTheme(
                name = "moonlight",
                palette = HighlightPalette(
                    background = Color(0xFF222436),
                    foreground = Color(0xFFC8D3F5),
                    keyword = Color(0xFFC099FF),
                    builtIn = Color(0xFF82AAFF),
                    string = Color(0xFFC3E88D),
                    number = Color(0xFFFFA7C4),
                    comment = Color(0xFF636DA6),
                    title = Color(0xFF65BCFF),
                    attribute = Color(0xFFFFC777),
                    meta = Color(0xFFFF757F)
                )
            )

            Horizon -> customTheme(
                name = "horizon",
                palette = HighlightPalette(
                    background = Color(0xFF1C1E26),
                    foreground = Color(0xFFD5D8DA),
                    keyword = Color(0xFFB877DB),
                    builtIn = Color(0xFF25B0BC),
                    string = Color(0xFFE95678),
                    number = Color(0xFFFAB795),
                    comment = Color(0xFF6C6F93),
                    title = Color(0xFF59E1E3),
                    attribute = Color(0xFFFAC29A),
                    meta = Color(0xFFF43E5C)
                )
            )

            Palenight -> customTheme(
                name = "palenight",
                palette = HighlightPalette(
                    background = Color(0xFF292D3E),
                    foreground = Color(0xFFA6ACCD),
                    keyword = Color(0xFFC792EA),
                    builtIn = Color(0xFF82AAFF),
                    string = Color(0xFFC3E88D),
                    number = Color(0xFFF78C6C),
                    comment = Color(0xFF676E95),
                    title = Color(0xFF89DDFF),
                    attribute = Color(0xFFFFCB6B),
                    meta = Color(0xFFF07178)
                )
            )

            Synthwave84 -> customTheme(
                name = "synthwave-84",
                palette = HighlightPalette(
                    background = Color(0xFF262335),
                    foreground = Color(0xFFFFFFFF),
                    keyword = Color(0xFFF92AAD),
                    builtIn = Color(0xFF36F9F6),
                    string = Color(0xFFFFE261),
                    number = Color(0xFFFF8B39),
                    comment = Color(0xFF848BBD),
                    title = Color(0xFF72F1B8),
                    attribute = Color(0xFFFFDE5D),
                    meta = Color(0xFFFE4450)
                )
            )

            ShadesOfPurple -> customTheme(
                name = "shades-of-purple",
                palette = HighlightPalette(
                    background = Color(0xFF2D2B55),
                    foreground = Color(0xFFFFFFFF),
                    keyword = Color(0xFFFAD000),
                    builtIn = Color(0xFF80FFEA),
                    string = Color(0xFFA5FF90),
                    number = Color(0xFFFF628C),
                    comment = Color(0xFFB362FF),
                    title = Color(0xFF9EFFFF),
                    attribute = Color(0xFFFFEE80),
                    meta = Color(0xFFFF9D00)
                )
            )

            CatppuccinMacchiato -> customTheme(
                name = "catppuccin-macchiato",
                palette = HighlightPalette(
                    background = Color(0xFF24273A),
                    foreground = Color(0xFFCAD3F5),
                    keyword = Color(0xFFC6A0F6),
                    builtIn = Color(0xFF91D7E3),
                    string = Color(0xFFA6DA95),
                    number = Color(0xFFF5A97F),
                    comment = Color(0xFF6E738D),
                    title = Color(0xFF8AADF4),
                    attribute = Color(0xFFEED49F),
                    meta = Color(0xFFED8796)
                )
            )

            CatppuccinFrappe -> customTheme(
                name = "catppuccin-frappe",
                palette = HighlightPalette(
                    background = Color(0xFF303446),
                    foreground = Color(0xFFC6D0F5),
                    keyword = Color(0xFFCA9EE6),
                    builtIn = Color(0xFF85C1DC),
                    string = Color(0xFFA6D189),
                    number = Color(0xFFEF9F76),
                    comment = Color(0xFF737994),
                    title = Color(0xFF8CAAEE),
                    attribute = Color(0xFFE5C890),
                    meta = Color(0xFFE78284)
                )
            )

            Poimandres -> customTheme(
                name = "poimandres",
                palette = HighlightPalette(
                    background = Color(0xFF1B1E28),
                    foreground = Color(0xFFA6ACCD),
                    keyword = Color(0xFF5DE4C7),
                    builtIn = Color(0xFF89DDFF),
                    string = Color(0xFF5DE4C7),
                    number = Color(0xFFADD7FF),
                    comment = Color(0xFF767C9D),
                    title = Color(0xFF91B4D5),
                    attribute = Color(0xFFFFC777),
                    meta = Color(0xFFD0679D)
                )
            )

            Andromeda -> customTheme(
                name = "andromeda",
                palette = HighlightPalette(
                    background = Color(0xFF23262E),
                    foreground = Color(0xFFD5CED9),
                    keyword = Color(0xFFC74DED),
                    builtIn = Color(0xFF00E8C6),
                    string = Color(0xFF96E072),
                    number = Color(0xFFF39C12),
                    comment = Color(0xFF6D7A90),
                    title = Color(0xFFFFC76D),
                    attribute = Color(0xFFF92672),
                    meta = Color(0xFFEE5D43)
                )
            )

            AuraDark -> customTheme(
                name = "aura-dark",
                palette = HighlightPalette(
                    background = Color(0xFF15141B),
                    foreground = Color(0xFFEDECEE),
                    keyword = Color(0xFFA277FF),
                    builtIn = Color(0xFF61FFCA),
                    string = Color(0xFF61FFCA),
                    number = Color(0xFFFFCA85),
                    comment = Color(0xFF6D6D6D),
                    title = Color(0xFF82E2FF),
                    attribute = Color(0xFFF694FF),
                    meta = Color(0xFFFF6767)
                )
            )

            Cyberpunk -> customTheme(
                name = "cyberpunk",
                palette = HighlightPalette(
                    background = Color(0xFF000B1E),
                    foreground = Color(0xFFD7F9FF),
                    keyword = Color(0xFFFF2A6D),
                    builtIn = Color(0xFF05D9E8),
                    string = Color(0xFF00FF9C),
                    number = Color(0xFFFFF951),
                    comment = Color(0xFF4B6382),
                    title = Color(0xFF01CDFF),
                    attribute = Color(0xFFD300C5),
                    meta = Color(0xFFFF5C8A)
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

            AyuLight -> customTheme(
                name = "ayu-light",
                palette = HighlightPalette(
                    background = Color(0xFFFAFAFA),
                    foreground = Color(0xFF5C6166),
                    keyword = Color(0xFFFA8D3E),
                    builtIn = Color(0xFF55B4D4),
                    string = Color(0xFF86B300),
                    number = Color(0xFFA37ACC),
                    comment = Color(0xFFABB0B6),
                    title = Color(0xFF399EE6),
                    attribute = Color(0xFFF2AE49),
                    meta = Color(0xFFE65050)
                )
            )

            GruvboxLight -> customTheme(
                name = "gruvbox-light",
                palette = HighlightPalette(
                    background = Color(0xFFFBF1C7),
                    foreground = Color(0xFF3C3836),
                    keyword = Color(0xFF9D0006),
                    builtIn = Color(0xFFB57614),
                    string = Color(0xFF79740E),
                    number = Color(0xFF8F3F71),
                    comment = Color(0xFF928374),
                    title = Color(0xFF076678),
                    attribute = Color(0xFFAF3A03),
                    meta = Color(0xFF427B58)
                )
            )

            RosePineDawn -> customTheme(
                name = "rose-pine-dawn",
                palette = HighlightPalette(
                    background = Color(0xFFFAF4ED),
                    foreground = Color(0xFF575279),
                    keyword = Color(0xFF907AA9),
                    builtIn = Color(0xFF56949F),
                    string = Color(0xFFEA9D34),
                    number = Color(0xFFB4637A),
                    comment = Color(0xFF9893A5),
                    title = Color(0xFF286983),
                    attribute = Color(0xFFD7827E),
                    meta = Color(0xFFD7827E)
                )
            )

            EverforestLight -> customTheme(
                name = "everforest-light",
                palette = HighlightPalette(
                    background = Color(0xFFFDF6E3),
                    foreground = Color(0xFF5C6A72),
                    keyword = Color(0xFFF85552),
                    builtIn = Color(0xFF35A77C),
                    string = Color(0xFF8DA101),
                    number = Color(0xFFDF69BA),
                    comment = Color(0xFFA6B0A0),
                    title = Color(0xFF3A94C5),
                    attribute = Color(0xFFDFA000),
                    meta = Color(0xFFF57D26)
                )
            )

            KanagawaLotus -> customTheme(
                name = "kanagawa-lotus",
                palette = HighlightPalette(
                    background = Color(0xFFF2ECBC),
                    foreground = Color(0xFF545464),
                    keyword = Color(0xFF624C83),
                    builtIn = Color(0xFF4D699B),
                    string = Color(0xFF6F894E),
                    number = Color(0xFFA292A3),
                    comment = Color(0xFF8A8980),
                    title = Color(0xFF597B75),
                    attribute = Color(0xFF9F5F80),
                    meta = Color(0xFFC84053)
                )
            )

            TokyoNightDay -> customTheme(
                name = "tokyo-night-day",
                palette = HighlightPalette(
                    background = Color(0xFFD5D6DB),
                    foreground = Color(0xFF3760BF),
                    keyword = Color(0xFF9854F1),
                    builtIn = Color(0xFF007197),
                    string = Color(0xFF587539),
                    number = Color(0xFFB15C00),
                    comment = Color(0xFF8990B3),
                    title = Color(0xFF166775),
                    attribute = Color(0xFF8C6C3E),
                    meta = Color(0xFFF52A65)
                )
            )

            VitesseLight -> customTheme(
                name = "vitesse-light",
                palette = HighlightPalette(
                    background = Color(0xFFFFFFFF),
                    foreground = Color(0xFF393A34),
                    keyword = Color(0xFF1E754F),
                    builtIn = Color(0xFF2E8F82),
                    string = Color(0xFFB56959),
                    number = Color(0xFF998418),
                    comment = Color(0xFFA0ADA0),
                    title = Color(0xFF59873A),
                    attribute = Color(0xFFD9739F),
                    meta = Color(0xFFAB5959)
                )
            )

            NordLight -> customTheme(
                name = "nord-light",
                palette = HighlightPalette(
                    background = Color(0xFFECEFF4),
                    foreground = Color(0xFF2E3440),
                    keyword = Color(0xFF5E81AC),
                    builtIn = Color(0xFF8FBCBB),
                    string = Color(0xFFA3BE8C),
                    number = Color(0xFFB48EAD),
                    comment = Color(0xFF7B88A1),
                    title = Color(0xFF5E81AC),
                    attribute = Color(0xFFD08770),
                    meta = Color(0xFFBF616A)
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
