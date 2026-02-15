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

package com.t8rin.imagetoolbox.core.data.saving

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.data.utils.computeFromByteArray
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.title
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.RandomStringGenerator
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Date
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.DateUpper
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Extension
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.ExtensionUpper
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Height
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.OriginalName
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.OriginalNameUpper
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Prefix
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.PrefixUpper
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.PresetInfo
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.PresetInfoUpper
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Rand
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.ScaleMode
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.ScaleModeUpper
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Sequence
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Suffix
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.SuffixUpper
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.Width
import com.t8rin.imagetoolbox.core.domain.saving.model.FilenamePattern.Companion.replace
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
import com.t8rin.imagetoolbox.core.settings.domain.model.FilenameBehavior
import com.t8rin.imagetoolbox.core.utils.filename
import com.t8rin.logger.makeLog
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.random.Random

internal class AndroidFilenameCreator @Inject constructor(
    private val randomStringGenerator: RandomStringGenerator,
    @ApplicationContext private val context: Context,
    settingsManager: SettingsManager,
    dispatchersHolder: DispatchersHolder,
    resourceManager: ResourceManager
) : FilenameCreator,
    DispatchersHolder by dispatchersHolder,
    ResourceManager by resourceManager {

    private val _settingsState = settingsManager.settingsState
    private val settingsState get() = _settingsState.value

    override fun constructImageFilename(
        saveTarget: ImageSaveTarget,
        oneTimePrefix: String?,
        forceNotAddSizeInFilename: Boolean,
        pattern: String?
    ): String {
        val extension = saveTarget.extension

        return when (val behavior = settingsState.filenameBehavior) {
            is FilenameBehavior.Checksum -> "${behavior.hashingType.computeFromByteArray(saveTarget.data)}.$extension"
            is FilenameBehavior.Random -> "${randomStringGenerator.generate(32)}.$extension"
            is FilenameBehavior.Overwrite,
            is FilenameBehavior.None -> constructImageFilename(
                saveTarget = saveTarget,
                oneTimePrefix = oneTimePrefix,
                pattern = (pattern ?: settingsState.filenamePattern).orEmpty().ifBlank {
                    if (settingsState.addOriginalFilename) {
                        FilenamePattern.ForOriginal
                    } else {
                        FilenamePattern.Default
                    }
                }
            )
        }.makeLog("Filename")
    }

    private fun constructImageFilename(
        saveTarget: ImageSaveTarget,
        oneTimePrefix: String?,
        pattern: String
    ): String {
        val random = Random(Date().time)
        val extension = saveTarget.extension

        val isOriginalEmpty = saveTarget.originalUri.toUri() == Uri.EMPTY

        val widthString = if (settingsState.addSizeInFilename) {
            if (isOriginalEmpty) "" else saveTarget.imageInfo.width.toString()
        } else ""
        val heightString = if (settingsState.addSizeInFilename) {
            if (isOriginalEmpty) "" else saveTarget.imageInfo.height.toString()
        } else ""

        val prefix = oneTimePrefix ?: settingsState.filenamePrefix
        val suffix = settingsState.filenameSuffix

        val originalName = if (settingsState.addOriginalFilename && !isOriginalEmpty) {
            saveTarget.originalUri.toUri().filename(context)
                ?.substringBeforeLast('.').orEmpty()
        } else ""

        val presetInfo =
            if (settingsState.addPresetInfoToFilename && saveTarget.presetInfo != null && saveTarget.presetInfo != Preset.None) {
                saveTarget.presetInfo?.asString().orEmpty()
            } else ""

        val scaleModeInfo =
            if (settingsState.addImageScaleModeInfoToFilename && saveTarget.imageInfo.imageScaleMode != ImageScaleMode.NotPresent) {
                getStringLocalized(
                    resId = saveTarget.imageInfo.imageScaleMode.title,
                    language = Locale.ENGLISH.language
                ).replace(" ", "-")
            } else ""

        val randomNumber: (count: Int) -> String = { count ->
            buildString {
                repeat(count.coerceAtMost(500)) {
                    append(random.nextInt(0, 10))
                }
            }.take(count)
        }

        val timestampString = if (settingsState.addTimestampToFilename) {
            if (settingsState.useFormattedFilenameTimestamp) {
                "${timestamp()}_${randomNumber(4)}"
            } else Date().time.toString()
        } else ""

        val sequenceNumber = saveTarget.sequenceNumber?.toString() ?: ""

        var result = pattern

        runCatching {
            result = result.replace("""\\d\{(.*?)\}""".toRegex()) { match ->
                if (settingsState.addTimestampToFilename) {
                    timestamp(
                        format = match.groupValues[1]
                    )
                } else {
                    ""
                }
            }

            result = result.replace("""\\D\{(.*?)\}""".toRegex()) { match ->
                if (settingsState.addTimestampToFilename) {
                    timestamp(
                        format = match.groupValues[1]
                    ).uppercase()
                } else {
                    ""
                }
            }
        }.onFailure {
            it.makeLog("pattern date")
        }

        runCatching {
            result = result.replace("""\\r\{(\d+)\}""".toRegex()) { match ->
                randomNumber(match.groupValues[1].toIntOrNull() ?: 4)
            }
        }.onFailure {
            it.makeLog("pattern random")
        }

        return result
            .replace(Width, widthString)
            .replace(Height, heightString)
            .replace(Prefix, prefix)
            .replace(Suffix, suffix)
            .replace(OriginalName, originalName)
            .replace(Sequence, sequenceNumber)
            .replace(PresetInfo, presetInfo)
            .replace(ScaleMode, scaleModeInfo)
            .replace(Extension, extension)
            .replace(Rand, randomNumber(4))
            .replace(Date, timestampString)
            .replace(PrefixUpper, prefix.uppercase())
            .replace(SuffixUpper, suffix.uppercase())
            .replace(OriginalNameUpper, originalName.uppercase())
            .replace(PresetInfoUpper, presetInfo.uppercase())
            .replace(ScaleModeUpper, scaleModeInfo.uppercase())
            .replace(ExtensionUpper, extension.uppercase())
            .replace(DateUpper, timestampString.uppercase())
            .clean()
            .let { str ->
                if (str.split(".").filter { it.isNotBlank() }.size < 2) {
                    "image${randomNumber(4)}.$extension"
                } else {
                    str
                }
            }
            .clean()
    }

    private fun String.clean(): String = this
        .replace("[]", "")
        .replace("{}", "")
        .replace("()x()", "")
        .replace("()", "")
        .replace("___", "_")
        .replace("__", "_")
        .replace("_.", ".")
        .removePrefix("_")

    override fun constructRandomFilename(
        extension: String,
        length: Int
    ): String = "${randomStringGenerator.generate(length)}.${extension}"

    override fun getFilename(uri: String): String = uri.toUri().filename(context) ?: ""

}