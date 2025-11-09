/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2025 T8RIN (Malik Mukhametzyanov)
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

package com.t8rin.imagetoolbox.feature.quick_tiles.tiles

import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.quick_tiles.tiles.TileAction.OpenApp
import com.t8rin.imagetoolbox.feature.quick_tiles.tiles.TileAction.OpenScreen
import com.t8rin.imagetoolbox.feature.quick_tiles.tiles.TileAction.Screenshot
import com.t8rin.imagetoolbox.feature.quick_tiles.tiles.TileAction.ScreenshotAndOpenScreen

class ImageToolboxTile : QuickTile(
    tileAction = OpenApp
)

class TakeScreenshotTile : QuickTile(
    tileAction = Screenshot
)

class EditScreenshotTile : QuickTile(
    tileAction = ScreenshotAndOpenScreen(null)
)

class GeneratePaletteTile : QuickTile(
    tileAction = ScreenshotAndOpenScreen(Screen.PaletteTools())
)

class ColorPickerTile : QuickTile(
    tileAction = ScreenshotAndOpenScreen(Screen.PickColorFromImage())
)

class QrTile : QuickTile(
    tileAction = OpenScreen(Screen.ScanQrCode())
)

class DocumentScannerTile : QuickTile(
    tileAction = OpenScreen(Screen.DocumentScanner)
)

class TextRecognitionTile : QuickTile(
    tileAction = OpenScreen(Screen.RecognizeText())
)

class ResizeAndConvertTile : QuickTile(
    tileAction = OpenScreen(Screen.ResizeAndConvert())
)