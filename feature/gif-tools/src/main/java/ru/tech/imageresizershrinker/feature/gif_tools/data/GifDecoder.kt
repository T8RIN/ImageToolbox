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

@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package ru.tech.imageresizershrinker.feature.gif_tools.data

import android.graphics.Bitmap
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.pow

/**
 * Copyright (c) 2013 Xcellent Creations, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */


/**
 * Reads frame data from a GIF image source and decodes it into individual frames
 * for animation purposes.  Image data can be read from either and InputStream source
 * or a byte[].
 *
 * This class is optimized for running animations with the frames, there
 * are no methods to get individual frame images, only to decode the next frame in the
 * animation sequence.  Instead, it lowers its memory footprint by only housing the minimum
 * data necessary to decode the next frame in the animation sequence.
 *
 * The animation must be manually moved forward using [.advance] before requesting the next
 * frame.  This method must also be called before you request the first frame or an error will
 * occur.
 *
 * Implementation adapted from sample code published in Lyons. (2004). *Java for Programmers*,
 * republished under the MIT Open Source License
 */
class GifDecoder {
    /**
     * Global status code of GIF data parsing
     */
    private var status = 0

    //Global File Header values and parsing flags
    private var width = 0 // full image width
    private var height = 0 // full image height
    private var gctFlag = false // global color table used
    private var gctSize = 0 // size of global color table

    /**
     * Gets the "Netscape" iteration count, if any. A count of 0 means repeat indefinitiely.
     *
     * @return iteration count if one was specified, else 1.
     */
    var loopCount = 1 // iterations; 0 = repeat forever
        private set
    private var gct // global color table
            : IntArray? = null
    private var act // active color table
            : IntArray? = null
    private var bgIndex = 0 // background color index
    private var bgColor = 0 // background color
    private var pixelAspect = 0 // pixel aspect ratio
    private var lctFlag = false // local color table flag
    private var lctSize = 0 // local color table size

    // Raw GIF data from input source
    private var rawData: ByteBuffer? = null

    // Raw data read working array
    private var block = ByteArray(256) // current data block
    private var blockSize = 0 // block size last graphic control extension info

    // LZW decoder working arrays
    private var prefix: ShortArray? = null
    private var suffix: ByteArray? = null
    private var pixelStack: ByteArray? = null
    private lateinit var mainPixels: ByteArray
    private lateinit var mainScratch: IntArray
    private lateinit var copyScratch: IntArray
    private var frames: ArrayList<GifFrame?>? = null // frames read from current file
    private var currentFrame: GifFrame? = null
    private var previousImage: Bitmap? = null
    private var currentImage: Bitmap? = null
    private var renderImage: Bitmap? = null

    /**
     * Gets the current index of the animation frame, or -1 if animation hasn't not yet started
     *
     * @return frame index
     */
    var currentFrameIndex = 0
        private set

    /**
     * Gets the number of frames read from file.
     *
     * @return frame count
     */
    var frameCount = 0
        private set

    /**
     * Inner model class housing metadata for each frame
     */
    private class GifFrame {
        var ix = 0
        var iy = 0
        var iw = 0
        var ih = 0

        /* Control Flags */
        var interlace = false
        var transparency = false

        /* Disposal Method */
        var dispose = 0

        /* Transparency Index */
        var transIndex = 0

        /* Delay, in ms, to next frame */
        var delay = 0

        /* Index in the raw buffer where we need to start reading to decode */
        var bufferFrameStart = 0

        /* Local Color Table */
        var lct: IntArray? = null
    }

    /**
     * Move the animation frame counter forward
     */
    fun advance() {
        currentFrameIndex = (currentFrameIndex + 1) % frameCount
    }

    /**
     * Gets display duration for specified frame.
     *
     * @param n int index of frame
     * @return delay in milliseconds
     */
    fun getDelay(n: Int): Int {
        var delay = -1
        if (n in 0..<frameCount) {
            delay = frames!![n]!!.delay
        }
        return delay
    }

