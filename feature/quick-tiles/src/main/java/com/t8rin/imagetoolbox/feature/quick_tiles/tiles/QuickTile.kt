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

import android.annotation.SuppressLint
import android.service.quicksettings.TileService
import com.t8rin.imagetoolbox.core.ui.utils.navigation.Screen
import com.t8rin.imagetoolbox.feature.quick_tiles.utils.TileAction
import com.t8rin.imagetoolbox.feature.quick_tiles.utils.startActivityAndCollapse

@SuppressLint("NewApi")
sealed class QuickTile(
    private val tileAction: TileAction
) : TileService() {
    override fun onClick() {
        super.onClick()
        startActivityAndCollapse(tileAction)
    }
}

class ImageToolboxTile : QuickTile(
    tileAction = TileAction.OpenApp
)

class TakeScreenshotTile : QuickTile(
    tileAction = TileAction.Screenshot
)

class EditScreenshotTile : QuickTile(
    tileAction = TileAction.ScreenshotAndOpenScreen(null)
)

class GeneratePaletteTile : QuickTile(
    tileAction = TileAction.ScreenshotAndOpenScreen(Screen.GeneratePalette())
)

class ColorPickerTile : QuickTile(
    tileAction = TileAction.ScreenshotAndOpenScreen(Screen.PickColorFromImage())
)

class QrTile : QuickTile(
    tileAction = TileAction.OpenScreen(Screen.ScanQrCode())
)

class DocumentScannerTile : QuickTile(
    tileAction = TileAction.OpenScreen(Screen.DocumentScanner)
)

class TextRecognitionTile : QuickTile(
    tileAction = TileAction.OpenScreen(Screen.RecognizeText())
)

class ResizeAndConvertTile : QuickTile(
    tileAction = TileAction.OpenScreen(Screen.ResizeAndConvert())
)