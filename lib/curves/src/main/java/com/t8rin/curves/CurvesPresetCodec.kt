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

@file:Suppress("ConstPropertyName", "SameParameterValue")

package com.t8rin.curves

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipInputStream
import kotlin.math.roundToInt

/** Imports ImageToolbox presets, Lightroom XMP/lrtemplate curves, and Adobe ACV files. */
data object CurvesPresetCodec {

    private val lightroomCurveNames = listOf(
        "ToneCurvePV2012",
        "ToneCurvePV2012Red",
        "ToneCurvePV2012Green",
        "ToneCurvePV2012Blue"
    )

    /** The native, human-readable ImageToolbox .itcurve format. */
    fun encode(state: ImageCurvesEditorState): String {
        val controlPoints = state.controlPoints
        return buildString {
            append("{\n")
            append("  \"format\": \"ImageToolbox Curves\",\n")
            append("  \"version\": 1,\n")
            append("  \"curves\": [\n")
            controlPoints.take(CurveCount).forEachIndexed { index, points ->
                append("    ")
                appendPointArray(points)
                if (index < CurveCount - 1) append(',')
                append('\n')
            }
            append("  ],\n")
            append("  \"colorWheels\": ")
            appendPointArray(controlPoints.drop(CurveCount).flatten())
            append("\n}\n")
        }
    }

    /** A Lightroom-compatible XMP preset containing the master and RGB point curves. */
    fun encodeXmp(state: ImageCurvesEditorState): String {
        val controlPoints = state.controlPoints
        return buildString {
            append(
                """<?xml version="1.0" encoding="UTF-8"?>
<x:xmpmeta xmlns:x="adobe:ns:meta/">
  <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    <rdf:Description rdf:about=""
      xmlns:crs="http://ns.adobe.com/camera-raw-settings/1.0/"
      crs:PresetType="Normal"
      crs:Cluster="ImageToolbox"
      crs:SupportsAmount="False"
      crs:ProcessVersion="11.0"
      crs:HasSettings="True"
      crs:ToneCurveName2012="Custom">
"""
            )
            lightroomCurveNames.forEachIndexed { index, name ->
                appendXmpSequence(name, controlPoints[index])
            }
            append(
                """    </rdf:Description>
  </rdf:RDF>
</x:xmpmeta>
"""
            )
        }
    }

    fun decode(bytes: ByteArray): ImageCurvesEditorState {
        require(bytes.isNotEmpty()) { "The curve preset is empty" }
        return when {
            bytes.looksLikeZip() -> decodeZip(bytes)
            bytes.looksLikeText() -> decode(bytes.toString(Charsets.UTF_8))
            else -> decodeAcv(bytes)
        }
    }

    fun decode(text: String): ImageCurvesEditorState {
        require(text.isNotBlank()) { "The curve preset is empty" }
        if (text.trimStart('\uFEFF', ' ', '\t', '\r', '\n').startsWith('{')) {
            return decodeImageToolboxJson(text)
        }

        val importedCurves = lightroomCurveNames.map { name ->
            extractXmlSequence(text, name, LightroomPointScale)
                ?: extractLrtemplateSequence(text, name, LightroomPointScale)
                ?: legacyName(name)?.let { legacyName ->
                    extractXmlSequence(text, legacyName, LightroomPointScale)
                        ?: extractLrtemplateSequence(text, legacyName, LightroomPointScale)
                }
        }
        require(importedCurves.any { it != null }) {
            "No supported tone curves were found in the preset"
        }
        return stateWithRgbCurves(importedCurves)
    }

    private fun decodeImageToolboxJson(text: String): ImageCurvesEditorState {
        require(FormatRegex.containsMatchIn(text)) { "Unsupported curve preset format" }
        val curves = extractJsonArray(text, "curves")
            ?.topLevelArrays()
            ?.map { it.parseNumbers() }
            .orEmpty()
        require(curves.size == CurveCount && curves.all { it.size >= 4 && it.size % 2 == 0 }) {
            "The ImageToolbox curve preset is incomplete"
        }

        val wheelValues = extractJsonArray(text, "colorWheels")?.parseNumbers().orEmpty()
        require(wheelValues.size == ColorWheelValueCount) {
            "The ImageToolbox color wheels are invalid"
        }
        val wheels = listOf(
            wheelValues.subList(0, 2),
            wheelValues.subList(2, 4),
            wheelValues.subList(4, 6),
            wheelValues.subList(6, 7)
        )
        return ImageCurvesEditorState(curves + wheels)
    }