    val nextDelay: Int
        /**
         * Gets display duration for the upcoming frame
         */
        get() = if (frameCount <= 0 || currentFrameIndex < 0) {
            -1
        } else getDelay(currentFrameIndex)
    val nextFrame: Bitmap?
        /**
         * Get the next frame in the animation sequence.
         *
         * @return Bitmap representation of frame
         */
        get() {
            if (frameCount <= 0 || currentFrameIndex < 0 || currentImage == null) {
                return null
            }
            val frame = frames!![currentFrameIndex]

            //Set the appropriate color table
            if (frame!!.lct == null) {
                act = gct
            } else {
                act = frame.lct
                if (bgIndex == frame.transIndex) {
                    bgColor = 0
                }
            }
            var save = 0
            if (frame.transparency) {
                save = act!![frame.transIndex]
                act!![frame.transIndex] = 0 // set transparent color if specified
            }
            if (act == null) {
                Log.w(TAG, "No Valid Color Table")
                status = STATUS_FORMAT_ERROR // no color table defined
                return null
            }
            setPixels(currentFrameIndex) // transfer pixel data to image

            // Reset the transparent pixel in the color table
            if (frame.transparency) {
                act!![frame.transIndex] = save
            }
            return currentImage
        }

    /**
     * Reads GIF image from stream
     *
     * @param inputStream containing GIF file.
     * @return read status code (0 = no errors)
     */
    fun read(inputStream: InputStream?, contentLength: Int): Int {
        if (inputStream != null) {
            try {
                val capacity = if (contentLength > 0) contentLength + 4096 else 4096
                val buffer = ByteArrayOutputStream(capacity)
                var nRead: Int
                val data = ByteArray(16384)
                while (inputStream.read(data, 0, data.size).also { nRead = it } != -1) {
                    buffer.write(data, 0, nRead)
                }
                buffer.flush()
                read(buffer.toByteArray())
            } catch (e: IOException) {
                Log.w(TAG, "Error reading data from stream", e)
            }
        } else {
            status = STATUS_OPEN_ERROR
        }
        try {
            inputStream!!.close()
        } catch (e: Exception) {
            Log.w(TAG, "Error closing stream", e)
        }
        return status
    }

    /**
     * Reads GIF image from byte array
     *
     * @param data containing GIF file.
     * @return read status code (0 = no errors)
     */
    fun read(data: ByteArray?): Int {
        init()
        if (data != null) {
            //Initiliaze the raw data buffer
            rawData = ByteBuffer.wrap(data).also {
                it.rewind()
                it.order(ByteOrder.LITTLE_ENDIAN)
            }
            readHeader()
            if (!err()) {
                readContents()
                if (frameCount < 0) {
                    status = STATUS_FORMAT_ERROR
                }
            }
        } else {
            status = STATUS_OPEN_ERROR
        }
        return status
    }

    /**
     * Creates new frame image from current data (and previous frames as specified by their disposition codes).
     */
    private fun setPixels(frameIndex: Int) {
        val currentFrame = frames!![frameIndex]
        var previousFrame: GifFrame? = null
        val previousIndex = frameIndex - 1
        if (previousIndex >= 0) {
            previousFrame = frames!![previousIndex]
        }

        // final location of blended pixels
        val dest = mainScratch

        // fill in starting image contents based on last image's dispose code
        if (previousFrame != null && previousFrame.dispose > DISPOSAL_UNSPECIFIED) {
            if (previousFrame.dispose == DISPOSAL_NONE && currentImage != null) {
                // Start with the current image
                currentImage!!.getPixels(dest, 0, width, 0, 0, width, height)
            }
            if (previousFrame.dispose == DISPOSAL_BACKGROUND) {
                // Start with a canvas filled with the background color
                var c = 0
                if (!currentFrame!!.transparency) {
                    c = bgColor
                }
                for (i in 0 until previousFrame.ih) {
                    val n1 = (previousFrame.iy + i) * width + previousFrame.ix
                    val n2 = n1 + previousFrame.iw
                    for (k in n1 until n2) {
                        dest[k] = c
                    }
                }
            }
            if (previousFrame.dispose == DISPOSAL_PREVIOUS && previousImage != null) {
                // Start with the previous frame
                previousImage!!.getPixels(dest, 0, width, 0, 0, width, height)
            }
        }

        //Decode pixels for this frame  into the global pixels[] scratch
        decodeBitmapData(currentFrame, mainPixels) // decode pixel data

        // copy each source line to the appropriate place in the destination
        var pass = 1
        var inc = 8
        var iline = 0
        for (i in 0 until currentFrame!!.ih) {
            var line = i
            if (currentFrame.interlace) {
                if (iline >= currentFrame.ih) {
                    pass++
                    when (pass) {
                        2 -> iline = 4
                        3 -> {
                            iline = 2
                            inc = 4
                        }

                        4 -> {
                            iline = 1
                            inc = 2
                        }

                        else -> {}
                    }
                }
                line = iline
                iline += inc
            }
            line += currentFrame.iy
            if (line < height) {
                val k = line * width
                var dx = k + currentFrame.ix // start of line in dest
                var dlim = dx + currentFrame.iw // end of dest line
                if (k + width < dlim) {
                    dlim = k + width // past dest edge
                }
                var sx = i * currentFrame.iw // start of line in source
                while (dx < dlim) {
                    // map color and insert in destination
                    val index = mainPixels[sx++].toInt() and 0xff
                    val c = act!![index]
                    if (c != 0) {
                        dest[dx] = c
                    }
                    dx++
                }
            }
        }

        //Copy pixels into previous image
        currentImage!!.getPixels(copyScratch, 0, width, 0, 0, width, height)
        previousImage!!.setPixels(copyScratch, 0, width, 0, 0, width, height)
        //Set pixels for current image
        currentImage!!.setPixels(dest, 0, width, 0, 0, width, height)
    }

