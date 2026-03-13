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

package com.t8rin.imagetoolbox.core.utils

import com.t8rin.imagetoolbox.core.domain.utils.cast
import com.t8rin.imagetoolbox.core.resources.BuildConfig
import org.w3c.dom.Element
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

fun isNeedUpdate(
    updateName: String,
    allowBetas: Boolean
): Boolean {
    val currentName = BuildConfig.VERSION_NAME
    val betaList = listOf(
        "alpha", "beta", "rc"
    )

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

fun InputStream.parseChangelog(): Changelog {
    var tag = ""
    var changelog = ""

    val tree = DocumentBuilderFactory.newInstance()
        .newDocumentBuilder().parse(this)
        .getElementsByTagName("feed")

    repeat(tree.length) {
        val line = tree.item(it).cast<Element>()
            .getElementsByTagName("entry").item(0)
            .cast<Element>()

        tag = line.getElementsByTagName("title").item(0).textContent
        changelog = line.getElementsByTagName("content").item(0).textContent
    }

    return Changelog(
        tag = tag,
        changelog = changelog
    )
}

data class Changelog(
    val tag: String,
    val changelog: String
)

private fun String.toVersionCodeString(): String {
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