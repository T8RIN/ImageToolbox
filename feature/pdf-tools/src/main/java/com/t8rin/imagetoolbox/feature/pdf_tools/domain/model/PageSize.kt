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

package com.t8rin.imagetoolbox.feature.pdf_tools.domain.model

data class PageSize(
    val width: Int,
    val height: Int,
    val name: String?
) {
    companion object {
        val Auto by lazy {
            PageSize(
                width = -100,
                height = -100,
                name = null
            )
        }

        val entries: List<PageSize> by lazy {
            listOf(
                // ISO A Series
                PageSize(2384, 3370, "A0"),
                PageSize(1684, 2384, "A1"),
                PageSize(1191, 1684, "A2"),
                PageSize(842, 1191, "A3"),
                PageSize(595, 842, "A4"),
                PageSize(420, 595, "A5"),
                PageSize(297, 420, "A6"),
                PageSize(210, 297, "A7"),
                PageSize(148, 210, "A8"),
                PageSize(105, 148, "A9"),
                PageSize(74, 105, "A10"),

                // ISO B Series
                PageSize(2835, 4008, "B0"),
                PageSize(2004, 2835, "B1"),
                PageSize(1417, 2004, "B2"),
                PageSize(1001, 1417, "B3"),
                PageSize(709, 1001, "B4"),
                PageSize(499, 709, "B5"),
                PageSize(354, 499, "B6"),
                PageSize(249, 354, "B7"),
                PageSize(176, 249, "B8"),
                PageSize(125, 176, "B9"),
                PageSize(88, 125, "B10"),

                // ISO C Series (envelopes)
                PageSize(2599, 3677, "C0"),
                PageSize(1837, 2599, "C1"),
                PageSize(1298, 1837, "C2"),
                PageSize(918, 1298, "C3"),
                PageSize(649, 918, "C4"),
                PageSize(459, 649, "C5"),
                PageSize(323, 459, "C6"),
                PageSize(230, 323, "C7"),
                PageSize(162, 230, "C8"),
                PageSize(113, 162, "C9"),
                PageSize(79, 113, "C10"),

                // US/ANSI Series
                PageSize(612, 792, "Letter"),           // 8.5 x 11 in
                PageSize(612, 1008, "Legal"),            // 8.5 x 14 in
                PageSize(792, 1224, "Tabloid"),          // 11 x 17 in
                PageSize(612, 936, "Statement"),          // 5.5 x 8.5 in
                PageSize(396, 612, "Executive"),          // 7.25 x 10.5 in

                // ANSI Series
                PageSize(612, 792, "ANSI A"),             // Letter
                PageSize(792, 1224, "ANSI B"),            // Tabloid/Ledger
                PageSize(1224, 1584, "ANSI C"),
                PageSize(1584, 2448, "ANSI D"),
                PageSize(2448, 3168, "ANSI E"),

                // Architectural Series
                PageSize(432, 576, "Arch A"),             // 9 x 12 in
                PageSize(576, 864, "Arch B"),             // 12 x 18 in
                PageSize(864, 1152, "Arch C"),            // 18 x 24 in
                PageSize(1152, 1728, "Arch D"),           // 24 x 36 in
                PageSize(1728, 2304, "Arch E"),           // 36 x 48 in
                PageSize(2304, 3240, "Arch E1"),          // 30 x 42 in

                // Other Common Sizes
                PageSize(280, 416, "Business Card"),      // 2.91 x 4.33 in (ISO)
                PageSize(255, 408, "Business Card US"),   // 2 x 3.5 in
                PageSize(499, 709, "A5+"),
                PageSize(595, 984, "A4+"),
                PageSize(842, 1338, "A3+"),

                // Photo Sizes
                PageSize(300, 450, "Photo 4x6"),           // 4 x 6 in
                PageSize(450, 600, "Photo 5x7"),           // 5 x 7 in
                PageSize(600, 720, "Photo 6x8"),           // 6 x 8 in
                PageSize(720, 960, "Photo 8x10"),          // 8 x 10 in
                PageSize(900, 1200, "Photo 10x12"),        // 10 x 12 in
                PageSize(1200, 1800, "Photo 12x18"),       // 12 x 18 in

                // Other International
                PageSize(700, 1000, "F4"),                 // 8.27 x 13 in
                PageSize(827, 1169, "Quarto"),             // 8.46 x 10.83 in
                PageSize(649, 918, "C5 Envelope"),
                PageSize(461, 648, "DL Envelope"),         // 110 x 220 mm
                PageSize(413, 610, "Japanese Postcard"),   // 100 x 148 mm
                PageSize(630, 882, "ISO B6"),

                // Square formats
                PageSize(420, 420, "Square 15cm"),         // 15 x 15 cm
                PageSize(595, 595, "Square 21cm"),         // 21 x 21 cm
                PageSize(842, 842, "Square 30cm"),         // 30 x 30 cm

                // Digital formats
                PageSize(768, 1024, "iPad"),
                PageSize(600, 800, "E-reader"),
                PageSize(1080, 1920, "HD Video"),

                // Posters
                PageSize(1191, 1684, "A2 Poster"),
                PageSize(842, 1191, "A3 Poster"),
                PageSize(1684, 2384, "A1 Poster"),
                PageSize(1224, 1584, "ANSI C Poster"),
                PageSize(1584, 2448, "ANSI D Poster"),

                // Index cards
                PageSize(300, 500, "Index Card 3x5"),      // 3 x 5 in
                PageSize(400, 600, "Index Card 4x6"),      // 4 x 6 in
                PageSize(500, 800, "Index Card 5x8")       // 5 x 8 in
            )
        }
    }
}