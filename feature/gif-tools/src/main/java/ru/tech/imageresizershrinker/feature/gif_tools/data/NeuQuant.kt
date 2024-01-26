@file:Suppress("MemberVisibilityCanBePrivate", "KotlinConstantConditions")

package ru.tech.imageresizershrinker.feature.gif_tools.data


/*
 * NeuQuant Neural-Net Quantization Algorithm
 * ------------------------------------------
 *
 * Copyright (c) 1994 Anthony Dekker
 *
 * NEUQUANT Neural-Net quantization algorithm by Anthony Dekker, 1994. See
 * "Kohonen neural networks for optimal colour quantization" in "Network:
 * Computation in Neural Systems" Vol. 5 (1994) pp 351-367. for a discussion of
 * the algorithm.
 *
 * Any party obtaining a copy of these files from the author, directly or
 * indirectly, is granted, free of charge, a full and unrestricted irrevocable,
 * world-wide, paid up, royalty-free, nonexclusive right and license to deal in
 * this software and documentation files (the "Software"), including without
 * limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons who
 * receive copies from any such party to do so, with the only requirement being
 * that this copyright notice remain intact.
 */

//	 Ported to Java 12/00 K Weiner

/* radpower for precomputation */

/*
* Initialise network in range (0,0,0) to (255,255,255) and set parameters
* -----------------------------------------------------------------------
*/
internal class NeuQuant(
    private var thepicture: ByteArray,
    private var lengthcount: Int,
    private var samplefac: Int
) {
    /* number of colours used */
    private val netsize: Int = 256

    /* four primes near 500 - assume no image has a length so large */
    /* that it is divisible by all four primes */
    private val prime1: Int = 499

    private val prime2: Int = 491

    private val prime3: Int = 487

    private val prime4: Int = 503

    private val minpicturebytes: Int = (3 * prime4)

    /* minimum size for input image */

    /*
* Network Definitions -------------------
*/

    private val maxnetpos: Int = (netsize - 1)

    private val netbiasshift: Int = 4 /* bias for colour values */

    private val ncycles: Int = 100 /* no. of learning cycles */

    /* defs for freq and bias */
    private val intbiasshift: Int = 16 /* bias for fractions */

    private val intbias: Int = 1 shl intbiasshift

    private val gammashift: Int = 10 /* gamma = 1024 */

    private val betashift: Int = 10

    private val beta: Int = (intbias shr betashift) /* beta = 1/1024 */

    private val betagamma: Int = (intbias shl (gammashift - betashift))

    /* defs for decreasing radius factor */
    /*
    * for 256 cols, radius
    * starts
    */
    private val initrad: Int = (netsize shr 3)

    private val radiusbiasshift: Int = 6 /* at 32.0 biased by 6 bits */

    private val radiusbias: Int = 1 shl radiusbiasshift

    /*
    * and
    * decreases
    * by a
    */
    private val initradius: Int = (initrad * radiusbias)

    private val radiusdec: Int = 30 /* factor of 1/30 each cycle */

    /* defs for decreasing alpha factor */
    private val alphabiasshift: Int = 10 /* alpha starts at 1.0 */

    private val initalpha: Int = 1 shl alphabiasshift

    /* radbias and alpharadbias used for radpower calculation */
    private val radbiasshift: Int = 8

    private val radbias: Int = 1 shl radbiasshift

    private val alpharadbshift: Int = (alphabiasshift + radbiasshift)

    private val alpharadbias: Int = 1 shl alpharadbshift

    private var alphadec: Int = 0 /* biased by 10 bits */

    /* the network itself - [netsize][4] */
    private var network: Array<IntArray>

    private var netindex: IntArray = IntArray(256)

    /* for network lookup - really 256 */
    private var bias: IntArray = IntArray(netsize)

    /* bias and freq arrays for learning */
    private var freq: IntArray = IntArray(netsize)

    private var radpower: IntArray = IntArray(initrad)

    init {
        network = Array(netsize) { i ->
            val p = IntArray(4)
            val temp = (i shl (netbiasshift + 8)) / netsize
            p[0] = temp
            p[1] = temp
            p[2] = temp
            freq[i] = intbias / netsize /* 1/netsize */
            bias[i] = 0
            p
        }
    }

    fun colorMap(): ByteArray {
        val map = ByteArray(3 * netsize)
        val index = IntArray(netsize)
        for (i in 0..<netsize) {
            index[network[i][3]] = i
        }
        var k = 0
        for (i in 0..<netsize) {
            val j = index[i]
            map[k++] = network[j][0].toByte()
            map[k++] = network[j][1].toByte()
            map[k++] = network[j][2].toByte()
        }
        return map
    }

    /*
     * Insertion sort of network and building of netindex[0..255] (to do after
     * unbias)
     * -------------------------------------------------------------------------------
     */
    fun inxbuild() {

        var i: Int
        var j: Int
        var smallpos: Int
        var smallval: Int
        var p: IntArray
        var q: IntArray
        var previouscol: Int
        var startpos: Int

        previouscol = 0
        startpos = 0
        run {
            i = 0
            while (i < netsize) {
                p = network[i]
                smallpos = i
                smallval = p[1] /* index on g */
                /* find smallest in i..netsize-1 */
                run {
                    j = i + 1
                    while (j < netsize) {
                        q = network[j]
                        if (q[1] < smallval) {
                            /* index on g */
                            smallpos = j
                            smallval = q[1] /* index on g */
                        }
                        j++
                    }
                }
                q = network[smallpos]
                /* swap p (i) and q (smallpos) entries */
                if (i != smallpos) {
                    j = q[0]
                    q[0] = p[0]
                    p[0] = j
                    j = q[1]
                    q[1] = p[1]
                    p[1] = j
                    j = q[2]
                    q[2] = p[2]
                    p[2] = j
                    j = q[3]
                    q[3] = p[3]
                    p[3] = j
                }
                /* smallval entry is now in position i */
                if (smallval != previouscol) {
                    netindex[previouscol] = (startpos + i) shr 1
                    run {
                        j = previouscol + 1
                        while (j < smallval) {
                            netindex[j] = i
                            j++
                        }
                    }
                    previouscol = smallval
                    startpos = i
                }
                i++
            }
        }
        netindex[previouscol] = (startpos + maxnetpos) shr 1
        run {
            j = previouscol + 1
            while (j < 256) {
                netindex[j] = maxnetpos
                j++
            }
        } /* really 256 */
    }

    /*
     * Main Learning Loop ------------------
     */
    fun learn() {

        var j: Int
        var b: Int
        var g: Int
        var r: Int
        var rad: Int
        var delta: Int

        if (lengthcount < minpicturebytes)
            samplefac = 1
        alphadec = 30 + ((samplefac - 1) / 3)
        var pix = 0
        val samplePixels: Int = lengthcount / (3 * samplefac)
        delta = samplePixels / ncycles
        var alpha: Int = initalpha
        var radius: Int = initradius

        rad = radius shr radiusbiasshift
        if (rad <= 1) rad = 0

        run {
            val rad2 = rad * rad
            for (index in 0..<rad) {
                radpower[index] = alpha * (((rad2 - index * index) * radbias) / rad2)
            }
        }

        val step: Int = if (lengthcount < minpicturebytes)
            3
        else if ((lengthcount % prime1) != 0)
            3 * prime1
        else {
            if ((lengthcount % prime2) != 0)
                3 * prime2
            else {
                if ((lengthcount % prime3) != 0)
                    3 * prime3
                else
                    3 * prime4
            }
        }

        var i = 0
        while (i < samplePixels) {
            b = (thepicture[pix + 0].toInt() and 255) shl netbiasshift
            g = (thepicture[pix + 1].toInt() and 255) shl netbiasshift
            r = (thepicture[pix + 2].toInt() and 255) shl netbiasshift
            j = contest(b, g, r)

            altersingle(alpha, j, b, g, r)
            if (rad != 0)
                alterneigh(rad, j, b, g, r) /* alter neighbours */

            pix += step
            if (pix >= lengthcount)
                pix -= lengthcount

            i++
            if (delta == 0)
                delta = 1
            if (i % delta == 0) {
                alpha -= alpha / alphadec
                radius -= radius / radiusdec
                rad = radius shr radiusbiasshift
                if (rad <= 1)
                    rad = 0
                run {
                    val rad2 = rad * rad
                    for (index in 0..<rad) {
                        radpower[index] = alpha * (((rad2 - index * index) * radbias) / rad2)
                    }
                }
            }
        }
    }

    /*
     * Search for BGR values 0..255 (after net is unbiased) and return colour
     * index
     * ----------------------------------------------------------------------------
     */
    fun map(b: Int, g: Int, r: Int): Int {

        var i: Int
        var j: Int
        var dist: Int
        var a: Int
        var bestd: Int
        var p: IntArray
        var best: Int

        bestd = 1000 /* biggest possible dist is 256*3 */
        best = -1
        i = netindex[g] /* index on g */
        j = i - 1 /* start at netindex[g] and work outwards */

        while ((i < netsize) || (j >= 0)) {
            if (i < netsize) {
                p = network[i]
                dist = p[1] - g /* inx key */
                if (dist >= bestd)
                    i = netsize /* stop iter */
                else {
                    i++
                    if (dist < 0)
                        dist = -dist
                    a = p[0] - b
                    if (a < 0)
                        a = -a
                    dist += a
                    if (dist < bestd) {
                        a = p[2] - r
                        if (a < 0)
                            a = -a
                        dist += a
                        if (dist < bestd) {
                            bestd = dist
                            best = p[3]
                        }
                    }
                }
            }
            if (j >= 0) {
                p = network[j]
                dist = g - p[1] /* inx key - reverse dif */
                if (dist >= bestd)
                    j = -1 /* stop iter */
                else {
                    j--
                    if (dist < 0)
                        dist = -dist
                    a = p[0] - b
                    if (a < 0)
                        a = -a
                    dist += a
                    if (dist < bestd) {
                        a = p[2] - r
                        if (a < 0)
                            a = -a
                        dist += a
                        if (dist < bestd) {
                            bestd = dist
                            best = p[3]
                        }
                    }
                }
            }
        }
        return (best)
    }

    fun process(): ByteArray {
        learn()
        unbiasnet()
        inxbuild()
        return colorMap()
    }

    /*
     * Unbias network to give byte values 0..255 and record position i to prepare
     * for sort
     * -----------------------------------------------------------------------------------
     */
    fun unbiasnet() {
        for (i in 0..<netsize) {
            network[i][0] = network[i][0] shr netbiasshift
            network[i][1] = network[i][1] shr netbiasshift
            network[i][2] = network[i][2] shr netbiasshift
            network[i][3] = i /* record colour no */
        }
    }

    /*
     * Move adjacent neurons by precomputed alpha*(1-((i-j)^2/[r]^2)) in
     * radpower[|i-j|]
     * ---------------------------------------------------------------------------------
     */
    private fun alterneigh(rad: Int, i: Int, b: Int, g: Int, r: Int) {

        var lo: Int
        var hi: Int
        var a: Int
        var p: IntArray

        lo = i - rad
        if (lo < -1)
            lo = -1
        hi = i + rad
        if (hi > netsize)
            hi = netsize

        var j: Int = i + 1
        var k: Int = i - 1
        var m = 1
        while ((j < hi) || (k > lo)) {
            a = radpower[m++]
            if (j < hi) {
                p = network[j++]
                try {
                    p[0] -= (a * (p[0] - b)) / alpharadbias
                    p[1] -= (a * (p[1] - g)) / alpharadbias
                    p[2] -= (a * (p[2] - r)) / alpharadbias
                } catch (_: Exception) {
                }
                // prevents 1.3 miscompilation
            }
            if (k > lo) {
                p = network[k--]
                try {
                    p[0] -= (a * (p[0] - b)) / alpharadbias
                    p[1] -= (a * (p[1] - g)) / alpharadbias
                    p[2] -= (a * (p[2] - r)) / alpharadbias
                } catch (_: Exception) {
                }
            }
        }
    }

    /*
     * Move neuron i towards biased (b,g,r) by factor alpha
     * ----------------------------------------------------
     */
    private fun altersingle(alpha: Int, i: Int, b: Int, g: Int, r: Int) {
        /* alter hit neuron */
        val n = network[i]
        n[0] = n[0] - (alpha * (n[0] - b)) / initalpha
        n[1] = n[1] - (alpha * (n[1] - g)) / initalpha
        n[2] = n[2] - (alpha * (n[2] - r)) / initalpha
    }

    /*
     * Search for biased BGR values ----------------------------
     */
    private fun contest(b: Int, g: Int, r: Int): Int {

        /* finds closest neuron (min dist) and updates freq */
        /* finds best neuron (min dist-bias) and returns position */
        /* for frequently chosen neurons, freq[i] is high and bias[i] is negative */
        /* bias[i] = gamma*((1/netsize)-freq[i]) */

        var dist: Int
        var a: Int
        var biasdist: Int
        var betafreq: Int
        var bestpos: Int
        var bestbiaspos: Int
        var bestd: Int
        var bestbiasd: Int
        var n: IntArray

        bestd = (1 shl 31).inv()
        bestbiasd = bestd
        bestpos = -1
        bestbiaspos = bestpos

        for (i in 0..<netsize) {
            n = network[i]
            dist = n[0] - b
            if (dist < 0)
                dist = -dist
            a = n[1] - g
            if (a < 0)
                a = -a
            dist += a
            a = n[2] - r
            if (a < 0)
                a = -a
            dist += a
            if (dist < bestd) {
                bestd = dist
                bestpos = i
            }
            biasdist = dist - ((bias[i]) shr (intbiasshift - netbiasshift))
            if (biasdist < bestbiasd) {
                bestbiasd = biasdist
                bestbiaspos = i
            }
            betafreq = (freq[i] shr betashift)
            freq[i] -= betafreq
            bias[i] += (betafreq shl gammashift)
        }

        freq[bestpos] += beta
        bias[bestpos] -= betagamma
        return (bestbiaspos)
    }
}
