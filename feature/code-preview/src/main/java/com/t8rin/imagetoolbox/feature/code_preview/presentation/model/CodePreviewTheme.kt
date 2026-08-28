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
    OneDark("Atom One Dark"),
    GitHubDark("GitHub Dark"),
    TomorrowNight("Tomorrow Night"),
    AlucardDark("Alucard Dark"),
    CatppuccinMocha("Catppuccin Mocha"),
    TokyoNight("Tokyo Night"),
    Nord("Nord"),
    Monokai("Monokai"),
    MonokaiPro("Monokai Pro"),
    ArcDark("Arc Dark"),
    GruvboxDark("Gruvbox Dark"),
    SolarizedDark("Solarized Dark"),
    MaterialOceanic("Oceanic"),
    MaterialDarker("Darker"),
    MaterialDeepOcean("Deep Ocean"),
    MaterialForest("Forest"),
    MaterialVolcano("Volcano"),
    MaterialSpace("Space"),
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
    OneLight("Atom One Light"),
    GitHubLight("GitHub"),
    Tomorrow("Tomorrow"),
    CatppuccinLatte("Catppuccin Latte"),
    SolarizedLight("Solarized Light"),
    MaterialLighter("Lighter"),
    MaterialSkyBlue("Sky Blue"),
    MaterialSandyBeach("Sandy Beach"),
    LightOwl("Light Owl"),
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
            Dracula -> materialTheme(
                name = "dracula-material",
                background = Color(0xFF282A36),
                foreground = Color(0xFFF8F8F2),
                keyword = Color(0xFFF780BF),
                function = Color(0xFF8AFF80),
                string = Color(0xFFFEFF80),
                number = Color(0xFF9580FF),
                comment = Color(0xFF6272A4),
                attribute = Color(0xFF8AFF80),
                tag = Color(0xFFF780BF)
            )

            OneDark -> materialTheme(
                name = "atom-one-dark-material",
                background = Color(0xFF282C34),
                foreground = Color(0xFFD19A66),
                keyword = Color(0xFFC679DD),
                function = Color(0xFF61AEEF),
                string = Color(0xFF98C379),
                number = Color(0xFFD19A66),
                comment = Color(0xFF59626F),
                attribute = Color(0xFFE5C17C),
                tag = Color(0xFFE06C75)
            )

            GitHubDark -> materialTheme(
                name = "github-dark-material",
                background = Color(0xFF24292E),
                foreground = Color(0xFFD1D5DA),
                keyword = Color(0xFFF97583),
                function = Color(0xFFB392F0),
                string = Color(0xFF79B8FF),
                number = Color(0xFF79B8FF),
                comment = Color(0xFF959DA5),
                attribute = Color(0xFFB392F0),
                tag = Color(0xFF85E89D)
            )
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

            MonokaiPro -> materialTheme(
                name = "monokai-pro-material",
                background = Color(0xFF2D2A2E),
                foreground = Color(0xFFFCFCFA),
                keyword = Color(0xFFFF6188),
                function = Color(0xFFA9DC76),
                string = Color(0xFFFFD866),
                number = Color(0xFFAB9DF2),
                comment = Color(0xFF727072),
                attribute = Color(0xFF78DCE8),
                tag = Color(0xFFFF6188)
            )

            ArcDark -> materialTheme(
                name = "arc-dark-material",
                background = Color(0xFF2F343F),
                foreground = Color(0xFFCF6A4C),
                keyword = Color(0xFF9B859D),
                function = Color(0xFF7587A6),
                string = Color(0xFF8F9D6A),
                number = Color(0xFFCDA869),
                comment = Color(0xFF747C84),
                attribute = Color(0xFFF9EE98),
                tag = Color(0xFFCF6A4C)
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

            SolarizedDark -> materialTheme(
                name = "solarized-dark-material",
                background = Color(0xFF002B36),
                foreground = Color(0xFF268BD2),
                keyword = Color(0xFF859900),
                function = Color(0xFFB58900),
                string = Color(0xFF2AA198),
                number = Color(0xFFD33682),
                comment = Color(0xFF657B83),
                attribute = Color(0xFFB58900),
                tag = Color(0xFF268BD2)
            )

            MaterialOceanic -> materialTheme(
                name = "material-oceanic",
                background = Color(0xFF263238),
                foreground = Color(0xFFEEFFFF),
                keyword = Color(0xFFC792EA),
                function = Color(0xFF82AAFF),
                string = Color(0xFFC3E88D),
                number = Color(0xFFF78C6C),
                comment = Color(0xFF546E7A),
                attribute = Color(0xFFFFCB6B),
                tag = Color(0xFFF07178)
            )

            MaterialDarker -> materialTheme(
                name = "material-darker",
                background = Color(0xFF212121),
                foreground = Color(0xFFEEFFFF),
                keyword = Color(0xFFC792EA),
                function = Color(0xFF82AAFF),
                string = Color(0xFFC3E88D),
                number = Color(0xFFF78C6C),
                comment = Color(0xFF616161),
                attribute = Color(0xFFFFCB6B),
                tag = Color(0xFFF07178)
            )

            MaterialDeepOcean -> materialTheme(
                name = "material-deep-ocean",
                background = Color(0xFF0F111A),
                foreground = Color(0xFFEEFFFF),
                keyword = Color(0xFFC792EA),
                function = Color(0xFF82AAFF),
                string = Color(0xFFC3E88D),
                number = Color(0xFFF78C6C),
                comment = Color(0xFF717CB4),
                attribute = Color(0xFFFFCB6B),
                tag = Color(0xFFF07178)
            )

            MaterialForest -> materialTheme(
                name = "material-forest",
                background = Color(0xFF002626),
                foreground = Color(0xFFEEFFFF),
                keyword = Color(0xFFC792EA),
                function = Color(0xFF82AAFF),
                string = Color(0xFFC3E88D),
                number = Color(0xFFF78C6C),
                comment = Color(0xFF005454),
                attribute = Color(0xFFFFCB6B),
                tag = Color(0xFFF07178)
            )

            MaterialVolcano -> materialTheme(
                name = "material-volcano",
                background = Color(0xFF390000),
                foreground = Color(0xFFEEFFFF),
                keyword = Color(0xFFC792EA),
                function = Color(0xFF82AAFF),
                string = Color(0xFFC3E88D),
                number = Color(0xFFF78C6C),
                comment = Color(0xFF7F6451),
                attribute = Color(0xFFFFCB6B),
                tag = Color(0xFFF07178)
            )

            MaterialSpace -> materialTheme(
                name = "material-space",
                background = Color(0xFF1B2240),
                foreground = Color(0xFFEEFFFF),
                keyword = Color(0xFFC792EA),
                function = Color(0xFF82AAFF),
                string = Color(0xFFC3E88D),
                number = Color(0xFFF78C6C),
                comment = Color(0xFF959DAA),
                attribute = Color(0xFFFFCB6B),
                tag = Color(0xFFF07178)
            )

            NightOwl -> materialTheme(
                name = "night-owl-material",
                background = Color(0xFF011627),
                foreground = Color(0xFFADDB67),
                keyword = Color(0xFFC792EA),
                function = Color(0xFF82AAFF),
                string = Color(0xFFECC48D),
                number = Color(0xFFF78C6C),
                comment = Color(0xFF637777),
                attribute = Color(0xFFADDB67),
                tag = Color(0xFF7FDBCA)
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

            Moonlight -> materialTheme(
                name = "moonlight-material",
                background = Color(0xFF222436),
                foreground = Color(0xFFC8D3F5),
                keyword = Color(0xFFBAACFF),
                function = Color(0xFF70B0FF),
                string = Color(0xFF7AF8CA),
                number = Color(0xFFFF9668),
                comment = Color(0xFF7E8EDA),
                attribute = Color(0xFFFFBD76),
                tag = Color(0xFFFF757F)
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

            Palenight -> materialTheme(
                name = "material-palenight",
                background = Color(0xFF292D3E),
                foreground = Color(0xFFEEFFFF),
                keyword = Color(0xFFC792EA),
                function = Color(0xFF82AAFF),
                string = Color(0xFFC3E88D),
                number = Color(0xFFF78C6C),
                comment = Color(0xFF676E95),
                attribute = Color(0xFFFFCB6B),
                tag = Color(0xFFF07178)
            )

            Synthwave84 -> materialTheme(
                name = "synthwave-84-material",
                background = Color(0xFF2A2139),
                foreground = Color(0xFFB6B1B1),
                keyword = Color(0xFFFEDE5D),
                function = Color(0xFF36F9F6),
                string = Color(0xFFFF8B39),
                number = Color(0xFFF97E72),
                comment = Color(0xFF848BBD),
                attribute = Color(0xFFFEDE5D),
                tag = Color(0xFF72F1B8)
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
            OneLight -> materialTheme(
                name = "atom-one-light-material",
                background = Color(0xFFF4F4F4),
                foreground = Color(0xFF986801),
                keyword = Color(0xFFA626A4),
                function = Color(0xFF4078F2),
                string = Color(0xFF50A14E),
                number = Color(0xFF986801),
                comment = Color(0xFFA0A1A7),
                attribute = Color(0xFFC18401),
                tag = Color(0xFFE4564A)
            )

            GitHubLight -> materialTheme(
                name = "github-light-material",
                background = Color(0xFFF7F8FA),
                foreground = Color(0xFF24292E),
                keyword = Color(0xFFD73A49),
                function = Color(0xFF6F42C1),
                string = Color(0xFF032F62),
                number = Color(0xFF005CC5),
                comment = Color(0xFF6A737D),
                attribute = Color(0xFF6F42C1),
                tag = Color(0xFF22863A)
            )

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

            SolarizedLight -> materialTheme(
                name = "solarized-light-material",
                background = Color(0xFFFDF6E3),
                foreground = Color(0xFF268BD2),
                keyword = Color(0xFF859900),
                function = Color(0xFFB58900),
                string = Color(0xFF2AA198),
                number = Color(0xFFD33682),
                comment = Color(0xFF93A1A1),
                attribute = Color(0xFF657B83),
                tag = Color(0xFF268BD2)
            )

            MaterialLighter -> materialTheme(
                name = "material-lighter",
                background = Color(0xFFFAFAFA),
                foreground = Color(0xFF272727),
                keyword = Color(0xFF7C4DFF),
                function = Color(0xFF6182B8),
                string = Color(0xFF91B859),
                number = Color(0xFFF76D47),
                comment = Color(0xFFAABFC9),
                attribute = Color(0xFFF6A434),
                tag = Color(0xFFE53935)
            )

            MaterialSkyBlue -> materialTheme(
                name = "material-sky-blue",
                background = Color(0xFFF5F5F5),
                foreground = Color(0xFF272727),
                keyword = Color(0xFF7C4DFF),
                function = Color(0xFF6182B8),
                string = Color(0xFF91B859),
                number = Color(0xFFF76D47),
                comment = Color(0xFF01579B),
                attribute = Color(0xFFF6A434),
                tag = Color(0xFFE53935)
            )

            MaterialSandyBeach -> materialTheme(
                name = "material-sandy-beach",
                background = Color(0xFFFFF8ED),
                foreground = Color(0xFF272727),
                keyword = Color(0xFF7C4DFF),
                function = Color(0xFF6182B8),
                string = Color(0xFF91B859),
                number = Color(0xFFF76D47),
                comment = Color(0xFF888477),
                attribute = Color(0xFFF6A434),
                tag = Color(0xFFE53935)
            )

            LightOwl -> materialTheme(
                name = "light-owl-material",
                background = Color(0xFFF0F0F0),
                foreground = Color(0xFF4876D6),
                keyword = Color(0xFF994CC3),
                function = Color(0xFF4876D6),
                string = Color(0xFFC96765),
                number = Color(0xFFAA0982),
                comment = Color(0xFF989FB1),
                attribute = Color(0xFF0C969B),
                tag = Color(0xFF994CC3)
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

private fun materialTheme(
    name: String,
    background: Color,
    foreground: Color,
    keyword: Color,
    function: Color,
    string: Color,
    number: Color,
    comment: Color,
    attribute: Color,
    tag: Color
): HighlightTheme = customTheme(
    name = name,
    palette = HighlightPalette(
        background = background,
        foreground = foreground,
        keyword = keyword,
        builtIn = function,
        string = string,
        number = number,
        comment = comment,
        title = function,
        attribute = attribute,
        meta = tag
    )
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