    /**
     * Decodes LZW image data into pixel array. Adapted from John Cristy's BitmapMagick.
     */
    private fun decodeBitmapData(
        frame: GifFrame?,
        dstPixels: ByteArray?
    ) {
        var tempDstPixels = dstPixels

        if (frame != null) {
            //Jump to the frame start position
            rawData!!.position(frame.bufferFrameStart)
        }
        val nullCode = -1
        val pixelCount = if (frame == null) width * height else frame.iw * frame.ih
        var available: Int
        val clear: Int
        var codeMask: Int
        var codeSize: Int
        var inCode: Int
        var oldCode: Int
        var code: Int
        if (tempDstPixels == null || tempDstPixels.size < pixelCount) {
            tempDstPixels = ByteArray(pixelCount) // allocate new pixel array
        }
        if (prefix == null) {
            prefix = ShortArray(MAX_STACK_SIZE)
        }
        if (suffix == null) {
            suffix = ByteArray(MAX_STACK_SIZE)
        }
        if (pixelStack == null) {
            pixelStack = ByteArray(MAX_STACK_SIZE + 1)
        }

        // Initialize GIF data stream decoder.
        val dataSize: Int = read()
        clear = 1 shl dataSize
        val endOfInformation: Int = clear + 1
        available = clear + 2
        oldCode = nullCode
        codeSize = dataSize + 1
        codeMask = (1 shl codeSize) - 1
        code = 0
        while (code < clear) {
            prefix!![code] = 0 // XXX ArrayIndexOutOfBoundsException
            suffix!![code] = code.toByte()
            code++
        }

        // Decode GIF pixel stream.
        var bi = 0
        var pi = 0
        var top = 0
        var first = 0
        var count = 0
        var bits = 0
        var datum = 0
        var i = 0
        while (i < pixelCount) {
            if (top == 0) {
                if (bits < codeSize) {
                    // Load bytes until there are enough bits for a code.
                    if (count == 0) {
                        // Read a new data block.
                        count = readBlock()
                        if (count <= 0) {
                            break
                        }
                        bi = 0
                    }
                    datum += block[bi].toInt() and 0xff shl bits
                    bits += 8
                    bi++
                    count--
                    continue
                }
                // Get the next code.
                code = datum and codeMask
                datum = datum shr codeSize
                bits -= codeSize
                // Interpret the code
                if (code > available || code == endOfInformation) {
                    break
                }
                if (code == clear) {
                    // Reset decoder.
                    codeSize = dataSize + 1
                    codeMask = (1 shl codeSize) - 1
                    available = clear + 2
                    oldCode = nullCode
                    continue
                }
                if (oldCode == nullCode) {
                    pixelStack!![top++] = suffix!![code]
                    oldCode = code
                    first = code
                    continue
                }
                inCode = code
                if (code == available) {
                    pixelStack!![top++] = first.toByte()
                    code = oldCode
                }
                while (code > clear) {
                    pixelStack!![top++] = suffix!![code]
                    code = prefix!![code].toInt()
                }
                first = suffix!![code].toInt() and 0xff
                // Add a new string to the string table,
                if (available >= MAX_STACK_SIZE) {
                    break
                }
                pixelStack!![top++] = first.toByte()
                prefix!![available] = oldCode.toShort()
                suffix!![available] = first.toByte()
                available++
                if (available and codeMask == 0 && available < MAX_STACK_SIZE) {
                    codeSize++
                    codeMask += available
                }
                oldCode = inCode
            }
            // Pop a pixel off the pixel stack.
            top--
            tempDstPixels[pi++] = pixelStack!![top]
            i++
        }
        i = pi
        while (i < pixelCount) {
            tempDstPixels[i] = 0 // clear missing pixels
            i++
        }
    }

