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

package com.t8rin.imagetoolbox.core.data.saving

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.t8rin.imagetoolbox.core.data.utils.computeFromByteArray
import com.t8rin.imagetoolbox.core.data.utils.getFilename
import com.t8rin.imagetoolbox.core.domain.coroutines.DispatchersHolder
import com.t8rin.imagetoolbox.core.domain.image.model.ImageScaleMode
import com.t8rin.imagetoolbox.core.domain.image.model.Preset
import com.t8rin.imagetoolbox.core.domain.image.model.title
import com.t8rin.imagetoolbox.core.domain.resource.ResourceManager
import com.t8rin.imagetoolbox.core.domain.saving.FilenameCreator
import com.t8rin.imagetoolbox.core.domain.saving.RandomStringGenerator
import com.t8rin.imagetoolbox.core.domain.saving.model.ImageSaveTarget
import com.t8rin.imagetoolbox.core.domain.utils.timestamp
import com.t8rin.imagetoolbox.core.resources.R
import com.t8rin.imagetoolbox.core.settings.domain.SettingsManager
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
        forceNotAddSizeInFilename: Boolean
    ): String {
        val extension = saveTarget.extension

        val checksumType = settingsState.hashingTypeForFilename
        if (checksumType != null && saveTarget.data.isNotEmpty()) {
            val name = checksumType.computeFromByteArray(saveTarget.data)

            if (name.isNotEmpty()) return "$name.$extension"
        }

        if (settingsState.randomizeFilename) return "${randomStringGenerator.generate(32)}.$extension"

        val isOriginalEmpty = saveTarget.originalUri.toUri() == Uri.EMPTY

        val widthString =
            if (isOriginalEmpty) getString(R.string.width).split(" ")[0] else saveTarget.imageInfo.width
        val heightString = if (isOriginalEmpty) getString(
            R.string.height
        ).split(" ")[0] else saveTarget.imageInfo.height

        val wh = "($widthString)x($heightString)"

        var prefix = oneTimePrefix ?: settingsState.filenamePrefix
        var suffix = settingsState.filenameSuffix

        if (prefix.isNotEmpty()) prefix = "${prefix}_"
        if (suffix.isNotEmpty()) suffix = "_$suffix"

        if (settingsState.addOriginalFilename) {
            prefix += if (saveTarget.originalUri.toUri() != Uri.EMPTY) {
                saveTarget.originalUri.toUri()
                    .getFilename(context)
                    ?.dropLastWhile { it != '.' }
                    ?.removeSuffix(".") ?: ""
            } else {
                getString(R.string.original_filename)
            }
        }
        if (settingsState.addSizeInFilename && !forceNotAddSizeInFilename) prefix += wh

        if (settingsState.addPresetInfoToFilename && saveTarget.presetInfo != null && saveTarget.presetInfo != Preset.None) {
            suffix += "_${saveTarget.presetInfo?.asString()}"
        }

        if (settingsState.addImageScaleModeInfoToFilename && saveTarget.imageInfo.imageScaleMode != ImageScaleMode.NotPresent) {
            suffix += "_${
                getStringLocalized(
                    resId = saveTarget.imageInfo.imageScaleMode.title,
                    language = Locale.ENGLISH.language
                ).replace(" ", "-")
            }"
        }

        val randomNumber: () -> String = {
            Random(Random.nextInt()).hashCode().toString().take(4)
        }

        val timeStamp = if (settingsState.useFormattedFilenameTimestamp) {
            "${timestamp()}_${randomNumber()}"
        } else Date().time.toString()

        var body = if (settingsState.addSequenceNumber && saveTarget.sequenceNumber != null) {
            if (settingsState.addOriginalFilename) {
                saveTarget.sequenceNumber.toString()
            } else {
                val timeStampPart = if (settingsState.addTimestampToFilename) {
                    timeStamp.dropLastWhile { it != '_' }
                } else ""

                timeStampPart + saveTarget.sequenceNumber
            }
        } else if (settingsState.addTimestampToFilename) {
            timeStamp
        } else ""

        if (body.isEmpty()) {
            if (prefix.endsWith("_")) prefix = prefix.dropLast(1)
            if (suffix.startsWith("_")) suffix = suffix.drop(1)
            if (prefix.isEmpty() && suffix.isEmpty()) body = "image${randomNumber()}"
        }

        return "$prefix$body$suffix.$extension"
    }

    override fun constructRandomFilename(
        extension: String,
        length: Int
    ): String = "${randomStringGenerator.generate(length)}.${extension}"

    override fun getFilename(uri: String): String = uri.toUri().getFilename(context) ?: ""

}