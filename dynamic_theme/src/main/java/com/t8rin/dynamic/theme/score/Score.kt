/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.t8rin.dynamic.theme.score

import com.t8rin.dynamic.theme.hct.Cam16
import com.t8rin.dynamic.theme.utils.ColorUtils.lstarFromArgb
import com.t8rin.dynamic.theme.utils.MathUtils.differenceDegrees
import com.t8rin.dynamic.theme.utils.MathUtils.sanitizeDegreesInt
import java.util.*
import kotlin.math.roundToInt

/**
 * Given a large set of colors, remove colors that are unsuitable for a UI theme, and rank the rest
 * based on suitability.
 *
 *
 * Enables use of a high cluster count for image quantization, thus ensuring colors aren't
 * muddied, while curating the high cluster count to a much smaller number of appropriate choices.
 */
object Score {
    private const val CUTOFF_CHROMA = 15.0
    private const val CUTOFF_EXCITED_PROPORTION = 0.01
    private const val CUTOFF_TONE = 10.0
    private const val TARGET_CHROMA = 48.0
    private const val WEIGHT_PROPORTION = 0.7
    private const val WEIGHT_CHROMA_ABOVE = 0.3
    private const val WEIGHT_CHROMA_BELOW = 0.1

    /**
     * Given a map with keys of colors and values of how often the color appears, rank the colors
     * based on suitability for being used for a UI theme.
     *
     * @param colorsToPopulation map with keys of colors and values of how often the color appears,
     * usually from a source image.
     * @return Colors sorted by suitability for a UI theme. The most suitable color is the first item,
     * the least suitable is the last. There will always be at least one color returned. If all
     * the input colors were not suitable for a theme, a default fallback color will be provided,
     * Google Blue.
     */
    fun score(colorsToPopulation: Map<Int, Int>): List<Int> {
        // Determine the total count of all colors.
        var populationSum = 0.0
        for ((_, value) in colorsToPopulation) {
            populationSum += value.toDouble()
        }

        // Turn the count of each color into a proportion by dividing by the total
        // count. Also, fill a cache of CAM16 colors representing each color, and
        // record the proportion of colors for each CAM16 hue.
        val colorsToCam: MutableMap<Int, Cam16> = HashMap()
        val hueProportions = DoubleArray(361)
        for ((color, value) in colorsToPopulation) {
            val population = value.toDouble()
            val proportion = population / populationSum
            val cam = Cam16.fromInt(color)
            colorsToCam[color] = cam
            val hue = cam.hue.roundToInt().toInt()
            hueProportions[hue] += proportion
        }

        // Determine the proportion of the colors around each color, by summing the
        // proportions around each color's hue.
        val colorsToExcitedProportion: MutableMap<Int, Double> = HashMap()
        for ((color, cam) in colorsToCam) {
            val hue = cam.hue.roundToInt().toInt()
            var excitedProportion = 0.0
            for (j in hue - 15 until hue + 15) {
                val neighborHue = sanitizeDegreesInt(j)
                excitedProportion += hueProportions[neighborHue]
            }
            colorsToExcitedProportion[color] = excitedProportion
        }

        // Score the colors by their proportion, as well as how chromatic they are.
        val colorsToScore: MutableMap<Int, Double> = HashMap()
        for ((color, cam) in colorsToCam) {
            val proportion = colorsToExcitedProportion[color]!!
            val proportionScore = proportion * 100.0 * WEIGHT_PROPORTION
            val chromaWeight =
                if (cam.chroma < TARGET_CHROMA) WEIGHT_CHROMA_BELOW else WEIGHT_CHROMA_ABOVE
            val chromaScore = (cam.chroma - TARGET_CHROMA) * chromaWeight
            val score = proportionScore + chromaScore
            colorsToScore[color] = score
        }

        // Remove colors that are unsuitable, ex. very dark or unchromatic colors.
        // Also, remove colors that are very similar in hue.
        val filteredColors = filter(colorsToExcitedProportion, colorsToCam)
        val filteredColorsToScore: MutableMap<Int, Double> = HashMap()
        for (color in filteredColors) {
            filteredColorsToScore[color] = colorsToScore[color] ?: 0.0
        }

        // Ensure the list of colors returned is sorted such that the first in the
        // list is the most suitable, and the last is the least suitable.
        val entryList: List<Map.Entry<Int, Double>> =
            ArrayList<Map.Entry<Int, Double>>(filteredColorsToScore.entries)
        Collections.sort(entryList, ScoredComparator())
        val colorsByScoreDescending: MutableList<Int> = ArrayList()
        for ((color) in entryList) {
            val cam = colorsToCam[color]
            var duplicateHue = false
            for (alreadyChosenColor in colorsByScoreDescending) {
                val alreadyChosenCam = colorsToCam[alreadyChosenColor]
                if (differenceDegrees(cam!!.hue, alreadyChosenCam!!.hue) < 15) {
                    duplicateHue = true
                    break
                }
            }
            if (duplicateHue) {
                continue
            }
            colorsByScoreDescending.add(color)
        }

        // Ensure that at least one color is returned.
        if (colorsByScoreDescending.isEmpty()) {
            colorsByScoreDescending.add(-0xbd7a0c) // Google Blue
        }
        return colorsByScoreDescending
    }

    private fun filter(
        colorsToExcitedProportion: Map<Int, Double>, colorsToCam: Map<Int, Cam16>
    ): List<Int> {
        val filtered: MutableList<Int> = ArrayList()
        for ((color, cam) in colorsToCam) {
            val proportion = colorsToExcitedProportion[color]!!
            if (cam.chroma >= CUTOFF_CHROMA && lstarFromArgb(color) >= CUTOFF_TONE && proportion >= CUTOFF_EXCITED_PROPORTION) {
                filtered.add(color)
            }
        }
        return filtered
    }

    internal class ScoredComparator : Comparator<Map.Entry<Int, Double>> {
        override fun compare(
            entry1: Map.Entry<Int, Double>,
            entry2: Map.Entry<Int, Double>
        ): Int {
            return -entry1.value.compareTo(entry2.value)
        }
    }
}