/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
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

package ru.tech.imageresizershrinker.feature.root.presentation.viewModel

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.exifinterface.media.ExifInterface
import coil.ImageLoader
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.t8rin.dynamic.theme.ColorTuple
import com.t8rin.dynamic.theme.extractPrimaryColor
import com.t8rin.logger.makeLog
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.w3c.dom.Element
import ru.tech.imageresizershrinker.colllage_maker.presentation.viewModel.CollageMakerViewModel
import ru.tech.imageresizershrinker.core.domain.APP_RELEASES
import ru.tech.imageresizershrinker.core.domain.dispatchers.DispatchersHolder
import ru.tech.imageresizershrinker.core.domain.image.ImageGetter
import ru.tech.imageresizershrinker.core.domain.model.PerformanceClass
import ru.tech.imageresizershrinker.core.domain.saving.FileController
import ru.tech.imageresizershrinker.core.resources.BuildConfig
import ru.tech.imageresizershrinker.core.settings.domain.SettingsInteractor
import ru.tech.imageresizershrinker.core.settings.domain.SettingsManager
import ru.tech.imageresizershrinker.core.settings.domain.model.SettingsState
import ru.tech.imageresizershrinker.core.ui.utils.BaseViewModel
import ru.tech.imageresizershrinker.core.ui.utils.navigation.Screen
import ru.tech.imageresizershrinker.core.ui.utils.state.update
import ru.tech.imageresizershrinker.core.ui.widget.other.ToastHostState
import ru.tech.imageresizershrinker.feature.apng_tools.presentation.viewModel.ApngToolsViewModel
import ru.tech.imageresizershrinker.feature.cipher.presentation.viewModel.CipherViewModel
import ru.tech.imageresizershrinker.feature.compare.presentation.viewModel.CompareViewModel
import ru.tech.imageresizershrinker.feature.crop.presentation.viewModel.CropViewModel
import ru.tech.imageresizershrinker.feature.delete_exif.presentation.viewModel.DeleteExifViewModel
import ru.tech.imageresizershrinker.feature.document_scanner.presentation.viewModel.DocumentScannerViewModel
import ru.tech.imageresizershrinker.feature.draw.presentation.viewModel.DrawViewModel
import ru.tech.imageresizershrinker.feature.erase_background.presentation.viewModel.EraseBackgroundViewModel
import ru.tech.imageresizershrinker.feature.filters.presentation.viewModel.FilterViewModel
import ru.tech.imageresizershrinker.feature.format_conversion.presentation.viewModel.FormatConversionViewModel
import ru.tech.imageresizershrinker.feature.generate_palette.presentation.viewModel.GeneratePaletteViewModel
import ru.tech.imageresizershrinker.feature.gif_tools.presentation.viewModel.GifToolsViewModel
import ru.tech.imageresizershrinker.feature.gradient_maker.presentation.viewModel.GradientMakerViewModel
import ru.tech.imageresizershrinker.feature.image_preview.presentation.viewModel.ImagePreviewViewModel
import ru.tech.imageresizershrinker.feature.image_stacking.presentation.viewModel.ImageStackingViewModel
import ru.tech.imageresizershrinker.feature.image_stitch.presentation.viewModel.ImageStitchingViewModel
import ru.tech.imageresizershrinker.feature.jxl_tools.presentation.viewModel.JxlToolsViewModel
import ru.tech.imageresizershrinker.feature.limits_resize.presentation.viewModel.LimitsResizeViewModel
import ru.tech.imageresizershrinker.feature.load_net_image.presentation.viewModel.LoadNetImageViewModel
import ru.tech.imageresizershrinker.feature.pdf_tools.presentation.viewModel.PdfToolsViewModel
import ru.tech.imageresizershrinker.feature.pick_color.presentation.viewModel.PickColorViewModel
import ru.tech.imageresizershrinker.feature.recognize.text.presentation.viewModel.RecognizeTextViewModel
import ru.tech.imageresizershrinker.feature.resize_convert.presentation.viewModel.ResizeAndConvertViewModel
import ru.tech.imageresizershrinker.feature.scan_qr_code.presentation.viewModel.ScanQrCodeViewModel
import ru.tech.imageresizershrinker.feature.settings.presentation.viewModel.SettingsViewModel
import ru.tech.imageresizershrinker.feature.single_edit.presentation.viewModel.SingleEditViewModel
import ru.tech.imageresizershrinker.feature.svg_maker.presentation.viewModel.SvgMakerViewModel
import ru.tech.imageresizershrinker.feature.watermarking.presentation.viewModel.WatermarkingViewModel
import ru.tech.imageresizershrinker.feature.webp_tools.presentation.viewModel.WebpToolsViewModel
import ru.tech.imageresizershrinker.feature.weight_resize.presentation.viewModel.WeightResizeViewModel
import ru.tech.imageresizershrinker.feature.zip.presentation.viewModel.ZipViewModel
import ru.tech.imageresizershrinker.image_splitting.presentation.viewModel.ImageSplitterViewModel
import ru.tech.imageresizershrinker.noise_generation.presentation.viewModel.NoiseGenerationViewModel
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class RootViewModel @AssistedInject constructor(
    @Assisted componentContext: ComponentContext,
    val imageLoader: ImageLoader,
    private val imageGetter: ImageGetter<Bitmap, ExifInterface>,
    private val settingsManager: SettingsManager,
    fileController: FileController,
    dispatchersHolder: DispatchersHolder,
    private val apngToolsComponentFactory: ApngToolsViewModel.Factory,
    private val cipherComponentFactory: CipherViewModel.Factory,
    private val collageMakerComponentFactory: CollageMakerViewModel.Factory,
    private val compareComponentFactory: CompareViewModel.Factory,
    private val cropComponentFactory: CropViewModel.Factory,
    private val deleteExifComponentFactory: DeleteExifViewModel.Factory,
    private val documentScannerComponentFactory: DocumentScannerViewModel.Factory,
    private val drawComponentFactory: DrawViewModel.Factory,
    private val eraseBackgroundComponentFactory: EraseBackgroundViewModel.Factory,
    private val filterComponentFactory: FilterViewModel.Factory,
    private val formatConversionComponentFactory: FormatConversionViewModel.Factory,
    private val generatePaletteComponentFactory: GeneratePaletteViewModel.Factory,
    private val gifToolsComponentFactory: GifToolsViewModel.Factory,
    private val gradientMakerComponentFactory: GradientMakerViewModel.Factory,
    private val imagePreviewComponentFactory: ImagePreviewViewModel.Factory,
    private val imageSplittingComponentFactory: ImageSplitterViewModel.Factory,
    private val imageStackingComponentFactory: ImageStackingViewModel.Factory,
    private val imageStitchingComponentFactory: ImageStitchingViewModel.Factory,
    private val jxlToolsComponentFactory: JxlToolsViewModel.Factory,
    private val limitResizeComponentFactory: LimitsResizeViewModel.Factory,
    private val loadNetImageComponentFactory: LoadNetImageViewModel.Factory,
    private val noiseGenerationComponentFactory: NoiseGenerationViewModel.Factory,
    private val pdfToolsComponentFactory: PdfToolsViewModel.Factory,
    private val pickColorFromImageComponentFactory: PickColorViewModel.Factory,
    private val recognizeTextComponentFactory: RecognizeTextViewModel.Factory,
    private val resizeAndConvertComponentFactory: ResizeAndConvertViewModel.Factory,
    private val scanQrCodeComponentFactory: ScanQrCodeViewModel.Factory,
    private val settingsComponentFactory: SettingsViewModel.Factory,
    private val singleEditComponentFactory: SingleEditViewModel.Factory,
    private val svgMakerComponentFactory: SvgMakerViewModel.Factory,
    private val watermarkingComponentFactory: WatermarkingViewModel.Factory,
    private val webpToolsComponentFactory: WebpToolsViewModel.Factory,
    private val weightResizeComponentFactory: WeightResizeViewModel.Factory,
    private val zipComponentFactory: ZipViewModel.Factory,
) : BaseViewModel(dispatchersHolder, componentContext) {

    private val _settingsState = mutableStateOf(SettingsState.Default)
    val settingsState: SettingsState by _settingsState

    val navController = StackNavigation<Screen>()

    val childStack: Value<ChildStack<Screen, Child>> =
        childStack(
            source = navController,
            initialConfiguration = Screen.Main,
            serializer = Screen.serializer(),
            handleBackButton = true,
            childFactory = ::child,
        )

    fun onBackClicked() = navController.pop()

    private val _uris = mutableStateOf<List<Uri>?>(null)
    val uris by _uris

    private val _extraImageType = mutableStateOf<String?>(null)
    val extraImageType by _extraImageType

    private val _showSelectDialog = mutableStateOf(false)
    val showSelectDialog by _showSelectDialog

    private val _showUpdateDialog = mutableStateOf(false)
    val showUpdateDialog by _showUpdateDialog

    private val _isUpdateAvailable = mutableStateOf(false)
    val isUpdateAvailable by _isUpdateAvailable

    private val _isUpdateCancelled = mutableStateOf(false)

    private val _shouldShowExitDialog = mutableStateOf(true)
    val shouldShowDialog by _shouldShowExitDialog

    private val _showGithubReviewDialog = mutableStateOf(false)
    val showGithubReviewDialog by _showGithubReviewDialog

    private val _showTelegramGroupDialog = mutableStateOf(false)
    val showTelegramGroupDialog by _showTelegramGroupDialog

    private val _tag = mutableStateOf("")
    val tag by _tag

    private val _changelog = mutableStateOf("")
    val changelog by _changelog

    val toastHostState = ToastHostState()

    init {
        if (settingsState.clearCacheOnLaunch) fileController.clearCache()

        runBlocking {
            settingsManager.registerAppOpen()
            _settingsState.value = settingsManager.getSettingsState()
        }
        settingsManager
            .getSettingsStateFlow()
            .onEach {
                _settingsState.value = it
            }
            .launchIn(viewModelScope)

        settingsManager
            .getNeedToShowTelegramGroupDialog()
            .onEach { value ->
                _showTelegramGroupDialog.update { value }
            }
            .launchIn(viewModelScope)
    }

    fun toggleShowUpdateDialog() {
        viewModelScope.launch {
            settingsManager.toggleShowUpdateDialogOnStartup()
        }
    }

    fun setPresets(newPresets: List<Int>) {
        viewModelScope.launch {
            settingsManager.setPresets(
                newPresets.joinToString("*")
            )
        }
    }

    fun cancelledUpdate(showAgain: Boolean = false) {
        if (!showAgain) _isUpdateCancelled.value = true
        _showUpdateDialog.value = false
    }

    fun tryGetUpdate(
        isNewRequest: Boolean = false,
        isInstalledFromMarket: Boolean,
        onNoUpdates: () -> Unit = {}
    ) {
        if (settingsState.appOpenCount < 2 && !isNewRequest) return

        val showDialog = settingsState.showUpdateDialogOnStartup
        if (isInstalledFromMarket) {
            if (showDialog) {
                _showUpdateDialog.value = isNewRequest
            }
        } else {
            if (!_isUpdateCancelled.value || isNewRequest) {
                viewModelScope.launch {
                    checkForUpdates(showDialog, onNoUpdates)
                }
            }
        }
    }

    private suspend fun checkForUpdates(
        showDialog: Boolean,
        onNoUpdates: () -> Unit
    ) = withContext(defaultDispatcher) {
        runCatching {
            val nodes =
                DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    URL("$APP_RELEASES.atom").openConnection().getInputStream()
                )?.getElementsByTagName("feed")

            if (nodes != null) {
                for (i in 0 until nodes.length) {
                    val element = nodes.item(i) as Element
                    val title = element.getElementsByTagName("entry")
                    val line = (title.item(0) as Element)
                    _tag.value = (line.getElementsByTagName("title")
                        .item(0) as Element).textContent
                    _changelog.value = (line.getElementsByTagName("content")
                        .item(0) as Element).textContent
                }
            }

            if (
                isNeedUpdate(
                    currentName = BuildConfig.VERSION_NAME,
                    updateName = tag
                )
            ) {
                _isUpdateAvailable.value = true
                if (showDialog) {
                    _showUpdateDialog.value = true
                }
            } else {
                onNoUpdates()
            }
        }
    }

    @Suppress("unused")
    private fun isNeedUpdateTest() {
        if (BuildConfig.DEBUG) {
            val updateVersions = listOf(
                "2.6.0",
                "2.7.0",
                "2.6.0-rc1",
                "2.6.0-rc01",
                "3.0.0",
                "2.6.0-beta02",
                BuildConfig.VERSION_NAME,
                "2.6.1",
                "2.6.1-alpha01",
                "2.6.0-rc02",
                "2.5.1"
            )
            val currentVersions = listOf(
                BuildConfig.VERSION_NAME,
                "2.6.0-beta03",
                "2.5.0",
                "2.5.1",
                "2.6.0",
                "2.6.2"
            )
            val allowBetas = listOf(false, true)

            allowBetas.forEach { betas ->
                currentVersions.forEach { current ->
                    updateVersions.forEach { update ->
                        val needUpdate = isNeedUpdate(
                            currentName = current,
                            updateName = update,
                            allowBetas = betas
                        )
                        "$current -> $update = $needUpdate, for betaAllowed = $betas".makeLog("Test_Updates")
                    }
                }
            }
        }
    }

    private fun isNeedUpdate(
        currentName: String,
        updateName: String,
        allowBetas: Boolean = settingsState.allowBetas
    ): Boolean {

        val betaList = listOf(
            "alpha", "beta", "rc"
        )

        fun String.toVersionCodeString(): String {
            return replace(
                regex = Regex("0\\d"),
                transform = {
                    it.value.replace("0", "")
                }
            ).replace("-", "")
                .replace(".", "")
                .replace("_", "")
                .let { version ->
                    if (betaList.any { it in version }) version
                    else version + "4"
                }
                .replace("alpha", "1")
                .replace("beta", "2")
                .replace("rc", "3")
                .replace("foss", "")
                .replace("jxl", "")
        }

        val currentVersionCodeString = currentName.toVersionCodeString()
        val updateVersionCodeString = updateName.toVersionCodeString()

        val maxLength = maxOf(currentVersionCodeString.length, updateVersionCodeString.length)

        val currentVersionCode = currentVersionCodeString.padEnd(maxLength, '0').toIntOrNull() ?: -1
        val updateVersionCode = updateVersionCodeString.padEnd(maxLength, '0').toIntOrNull() ?: -1

        return if (!updateName.startsWith(currentName)) {
            if (betaList.all { it !in updateName }) {
                updateVersionCode > currentVersionCode
            } else {
                if (allowBetas || betaList.any { it in currentName }) {
                    updateVersionCode > currentVersionCode
                } else false
            }
        } else false
    }

    fun hideSelectDialog() {
        _showSelectDialog.value = false
    }

    fun updateUris(uris: List<Uri>?) {
        _uris.value = null
        _uris.value = uris

        if (!uris.isNullOrEmpty() || (uris.isNullOrEmpty() && extraImageType != null)) {
            _showSelectDialog.value = true
        }
    }

    fun updateExtraImageType(type: String?) {
        _extraImageType.update { null }
        _extraImageType.update { type }
    }

    fun showToast(
        message: String,
        icon: ImageVector? = null,
    ) {
        viewModelScope.launch {
            toastHostState.showToast(
                message = message,
                icon = icon
            )
        }
    }

    fun cancelShowingExitDialog() {
        _shouldShowExitDialog.update { false }
    }

    fun toggleAllowBetas(installedFromMarket: Boolean) {
        viewModelScope.launch {
            settingsManager.toggleAllowBetas()
            tryGetUpdate(
                isNewRequest = true,
                isInstalledFromMarket = installedFromMarket
            )
        }
    }

    suspend fun getColorTupleFromEmoji(
        emojiUri: String
    ): ColorTuple? = imageGetter
        .getImage(data = emojiUri)
        ?.extractPrimaryColor()
        ?.let { ColorTuple(it) }

    fun onWantGithubReview() {
        _showGithubReviewDialog.update { true }
    }

    fun hideReviewDialog() {
        _showGithubReviewDialog.update { false }
    }

    fun hideTelegramGroupDialog() {
        _showTelegramGroupDialog.update { false }
    }

    fun getSettingsInteractor(): SettingsInteractor = settingsManager

    fun adjustPerformance(performanceClass: PerformanceClass) {
        viewModelScope.launch {
            settingsManager.adjustPerformance(performanceClass)
        }
    }

    fun registerDonateDialogOpen() {
        viewModelScope.launch {
            settingsManager.registerDonateDialogOpen()
        }
    }

    fun notShowDonateDialogAgain() {
        viewModelScope.launch {
            settingsManager.setNotShowDonateDialogAgain()
        }
    }

    fun toggleFavoriteScreen(screen: Screen) {
        viewModelScope.launch {
            settingsManager.toggleFavoriteScreen(screen.id)
        }
    }

    fun registerTelegramGroupOpen() {
        viewModelScope.launch {
            settingsManager.registerTelegramGroupOpen()
        }
    }

    sealed class Child {
        class ApngTools(val component: ApngToolsViewModel) : Child()
        class Cipher(val component: CipherViewModel) : Child()
        class CollageMaker(val component: CollageMakerViewModel) : Child()
        data object ColorTools : Child()
        class Compare(val component: CompareViewModel) : Child()
        class Crop(val component: CropViewModel) : Child()
        class DeleteExif(val component: DeleteExifViewModel) : Child()
        class DocumentScanner(val component: DocumentScannerViewModel) : Child()
        class Draw(val component: DrawViewModel) : Child()
        data object EasterEgg : Child()
        class EraseBackground(val component: EraseBackgroundViewModel) : Child()
        class Filter(val component: FilterViewModel) : Child()
        class FormatConversion(val component: FormatConversionViewModel) : Child()
        class GeneratePalette(val component: GeneratePaletteViewModel) : Child()
        class GifTools(val component: GifToolsViewModel) : Child()
        class GradientMaker(val component: GradientMakerViewModel) : Child()
        class ImagePreview(val component: ImagePreviewViewModel) : Child()
        class ImageSplitting(val component: ImageSplitterViewModel) : Child()
        class ImageStacking(val component: ImageStackingViewModel) : Child()
        class ImageStitching(val component: ImageStitchingViewModel) : Child()
        class JxlTools(val component: JxlToolsViewModel) : Child()
        class LimitResize(val component: LimitsResizeViewModel) : Child()
        class LoadNetImage(val component: LoadNetImageViewModel) : Child()
        class Main(val component: SettingsViewModel) : Child()
        class NoiseGeneration(val component: NoiseGenerationViewModel) : Child()
        class PdfTools(val component: PdfToolsViewModel) : Child()
        class PickColorFromImage(val component: PickColorViewModel) : Child()
        class RecognizeText(val component: RecognizeTextViewModel) : Child()
        class ResizeAndConvert(val component: ResizeAndConvertViewModel) : Child()
        class ScanQrCode(val component: ScanQrCodeViewModel) : Child()
        class Settings(val component: SettingsViewModel) : Child()
        class SingleEdit(val component: SingleEditViewModel) : Child()
        class SvgMaker(val component: SvgMakerViewModel) : Child()
        class Watermarking(val component: WatermarkingViewModel) : Child()
        class WebpTools(val component: WebpToolsViewModel) : Child()
        class WeightResize(val component: WeightResizeViewModel) : Child()
        class Zip(val component: ZipViewModel) : Child()
    }

    private fun child(
        config: Screen,
        componentContext: ComponentContext
    ): Child = when (config) {
        Screen.ColorTools -> Child.ColorTools
        Screen.EasterEgg -> Child.EasterEgg

        Screen.Main -> Child.Main(
            settingsComponentFactory(
                componentContext = componentContext
            )
        )

        is Screen.ApngTools -> Child.ApngTools(
            apngToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.Cipher -> Child.Cipher(
            cipherComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.CollageMaker -> Child.CollageMaker(
            collageMakerComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.Compare -> Child.Compare(
            compareComponentFactory(
                componentContext = componentContext,
                initialComparableUris = config.uris
                    ?.takeIf { it.size == 2 }
                    ?.let { it[0] to it[1] }
            )
        )

        is Screen.Crop -> Child.Crop(
            cropComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.DeleteExif -> Child.DeleteExif(
            deleteExifComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        Screen.DocumentScanner -> Child.DocumentScanner(
            documentScannerComponentFactory(
                componentContext = componentContext
            )
        )

        is Screen.Draw -> Child.Draw(
            drawComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.EraseBackground -> Child.EraseBackground(
            eraseBackgroundComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.Filter -> Child.Filter(
            filterComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.FormatConversion -> Child.FormatConversion(
            formatConversionComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.GeneratePalette -> Child.GeneratePalette(
            generatePaletteComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.GifTools -> Child.GifTools(
            gifToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.GradientMaker -> Child.GradientMaker(
            gradientMakerComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.ImagePreview -> Child.ImagePreview(
            imagePreviewComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.ImageSplitting -> Child.ImageSplitting(
            imageSplittingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uri
            )
        )

        is Screen.ImageStacking -> Child.ImageStacking(
            imageStackingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.ImageStitching -> Child.ImageStitching(
            imageStitchingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.JxlTools -> Child.JxlTools(
            jxlToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.LimitResize -> Child.LimitResize(
            limitResizeComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.LoadNetImage -> Child.LoadNetImage(
            loadNetImageComponentFactory(
                componentContext = componentContext,
                initialUrl = config.url
            )
        )


        Screen.NoiseGeneration -> Child.NoiseGeneration(
            noiseGenerationComponentFactory(
                componentContext = componentContext
            )
        )

        is Screen.PdfTools -> Child.PdfTools(
            pdfToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.PickColorFromImage -> Child.PickColorFromImage(
            pickColorFromImageComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.RecognizeText -> Child.RecognizeText(
            recognizeTextComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.ResizeAndConvert -> Child.ResizeAndConvert(
            resizeAndConvertComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.ScanQrCode -> Child.ScanQrCode(
            scanQrCodeComponentFactory(
                componentContext = componentContext,
                initialQrCodeContent = config.qrCodeContent
            )
        )

        Screen.Settings -> Child.Settings(
            settingsComponentFactory(
                componentContext = componentContext
            )
        )

        is Screen.SingleEdit -> Child.SingleEdit(
            singleEditComponentFactory(
                componentContext = componentContext,
                initialUri = config.uri
            )
        )

        is Screen.SvgMaker -> Child.SvgMaker(
            svgMakerComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.Watermarking -> Child.Watermarking(
            watermarkingComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.WebpTools -> Child.WebpTools(
            webpToolsComponentFactory(
                componentContext = componentContext,
                initialType = config.type
            )
        )

        is Screen.WeightResize -> Child.WeightResize(
            weightResizeComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )

        is Screen.Zip -> Child.Zip(
            zipComponentFactory(
                componentContext = componentContext,
                initialUris = config.uris
            )
        )
    }

    @AssistedFactory
    fun interface Factory {
        operator fun invoke(componentContext: ComponentContext): RootViewModel
    }

}