    /**
     * Returns true if an error was encountered during reading/decoding
     */
    private fun err(): Boolean {
        return status != STATUS_OK
    }

    /**
     * Initializes or re-initializes reader
     */
    private fun init() {
        status = STATUS_OK
        frameCount = 0
        currentFrameIndex = -1
        frames = ArrayList()
        gct = null
    }

    /**
     * Reads a single byte from the input stream.
     */
    private fun read(): Int {
        var curByte = 0
        try {
            curByte = rawData!!.get().toInt() and 0xFF
        } catch (e: Exception) {
            status = STATUS_FORMAT_ERROR
        }
        return curByte
    }

    /**
     * Reads next variable length block from input.
     *
     * @return number of bytes stored in "buffer"
     */
    private fun readBlock(): Int {
        blockSize = read()
        var n = 0
        if (blockSize > 0) {
            try {
                var count: Int
                while (n < blockSize) {
                    count = blockSize - n
                    rawData!![block, n, count]
                    n += count
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error Reading Block", e)
                status = STATUS_FORMAT_ERROR
            }
        }
        return n
    }

    /**
     * Reads color table as 256 RGB integer values
     *
     * @param ncolors int number of colors to read
     * @return int array containing 256 colors (packed ARGB with full alpha)
     */
    private fun readColorTable(ncolors: Int): IntArray? {
        val nbytes = 3 * ncolors
        var tab: IntArray? = null
        val c = ByteArray(nbytes)
        try {
            rawData!![c]
            tab = IntArray(256) // max size to avoid bounds checks
            var i = 0
            var j = 0
            while (i < ncolors) {
                val r = c[j++].toInt() and 0xff
                val g = c[j++].toInt() and 0xff
                val b = c[j++].toInt() and 0xff
                tab[i++] = -0x1000000 or (r shl 16) or (g shl 8) or b
            }
        } catch (e: BufferUnderflowException) {
            Log.w(TAG, "Format Error Reading Color Table", e)
            status = STATUS_FORMAT_ERROR
        }
        return tab
    }

    /**
     * Main file parser. Reads GIF content blocks.
     */
    private fun readContents() {
        // read GIF file content blocks
        var done = false
        while (!(done || err())) {
            var code = read()
            when (code) {
                0x2C -> readBitmap()
                0x21 -> {
                    code = read()
                    when (code) {
                        0xf9 -> {
                            //Start a new frame
                            currentFrame = GifFrame()
                            readGraphicControlExt()
                        }

                        0xff -> {
                            readBlock()
                            var app = ""
                            var i = 0
                            while (i < 11) {
                                app += Char(block[i].toUShort())
                                i++
                            }
                            if (app == "NETSCAPE2.0") {
                                readNetscapeExt()
                            } else {
                                skip() // don't care
                            }
                        }

                        0xfe -> skip()
                        0x01 -> skip()
                        else -> skip()
                    }
                }

                0x3b -> done = true
                0x00 -> status = STATUS_FORMAT_ERROR
                else -> status = STATUS_FORMAT_ERROR
            }
        }
    }

    /**
     * Reads GIF file header information.
     */
    private fun readHeader() {
        var id = ""
        for (i in 0..5) {
            id += read().toChar()
        }
        if (!id.startsWith("GIF")) {
            status = STATUS_FORMAT_ERROR
            return
        }
        readLSD()
        if (gctFlag && !err()) {
            gct = readColorTable(gctSize)
            bgColor = gct!![bgIndex]
        }
    }

    /**
     * Reads Graphics Control Extension values
     */
    private fun readGraphicControlExt() {
        read() // block size
        val packed = read() // packed fields
        currentFrame!!.dispose = packed and 0x1c shr 2 // disposal method
        if (currentFrame!!.dispose == 0) {
            currentFrame!!.dispose = 1 // elect to keep old image if discretionary
        }
        currentFrame!!.transparency = packed and 1 != 0
        currentFrame!!.delay = readShort() * 10 // delay in milliseconds
        currentFrame!!.transIndex = read() // transparent color index
        read() // block terminator
    }

    /**
     * Reads next frame image
     */
    private fun readBitmap() {
        currentFrame!!.ix = readShort() // (sub)image position & size
        currentFrame!!.iy = readShort()
        currentFrame!!.iw = readShort()
        currentFrame!!.ih = readShort()
        val packed = read()
        lctFlag = packed and 0x80 != 0 // 1 - local color table flag interlace
        lctSize = 2.0.pow(((packed and 0x07) + 1).toDouble()).toInt()
        // 3 - sort flag
        // 4-5 - reserved lctSize = 2 << (packed & 7); // 6-8 - local color
        // table size
        currentFrame!!.interlace = packed and 0x40 != 0
        if (lctFlag) {
            currentFrame!!.lct = readColorTable(lctSize) // read table
        } else {
            currentFrame!!.lct = null //No local color table
        }
        currentFrame!!.bufferFrameStart =
            rawData!!.position() //Save this as the decoding position pointer
        decodeBitmapData(null, mainPixels) // false decode pixel data to advance buffer
        skip()
        if (err()) {
            return
        }
        frameCount++
        frames!!.add(currentFrame) // add image to frame
    }

    /**
     * Reads Logical Screen Descriptor
     */
    private fun readLSD() {
        // logical screen size
        width = readShort()
        height = readShort()
        // packed fields
        val packed = read()
        gctFlag = packed and 0x80 != 0 // 1 : global color table flag
        // 2-4 : color resolution
        // 5 : gct sort flag
        gctSize = 2 shl (packed and 7) // 6-8 : gct size
        bgIndex = read() // background color index
        pixelAspect = read() // pixel aspect ratio

        //Now that we know the size, init scratch arrays
        mainPixels = ByteArray(width * height)
        mainScratch = IntArray(width * height)
        copyScratch = IntArray(width * height)
        previousImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        currentImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    }

    /**
     * Reads Netscape extenstion to obtain iteration count
     */
    private fun readNetscapeExt() {
        do {
            readBlock()
            if (block[0].toInt() == 1) {
                // loop count sub-block
                val b1 = block[1].toInt() and 0xff
                val b2 = block[2].toInt() and 0xff
                loopCount = b2 shl 8 or b1
            }
        } while (blockSize > 0 && !err())
    }

    /**
     * Reads next 16-bit value, LSB first
     */
    private fun readShort(): Int {
        // read 16-bit value
        return rawData!!.getShort().toInt()
    }

    /**
     * Skips variable length blocks up to and including next zero length block.
     */
    private fun skip() {
        do {
            readBlock()
        } while (blockSize > 0 && !err())
    }

    companion object {
        private val TAG = GifDecoder::class.java.simpleName

        /**
         * File read status: No errors.
         */
        const val STATUS_OK = 0

        /**
         * File read status: Error decoding file (may be partially decoded)
         */
        const val STATUS_FORMAT_ERROR = 1

        /**
         * File read status: Unable to open source.
         */
        const val STATUS_OPEN_ERROR = 2

        /**
         * max decoder pixel stack size
         */
        private const val MAX_STACK_SIZE = 4096

        /**
         * GIF Disposal Method meaning take no action
         */
        private const val DISPOSAL_UNSPECIFIED = 0

        /**
         * GIF Disposal Method meaning leave canvas from previous frame
         */
        private const val DISPOSAL_NONE = 1

        /**
         * GIF Disposal Method meaning clear canvas to background color
         */
        private const val DISPOSAL_BACKGROUND = 2

        /**
         * GIF Disposal Method meaning clear canvas to frame before last
         */
        private const val DISPOSAL_PREVIOUS = 3
    }
}