    private fun decodeAcv(bytes: ByteArray): ImageCurvesEditorState {
        var offset = 0
        fun readUnsignedShort(): Int {
            require(offset + 2 <= bytes.size) { "The ACV file is truncated" }
            return ((bytes[offset++].toInt() and 0xff) shl 8) or
                    (bytes[offset++].toInt() and 0xff)
        }

        val version = readUnsignedShort()
        require(version == 1 || version == 4) { "Unsupported ACV version: $version" }
        val curveCount = readUnsignedShort()
        require(curveCount in 1..MaxAcvCurveCount) { "Invalid ACV curve count" }
        val rawCurves = List(curveCount) {
            val pointCount = readUnsignedShort()
            require(pointCount in 2..MaxPointCount) { "Invalid ACV point count" }
            List(pointCount) {
                val output = readUnsignedShort()
                val input = readUnsignedShort()
                input to output
            }
        }
        val scale = if (rawCurves.flatten().any { (x, y) -> x > 255 || y > 255 }) {
            UShort.MAX_VALUE.toFloat()
        } else {
            LightroomPointScale
        }
        val curves = rawCurves.take(RgbCurveCount).map { curve ->
            curve.flatMap { (input, output) -> listOf(input / scale, output / scale) }
        }
        return stateWithRgbCurves(curves)
    }

