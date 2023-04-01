/*
 * Copyright 2022 Google LLC
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
package com.t8rin.dynamic.theme.dynamiccolor

import com.t8rin.dynamic.theme.contrast.Contrast
import com.t8rin.dynamic.theme.contrast.Contrast.darkerUnsafe
import com.t8rin.dynamic.theme.contrast.Contrast.lighterUnsafe
import com.t8rin.dynamic.theme.contrast.Contrast.ratioOfTones
import com.t8rin.dynamic.theme.hct.Hct
import com.t8rin.dynamic.theme.palettes.TonalPalette
import com.t8rin.dynamic.theme.scheme.DynamicScheme
import com.t8rin.dynamic.theme.utils.MathUtils.clampDouble
import com.t8rin.dynamic.theme.utils.MathUtils.clampInt
import java.util.function.BiFunction
import java.util.function.Function

/**
 * A color that adjusts itself based on UI state, represented by DynamicScheme.
 *
 *
 * This color automatically adjusts to accommodate a desired contrast level, or other adjustments
 * such as differing in light mode versus dark mode, or what the theme is, or what the color that
 * produced the theme is, etc.
 *
 *
 * Colors without backgrounds do not change tone when contrast changes. Colors with backgrounds
 * become closer to their background as contrast lowers, and further when contrast increases.
 *
 *
 * Prefer the static constructors. They provide a much more simple interface, such as requiring
 * just a hexcode, or just a hexcode and a background.
 *
 *
 * Ultimately, each component necessary for calculating a color, adjusting it for a desired
 * contrast level, and ensuring it has a certain lightness/tone difference from another color, is
 * provided by a function that takes a DynamicScheme and returns a value. This ensures ultimate
 * flexibility, any desired behavior of a color for any design system, but it usually unnecessary.
 * See the default constructor for more information.
 */
