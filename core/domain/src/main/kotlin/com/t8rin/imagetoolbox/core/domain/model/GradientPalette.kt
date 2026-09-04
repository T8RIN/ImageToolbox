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

package com.t8rin.imagetoolbox.core.domain.model

import kotlin.math.floor
import kotlin.math.roundToInt

enum class GradientPalette(
    private vararg val stops: Int
) {
    Classic(
        0xFF05051A.toInt(),
        0xFF123EAB.toInt(),
        0xFF26BCE1.toInt(),
        0xFFF8E16C.toInt(),
        0xFFF06A24.toInt(),
        0xFF3A0812.toInt()
    ),
    Fire(
        0xFF090004.toInt(),
        0xFF5A0900.toInt(),
        0xFFD33A00.toInt(),
        0xFFFFA51F.toInt(),
        0xFFFFFFB0.toInt()
    ),
    Ocean(
        0xFF020B22.toInt(),
        0xFF063B73.toInt(),
        0xFF087E8B.toInt(),
        0xFF31C5C0.toInt(),
        0xFFD8FFF2.toInt()
    ),
    Viridis(
        0xFF440154.toInt(),
        0xFF3B528B.toInt(),
        0xFF21918C.toInt(),
        0xFF5EC962.toInt(),
        0xFFFDE725.toInt()
    ),
    Magma(
        0xFF000004.toInt(),
        0xFF3B0F70.toInt(),
        0xFF8C2981.toInt(),
        0xFFDE4968.toInt(),
        0xFFFE9F6D.toInt(),
        0xFFFCFDBF.toInt()
    ),
    Inferno(
        0xFF000004.toInt(),
        0xFF420A68.toInt(),
        0xFF932667.toInt(),
        0xFFDD513A.toInt(),
        0xFFFCA50A.toInt(),
        0xFFFCFFA4.toInt()
    ),
    Plasma(
        0xFF0D0887.toInt(),
        0xFF6A00A8.toInt(),
        0xFFB12A90.toInt(),
        0xFFE16462.toInt(),
        0xFFFCA636.toInt(),
        0xFFF0F921.toInt()
    ),
    Turbo(
        0xFF30123B.toInt(),
        0xFF4145AB.toInt(),
        0xFF2A9DF4.toInt(),
        0xFF20D5A5.toInt(),
        0xFF8BEB55.toInt(),
        0xFFF9D423.toInt(),
        0xFFF36B1B.toInt(),
        0xFF7A0403.toInt()
    ),
    Twilight(
        0xFF20134E.toInt(),
        0xFF6D3580.toInt(),
        0xFFC75D75.toInt(),
        0xFFF6B36B.toInt(),
        0xFF8ED1C5.toInt(),
        0xFF315A8A.toInt(),
        0xFF20134E.toInt()
    ),
    Ice(
        0xFF02040F.toInt(),
        0xFF102A56.toInt(),
        0xFF2D78B7.toInt(),
        0xFF8BE4F0.toInt(),
        0xFFF5FFFF.toInt()
    ),
    Forest(
        0xFF07150A.toInt(),
        0xFF174B2B.toInt(),
        0xFF3B7D3A.toInt(),
        0xFF9BBF48.toInt(),
        0xFFF1E7A1.toInt()
    ),
    Neon(
        0xFF050011.toInt(),
        0xFF7400B8.toInt(),
        0xFFFF007A.toInt(),
        0xFFFFC800.toInt(),
        0xFF00F5D4.toInt(),
        0xFF0077FF.toInt()
    ),
    Cividis(
        0xFF00204C.toInt(),
        0xFF2E4A7D.toInt(),
        0xFF666870.toInt(),
        0xFFA08A5B.toInt(),
        0xFFD6AF3C.toInt(),
        0xFFFFE945.toInt()
    ),
    Cubehelix(
        0xFF000000.toInt(),
        0xFF1D2B53.toInt(),
        0xFF5E3C99.toInt(),
        0xFFB35C8C.toInt(),
        0xFFE3A35D.toInt(),
        0xFFFFFFFF.toInt()
    ),
    Spectral(
        0xFF9E0142.toInt(),
        0xFFD53E4F.toInt(),
        0xFFF46D43.toInt(),
        0xFFFEE08B.toInt(),
        0xFFE6F598.toInt(),
        0xFF66C2A5.toInt(),
        0xFF3288BD.toInt(),
        0xFF5E4FA2.toInt()
    ),
    Aurora(
        0xFF07152B.toInt(),
        0xFF44318D.toInt(),
        0xFF0B8F9C.toInt(),
        0xFF35D07F.toInt(),
        0xFFD9F36A.toInt()
    ),
    Sunset(
        0xFF10143D.toInt(),
        0xFF4A236B.toInt(),
        0xFFA33B69.toInt(),
        0xFFF06A4D.toInt(),
        0xFFFFC56E.toInt(),
        0xFFFFF0B3.toInt()
    ),
    Copper(
        0xFF080403.toInt(),
        0xFF3A1C12.toInt(),
        0xFF814425.toInt(),
        0xFFC8783E.toInt(),
        0xFFF0B878.toInt(),
        0xFFFFE0B8.toInt()
    ),
    Rocket(
        0xFF03051A.toInt(),
        0xFF3F1B43.toInt(),
        0xFF841E5A.toInt(),
        0xFFCB1B4F.toInt(),
        0xFFF06043.toInt(),
        0xFFF6B48F.toInt(),
        0xFFFAEBDD.toInt()
    ),
    Mako(
        0xFF0B0405.toInt(),
        0xFF342032.toInt(),
        0xFF3B496C.toInt(),
        0xFF357BA3.toInt(),
        0xFF39A7A5.toInt(),
        0xFF8BDAB2.toInt(),
        0xFFDEF5E5.toInt()
    ),
    Amethyst(
        0xFF10002B.toInt(),
        0xFF240046.toInt(),
        0xFF5A189A.toInt(),
        0xFF9D4EDD.toInt(),
        0xFFE0AAFF.toInt(),
        0xFFFFF0FF.toInt()
    ),
    Vaporwave(
        0xFF17002E.toInt(),
        0xFF5800A3.toInt(),
        0xFFB5179E.toInt(),
        0xFFF72585.toInt(),
        0xFF4CC9F0.toInt(),
        0xFF00F5D4.toInt()
    ),
    Earth(
        0xFF071A12.toInt(),
        0xFF1D4D32.toInt(),
        0xFF607D3B.toInt(),
        0xFFB49A55.toInt(),
        0xFFD9C9A2.toInt(),
        0xFFF2EFE6.toInt()
    ),
    Rainbow(
        rgb(1.0, 0.0, 0.0),
        rgb(1.0, 0.5, 0.0),
        rgb(1.0, 1.0, 0.0),
        rgb(0.0, 1.0, 0.0),
        rgb(0.0, 1.0, 1.0),
        rgb(0.0, 0.0, 1.0),
        rgb(0.5, 0.0, 1.0),
        rgb(1.0, 0.0, 0.5)
    ),
    Cool(
        rgb(0.0, 1.0, 1.0),
        rgb(0.125, 0.875, 1.0),
        rgb(0.25, 0.75, 1.0),
        rgb(0.375, 0.625, 1.0),
        rgb(0.5, 0.5, 1.0),
        rgb(0.625, 0.375, 1.0),
        rgb(0.75, 0.25, 1.0),
        rgb(1.0, 0.0, 1.0)
    ),
    Hot(
        rgb(0.0, 0.0, 0.0),
        rgb(0.25, 0.0, 0.0),
        rgb(0.5, 0.0, 0.0),
        rgb(0.75, 0.25, 0.0),
        rgb(1.0, 0.5, 0.0),
        rgb(1.0, 0.75, 0.25),
        rgb(1.0, 1.0, 0.5),
        rgb(1.0, 1.0, 1.0)
    ),
    PurpleDream(
        rgb(0.05, 0.0, 0.1),
        rgb(0.1, 0.0, 0.2),
        rgb(0.25, 0.0, 0.4),
        rgb(0.4, 0.0, 0.6),
        rgb(0.55, 0.15, 0.75),
        rgb(0.7, 0.3, 0.9),
        rgb(0.85, 0.6, 1.0),
        rgb(1.0, 0.9, 1.0)
    ),
    Lava(
        rgb(0.05, 0.0, 0.0),
        rgb(0.1, 0.0, 0.0),
        rgb(0.3, 0.0, 0.0),
        rgb(0.5, 0.0, 0.0),
        rgb(0.7, 0.1, 0.0),
        rgb(0.85, 0.3, 0.0),
        rgb(1.0, 0.5, 0.0),
        rgb(1.0, 0.8, 0.2)
    ),
    Galaxy(
        rgb(0.025, 0.0, 0.075),
        rgb(0.05, 0.0, 0.15),
        rgb(0.15, 0.0, 0.35),
        rgb(0.3, 0.0, 0.5),
        rgb(0.5, 0.1, 0.7),
        rgb(0.7, 0.25, 0.8),
        rgb(0.85, 0.45, 0.85),
        rgb(1.0, 0.7, 0.9)
    ),
    Mint(
        rgb(0.0, 0.15, 0.15),
        rgb(0.0, 0.3, 0.3),
        rgb(0.1, 0.4, 0.4),
        rgb(0.2, 0.5, 0.5),
        rgb(0.3, 0.6, 0.55),
        rgb(0.5, 0.8, 0.7),
        rgb(0.7, 0.92, 0.85),
        rgb(0.85, 1.0, 0.95)
    ),
    Cherry(
        rgb(0.15, 0.0, 0.05),
        rgb(0.3, 0.0, 0.1),
        rgb(0.45, 0.0, 0.15),
        rgb(0.6, 0.0, 0.2),
        rgb(0.75, 0.1, 0.3),
        rgb(0.9, 0.3, 0.45),
        rgb(1.0, 0.55, 0.65),
        rgb(1.0, 0.8, 0.85)
    ),
    XfAlternatingGrey(
        rgb(0.0, 0.0, 0.0),
        rgb(0.863, 0.847, 0.847),
        rgb(0.706, 0.706, 0.706),
        rgb(0.565, 0.565, 0.565),
        rgb(0.408, 0.424, 0.424),
        rgb(0.267, 0.282, 0.282),
        rgb(0.141, 0.125, 0.157),
        rgb(0.0, 0.0, 0.0)
    ),
    XfBlues(
        rgb(0.0, 0.0, 0.0),
        rgb(0.0, 0.094, 0.8),
        rgb(0.235, 0.831, 0.988),
        rgb(0.722, 0.988, 0.988),
        rgb(0.0, 0.973, 0.988),
        rgb(0.0, 0.22, 0.988),
        rgb(0.0, 0.0, 0.58),
        rgb(0.0, 0.0, 0.0)
    ),
    XfChromatic(
        rgb(0.188, 0.188, 0.188),
        rgb(0.188, 0.376, 0.878),
        rgb(0.596, 0.878, 0.596),
        rgb(0.878, 0.251, 0.188),
        rgb(0.816, 0.188, 0.878),
        rgb(0.188, 0.878, 0.596),
        rgb(0.878, 0.878, 0.376),
        rgb(0.157, 0.188, 0.188)
    ),
    XfDefault(
        rgb(0.0, 0.0, 0.0),
        rgb(0.988, 0.0, 0.988),
        rgb(0.486, 0.988, 0.486),
        rgb(0.439, 0.0, 0.329),
        rgb(0.22, 0.439, 0.267),
        rgb(0.251, 0.0, 0.125),
        rgb(0.125, 0.251, 0.188),
        rgb(0.0, 0.0, 0.0)
    ),
    XfDefaultWhite(
        rgb(0.0, 0.0, 0.0),
        rgb(0.988, 0.612, 0.235),
        rgb(0.235, 0.486, 0.361),
        rgb(0.486, 0.235, 0.486),
        rgb(0.612, 0.863, 0.486),
        rgb(0.863, 0.612, 0.612),
        rgb(0.988, 0.361, 0.737),
        rgb(0.988, 0.988, 0.988)
    ),
    XfFireStorm(
        rgb(0.0, 0.0, 0.0),
        rgb(0.918, 0.055, 0.529),
        rgb(0.976, 0.392, 0.129),
        rgb(0.671, 0.82, 0.008),
        rgb(0.243, 1.0, 0.255),
        rgb(0.004, 0.8, 0.694),
        rgb(0.137, 0.38, 0.98),
        rgb(0.553, 0.043, 0.902)
    ),
    XfFroth3(
        rgb(0.0, 0.0, 0.0),
        rgb(0.596, 0.0, 0.0),
        rgb(0.408, 0.0, 0.0),
        rgb(0.0, 0.659, 0.0),
        rgb(0.0, 0.471, 0.0),
        rgb(0.0, 0.0, 0.816),
        rgb(0.0, 0.0, 0.533),
        rgb(0.0, 0.0, 0.345)
    ),
    XfFroth316(
        rgb(0.0, 0.0, 0.0),
        rgb(0.831, 0.0, 0.0),
        rgb(0.486, 0.0, 0.0),
        rgb(0.0, 0.988, 0.0),
        rgb(0.0, 0.659, 0.0),
        rgb(0.0, 0.314, 0.0),
        rgb(0.0, 0.0, 0.831),
        rgb(0.0, 0.0, 0.314)
    ),
    XfFroth6(
        rgb(0.0, 0.0, 0.0),
        rgb(0.439, 0.0, 0.439),
        rgb(0.533, 0.0, 0.0),
        rgb(0.612, 0.612, 0.0),
        rgb(0.0, 0.706, 0.0),
        rgb(0.0, 0.0, 0.784),
        rgb(0.0, 0.878, 0.878),
        rgb(0.988, 0.988, 0.988)
    ),
    XfFroth616(
        rgb(0.0, 0.0, 0.0),
        rgb(0.627, 0.0, 0.627),
        rgb(0.627, 0.0, 0.0),
        rgb(0.627, 0.627, 0.0),
        rgb(0.0, 0.627, 0.0),
        rgb(0.0, 0.0, 0.627),
        rgb(0.0, 0.627, 0.627),
        rgb(0.988, 0.988, 0.988)
    ),
    XfGamma1(
        rgb(0.0, 0.0, 0.0),
        rgb(0.361, 0.376, 0.376),
        rgb(0.518, 0.533, 0.502),
        rgb(0.659, 0.643, 0.627),
        rgb(0.753, 0.737, 0.769),
        rgb(0.831, 0.831, 0.863),
        rgb(0.91, 0.91, 0.941),
        rgb(0.988, 0.988, 0.988)
    ),
    XfGamma2(
        rgb(0.0, 0.0, 0.0),
        rgb(0.141, 0.141, 0.125),
        rgb(0.282, 0.282, 0.251),
        rgb(0.424, 0.424, 0.408),
        rgb(0.549, 0.565, 0.58),
        rgb(0.706, 0.706, 0.706),
        rgb(0.863, 0.831, 0.863),
        rgb(0.988, 0.988, 0.988)
    ),
    XfGlasses1(
        rgb(0.0, 0.0, 0.0),
        rgb(0.282, 0.0, 0.0),
        rgb(0.565, 0.0, 0.0),
        rgb(0.847, 0.0, 0.0),
        rgb(0.0, 0.0, 0.125),
        rgb(0.0, 0.0, 0.424),
        rgb(0.0, 0.0, 0.706),
        rgb(0.0, 0.0, 0.988)
    ),
    XfGlasses2(
        rgb(0.0, 0.0, 0.0),
        rgb(0.251, 0.0, 0.125),
        rgb(0.502, 0.0, 0.251),
        rgb(0.816, 0.0, 0.376),
        rgb(0.063, 0.0, 0.565),
        rgb(0.376, 0.0, 0.69),
        rgb(0.627, 0.0, 0.816),
        rgb(0.941, 0.0, 0.941)
    ),
    XfGoodEga(
        rgb(0.0, 0.0, 0.0),
        rgb(0.988, 0.329, 0.0),
        rgb(0.988, 0.494, 0.0),
        rgb(0.988, 0.659, 0.0),
        rgb(0.988, 0.824, 0.0),
        rgb(0.988, 0.988, 0.0),
        rgb(0.988, 0.988, 0.494),
        rgb(0.988, 0.988, 0.988)
    ),
    XfGreen(
        rgb(0.0, 0.0, 0.0),
        rgb(0.0, 0.847, 0.0),
        rgb(0.0, 0.706, 0.0),
        rgb(0.0, 0.565, 0.0),
        rgb(0.0, 0.424, 0.0),
        rgb(0.0, 0.282, 0.0),
        rgb(0.0, 0.125, 0.0),
        rgb(0.0, 0.0, 0.0)
    ),
    XfGrey(
        rgb(0.0, 0.0, 0.0),
        rgb(0.863, 0.863, 0.863),
        rgb(0.718, 0.718, 0.718),
        rgb(0.576, 0.576, 0.576),
        rgb(0.431, 0.431, 0.431),
        rgb(0.29, 0.29, 0.29),
        rgb(0.145, 0.145, 0.145),
        rgb(0.0, 0.0, 0.0)
    ),
    XfGrid(
        rgb(0.0, 0.0, 0.0),
        rgb(0.143, 0.0, 1.0),
        rgb(0.286, 0.0, 1.0),
        rgb(0.429, 0.0, 1.0),
        rgb(0.571, 0.0, 1.0),
        rgb(0.714, 0.0, 1.0),
        rgb(0.857, 0.0, 1.0),
        rgb(1.0, 0.0, 1.0)
    ),
    XfHeadache2(
        rgb(0.941, 0.0, 0.0),
        rgb(0.941, 0.125, 0.0),
        rgb(0.941, 0.267, 0.0),
        rgb(0.957, 0.408, 0.0),
        rgb(0.0, 0.424, 0.565),
        rgb(0.0, 0.282, 0.706),
        rgb(0.0, 0.141, 0.847),
        rgb(0.0, 0.502, 0.502)
    ),
    XfHeadache(
        rgb(0.0, 0.0, 0.0),
        rgb(0.0, 0.863, 0.125),
        rgb(0.0, 0.722, 0.267),
        rgb(0.957, 0.408, 0.0),
        rgb(0.957, 0.549, 0.0),
        rgb(0.0, 0.282, 0.706),
        rgb(0.0, 0.141, 0.847),
        rgb(0.0, 0.502, 0.502)
    ),
    XfLandscape(
        rgb(0.0, 0.0, 0.0),
        rgb(0.0, 0.0, 0.659),
        rgb(0.0, 0.0, 0.659),
        rgb(0.251, 0.325, 0.0),
        rgb(0.251, 0.18, 0.0),
        rgb(0.251, 0.055, 0.0),
        rgb(0.824, 0.824, 1.0),
        rgb(1.0, 1.0, 1.0)
    ),
    XfLyapunov(
        rgb(0.0, 0.0, 0.0),
        rgb(0.863, 0.627, 0.0),
        rgb(0.722, 0.486, 0.0),
        rgb(0.565, 0.329, 0.0),
        rgb(0.424, 0.188, 0.0),
        rgb(0.282, 0.047, 0.0),
        rgb(0.141, 0.0, 0.0),
        rgb(0.0, 0.0, 0.0)
    ),
    XfNeon(
        rgb(0.0, 0.0, 0.0),
        rgb(0.878, 0.282, 0.424),
        rgb(0.0, 0.0, 0.0),
        rgb(0.0, 0.894, 0.0),
        rgb(0.0, 0.0, 0.0),
        rgb(0.675, 0.675, 0.0),
        rgb(0.188, 0.188, 0.0),
        rgb(0.0, 0.0, 0.0)
    ),
    XfPaintJet(
        rgb(0.094, 0.078, 0.047),
        rgb(0.769, 0.267, 0.282),
        rgb(0.941, 0.91, 0.282),
        rgb(0.737, 0.188, 0.424),
        rgb(0.094, 0.078, 0.047),
        rgb(0.769, 0.267, 0.282),
        rgb(0.941, 0.91, 0.282),
        rgb(0.157, 0.455, 0.769)
    ),
    XfRoyal(
        rgb(0.0, 0.0, 0.0),
        rgb(0.361, 0.0, 0.486),
        rgb(0.549, 0.141, 0.675),
        rgb(0.847, 0.722, 0.878),
        rgb(0.988, 0.988, 0.706),
        rgb(0.988, 0.988, 0.125),
        rgb(0.659, 0.549, 0.141),
        rgb(0.235, 0.0, 0.314)
    ),
    XfTopo(
        rgb(0.0, 0.0, 0.0),
        rgb(0.267, 0.518, 0.91),
        rgb(0.235, 0.549, 0.063),
        rgb(0.392, 0.706, 0.22),
        rgb(0.58, 0.894, 0.424),
        rgb(0.91, 0.894, 0.408),
        rgb(0.91, 0.675, 0.141),
        rgb(0.988, 0.988, 0.988)
    ),
    XfVolcano(
        rgb(0.0, 0.0, 0.0),
        rgb(0.988, 0.11, 0.0),
        rgb(0.988, 0.988, 0.235),
        rgb(0.988, 0.894, 0.8),
        rgb(0.988, 0.596, 0.235),
        rgb(0.988, 0.157, 0.0),
        rgb(0.706, 0.0, 0.0),
        rgb(0.235, 0.235, 0.235)
    ),
    Rgb(
        rgb(1.0, 0.0, 0.0),
        rgb(1.0, 1.0, 0.0),
        rgb(0.0, 1.0, 0.0),
        rgb(0.0, 1.0, 1.0),
        rgb(0.0, 0.0, 1.0),
        rgb(1.0, 0.0, 1.0),
        rgb(1.0, 0.0, 0.0)
    ),
    Ryb(
        rgb(0.95, 0.05, 0.05),
        rgb(1.0, 0.55, 0.0),
        rgb(1.0, 0.95, 0.05),
        rgb(0.1, 0.4, 0.9),
        rgb(0.45, 0.05, 0.75),
        rgb(0.95, 0.05, 0.05)
    ),
    Cmyk(
        rgb(0.0, 1.0, 1.0),
        rgb(0.0, 0.2, 1.0),
        rgb(1.0, 0.0, 1.0),
        rgb(1.0, 0.2, 0.0),
        rgb(1.0, 1.0, 0.0),
        rgb(0.0, 0.0, 0.0),
        rgb(0.0, 1.0, 1.0)
    ),
    HsvWheel(
        rgb(1.0, 0.0, 0.0),
        rgb(1.0, 0.5, 0.0),
        rgb(1.0, 1.0, 0.0),
        rgb(0.0, 1.0, 0.0),
        rgb(0.0, 1.0, 1.0),
        rgb(0.0, 0.0, 1.0),
        rgb(0.5, 0.0, 1.0),
        rgb(1.0, 0.0, 1.0),
        rgb(1.0, 0.0, 0.0)
    ),
    RedChannel(
        rgb(0.0, 0.0, 0.0),
        rgb(0.25, 0.0, 0.0),
        rgb(0.55, 0.0, 0.0),
        rgb(0.85, 0.05, 0.02),
        rgb(1.0, 0.45, 0.25),
        rgb(1.0, 1.0, 1.0)
    ),
    GreenChannel(
        rgb(0.0, 0.0, 0.0),
        rgb(0.0, 0.22, 0.03),
        rgb(0.0, 0.52, 0.08),
        rgb(0.12, 0.82, 0.18),
        rgb(0.55, 1.0, 0.48),
        rgb(1.0, 1.0, 1.0)
    ),
    BlueChannel(
        rgb(0.0, 0.0, 0.0),
        rgb(0.01, 0.03, 0.28),
        rgb(0.02, 0.12, 0.62),
        rgb(0.05, 0.4, 0.95),
        rgb(0.4, 0.82, 1.0),
        rgb(1.0, 1.0, 1.0)
    ),
    Heatmap(
        rgb(0.0, 0.0, 0.0),
        rgb(0.25, 0.0, 0.4),
        rgb(0.75, 0.0, 0.1),
        rgb(1.0, 0.35, 0.0),
        rgb(1.0, 0.95, 0.0),
        rgb(1.0, 1.0, 1.0)
    ),
    ColdFire(
        rgb(0.0, 0.02, 0.12),
        rgb(0.0, 0.25, 0.75),
        rgb(0.0, 0.95, 1.0),
        rgb(1.0, 1.0, 1.0),
        rgb(1.0, 0.65, 0.0),
        rgb(0.75, 0.0, 0.05),
        rgb(0.08, 0.0, 0.0)
    ),
    Ultraviolet(
        rgb(0.01, 0.0, 0.05),
        rgb(0.08, 0.0, 0.28),
        rgb(0.35, 0.0, 0.7),
        rgb(0.72, 0.0, 1.0),
        rgb(1.0, 0.2, 0.82),
        rgb(0.45, 0.85, 1.0)
    ),
    ToxicWaste(
        rgb(0.0, 0.02, 0.0),
        rgb(0.04, 0.16, 0.0),
        rgb(0.2, 0.48, 0.0),
        rgb(0.58, 0.9, 0.0),
        rgb(0.9, 1.0, 0.05),
        rgb(0.12, 0.35, 0.15)
    ),
    BloodMoon(
        rgb(0.01, 0.0, 0.0),
        rgb(0.12, 0.0, 0.01),
        rgb(0.38, 0.0, 0.02),
        rgb(0.72, 0.05, 0.02),
        rgb(1.0, 0.28, 0.05),
        rgb(1.0, 0.72, 0.3)
    ),
    Abyss(
        rgb(0.0, 0.0, 0.02),
        rgb(0.0, 0.02, 0.12),
        rgb(0.0, 0.12, 0.24),
        rgb(0.0, 0.38, 0.48),
        rgb(0.08, 0.72, 0.68),
        rgb(0.55, 1.0, 0.88)
    ),
    ElectricCandy(
        rgb(0.02, 0.0, 0.08),
        rgb(0.25, 0.0, 0.85),
        rgb(1.0, 0.0, 0.72),
        rgb(1.0, 0.35, 0.05),
        rgb(0.9, 1.0, 0.0),
        rgb(0.0, 1.0, 0.72),
        rgb(0.02, 0.0, 0.08)
    ),
    BlackGold(
        rgb(0.0, 0.0, 0.0),
        rgb(0.08, 0.055, 0.01),
        rgb(0.28, 0.18, 0.025),
        rgb(0.62, 0.42, 0.06),
        rgb(0.95, 0.75, 0.22),
        rgb(1.0, 0.95, 0.68),
        rgb(0.0, 0.0, 0.0)
    ),
    Ghost(
        rgb(0.0, 0.0, 0.0),
        rgb(0.08, 0.1, 0.14),
        rgb(0.24, 0.3, 0.38),
        rgb(0.48, 0.62, 0.7),
        rgb(0.78, 0.92, 0.94),
        rgb(1.0, 1.0, 1.0),
        rgb(0.0, 0.0, 0.0)
    ),
    Grayscale(
        0xFF000000.toInt(),
        0xFF404040.toInt(),
        0xFF909090.toInt(),
        0xFFFFFFFF.toInt()
    );

    val colors: List<ColorModel> = stops.map(::ColorModel)
    val suggestedColors: List<ColorModel> = List(SUGGESTED_COLOR_COUNT) { index ->
        colorAt(index.toDouble() / SUGGESTED_COLOR_COUNT)
    }.distinctBy(ColorModel::colorInt)

    fun sampleColors(count: Int): List<ColorModel> {
        require(count > 0) { "Count must be greater than zero" }

        if (count == 1) return listOf(colors.first())

        return List(count) { index ->
            val position = index.toDouble() / (count - 1)
            val scaled = position * (stops.size - 1)
            val firstIndex = floor(scaled).toInt().coerceIn(0, stops.lastIndex)
            val secondIndex = (firstIndex + 1).coerceAtMost(stops.lastIndex)

            ColorModel(
                interpolate(
                    first = stops[firstIndex],
                    second = stops[secondIndex],
                    fraction = scaled - firstIndex
                )
            )
        }
    }

    fun colorAt(position: Double): ColorModel = ColorModel(colorIntAt(position))

    fun colorIntAt(position: Double): Int {
        if (stops.size == 1) return stops.first()

        val wrapped = position
            .takeIf(Double::isFinite)
            ?.let { it - floor(it) }
            ?: 0.0
        val scaled = wrapped * (stops.size - 1)
        val firstIndex = floor(scaled).toInt().coerceIn(0, stops.lastIndex)
        val secondIndex = (firstIndex + 1).coerceAtMost(stops.lastIndex)
        val fraction = scaled - firstIndex

        return interpolate(stops[firstIndex], stops[secondIndex], fraction)
    }

    private fun interpolate(
        first: Int,
        second: Int,
        fraction: Double
    ): Int {
        fun channel(shift: Int): Int {
            val start = first ushr shift and 0xFF
            val end = second ushr shift and 0xFF
            return (start + (end - start) * fraction).roundToInt().coerceIn(0, 255)
        }

        return channel(24) shl 24 or
                (channel(16) shl 16) or
                (channel(8) shl 8) or
                channel(0)
    }

}

private const val SUGGESTED_COLOR_COUNT = 12

private fun rgb(
    red: Double,
    green: Double,
    blue: Double
): Int = 0xFF000000.toInt() or
        ((red * 255).roundToInt().coerceIn(0, 255) shl 16) or
        ((green * 255).roundToInt().coerceIn(0, 255) shl 8) or
        (blue * 255).roundToInt().coerceIn(0, 255)
