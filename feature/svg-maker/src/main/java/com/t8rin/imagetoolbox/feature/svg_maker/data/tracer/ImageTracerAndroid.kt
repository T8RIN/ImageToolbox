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

@file:Suppress("unused")

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

package com.t8rin.imagetoolbox.feature.svg_maker.data.tracer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.nio.IntBuffer
import java.util.TreeMap
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

class ImageTracerAndroid {

    companion object {
        const val VERSION_NUMBER: String = "2.0.0"

        private val pathsDirLookup = byteArrayOf(
            0, 0, 3, 0,
            1, 0, 3, 0,
            0, 3, 3, 1,
            0, 3, 0, 0
        )

        private val pathsHolepathLookup = booleanArrayOf(
            false, false, false, false,
            false, false, false, true,
            false, false, false, true,
            false, true, true, false
        )

        private val pathsCombinedLookup = arrayOf(
            arrayOf(
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1)
            ),
            arrayOf(
                byteArrayOf(0, 1, 0, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 2, -1, 0)
            ),
            arrayOf(
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 1, 0, -1),
                byteArrayOf(0, 0, 1, 0)
            ),
            arrayOf(
                byteArrayOf(0, 0, 1, 0),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 2, -1, 0),
                byteArrayOf(-1, -1, -1, -1)
            ),
            arrayOf(
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 0, 1, 0),
                byteArrayOf(0, 3, 0, 1),
                byteArrayOf(-1, -1, -1, -1)
            ),
            arrayOf(
                byteArrayOf(13, 3, 0, 1),
                byteArrayOf(13, 2, -1, 0),
                byteArrayOf(7, 1, 0, -1),
                byteArrayOf(7, 0, 1, 0)
            ),
            arrayOf(
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 1, 0, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 3, 0, 1)
            ),
            arrayOf(
                byteArrayOf(0, 3, 0, 1),
                byteArrayOf(0, 2, -1, 0),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1)
            ),
            arrayOf(
                byteArrayOf(0, 3, 0, 1),
                byteArrayOf(0, 2, -1, 0),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1)
            ),
            arrayOf(
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 1, 0, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 3, 0, 1)
            ),
            arrayOf(
                byteArrayOf(11, 1, 0, -1),
                byteArrayOf(14, 0, 1, 0),
                byteArrayOf(14, 3, 0, 1),
                byteArrayOf(11, 2, -1, 0)
            ),
            arrayOf(
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 0, 1, 0),
                byteArrayOf(0, 3, 0, 1),
                byteArrayOf(-1, -1, -1, -1)
            ),
            arrayOf(
                byteArrayOf(0, 0, 1, 0),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 2, -1, 0),
                byteArrayOf(-1, -1, -1, -1)
            ),
            arrayOf(
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 1, 0, -1),
                byteArrayOf(0, 0, 1, 0)
            ),
            arrayOf(
                byteArrayOf(0, 1, 0, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(0, 2, -1, 0)
            ),
            arrayOf(
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1),
                byteArrayOf(-1, -1, -1, -1)
            )
        )

        private val gaussianKernels = arrayOf(
            doubleArrayOf(0.27901, 0.44198, 0.27901),
            doubleArrayOf(0.135336, 0.228569, 0.272192, 0.228569, 0.135336),
            doubleArrayOf(0.086776, 0.136394, 0.178908, 0.195843, 0.178908, 0.136394, 0.086776),
            doubleArrayOf(
                0.063327,
                0.093095,
                0.122589,
                0.144599,
                0.152781,
                0.144599,
                0.122589,
                0.093095,
                0.063327
            ),
            doubleArrayOf(
                0.049692,
                0.069304,
                0.089767,
                0.107988,
                0.120651,
                0.125194,
                0.120651,
                0.107988,
                0.089767,
                0.069304,
                0.049692
            )
        )

        fun saveString(filename: String, str: String) {
            val file = File(filename)
            if (!file.exists()) {
                file.createNewFile()
            }
            file.bufferedWriter().use { writer ->
                writer.write(str)
            }
        }

        fun loadImageData(filename: String): ImageData {
            val image = BitmapFactory.decodeFile(File(filename).absolutePath)
            return loadImageData(image)
        }

        fun loadImageData(image: Bitmap): ImageData {
            val width = image.width
            val height = image.height
            val intBuffer = IntBuffer.allocate(width * height)
            image.copyPixelsToBuffer(intBuffer)
            val rawData = intBuffer.array()
            val data = ByteArray(rawData.size * 4)

            for (i in rawData.indices) {
                data[(i * 4) + 3] = byteTransfer((rawData[i] ushr 24).toByte())
                data[(i * 4) + 2] = byteTransfer((rawData[i] ushr 16).toByte())
                data[(i * 4) + 1] = byteTransfer((rawData[i] ushr 8).toByte())
                data[i * 4] = byteTransfer(rawData[i].toByte())
            }

            return ImageData(width, height, data)
        }

        private fun byteTransfer(b: Byte): Byte = if (b < 0) {
            (b + 128).toByte()
        } else {
            (b - 128).toByte()
        }

        fun imageToSVG(
            filename: String,
            options: HashMap<String, Float>?,
            palette: Array<ByteArray>?
        ): String {
            val checkedOptions = checkoptions(options)
            val imageData = loadImageData(filename)
            return imageDataToSVG(imageData, checkedOptions, palette)
        }

        fun imageToSVG(
            bitmap: Bitmap,
            options: HashMap<String, Float>?,
            palette: Array<ByteArray>?
        ): String {
            val checkedOptions = checkoptions(options)
            val imageData = loadImageData(bitmap)
            return imageDataToSVG(imageData, checkedOptions, palette)
        }


        fun imageDataToSVG(
            imageData: ImageData,
            options: HashMap<String, Float>?,
            palette: Array<ByteArray>?
        ): String {
            val checkedOptions = checkoptions(options)
            val indexedImage = imageDataToIndexedImage(imageData, checkedOptions, palette)
            return getsvgstring(indexedImage, checkedOptions)
        }

        fun imageToSVG(
            bitmap: Bitmap,
            options: HashMap<String, Float>?,
            palette: Array<ByteArray>?,
            listener: SvgListener
        ) {
            val checkedOptions = checkoptions(options)
            val imageData = loadImageData(bitmap)
            imageDataToSVG(imageData, checkedOptions, palette, listener)
        }


        fun imageDataToSVG(
            imageData: ImageData,
            options: HashMap<String, Float>?,
            palette: Array<ByteArray>?,
            listener: SvgListener
        ) {
            val checkedOptions = checkoptions(options)
            val indexedImage = imageDataToIndexedImage(imageData, checkedOptions, palette)
            getsvgstring(indexedImage, checkedOptions, listener)
        }


        fun imageDataToIndexedImage(
            imageData: ImageData,
            options: HashMap<String, Float>?,
            palette: Array<ByteArray>?
        ): IndexedImage {
            val checkedOptions = checkoptions(options)
            val indexedImage = colorquantization(imageData, palette, checkedOptions)
            val rawLayers = layering(indexedImage)
            val batchPathScan = batchpathscan(
                layers = rawLayers,
                pathomit = floor(checkedOptions.option("pathomit").toDouble()).toFloat()
            )
            indexedImage.layers.clear()
            indexedImage.layers.addAll(
                batchtracelayers(
                    binternodes = batchinternodes(batchPathScan),
                    ltres = checkedOptions.option("ltres"),
                    qtres = checkedOptions.option("qtres")
                )
            )
            return indexedImage
        }


        fun checkoptions(options: HashMap<String, Float>?): HashMap<String, Float> {
            val checkedOptions = options ?: HashMap()

            checkedOptions.putIfAbsent("ltres", 1f)
            checkedOptions.putIfAbsent("qtres", 1f)
            checkedOptions.putIfAbsent("pathomit", 8f)
            checkedOptions.putIfAbsent("colorsampling", 1f)
            checkedOptions.putIfAbsent("numberofcolors", 16f)
            checkedOptions.putIfAbsent("mincolorratio", 0.02f)
            checkedOptions.putIfAbsent("colorquantcycles", 3f)
            checkedOptions.putIfAbsent("scale", 1f)
            checkedOptions.putIfAbsent("simplifytolerance", 0f)
            checkedOptions.putIfAbsent("roundcoords", 1f)
            checkedOptions.putIfAbsent("lcpr", 0f)
            checkedOptions.putIfAbsent("qcpr", 0f)
            checkedOptions.putIfAbsent("desc", 1f)
            checkedOptions.putIfAbsent("viewbox", 0f)
            checkedOptions.putIfAbsent("blurradius", 0f)
            checkedOptions.putIfAbsent("blurdelta", 20f)

            return checkedOptions
        }


        fun colorquantization(
            imgd: ImageData,
            palette: Array<ByteArray>?,
            options: HashMap<String, Float>
        ): IndexedImage {
            val numberOfColors = floor(options.option("numberofcolors").toDouble()).toInt()
            val minRatio = options.option("mincolorratio")
            val cycles = floor(options.option("colorquantcycles").toDouble()).toInt()
            val arr = Array(imgd.height + 2) { IntArray(imgd.width + 2) }

            for (j in 0 until imgd.height + 2) {
                arr[j][0] = -1
                arr[j][imgd.width + 1] = -1
            }
            for (i in 0 until imgd.width + 2) {
                arr[0][i] = -1
                arr[imgd.height + 1][i] = -1
            }

            var workingPalette = palette
            if (workingPalette == null) {
                workingPalette = if (options.option("colorsampling") != 0f) {
                    samplepalette(numberOfColors, imgd)
                } else {
                    createPalette(numberOfColors)
                }
            }

            var workingImageData = imgd
            if (options.option("blurradius") > 0f) {
                workingImageData = blur(
                    imgd = workingImageData,
                    rad = options.option("blurradius"),
                    del = options.option("blurdelta")
                )
            }

            val paletteAccumulator = Array(workingPalette.size) { LongArray(5) }

            for (cnt in 0 until cycles) {
                if (cnt > 0) {
                    for (k in workingPalette.indices) {
                        if (paletteAccumulator[k][3] > 0) {
                            workingPalette[k][0] =
                                (-128 + (paletteAccumulator[k][0] / paletteAccumulator[k][4])).toByte()
                            workingPalette[k][1] =
                                (-128 + (paletteAccumulator[k][1] / paletteAccumulator[k][4])).toByte()
                            workingPalette[k][2] =
                                (-128 + (paletteAccumulator[k][2] / paletteAccumulator[k][4])).toByte()
                            workingPalette[k][3] =
                                (-128 + (paletteAccumulator[k][3] / paletteAccumulator[k][4])).toByte()
                        }

                        val ratio =
                            paletteAccumulator[k][4].toDouble() /
                                    (workingImageData.width * workingImageData.height).toDouble()
                        if (ratio < minRatio && cnt < cycles - 1) {
                            workingPalette[k][0] = randomPaletteByte()
                            workingPalette[k][1] = randomPaletteByte()
                            workingPalette[k][2] = randomPaletteByte()
                            workingPalette[k][3] = randomPaletteByte()
                        }
                    }
                }

                for (i in workingPalette.indices) {
                    paletteAccumulator[i][0] = 0
                    paletteAccumulator[i][1] = 0
                    paletteAccumulator[i][2] = 0
                    paletteAccumulator[i][3] = 0
                    paletteAccumulator[i][4] = 0
                }

                for (j in 0 until workingImageData.height) {
                    for (i in 0 until workingImageData.width) {
                        val idx = ((j * workingImageData.width) + i) * 4
                        var closestDistance = 256 + 256 + 256 + 256
                        var closestIndex = 0

                        for (k in workingPalette.indices) {
                            val c1 =
                                abs(workingPalette[k][0].toInt() - workingImageData.data[idx].toInt())
                            val c2 =
                                abs(workingPalette[k][1].toInt() - workingImageData.data[idx + 1].toInt())
                            val c3 =
                                abs(workingPalette[k][2].toInt() - workingImageData.data[idx + 2].toInt())
                            val c4 =
                                abs(workingPalette[k][3].toInt() - workingImageData.data[idx + 3].toInt())
                            val colorDistance = c1 + c2 + c3 + (c4 * 4)

                            if (colorDistance < closestDistance) {
                                closestDistance = colorDistance
                                closestIndex = k
                            }
                        }

                        paletteAccumulator[closestIndex][0] += (128 + workingImageData.data[idx]).toLong()
                        paletteAccumulator[closestIndex][1] += (128 + workingImageData.data[idx + 1]).toLong()
                        paletteAccumulator[closestIndex][2] += (128 + workingImageData.data[idx + 2]).toLong()
                        paletteAccumulator[closestIndex][3] += (128 + workingImageData.data[idx + 3]).toLong()
                        paletteAccumulator[closestIndex][4]++

                        arr[j + 1][i + 1] = closestIndex
                    }
                }
            }

            return IndexedImage(arr, workingPalette)
        }


        private fun createPalette(colorNumber: Int): Array<ByteArray> {
            val palette = Array(colorNumber) { ByteArray(4) }

            if (colorNumber < 8) {
                val grayStep = 255.0 / (colorNumber - 1)
                for (colorCount in 0 until colorNumber) {
                    val gray = (colorCount * grayStep).roundToInt()
                    palette[colorCount][0] = (-128 + gray).toByte()
                    palette[colorCount][1] = (-128 + gray).toByte()
                    palette[colorCount][2] = (-128 + gray).toByte()
                    palette[colorCount][3] = 127.toByte()
                }
            } else {
                val colorQNum = floor(colorNumber.toDouble().pow(1.0 / 3.0)).toInt()
                val colorStep = floor(255.0 / (colorQNum - 1)).toInt()
                var colorCount = 0

                for (redCount in 0 until colorQNum) {
                    for (greenCount in 0 until colorQNum) {
                        for (blueCount in 0 until colorQNum) {
                            palette[colorCount][0] = (-128 + (redCount * colorStep)).toByte()
                            palette[colorCount][1] = (-128 + (greenCount * colorStep)).toByte()
                            palette[colorCount][2] = (-128 + (blueCount * colorStep)).toByte()
                            palette[colorCount][3] = 127.toByte()
                            colorCount++
                        }
                    }
                }

                for (randomCount in colorCount until colorNumber) {
                    palette[randomCount][0] = randomPaletteByte()
                    palette[randomCount][1] = randomPaletteByte()
                    palette[randomCount][2] = randomPaletteByte()
                    palette[randomCount][3] = randomPaletteByte()
                }
            }

            return palette
        }


        fun samplepalette(numberofcolors: Int, imgd: ImageData): Array<ByteArray> {
            val palette = Array(numberofcolors) { ByteArray(4) }

            for (i in 0 until numberofcolors) {
                val idx = (floor((Math.random() * imgd.data.size) / 4.0) * 4).toInt()
                palette[i][0] = imgd.data[idx]
                palette[i][1] = imgd.data[idx + 1]
                palette[i][2] = imgd.data[idx + 2]
                palette[i][3] = imgd.data[idx + 3]
            }

            return palette
        }


        fun layering(ii: IndexedImage): Array<Array<IntArray>> {
            val arrayWidth = ii.array[0].size
            val arrayHeight = ii.array.size
            val layers = Array(ii.palette.size) { Array(arrayHeight) { IntArray(arrayWidth) } }

            for (j in 1 until arrayHeight - 1) {
                for (i in 1 until arrayWidth - 1) {
                    val value = ii.array[j][i]
                    val n1 = if (ii.array[j - 1][i - 1] == value) 1 else 0
                    val n2 = if (ii.array[j - 1][i] == value) 1 else 0
                    val n3 = if (ii.array[j - 1][i + 1] == value) 1 else 0
                    val n4 = if (ii.array[j][i - 1] == value) 1 else 0
                    val n5 = if (ii.array[j][i + 1] == value) 1 else 0
                    val n6 = if (ii.array[j + 1][i - 1] == value) 1 else 0
                    val n7 = if (ii.array[j + 1][i] == value) 1 else 0
                    val n8 = if (ii.array[j + 1][i + 1] == value) 1 else 0

                    layers[value][j + 1][i + 1] = 1 + (n5 * 2) + (n8 * 4) + (n7 * 8)
                    if (n4 == 0) {
                        layers[value][j + 1][i] = 2 + (n7 * 4) + (n6 * 8)
                    }
                    if (n2 == 0) {
                        layers[value][j][i + 1] = (n3 * 2) + (n5 * 4) + 8
                    }
                    if (n1 == 0) {
                        layers[value][j][i] = (n2 * 2) + 4 + (n4 * 8)
                    }
                }
            }

            return layers
        }


        fun pathscan(arr: Array<IntArray>, pathomit: Float): ArrayList<ArrayList<Array<Int>>> {
            val paths = ArrayList<ArrayList<Array<Int>>>()

            for (j in arr.indices) {
                for (i in arr[j].indices) {
                    if (arr[j][i] != 0 && arr[j][i] != 15) {
                        var px = i
                        var py = j
                        val thisPath = ArrayList<Array<Int>>()
                        paths.add(thisPath)

                        var dir = pathsDirLookup[arr[py][px]].toInt()
                        val holePath = pathsHolepathLookup[arr[py][px]]
                        var pathFinished = false

                        while (!pathFinished) {
                            thisPath.add(arrayOf(px - 1, py - 1, arr[py][px]))

                            val lookupRow = pathsCombinedLookup[arr[py][px]][dir]
                            arr[py][px] = lookupRow[0].toInt()
                            dir = lookupRow[1].toInt()
                            px += lookupRow[2].toInt()
                            py += lookupRow[3].toInt()

                            if ((px - 1) == thisPath[0][0] && (py - 1) == thisPath[0][1]) {
                                pathFinished = true
                                if (holePath || thisPath.size.toFloat() < pathomit) {
                                    paths.remove(thisPath)
                                }
                            }
                        }
                    }
                }
            }

            return paths
        }


        fun batchpathscan(
            layers: Array<Array<IntArray>>,
            pathomit: Float
        ): ArrayList<ArrayList<ArrayList<Array<Int>>>> {
            val batchPaths = ArrayList<ArrayList<ArrayList<Array<Int>>>>()
            for (layer in layers) {
                batchPaths.add(pathscan(layer, pathomit))
            }
            return batchPaths
        }


        fun internodes(paths: ArrayList<ArrayList<Array<Int>>>): ArrayList<ArrayList<Array<Double>>> {
            val internodes = ArrayList<ArrayList<Array<Double>>>()

            for (path in paths) {
                val thisInternodePath = ArrayList<Array<Double>>()
                internodes.add(thisInternodePath)
                val pathLength = path.size

                for (pointIndex in 0 until pathLength) {
                    val nextIndex = (pointIndex + 1) % pathLength
                    val nextNextIndex = (pointIndex + 2) % pathLength
                    val currentPoint = path[pointIndex]
                    val nextPoint = path[nextIndex]
                    val nextNextPoint = path[nextNextIndex]

                    val x = (currentPoint[0] + nextPoint[0]) / 2.0
                    val y = (currentPoint[1] + nextPoint[1]) / 2.0
                    val nextX = (nextPoint[0] + nextNextPoint[0]) / 2.0
                    val nextY = (nextPoint[1] + nextNextPoint[1]) / 2.0

                    val direction = when {
                        x < nextX && y < nextY -> 1.0
                        x < nextX && y > nextY -> 7.0
                        x < nextX -> 0.0
                        x > nextX && y < nextY -> 3.0
                        x > nextX && y > nextY -> 5.0
                        x > nextX -> 4.0
                        y < nextY -> 2.0
                        y > nextY -> 6.0
                        else -> 8.0
                    }

                    thisInternodePath.add(arrayOf(x, y, direction))
                }
            }

            return internodes
        }

        private fun batchinternodes(
            batchPaths: ArrayList<ArrayList<ArrayList<Array<Int>>>>
        ): ArrayList<ArrayList<ArrayList<Array<Double>>>> {
            val batchInternodes = ArrayList<ArrayList<ArrayList<Array<Double>>>>()
            for (paths in batchPaths) {
                batchInternodes.add(internodes(paths))
            }
            return batchInternodes
        }


        fun tracepath(
            path: ArrayList<Array<Double>>,
            ltreshold: Float,
            qtreshold: Float
        ): ArrayList<Array<Double>> {
            var pointIndex = 0
            val smp = ArrayList<Array<Double>>()
            val pathLength = path.size

            while (pointIndex < pathLength) {
                val segmentType1 = path[pointIndex][2]
                var segmentType2 = -1.0
                var sequenceEnd = pointIndex + 1

                while (
                    sequenceEnd < pathLength - 1 &&
                    (
                            path[sequenceEnd][2] == segmentType1 ||
                                    path[sequenceEnd][2] == segmentType2 ||
                                    segmentType2 == -1.0
                            )
                ) {
                    if (path[sequenceEnd][2] != segmentType1 && segmentType2 == -1.0) {
                        segmentType2 = path[sequenceEnd][2]
                    }
                    sequenceEnd++
                }

                if (sequenceEnd == pathLength - 1) {
                    sequenceEnd = 0
                }

                smp.addAll(fitSequence(path, ltreshold, qtreshold, pointIndex, sequenceEnd))

                pointIndex = if (sequenceEnd > 0) {
                    sequenceEnd
                } else {
                    pathLength
                }
            }

            return smp
        }


        private fun fitSequence(
            path: ArrayList<Array<Double>>,
            ltreshold: Float,
            qtreshold: Float,
            seqstart: Int,
            seqend: Int
        ): ArrayList<Array<Double>> {
            var segment = ArrayList<Array<Double>>()
            val pathLength = path.size

            if (seqend !in 0..pathLength) {
                return segment
            }

            var errorPoint = seqstart
            var curvePass = true
            var errorValue = 0.0
            var totalLength = (seqend - seqstart).toDouble()
            if (totalLength < 0) {
                totalLength += pathLength.toDouble()
            }

            val vx = (path[seqend][0] - path[seqstart][0]) / totalLength
            val vy = (path[seqend][1] - path[seqstart][1]) / totalLength

            var pointIndex = (seqstart + 1) % pathLength
            while (pointIndex != seqend) {
                var pointLength = (pointIndex - seqstart).toDouble()
                if (pointLength < 0) {
                    pointLength += pathLength.toDouble()
                }

                val px = path[seqstart][0] + (vx * pointLength)
                val py = path[seqstart][1] + (vy * pointLength)
                val distance = squaredDistance(path[pointIndex][0], path[pointIndex][1], px, py)

                if (distance > ltreshold) {
                    curvePass = false
                }
                if (distance > errorValue) {
                    errorPoint = pointIndex
                    errorValue = distance
                }

                pointIndex = (pointIndex + 1) % pathLength
            }

            if (curvePass) {
                segment.add(
                    arrayOf(
                        1.0,
                        path[seqstart][0],
                        path[seqstart][1],
                        path[seqend][0],
                        path[seqend][1],
                        0.0,
                        0.0
                    )
                )
                return segment
            }

            val fitPoint = errorPoint
            curvePass = true
            errorValue = 0.0

            var fitLength = (fitPoint - seqstart).toDouble()
            if (fitLength < 0) {
                fitLength += pathLength.toDouble()
            }

            var t = fitLength / totalLength
            var t1 = (1.0 - t) * (1.0 - t)
            var t2 = 2.0 * (1.0 - t) * t
            var t3 = t * t
            val cpx =
                (((t1 * path[seqstart][0]) + (t3 * path[seqend][0])) - path[fitPoint][0]) / -t2
            val cpy =
                (((t1 * path[seqstart][1]) + (t3 * path[seqend][1])) - path[fitPoint][1]) / -t2

            pointIndex = (seqstart + 1) % pathLength
            while (pointIndex != seqend) {
                var pointLength = (pointIndex - seqstart).toDouble()
                if (pointLength < 0) {
                    pointLength += pathLength.toDouble()
                }

                t = pointLength / totalLength
                t1 = (1.0 - t) * (1.0 - t)
                t2 = 2.0 * (1.0 - t) * t
                t3 = t * t

                val px = (t1 * path[seqstart][0]) + (t2 * cpx) + (t3 * path[seqend][0])
                val py = (t1 * path[seqstart][1]) + (t2 * cpy) + (t3 * path[seqend][1])
                val distance = squaredDistance(path[pointIndex][0], path[pointIndex][1], px, py)

                if (distance > qtreshold) {
                    curvePass = false
                }
                if (distance > errorValue) {
                    errorPoint = pointIndex
                    errorValue = distance
                }

                pointIndex = (pointIndex + 1) % pathLength
            }

            if (curvePass) {
                segment.add(
                    arrayOf(
                        2.0,
                        path[seqstart][0],
                        path[seqstart][1],
                        cpx,
                        cpy,
                        path[seqend][0],
                        path[seqend][1]
                    )
                )
                return segment
            }

            val splitPoint = (fitPoint + errorPoint) / 2
            segment = fitSequence(path, ltreshold, qtreshold, seqstart, splitPoint)
            segment.addAll(fitSequence(path, ltreshold, qtreshold, splitPoint, seqend))
            return segment
        }


        fun batchtracepaths(
            internodepaths: ArrayList<ArrayList<Array<Double>>>,
            ltres: Float,
            qtres: Float
        ): ArrayList<ArrayList<Array<Double>>> {
            val batchTracedPaths = ArrayList<ArrayList<Array<Double>>>()
            for (internodePath in internodepaths) {
                batchTracedPaths.add(tracepath(internodePath, ltres, qtres))
            }
            return batchTracedPaths
        }


        fun batchtracelayers(
            binternodes: ArrayList<ArrayList<ArrayList<Array<Double>>>>,
            ltres: Float,
            qtres: Float
        ): ArrayList<ArrayList<ArrayList<Array<Double>>>> {
            val batchTracedLayers = ArrayList<ArrayList<ArrayList<Array<Double>>>>()
            for (internodePaths in binternodes) {
                batchTracedLayers.add(batchtracepaths(internodePaths, ltres, qtres))
            }
            return batchTracedLayers
        }


        private fun roundToDecimal(value: Float, places: Float): Float {
            val multiplier = 10.0.pow(places.toDouble())
            return ((value * multiplier).roundToInt() / multiplier).toFloat()
        }


        private fun trace(
            sb: StringBuilder,
            desc: String,
            segments: ArrayList<Array<Double>>,
            color: String,
            options: HashMap<String, Float>
        ) {
            val scale = options.option("scale").toDouble()
            val lcpr = options.option("lcpr").toDouble()
            val qcpr = options.option("qcpr").toDouble()
            val roundcoords = floor(options.option("roundcoords").toDouble()).toFloat()

            sb.append("<path ")
                .append(desc)
                .append(color)
                .append("d=\"")
                .append("M ")
                .append(segments[0][1] * scale)
                .append(" ")
                .append(segments[0][2] * scale)
                .append(" ")

            appendPathSegments(sb, segments, scale, roundcoords)

            sb.append("Z\" />")

            for (segment in segments) {
                if (lcpr > 0 && segment[0] == 1.0) {
                    sb.append("<circle cx=\"").append(segment[3] * scale)
                        .append("\" cy=\"").append(segment[4] * scale)
                        .append("\" r=\"").append(lcpr)
                        .append("\" fill=\"white\" stroke-width=\"").append(lcpr * 0.2)
                        .append("\" stroke=\"black\" />")
                }
                if (qcpr > 0 && segment[0] == 2.0) {
                    appendQuadraticControlPoints(sb, segment, scale, qcpr)
                }
            }
        }


        fun trace(
            listener: SvgListener,
            desc: String,
            segments: ArrayList<Array<Double>>,
            colorstr: String,
            options: HashMap<String, Float>
        ) {
            val scale = options.option("scale").toDouble()
            val lcpr = options.option("lcpr").toDouble()
            val qcpr = options.option("qcpr").toDouble()
            val roundcoords = floor(options.option("roundcoords").toDouble()).toFloat()

            listener.onProgress("<path ")
                .onProgress(desc)
                .onProgress(colorstr)
                .onProgress("d=\"")
                .onProgress("M ")
                .onProgress(segments[0][1] * scale)
                .onProgress(" ")
                .onProgress(segments[0][2] * scale)
                .onProgress(" ")

            appendPathSegments(listener, segments, scale, roundcoords)

            listener.onProgress("Z\" />")

            for (segment in segments) {
                if (lcpr > 0 && segment[0] == 1.0) {
                    listener.onProgress("<circle cx=\"").onProgress(segment[3] * scale)
                        .onProgress("\" cy=\"").onProgress(segment[4] * scale)
                        .onProgress("\" r=\"").onProgress(lcpr)
                        .onProgress("\" fill=\"white\" stroke-width=\"").onProgress(lcpr * 0.2)
                        .onProgress("\" stroke=\"black\" />")
                }
                if (qcpr > 0 && segment[0] == 2.0) {
                    appendQuadraticControlPoints(listener, segment, scale, qcpr)
                }
            }
        }


        fun getsvgstring(ii: IndexedImage, options: HashMap<String, Float>?): String {
            val checkedOptions = checkoptions(options)
            val w = (ii.width * checkedOptions.option("scale")).toInt()
            val h = (ii.height * checkedOptions.option("scale")).toInt()
            val viewBoxOrViewport = if (checkedOptions.option("viewbox") != 0f) {
                "viewBox=\"0 0 $w $h\" "
            } else {
                "width=\"$w\" height=\"$h\" "
            }
            val svgString = StringBuilder(
                "<svg ${viewBoxOrViewport}version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" "
            )

            if (checkedOptions.option("desc") != 0f) {
                svgString.append("desc=\"Created with ImageTracerAndroid.kt version $VERSION_NUMBER\" ")
            }
            svgString.append(">")

            val zindex = TreeMap<Double, Array<Int>>()
            for (layerIndex in ii.layers.indices) {
                for (pathIndex in ii.layers[layerIndex].indices) {
                    val label =
                        (ii.layers[layerIndex][pathIndex][0][2] * w) + ii.layers[layerIndex][pathIndex][0][1]
                    zindex[label] = arrayOf(layerIndex, pathIndex)
                }
            }

            for (entry in zindex.entries) {
                val layerIndex = entry.value[0]
                val pathIndex = entry.value[1]
                val pathDescription = if (checkedOptions.option("desc") != 0f) {
                    "desc=\"l $layerIndex p $pathIndex\" "
                } else {
                    ""
                }
                trace(
                    sb = svgString,
                    desc = pathDescription,
                    segments = ii.layers[layerIndex][pathIndex],
                    color = tosvgcolorstr(ii.palette[layerIndex]),
                    options = checkedOptions
                )
            }

            svgString.append("</svg>")
            return svgString.toString()
        }


        fun getsvgstring(
            ii: IndexedImage,
            options: HashMap<String, Float>?,
            listener: SvgListener
        ) {
            val checkedOptions = checkoptions(options)
            val w = (ii.width * checkedOptions.option("scale")).toInt()
            val h = (ii.height * checkedOptions.option("scale")).toInt()
            val viewBoxOrViewport = if (checkedOptions.option("viewbox") != 0f) {
                "viewBox=\"0 0 $w $h\" "
            } else {
                "width=\"$w\" height=\"$h\" "
            }

            listener.onProgress("<svg ${viewBoxOrViewport}version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" ")
            if (checkedOptions.option("desc") != 0f) {
                listener.onProgress("desc=\"Created with ImageTracerAndroid.kt version $VERSION_NUMBER\" ")
            }
            listener.onProgress(">")

            val zindex = TreeMap<Double, Array<Int>>()
            for (layerIndex in ii.layers.indices) {
                for (pathIndex in ii.layers[layerIndex].indices) {
                    val label =
                        (ii.layers[layerIndex][pathIndex][0][2] * w) + ii.layers[layerIndex][pathIndex][0][1]
                    zindex[label] = arrayOf(layerIndex, pathIndex)
                }
            }

            for (entry in zindex.entries) {
                val layerIndex = entry.value[0]
                val pathIndex = entry.value[1]
                val pathDescription = if (checkedOptions.option("desc") != 0f) {
                    "desc=\"l $layerIndex p $pathIndex\" "
                } else {
                    ""
                }
                trace(
                    listener = listener,
                    desc = pathDescription,
                    segments = ii.layers[layerIndex][pathIndex],
                    colorstr = tosvgcolorstr(ii.palette[layerIndex]),
                    options = checkedOptions
                )
            }

            listener.onProgress("</svg>")
        }

        private fun tosvgcolorstr(c: ByteArray): String {
            val red = c[0] + 128
            val green = c[1] + 128
            val blue = c[2] + 128
            return "fill=\"rgb($red,$green,$blue)\" stroke=\"rgb($red,$green,$blue)\" " +
                    "stroke-width=\"1\" opacity=\"${(c[3] + 128) / 255.0}\" "
        }

        private fun blur(imgd: ImageData, rad: Float, del: Float): ImageData {
            var radius = floor(rad.toDouble()).toInt()
            if (radius < 1) {
                return imgd
            }
            if (radius > 5) {
                radius = 5
            }

            var delta = abs(del.toInt())
            if (delta > 1024) {
                delta = 1024
            }

            val imageData2 =
                ImageData(imgd.width, imgd.height, ByteArray(imgd.width * imgd.height * 4))
            val kernel = gaussianKernels[radius - 1]

            for (j in 0 until imgd.height) {
                for (i in 0 until imgd.width) {
                    var redAccumulator = 0.0
                    var greenAccumulator = 0.0
                    var blueAccumulator = 0.0
                    var alphaAccumulator = 0.0
                    var weightAccumulator = 0.0

                    for (k in -radius until radius + 1) {
                        if (i + k > 0 && i + k < imgd.width) {
                            val idx = ((j * imgd.width) + i + k) * 4
                            val weight = kernel[k + radius]
                            redAccumulator += imgd.data[idx].toDouble() * weight
                            greenAccumulator += imgd.data[idx + 1].toDouble() * weight
                            blueAccumulator += imgd.data[idx + 2].toDouble() * weight
                            alphaAccumulator += imgd.data[idx + 3].toDouble() * weight
                            weightAccumulator += weight
                        }
                    }

                    val idx = ((j * imgd.width) + i) * 4
                    imageData2.data[idx] =
                        floor(redAccumulator / weightAccumulator).toInt().toByte()
                    imageData2.data[idx + 1] =
                        floor(greenAccumulator / weightAccumulator).toInt().toByte()
                    imageData2.data[idx + 2] =
                        floor(blueAccumulator / weightAccumulator).toInt().toByte()
                    imageData2.data[idx + 3] =
                        floor(alphaAccumulator / weightAccumulator).toInt().toByte()
                }
            }

            val horizontalImageData = imageData2.data.clone()

            for (j in 0 until imgd.height) {
                for (i in 0 until imgd.width) {
                    var redAccumulator = 0.0
                    var greenAccumulator = 0.0
                    var blueAccumulator = 0.0
                    var alphaAccumulator = 0.0
                    var weightAccumulator = 0.0

                    for (k in -radius until radius + 1) {
                        if (j + k > 0 && j + k < imgd.height) {
                            val idx = (((j + k) * imgd.width) + i) * 4
                            val weight = kernel[k + radius]
                            redAccumulator += horizontalImageData[idx].toDouble() * weight
                            greenAccumulator += horizontalImageData[idx + 1].toDouble() * weight
                            blueAccumulator += horizontalImageData[idx + 2].toDouble() * weight
                            alphaAccumulator += horizontalImageData[idx + 3].toDouble() * weight
                            weightAccumulator += weight
                        }
                    }

                    val idx = ((j * imgd.width) + i) * 4
                    imageData2.data[idx] =
                        floor(redAccumulator / weightAccumulator).toInt().toByte()
                    imageData2.data[idx + 1] =
                        floor(greenAccumulator / weightAccumulator).toInt().toByte()
                    imageData2.data[idx + 2] =
                        floor(blueAccumulator / weightAccumulator).toInt().toByte()
                    imageData2.data[idx + 3] =
                        floor(alphaAccumulator / weightAccumulator).toInt().toByte()
                }
            }

            for (j in 0 until imgd.height) {
                for (i in 0 until imgd.width) {
                    val idx = ((j * imgd.width) + i) * 4
                    val difference =
                        abs(imageData2.data[idx].toInt() - imgd.data[idx].toInt()) +
                                abs(imageData2.data[idx + 1].toInt() - imgd.data[idx + 1].toInt()) +
                                abs(imageData2.data[idx + 2].toInt() - imgd.data[idx + 2].toInt()) +
                                abs(imageData2.data[idx + 3].toInt() - imgd.data[idx + 3].toInt())

                    if (difference > delta) {
                        imageData2.data[idx] = imgd.data[idx]
                        imageData2.data[idx + 1] = imgd.data[idx + 1]
                        imageData2.data[idx + 2] = imgd.data[idx + 2]
                        imageData2.data[idx + 3] = imgd.data[idx + 3]
                    }
                }
            }

            return imageData2
        }

        private fun appendPathSegments(
            sb: StringBuilder,
            segments: ArrayList<Array<Double>>,
            scale: Double,
            roundcoords: Float
        ) {
            for (segment in segments) {
                if (segment[0] == 1.0) {
                    sb.append("L ")
                    appendCoordinate(sb, segment[3], scale, roundcoords)
                    sb.append(" ")
                    appendCoordinate(sb, segment[4], scale, roundcoords)
                    sb.append(" ")
                } else {
                    sb.append("Q ")
                    appendCoordinate(sb, segment[3], scale, roundcoords)
                    sb.append(" ")
                    appendCoordinate(sb, segment[4], scale, roundcoords)
                    sb.append(" ")
                    appendCoordinate(sb, segment[5], scale, roundcoords)
                    sb.append(" ")
                    appendCoordinate(sb, segment[6], scale, roundcoords)
                    sb.append(" ")
                }
            }
        }

        private fun appendPathSegments(
            listener: SvgListener,
            segments: ArrayList<Array<Double>>,
            scale: Double,
            roundcoords: Float
        ) {
            for (segment in segments) {
                if (segment[0] == 1.0) {
                    listener.onProgress("L ")
                    appendCoordinate(listener, segment[3], scale, roundcoords)
                    listener.onProgress(" ")
                    appendCoordinate(listener, segment[4], scale, roundcoords)
                    listener.onProgress(" ")
                } else {
                    listener.onProgress("Q ")
                    appendCoordinate(listener, segment[3], scale, roundcoords)
                    listener.onProgress(" ")
                    appendCoordinate(listener, segment[4], scale, roundcoords)
                    listener.onProgress(" ")
                    appendCoordinate(listener, segment[5], scale, roundcoords)
                    listener.onProgress(" ")
                    appendCoordinate(listener, segment[6], scale, roundcoords)
                    listener.onProgress(" ")
                }
            }
        }

        private fun appendCoordinate(
            sb: StringBuilder,
            coordinate: Double,
            scale: Double,
            roundcoords: Float
        ) {
            if (roundcoords == -1f) {
                sb.append(coordinate * scale)
            } else {
                sb.append(roundToDecimal((coordinate * scale).toFloat(), roundcoords))
            }
        }

        private fun appendCoordinate(
            listener: SvgListener,
            coordinate: Double,
            scale: Double,
            roundcoords: Float
        ) {
            if (roundcoords == -1f) {
                listener.onProgress(coordinate * scale)
            } else {
                listener.onProgress(
                    roundToDecimal(
                        (coordinate * scale).toFloat(),
                        roundcoords
                    ).toDouble()
                )
            }
        }

        private fun appendQuadraticControlPoints(
            sb: StringBuilder,
            segment: Array<Double>,
            scale: Double,
            qcpr: Double
        ) {
            sb.append("<circle cx=\"").append(segment[3] * scale)
                .append("\" cy=\"").append(segment[4] * scale)
                .append("\" r=\"").append(qcpr)
                .append("\" fill=\"cyan\" stroke-width=\"").append(qcpr * 0.2)
                .append("\" stroke=\"black\" />")
            sb.append("<circle cx=\"").append(segment[5] * scale)
                .append("\" cy=\"").append(segment[6] * scale)
                .append("\" r=\"").append(qcpr)
                .append("\" fill=\"white\" stroke-width=\"").append(qcpr * 0.2)
                .append("\" stroke=\"black\" />")
            sb.append("<line x1=\"").append(segment[1] * scale)
                .append("\" y1=\"").append(segment[2] * scale)
                .append("\" x2=\"").append(segment[3] * scale)
                .append("\" y2=\"").append(segment[4] * scale)
                .append("\" stroke-width=\"").append(qcpr * 0.2)
                .append("\" stroke=\"cyan\" />")
            sb.append("<line x1=\"").append(segment[3] * scale)
                .append("\" y1=\"").append(segment[4] * scale)
                .append("\" x2=\"").append(segment[5] * scale)
                .append("\" y2=\"").append(segment[6] * scale)
                .append("\" stroke-width=\"").append(qcpr * 0.2)
                .append("\" stroke=\"cyan\" />")
        }

        private fun appendQuadraticControlPoints(
            listener: SvgListener,
            segment: Array<Double>,
            scale: Double,
            qcpr: Double
        ) {
            listener.onProgress("<circle cx=\"").onProgress(segment[3] * scale)
                .onProgress("\" cy=\"").onProgress(segment[4] * scale)
                .onProgress("\" r=\"").onProgress(qcpr)
                .onProgress("\" fill=\"cyan\" stroke-width=\"").onProgress(qcpr * 0.2)
                .onProgress("\" stroke=\"black\" />")
            listener.onProgress("<circle cx=\"").onProgress(segment[5] * scale)
                .onProgress("\" cy=\"").onProgress(segment[6] * scale)
                .onProgress("\" r=\"").onProgress(qcpr)
                .onProgress("\" fill=\"white\" stroke-width=\"").onProgress(qcpr * 0.2)
                .onProgress("\" stroke=\"black\" />")
            listener.onProgress("<line x1=\"").onProgress(segment[1] * scale)
                .onProgress("\" y1=\"").onProgress(segment[2] * scale)
                .onProgress("\" x2=\"").onProgress(segment[3] * scale)
                .onProgress("\" y2=\"").onProgress(segment[4] * scale)
                .onProgress("\" stroke-width=\"").onProgress(qcpr * 0.2)
                .onProgress("\" stroke=\"cyan\" />")
            listener.onProgress("<line x1=\"").onProgress(segment[3] * scale)
                .onProgress("\" y1=\"").onProgress(segment[4] * scale)
                .onProgress("\" x2=\"").onProgress(segment[5] * scale)
                .onProgress("\" y2=\"").onProgress(segment[6] * scale)
                .onProgress("\" stroke-width=\"").onProgress(qcpr * 0.2)
                .onProgress("\" stroke=\"cyan\" />")
        }

        private fun HashMap<String, Float>.option(key: String): Float =
            this[key] ?: error("Missing ImageTracerAndroid option: $key")

        private fun randomPaletteByte(): Byte =
            (-128 + floor(Math.random() * 255.0).toInt()).toByte()

        private fun squaredDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double =
            ((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2))
    }

    fun imageToIndexedImage(
        filename: String,
        options: HashMap<String, Float>?,
        palette: Array<ByteArray>?
    ): IndexedImage {
        val checkedOptions = checkoptions(options)
        val imageData = loadImageData(filename)
        return imageDataToIndexedImage(imageData, checkedOptions, palette)
    }

    fun imageToIndexedImage(
        bitmap: Bitmap,
        options: HashMap<String, Float>?,
        palette: Array<ByteArray>?
    ): IndexedImage {
        val checkedOptions = checkoptions(options)
        val imageData = loadImageData(bitmap)
        return imageDataToIndexedImage(imageData, checkedOptions, palette)
    }

    class IndexedImage(
        var array: Array<IntArray>,
        var palette: Array<ByteArray>
    ) {
        val width: Int = array[0].size - 2


        val height: Int = array.size - 2


        val layers: ArrayList<ArrayList<ArrayList<Array<Double>>>> = ArrayList()
    }

    class ImageData(
        val width: Int,
        val height: Int,
        val data: ByteArray
    )

    interface SvgListener {
        fun onProgress(part: String): SvgListener

        fun onProgress(part: Double): SvgListener
    }
}