// Prevent lint for Function.apply not being available on Android before API level 14 (4.0.1).
// "AndroidJdkLibsChecker" for Function, "NewApi" for Function.apply().
// A java_library Bazel rule with an Android constraint cannot skip these warnings without this
// annotation; another solution would be to create an android_library rule and supply
// AndroidManifest with an SDK set higher than 14.
class DynamicColor
/**
 * The base constructor for DynamicColor.
 *
 *
 * Functional arguments allow overriding without risks that come with subclasses. _Strongly_
 * prefer using one of the static convenience constructors. This class is arguably too flexible to
 * ensure it can support any scenario.
 *
 *
 * For example, the default behavior of adjust tone at max contrast to be at a 7.0 ratio with
 * its background is principled and matches a11y guidance. That does not mean it's the desired
 * approach for _every_ design system, and every color pairing, always, in every case.
 *
 * @param hue                 given DynamicScheme, return the hue in HCT of the output color.
 * @param chroma              given DynamicScheme, return chroma in HCT of the output color.
 * @param tone                given DynamicScheme, return tone in HCT of the output color.
 * @param background          given DynamicScheme, return the DynamicColor that is the background of this
 * DynamicColor. When this is provided, automated adjustments to lower and raise contrast are
 * made.
 * @param toneMinContrast     given DynamicScheme, return tone in HCT/L* in L*a*b* this color should
 * be at minimum contrast. See toneMinContrastDefault for the default behavior, and strongly
 * consider using it unless you have strong opinions on a11y. The static constructors use it.
 * @param toneMaxContrast     given DynamicScheme, return tone in HCT/L* in L*a*b* this color should
 * be at maximum contrast. See toneMaxContrastDefault for the default behavior, and strongly
 * consider using it unless you have strong opinions on a11y. The static constructors use it.
 * @param toneDeltaConstraint given DynamicScheme, return a ToneDeltaConstraint instance that
 * describes a requirement that this DynamicColor must always have some difference in tone/L*
 * from another DynamicColor.<br></br>
 * Unlikely to be useful unless a design system has some distortions where colors that don't
 * have a background/foreground relationship must have _some_ difference in tone, yet, not
 * enough difference to create meaningful contrast.
 */(
    val hue: Function<DynamicScheme, Double>,
    val chroma: Function<DynamicScheme, Double>,
    val tone: Function<DynamicScheme, Double>,
    val opacity: Function<DynamicScheme, Double>?,
    val background: Function<DynamicScheme, DynamicColor?>?,
    val toneMinContrast: Function<DynamicScheme, Double>,
    val toneMaxContrast: Function<DynamicScheme, Double>,
    val toneDeltaConstraint: Function<DynamicScheme, ToneDeltaConstraint>?
) {
    private val hctCache = HashMap<DynamicScheme, Hct>()
    fun getArgb(scheme: DynamicScheme): Int {
        val argb = getHct(scheme).toInt()
        if (opacity == null) {
            return argb
        }
        val percentage = opacity.apply(scheme)
        val alpha = clampInt(0, 255, Math.round(percentage * 255).toInt())
        return argb and 0x00ffffff or (alpha shl 24)
    }

    fun getHct(scheme: DynamicScheme): Hct {
        val cachedAnswer = hctCache[scheme]
        if (cachedAnswer != null) {
            return cachedAnswer
        }
        // This is crucial for aesthetics: we aren't simply the taking the standard color
        // and changing its tone for contrast. Rather, we find the tone for contrast, then
        // use the specified chroma from the palette to construct a new color.
        //
        // For example, this enables colors with standard tone of T90, which has limited chroma, to
        // "recover" intended chroma as contrast increases.
        val answer = Hct.from(hue.apply(scheme), chroma.apply(scheme), getTone(scheme))
        // NOMUTANTS--trivial test with onerous dependency injection requirement.
        if (hctCache.size > 4) {
            hctCache.clear()
        }
        // NOMUTANTS--trivial test with onerous dependency injection requirement.
        hctCache[scheme] = answer
        return answer
    }

    /**
     * Returns the tone in HCT, ranging from 0 to 100, of the resolved color given scheme.
     */
    fun getTone(scheme: DynamicScheme): Double {
        var answer = tone.apply(scheme)
        val decreasingContrast = scheme.contrastLevel < 0.0
        if (scheme.contrastLevel != 0.0) {
            val startTone = tone.apply(scheme)
            val endTone =
                if (decreasingContrast) toneMinContrast.apply(scheme) else toneMaxContrast.apply(
                    scheme
                )
            val delta = (endTone - startTone) * Math.abs(scheme.contrastLevel)
            answer = delta + startTone
        }
        val bgDynamicColor = background?.apply(scheme)
        var minRatio = Contrast.RATIO_MIN
        var maxRatio = Contrast.RATIO_MAX
        if (bgDynamicColor != null) {
            val bgHasBg =
                bgDynamicColor.background != null && bgDynamicColor.background.apply(scheme) == null
            val standardRatio = ratioOfTones(
                tone.apply(scheme), bgDynamicColor.tone.apply(scheme)
            )
            if (decreasingContrast) {
                val minContrastRatio = ratioOfTones(
                    toneMinContrast.apply(scheme), bgDynamicColor.toneMinContrast.apply(scheme)
                )
                minRatio = if (bgHasBg) 1.0 else minContrastRatio
                maxRatio = standardRatio
            } else {
                val maxContrastRatio = ratioOfTones(
                    toneMaxContrast.apply(scheme), bgDynamicColor.toneMaxContrast.apply(scheme)
                )
                minRatio = if (!bgHasBg) 1.0 else Math.min(maxContrastRatio, standardRatio)
                maxRatio = if (!bgHasBg) 21.0 else Math.max(maxContrastRatio, standardRatio)
            }
        }
        val finalMinRatio = minRatio
        val finalMaxRatio = maxRatio
        val finalAnswer = answer
        answer = calculateDynamicTone(
            scheme,
            tone,
            { dynamicColor: DynamicColor -> dynamicColor.getTone(scheme) },
            { a: Double?, b: Double? -> finalAnswer },
            { s: DynamicScheme? -> bgDynamicColor },
            toneDeltaConstraint,
            { s: Double? -> finalMinRatio }
        ) { s: Double? -> finalMaxRatio }
        return answer
    }

    companion object {
        /**
         * Create a DynamicColor from a hex code.
         *
         *
         * Result has no background; thus no support for increasing/decreasing contrast for a11y.
         */
        fun fromArgb(argb: Int): DynamicColor {
            val hct = Hct.fromInt(argb)
            val palette = TonalPalette.fromInt(argb)
            return fromPalette({ s: DynamicScheme? -> palette }, { s: DynamicScheme? -> hct.tone })
        }

        /**
         * Create a DynamicColor from just a hex code.
         *
         *
         * Result has no background; thus cannot support increasing/decreasing contrast for a11y.
         *
         * @param argb A hex code.
         * @param tone Function that provides a tone given DynamicScheme. Useful for adjusting for dark
         * vs. light mode.
         */
        fun fromArgb(argb: Int, tone: Function<DynamicScheme, Double>): DynamicColor {
            return fromPalette({ s: DynamicScheme? -> TonalPalette.fromInt(argb) }, tone)
        }

        /**
         * Create a DynamicColor.
         *
         *
         * If you don't understand HCT fully, or your design team doesn't, but wants support for
         * automated contrast adjustment, this method is _extremely_ useful: you can take a standard
         * design system expressed as hex codes, create DynamicColors corresponding to each color, and
         * then wire up backgrounds.
         *
         *
         * If the design system uses the same hex code on multiple backgrounds, define that in multiple
         * DynamicColors so that the background is accurate for each one. If you define a DynamicColor
         * with one background, and actually use it on another, DynamicColor can't guarantee contrast. For
         * example, if you use a color on both black and white, increasing the contrast on one necessarily
         * decreases contrast of the other.
         *
         * @param argb       A hex code.
         * @param tone       Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         * @param background Function that provides background DynamicColor given DynamicScheme. Useful
         * for contrast, given a background, colors can adjust to increase/decrease contrast.
         */
        fun fromArgb(
            argb: Int,
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor?>?
        ): DynamicColor {
            return fromPalette(
                { s: DynamicScheme? -> TonalPalette.fromInt(argb) },
                tone,
                background
            )
        }

        /**
         * Create a DynamicColor from:
         *
         * @param argb                A hex code.
         * @param tone                Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         * @param background          Function that provides background DynamicColor given DynamicScheme. Useful
         * for contrast, given a background, colors can adjust to increase/decrease contrast.
         * @param toneDeltaConstraint Function that provides a ToneDeltaConstraint given DynamicScheme.
         * Useful for ensuring lightness difference between colors that don't _require_ contrast or
         * have a formal background/foreground relationship.
         */
        fun fromArgb(
            argb: Int,
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor?>?,
            toneDeltaConstraint: Function<DynamicScheme, ToneDeltaConstraint>?
        ): DynamicColor {
            return fromPalette(
                { s: DynamicScheme? -> TonalPalette.fromInt(argb) },
                tone,
                background,
                toneDeltaConstraint
            )
        }
        /**
         * Create a DynamicColor.
         *
         * @param palette             Function that provides a TonalPalette given DynamicScheme. A TonalPalette is
         * defined by a hue and chroma, so this replaces the need to specify hue/chroma. By providing
         * a tonal palette, when contrast adjustments are made, intended chroma can be preserved. For
         * example, at T/L* 90, there is a significant limit to the amount of chroma. There is no
         * colorful red, a red that light is pink. By preserving the _intended_ chroma if lightness
         * lowers for contrast adjustments, the intended chroma is restored.
         * @param tone                Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         * @param background          Function that provides background DynamicColor given DynamicScheme. Useful
         * for contrast, given a background, colors can adjust to increase/decrease contrast.
         * @param toneDeltaConstraint Function that provides a ToneDeltaConstraint given DynamicScheme.
         * Useful for ensuring lightness difference between colors that don't _require_ contrast or
         * have a formal background/foreground relationship.
         */
        /**
         * Create a DynamicColor.
         *
         * @param palette Function that provides a TonalPalette given DynamicScheme. A TonalPalette is
         * defined by a hue and chroma, so this replaces the need to specify hue/chroma. By providing
         * a tonal palette, when contrast adjustments are made, intended chroma can be preserved. For
         * example, at T/L* 90, there is a significant limit to the amount of chroma. There is no
         * colorful red, a red that light is pink. By preserving the _intended_ chroma if lightness
         * lowers for contrast adjustments, the intended chroma is restored.
         * @param tone    Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         */
        /**
         * Create a DynamicColor.
         *
         * @param palette    Function that provides a TonalPalette given DynamicScheme. A TonalPalette is
         * defined by a hue and chroma, so this replaces the need to specify hue/chroma. By providing
         * a tonal palette, when contrast adjustments are made, intended chroma can be preserved. For
         * example, at T/L* 90, there is a significant limit to the amount of chroma. There is no
         * colorful red, a red that light is pink. By preserving the _intended_ chroma if lightness
         * lowers for contrast adjustments, the intended chroma is restored.
         * @param tone       Function that provides a tone given DynamicScheme. (useful for dark vs. light mode)
         * @param background Function that provides background DynamicColor given DynamicScheme. Useful
         * for contrast, given a background, colors can adjust to increase/decrease contrast.
         */
        @JvmOverloads
        fun fromPalette(
            palette: Function<DynamicScheme?, TonalPalette>,
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor?>? = null,
            toneDeltaConstraint: Function<DynamicScheme, ToneDeltaConstraint>? = null
        ): DynamicColor {
            return DynamicColor(
                { scheme: DynamicScheme? -> palette.apply(scheme).hue },
                { scheme: DynamicScheme? -> palette.apply(scheme).chroma },
                tone,
                null,
                background,
                { scheme: DynamicScheme ->
                    toneMinContrastDefault(
                        tone,
                        background,
                        scheme,
                        toneDeltaConstraint
                    )
                },
                { scheme: DynamicScheme ->
                    toneMaxContrastDefault(
                        tone,
                        background,
                        scheme,
                        toneDeltaConstraint
                    )
                },
                toneDeltaConstraint
            )
        }

        /**
         * The default algorithm for calculating the tone of a color at minimum contrast.<br></br>
         * If the original contrast ratio was >= 7.0, reach contrast 4.5.<br></br>
         * If the original contrast ratio was >= 3.0, reach contrast 3.0.<br></br>
         * If the original contrast ratio was < 3.0, reach that ratio.
         */
        fun toneMinContrastDefault(
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor?>?,
            scheme: DynamicScheme,
            toneDeltaConstraint: Function<DynamicScheme, ToneDeltaConstraint>?
        ): Double {
            return calculateDynamicTone(
                scheme,
                tone,
                { c: DynamicColor -> c.toneMinContrast.apply(scheme) },
                { stdRatio: Double, bgTone: Double ->
                    var answer = tone.apply(scheme)
                    if (stdRatio >= Contrast.RATIO_70) {
                        answer = contrastingTone(bgTone, Contrast.RATIO_45)
                    } else if (stdRatio >= Contrast.RATIO_30) {
                        answer = contrastingTone(bgTone, Contrast.RATIO_30)
                    } else {
                        val backgroundHasBackground =
                            background != null && background.apply(scheme) != null && background.apply(
                                scheme
                            )!!.background != null && background.apply(scheme)!!.background!!.apply(
                                scheme
                            ) != null
                        if (backgroundHasBackground) {
                            answer = contrastingTone(bgTone, stdRatio)
                        }
                    }
                    answer
                },
                background,
                toneDeltaConstraint,
                null
            ) { standardRatio: Double? -> standardRatio }
        }

        /**
         * The default algorithm for calculating the tone of a color at maximum contrast.<br></br>
         * If the color's background has a background, reach contrast 7.0.<br></br>
         * If it doesn't, maintain the original contrast ratio.<br></br>
         *
         *
         * This ensures text on surfaces maintains its original, often detrimentally excessive,
         * contrast ratio. But, text on buttons can soften to not have excessive contrast.
         *
         *
         * Historically, digital design uses pure whites and black for text and surfaces. It's too much
         * of a jump at this point in history to introduce a dynamic contrast system _and_ insist that
         * text always had excessive contrast and should reach 7.0, it would deterimentally affect desire
         * to understand and use dynamic contrast.
         */
        fun toneMaxContrastDefault(
            tone: Function<DynamicScheme, Double>,
            background: Function<DynamicScheme, DynamicColor?>?,
            scheme: DynamicScheme,
            toneDeltaConstraint: Function<DynamicScheme, ToneDeltaConstraint>?
        ): Double {
            return calculateDynamicTone(
                scheme,
                tone,
                { c: DynamicColor -> c.toneMaxContrast.apply(scheme) },
                { stdRatio: Double?, bgTone: Double ->
                    val backgroundHasBackground =
                        background != null && background.apply(scheme) != null && background.apply(
                            scheme
                        )!!.background != null && background.apply(scheme)!!.background!!.apply(
                            scheme
                        ) != null
                    if (backgroundHasBackground) {
                        return@calculateDynamicTone contrastingTone(bgTone, Contrast.RATIO_70)
                    } else {
                        return@calculateDynamicTone contrastingTone(
                            bgTone,
                            Math.max(Contrast.RATIO_70, stdRatio!!)
                        )
                    }
                },
                background,
                toneDeltaConstraint,
                null,
                null
            )
        }

        /**
         * Core method for calculating a tone for under dynamic contrast.
         *
         *
         * It enforces important properties:<br></br>
         * #1. Desired contrast ratio is reached.<br></br>
         * As contrast increases from standard to max, the tones involved should always be at least the
         * standard ratio. For example, if a button is T90, and button text is T0, and the button is T0 at
         * max contrast, the button text cannot simply linearly interpolate from T0 to T100, or at some
         * point they'll both be at the same tone.
         *
         *
         * #2. Enable light foregrounds on midtones.<br></br>
         * The eye prefers light foregrounds on T50 to T60, possibly up to T70, but, contrast ratio 4.5
         * can't be reached with T100 unless the foreground is T50. Contrast ratio 4.5 is crucial, it
         * represents 'readable text', i.e. text smaller than ~40 dp / 1/4". So, if a tone is between T50
         * and T60, it is proactively changed to T49 to enable light foregrounds.
         *
         *
         * #3. Ensure tone delta with another color.<br></br>
         * In design systems, there may be colors that don't have a pure background/foreground
         * relationship, but, do require different tones for visual differentiation. ToneDeltaConstraint
         * models this requirement, and DynamicColor enforces it.
         */
        fun calculateDynamicTone(
            scheme: DynamicScheme,
            toneStandard: Function<DynamicScheme, Double>,
            toneToJudge: Function<DynamicColor, Double>,
            desiredTone: BiFunction<Double, Double, Double>,
            background: Function<DynamicScheme, DynamicColor?>?,
            toneDeltaConstraint: Function<DynamicScheme, ToneDeltaConstraint>?,
            minRatio: Function<Double?, Double?>?,
            maxRatio: Function<Double?, Double?>?
        ): Double {
            // Start with the tone with no adjustment for contrast.
            // If there is no background, don't perform any adjustment, return immediately.
            val toneStd = toneStandard.apply(scheme)
            var answer = toneStd
            val bgDynamic = background?.apply(scheme) ?: return answer
            val bgToneStd = bgDynamic.tone.apply(scheme)
            val stdRatio = ratioOfTones(toneStd, bgToneStd)

            // If there is a background, determine its tone after contrast adjustment.
            // Then, calculate the foreground tone that ensures the caller's desired contrast ratio is met.
            val bgTone = toneToJudge.apply(bgDynamic)
            val myDesiredTone = desiredTone.apply(stdRatio, bgTone)
            val currentRatio = ratioOfTones(bgTone, myDesiredTone)
            val minRatioRealized =
                if (minRatio == null) Contrast.RATIO_MIN else (if (minRatio.apply(stdRatio) == null) Contrast.RATIO_MIN else minRatio.apply(
                    stdRatio
                ))!!
            val maxRatioRealized =
                if (maxRatio == null) Contrast.RATIO_MAX else (if (maxRatio.apply(stdRatio) == null) Contrast.RATIO_MAX else maxRatio.apply(
                    stdRatio
                ))!!
            val desiredRatio = clampDouble(minRatioRealized, maxRatioRealized, currentRatio)
            answer = if (desiredRatio == currentRatio) {
                myDesiredTone
            } else {
                contrastingTone(bgTone, desiredRatio)
            }

            // If the background has no background,  adjust the foreground tone to ensure that
            // it is dark enough to have a light foreground.
            if (bgDynamic.background == null || bgDynamic.background.apply(scheme) == null) {
                answer = enableLightForeground(answer)
            }

            // If the caller has specified a constraint where it must have a certain  tone distance from
            // another color, enforce that constraint.
            answer = ensureToneDelta(answer, toneStd, scheme, toneDeltaConstraint, toneToJudge)
            return answer
        }

        fun ensureToneDelta(
            tone: Double,
            toneStandard: Double,
            scheme: DynamicScheme,
            toneDeltaConstraint: Function<DynamicScheme, ToneDeltaConstraint>?,
            toneToDistanceFrom: Function<DynamicColor, Double>
        ): Double {
            val constraint = toneDeltaConstraint?.apply(scheme)
                ?: return tone
            val requiredDelta = constraint.delta
            val keepAwayTone = toneToDistanceFrom.apply(constraint.keepAway)
            val delta = Math.abs(tone - keepAwayTone)
            return if (delta >= requiredDelta) {
                tone
            } else when (constraint.keepAwayPolarity) {
                TonePolarity.DARKER -> clampDouble(
                    0.0,
                    100.0,
                    keepAwayTone + requiredDelta
                )
                TonePolarity.LIGHTER -> clampDouble(
                    0.0,
                    100.0,
                    keepAwayTone - requiredDelta
                )
                TonePolarity.NO_PREFERENCE -> {
                    val keepAwayToneStandard = constraint.keepAway.tone.apply(scheme)
                    val preferLighten = toneStandard > keepAwayToneStandard
                    val alterAmount = Math.abs(delta - requiredDelta)
                    val lighten =
                        if (preferLighten) tone + alterAmount <= 100.0 else tone < alterAmount
                    if (lighten) tone + alterAmount else tone - alterAmount
                }
            }
            return tone
        }

        /**
         * Given a background tone, find a foreground tone, while ensuring they reach a contrast ratio
         * that is as close to ratio as possible.
         */
        fun contrastingTone(bgTone: Double, ratio: Double): Double {
            val lighterTone = lighterUnsafe(bgTone, ratio)
            val darkerTone = darkerUnsafe(bgTone, ratio)
            val lighterRatio = ratioOfTones(lighterTone, bgTone)
            val darkerRatio = ratioOfTones(darkerTone, bgTone)
            val preferLighter = tonePrefersLightForeground(bgTone)
            return if (preferLighter) {
                // "Neglible difference" handles an edge case where the initial contrast ratio is high
                // (ex. 13.0), and the ratio passed to the function is that high ratio, and both the lighter
                // and darker ratio fails to pass that ratio.
                //
                // This was observed with Tonal Spot's On Primary Container turning black momentarily between
                // high and max contrast in light mode. PC's standard tone was T90, OPC's was T10, it was
                // light mode, and the contrast level was 0.6568521221032331.
                val negligibleDifference =
                    Math.abs(lighterRatio - darkerRatio) < 0.1 && lighterRatio < ratio && darkerRatio < ratio
                if (lighterRatio >= ratio || lighterRatio >= darkerRatio || negligibleDifference) {
                    lighterTone
                } else {
                    darkerTone
                }
            } else {
                if (darkerRatio >= ratio || darkerRatio >= lighterRatio) darkerTone else lighterTone
            }
        }

        /**
         * Adjust a tone down such that white has 4.5 contrast, if the tone is reasonably close to
         * supporting it.
         */
        fun enableLightForeground(tone: Double): Double {
            return if (tonePrefersLightForeground(tone) && !toneAllowsLightForeground(
                    tone
                )
            ) {
                49.0
            } else tone
        }

        /**
         * People prefer white foregrounds on ~T60-70. Observed over time, and also by Andrew Somers
         * during research for APCA.
         *
         *
         * T60 used as to create the smallest discontinuity possible when skipping down to T49 in order
         * to ensure light foregrounds.
         */
        fun tonePrefersLightForeground(tone: Double): Boolean {
            return Math.round(tone) <= 60
        }

        /**
         * Tones less than ~T50 always permit white at 4.5 contrast.
         */
        fun toneAllowsLightForeground(tone: Double): Boolean {
            return Math.round(tone) <= 49
        }
    }
}