    private fun decodeZip(bytes: ByteArray): ImageCurvesEditorState {
        val candidates = mutableListOf<Pair<Int, ByteArray>>()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var entryCount = 0
            var entry = zip.nextEntry
            while (entry != null) {
                entryCount++
                require(entryCount <= MaxArchiveEntryCount) { "The preset archive has too many files" }
                val name = entry.name.replace('\\', '/')
                val fileName = name.substringAfterLast('/')
                val extension = fileName.substringAfterLast('.', "").lowercase()
                val priority = ArchiveExtensions.indexOf(extension)
                if (
                    !entry.isDirectory &&
                    !name.startsWith("__MACOSX/") &&
                    !fileName.startsWith("._") &&
                    priority >= 0
                ) {
                    val output = ByteArrayOutputStream()
                    val buffer = ByteArray(ArchiveBufferSize)
                    var size = 0
                    var read = zip.read(buffer)
                    while (read != -1) {
                        if (read > 0) {
                            size += read
                            require(size <= MaxArchivePresetSize) {
                                "A preset in the archive is too large"
                            }
                            output.write(buffer, 0, read)
                        }
                        read = zip.read(buffer)
                    }
                    candidates += priority to output.toByteArray()
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        val preset = candidates.minByOrNull { it.first }?.second
            ?: error("No supported curve preset was found in the archive")
        return decode(preset)
    }

    private fun stateWithRgbCurves(curves: List<List<Float>?>): ImageCurvesEditorState {
        val points = ImageCurvesEditorState.Default.controlPoints.toMutableList()
        curves.forEachIndexed { index, curve ->
            if (curve != null && index < RgbCurveCount) points[index] = curve
        }
        return ImageCurvesEditorState(points)
    }

    private fun StringBuilder.appendPointArray(points: List<Float>) {
        append('[')
        points.chunked(2).forEachIndexed { index, point ->
            if (point.size == 2) {
                if (index > 0) append(", ")
                append('[').append(point[0]).append(", ").append(point[1]).append(']')
            } else if (point.size == 1) {
                if (index > 0) append(", ")
                append('[').append(point[0]).append(']')
            }
        }
        append(']')
    }

    private fun StringBuilder.appendXmpSequence(name: String, points: List<Float>) {
        append("    <crs:$name>\n      <rdf:Seq>\n")
        points.chunked(2).forEach { point ->
            if (point.size == 2) {
                append("        <rdf:li>")
                    .append((point[0] * LightroomPointScale).roundToInt())
                    .append(", ")
                    .append((point[1] * LightroomPointScale).roundToInt())
                    .append("</rdf:li>\n")
            }
        }
        append("      </rdf:Seq>\n    </crs:$name>\n")
    }

    private fun extractJsonArray(text: String, key: String): String? {
        val keyMatch = Regex("\"$key\"\\s*:").find(text) ?: return null
        val start = text.indexOf('[', keyMatch.range.last + 1).takeIf { it >= 0 } ?: return null
        var depth = 0
        for (index in start until text.length) {
            when (text[index]) {
                '[' -> depth++
                ']' -> {
                    depth--
                    if (depth == 0) return text.substring(start + 1, index)
                }
            }
        }
        return null
    }

    private fun String.topLevelArrays(): List<String> {
        val arrays = mutableListOf<String>()
        var depth = 0
        var start = -1
        forEachIndexed { index, character ->
            when (character) {
                '[' -> {
                    if (depth == 0) start = index
                    depth++
                }

                ']' -> {
                    depth--
                    if (depth == 0 && start >= 0) arrays += substring(start + 1, index)
                }
            }
        }
        return arrays
    }

    private fun extractXmlSequence(text: String, name: String, scale: Float): List<Float>? {
        val body = Regex(
            pattern = "<(?:[\\w.-]+:)?$name\\b[^>]*>([\\s\\S]*?)" +
                    "</(?:[\\w.-]+:)?$name\\s*>",
            option = RegexOption.IGNORE_CASE
        ).find(text)?.groupValues?.get(1) ?: return null
        val points = XmlListItemRegex.findAll(body)
            .mapNotNull { match ->
                match.groupValues[1].parseNumbers().takeIf { it.size >= 2 }
            }
            .flatMap { it.take(2).asSequence() }
            .map { it / scale }
            .toList()
        return points.takeIf { it.size >= 4 && it.size % 2 == 0 }
    }

    private fun extractLrtemplateSequence(text: String, name: String, scale: Float): List<Float>? {
        val body = Regex(
            pattern = "\\b$name\\s*=\\s*\\{([\\s\\S]*?)\\}",
            option = RegexOption.IGNORE_CASE
        ).find(text)?.groupValues?.get(1) ?: return null
        val points = body.parseNumbers().map { it / scale }
        return points.takeIf { it.size >= 4 && it.size % 2 == 0 }
    }

    private fun String.parseNumbers(): List<Float> = NumberRegex.findAll(this)
        .mapNotNull { it.value.toFloatOrNull() }
        .toList()

    private fun ByteArray.looksLikeText(): Boolean = take(64).all { byte ->
        val value = byte.toInt() and 0xff
        value == 0x09 || value == 0x0a || value == 0x0d || value in 0x20..0x7e || value >= 0x80
    }

    private fun ByteArray.looksLikeZip(): Boolean = size >= 4 &&
            this[0] == 'P'.code.toByte() &&
            this[1] == 'K'.code.toByte() &&
            this[2].toInt() in ZipSignatureThirdBytes &&
            this[3].toInt() in ZipSignatureFourthBytes

    private fun legacyName(name: String): String? = when (name) {
        "ToneCurvePV2012" -> "ToneCurve"
        "ToneCurvePV2012Red" -> "ToneCurveRed"
        "ToneCurvePV2012Green" -> "ToneCurveGreen"
        "ToneCurvePV2012Blue" -> "ToneCurveBlue"
        else -> null
    }

    private const val CurveCount = 17
    private const val RgbCurveCount = 4
    private const val ColorWheelValueCount = 7
    private const val MaxAcvCurveCount = 19
    private const val MaxPointCount = 256
    private const val MaxArchiveEntryCount = 128
    private const val MaxArchivePresetSize = 2 * 1024 * 1024
    private const val ArchiveBufferSize = 8192
    private const val LightroomPointScale = 255f
    private val ArchiveExtensions = listOf("itcurve", "xmp", "lrtemplate", "acv")
    private val ZipSignatureThirdBytes = setOf(3, 5, 7)
    private val ZipSignatureFourthBytes = setOf(4, 6, 8)
    private val FormatRegex = Regex(
        "\"format\"\\s*:\\s*\"ImageToolbox Curves\"",
        RegexOption.IGNORE_CASE
    )
    private val XmlListItemRegex = Regex(
        "<(?:[\\w.-]+:)?li\\b[^>]*>([\\s\\S]*?)</(?:[\\w.-]+:)?li\\s*>",
        RegexOption.IGNORE_CASE
    )
    private val NumberRegex = Regex("[-+]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][-+]?\\d+)?")
}
