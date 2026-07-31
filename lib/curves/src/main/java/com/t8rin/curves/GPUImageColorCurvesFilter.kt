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

package com.t8rin.curves

import android.opengl.GLES20
import com.t8rin.curves.view.PhotoFilterCurvesControl.CurvesToolValue
import com.t8rin.curves.view.PhotoFilterCurvesControl.CurvesValue
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToneCurveFilter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.roundToInt

internal fun buildCurvesFilter(value: CurvesToolValue): GPUImageFilter {
    val filters = mutableListOf<GPUImageFilter>()

    if (value.curvesFor(ImageCurvesEditorType.RGB).any { !it.isDefault }) {
        filters += GPUImageToneCurveFilter().apply {
            setAllControlPoints(
                value.curvesFor(ImageCurvesEditorType.RGB).map { curve ->
                    curve.points.map { point ->
                        android.graphics.PointF(point.x, point.y)
                    }.toTypedArray()
                }
            )
        }
    }

    ImageCurvesEditorType.entries
        .filterNot { it == ImageCurvesEditorType.RGB || it.centeredCurve }
        .filter { type -> value.curvesFor(type).any { !it.isDefault } }
        .forEach { type ->
            filters += GPUImageColorCurvesFilter(
                type = type,
                curves = value.curvesFor(type)
            )
        }

    val relationFilters = ImageCurvesEditorType.entries
        .filter { it.centeredCurve }
        .filter { type -> value.curvesFor(type).any { !it.isDefault } }
        .map { type ->
            GPUImageRelationCurveFilter(
                type = type,
                curve = value.curvesFor(type).first()
            )
        }
    if (relationFilters.isNotEmpty()) {
        filters += GPUImageFilterGroup(
            buildList {
                add(GPUImageRgbToHsvFilter())
                addAll(relationFilters)
                add(GPUImageHsvToRgbFilter())
            }
        )
    }

    if (!value.colorWheels.isDefault) {
        filters += GPUImageColorWheelsFilter(value.colorWheels)
    }

    return when (filters.size) {
        0 -> GPUImageFilter()
        1 -> filters.first()
        else -> GPUImageFilterGroup(filters)
    }
}

private class GPUImageColorWheelsFilter(
    private val value: ColorWheelsValue
) : GPUImageFilter(
    NO_FILTER_VERTEX_SHADER,
    COLOR_WHEELS_FRAGMENT_SHADER
) {
    private var shadowsLocation = 0
    private var midtonesLocation = 0
    private var highlightsLocation = 0
    private var edgesLocation = 0

    override fun onInit() {
        super.onInit()
        shadowsLocation = GLES20.glGetUniformLocation(program, "shadows")
        midtonesLocation = GLES20.glGetUniformLocation(program, "midtones")
        highlightsLocation = GLES20.glGetUniformLocation(program, "highlights")
        edgesLocation = GLES20.glGetUniformLocation(program, "edges")
    }

    override fun onDrawArraysPre() {
        super.onDrawArraysPre()
        GLES20.glUniform2f(shadowsLocation, value.shadows.x, value.shadows.y)
        GLES20.glUniform2f(midtonesLocation, value.midtones.x, value.midtones.y)
        GLES20.glUniform2f(highlightsLocation, value.highlights.x, value.highlights.y)
        GLES20.glUniform1f(edgesLocation, value.edges)
    }

    private companion object {
        const val COLOR_WHEELS_FRAGMENT_SHADER = """
            varying highp vec2 textureCoordinate;
            uniform sampler2D inputImageTexture;
            uniform highp vec2 shadows;
            uniform highp vec2 midtones;
            uniform highp vec2 highlights;
            uniform highp float edges;

            highp vec3 hsvToRgb(highp vec3 hsv) {
                highp vec3 p = abs(
                    fract(hsv.xxx + vec3(0.0, 2.0 / 3.0, 1.0 / 3.0)) * 6.0 - 3.0
                );
                return hsv.z * mix(vec3(1.0), clamp(p - 1.0, 0.0, 1.0), hsv.y);
            }

            highp vec3 srgbToLinear(highp vec3 color) {
                highp vec3 lower = color / 12.92;
                highp vec3 upper = pow(
                    (color + vec3(0.055)) / 1.055,
                    vec3(2.4)
                );
                return mix(lower, upper, step(vec3(0.04045), color));
            }

            highp vec3 linearToSrgb(highp vec3 color) {
                highp vec3 safeColor = max(color, vec3(0.0));
                highp vec3 lower = safeColor * 12.92;
                highp vec3 upper = 1.055 * pow(
                    safeColor,
                    vec3(1.0 / 2.4)
                ) - vec3(0.055);
                return mix(lower, upper, step(vec3(0.0031308), safeColor));
            }

            highp vec3 linearToOklab(highp vec3 color) {
                highp vec3 lms = vec3(
                    dot(color, vec3(0.4122214708, 0.5363325363, 0.0514459929)),
                    dot(color, vec3(0.2119034982, 0.6806995451, 0.1073969566)),
                    dot(color, vec3(0.0883024619, 0.2817188376, 0.6299787005))
                );
                lms = sign(lms) * pow(abs(lms), vec3(1.0 / 3.0));
                return vec3(
                    dot(lms, vec3(0.2104542553, 0.7936177850, -0.0040720468)),
                    dot(lms, vec3(1.9779984951, -2.4285922050, 0.4505937099)),
                    dot(lms, vec3(0.0259040371, 0.7827717662, -0.8086757660))
                );
            }

            highp vec3 oklabToLinear(highp vec3 color) {
                highp vec3 lms = vec3(
                    color.x + 0.3963377774 * color.y + 0.2158037573 * color.z,
                    color.x - 0.1055613458 * color.y - 0.0638541728 * color.z,
                    color.x - 0.0894841775 * color.y - 1.2914855480 * color.z
                );
                lms = lms * lms * lms;
                return vec3(
                    dot(lms, vec3(4.0767416621, -3.3077115913, 0.2309699292)),
                    dot(lms, vec3(-1.2684380046, 2.6097574011, -0.3413193965)),
                    dot(lms, vec3(-0.0041960863, -0.7034186147, 1.7076147010))
                );
            }

            highp vec2 balance(highp vec2 wheel) {
                highp float strength = min(length(wheel), 1.0);
                highp float angle = atan(-wheel.y, wheel.x);
                highp float hue = fract((angle - 1.5707963268) / 6.2831853072);
                highp vec3 color = hsvToRgb(vec3(hue, 1.0, 1.0));
                highp vec3 lab = linearToOklab(srgbToLinear(color));
                highp vec2 chroma = lab.yz;
                return chroma / max(length(chroma), 0.0001) * strength;
            }

            void main() {
                lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);
                highp vec3 lab = linearToOklab(srgbToLinear(source.rgb));
                highp float tonalLuma = dot(
                    source.rgb,
                    vec3(0.2126, 0.7152, 0.0722)
                );
                highp float sharpness = mix(
                    0.8,
                    16.0,
                    smoothstep(0.0, 1.0, clamp(edges, 0.0, 1.0))
                );
                highp vec3 distances = vec3(
                    tonalLuma,
                    abs(tonalLuma - 0.5),
                    1.0 - tonalLuma
                );
                highp vec3 weights = exp(
                    -sharpness * distances * distances
                );
                weights /= max(
                    weights.x + weights.y + weights.z,
                    0.0001
                );
                highp vec2 adjustment =
                    balance(shadows) * weights.x +
                    balance(midtones) * weights.y +
                    balance(highlights) * weights.z;
                lab.yz += adjustment * 0.115;
                gl_FragColor = vec4(
                    clamp(linearToSrgb(oklabToLinear(lab)), 0.0, 1.0),
                    source.a
                );
            }
        """
    }
}

private class GPUImageColorCurvesFilter(
    private val type: ImageCurvesEditorType,
    curves: List<CurvesValue>
) : GPUImageFilter(
    NO_FILTER_VERTEX_SHADER,
    when (type) {
        ImageCurvesEditorType.CMYK -> CMYK_FRAGMENT_SHADER
        ImageCurvesEditorType.Lab -> LAB_FRAGMENT_SHADER
        else -> NO_FILTER_FRAGMENT_SHADER
    }
) {
    private val texture = intArrayOf(NoTexture)
    private val textureData = createTextureData(curves)
    private var curveTextureLocation = 0

    override fun onInit() {
        super.onInit()
        curveTextureLocation = GLES20.glGetUniformLocation(program, "toneCurveTexture")

        GLES20.glActiveTexture(CurveTextureUnit)
        GLES20.glGenTextures(1, texture, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
    }

    override fun onInitialized() {
        super.onInitialized()
        runOnDraw {
            GLES20.glActiveTexture(CurveTextureUnit)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            textureData.position(0)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                LutSize,
                1,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                textureData
            )
        }
    }

    override fun onDrawArraysPre() {
        if (texture[0] == NoTexture) return
        GLES20.glActiveTexture(CurveTextureUnit)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
        GLES20.glUniform1i(curveTextureLocation, CurveTextureIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (texture[0] != NoTexture) {
            GLES20.glDeleteTextures(1, texture, 0)
            texture[0] = NoTexture
        }
    }

    private fun createTextureData(curves: List<CurvesValue>): ByteBuffer {
        val luts = curves.map { it.toLut(LutSize) }
        return ByteBuffer.allocateDirect(LutSize * 4)
            .order(ByteOrder.nativeOrder())
            .apply {
                repeat(LutSize) { index ->
                    repeat(4) { channel ->
                        val value = luts.getOrNull(
                            if (luts.size == 1) 0 else channel
                        )?.get(index)
                            ?: index / (LutSize - 1f)
                        put((value.coerceIn(0f, 1f) * 255f).roundToInt().toByte())
                    }
                }
                position(0)
            }
    }

    companion object {
        private const val LutSize = 256
        private const val NoTexture = -1
        private const val CurveTextureIndex = 3
        private const val CurveTextureUnit = GLES20.GL_TEXTURE3

        private const val HEADER = """
            varying highp vec2 textureCoordinate;
            uniform sampler2D inputImageTexture;
            uniform sampler2D toneCurveTexture;
        """

        private const val CMYK_FRAGMENT_SHADER = HEADER + """
            void main() {
                lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);
                highp float k = 1.0 - max(max(source.r, source.g), source.b);
                highp float denominator = max(1.0 - k, 0.00001);
                highp vec3 cmy = (vec3(1.0) - source.rgb - vec3(k)) / denominator;
                cmy.r = texture2D(toneCurveTexture, vec2(cmy.r, 0.5)).r;
                cmy.g = texture2D(toneCurveTexture, vec2(cmy.g, 0.5)).g;
                cmy.b = texture2D(toneCurveTexture, vec2(cmy.b, 0.5)).b;
                k = texture2D(toneCurveTexture, vec2(k, 0.5)).a;
                highp vec3 rgb = (vec3(1.0) - cmy) * (1.0 - k);
                gl_FragColor = vec4(clamp(rgb, 0.0, 1.0), source.a);
            }
        """

        private const val LAB_FRAGMENT_SHADER = HEADER + """
            highp float toLinear(highp float value) {
                return value <= 0.04045
                    ? value / 12.92
                    : pow((value + 0.055) / 1.055, 2.4);
            }

            highp float toSrgb(highp float value) {
                return value <= 0.0031308
                    ? value * 12.92
                    : 1.055 * pow(max(value, 0.0), 1.0 / 2.4) - 0.055;
            }

            highp float labForward(highp float value) {
                return value > 0.008856
                    ? pow(value, 1.0 / 3.0)
                    : 7.787 * value + 16.0 / 116.0;
            }

            highp float labInverse(highp float value) {
                highp float cube = value * value * value;
                return cube > 0.008856
                    ? cube
                    : (value - 16.0 / 116.0) / 7.787;
            }

            void main() {
                lowp vec4 source = texture2D(inputImageTexture, textureCoordinate);
                highp vec3 linearRgb = vec3(
                    toLinear(source.r),
                    toLinear(source.g),
                    toLinear(source.b)
                );
                highp vec3 xyz = vec3(
                    dot(linearRgb, vec3(0.4124564, 0.3575761, 0.1804375)),
                    dot(linearRgb, vec3(0.2126729, 0.7151522, 0.0721750)),
                    dot(linearRgb, vec3(0.0193339, 0.1191920, 0.9503041))
                );
                highp float fx = labForward(xyz.x / 0.95047);
                highp float fy = labForward(xyz.y);
                highp float fz = labForward(xyz.z / 1.08883);
                highp vec3 lab = vec3(
                    (116.0 * fy - 16.0) / 100.0,
                    (500.0 * (fx - fy) + 128.0) / 255.0,
                    (200.0 * (fy - fz) + 128.0) / 255.0
                );
                lab = vec3(
                    texture2D(toneCurveTexture, vec2(clamp(lab.x, 0.0, 1.0), 0.5)).r,
                    texture2D(toneCurveTexture, vec2(clamp(lab.y, 0.0, 1.0), 0.5)).g,
                    texture2D(toneCurveTexture, vec2(clamp(lab.z, 0.0, 1.0), 0.5)).b
                );
                highp float l = lab.x * 100.0;
                highp float a = lab.y * 255.0 - 128.0;
                highp float b = lab.z * 255.0 - 128.0;
                fy = (l + 16.0) / 116.0;
                fx = fy + a / 500.0;
                fz = fy - b / 200.0;
                xyz = vec3(
                    0.95047 * labInverse(fx),
                    labInverse(fy),
                    1.08883 * labInverse(fz)
                );
                linearRgb = vec3(
                    dot(xyz, vec3(3.2404542, -1.5371385, -0.4985314)),
                    dot(xyz, vec3(-0.9692660, 1.8760108, 0.0415560)),
                    dot(xyz, vec3(0.0556434, -0.2040259, 1.0572252))
                );
                highp vec3 rgb = vec3(
                    toSrgb(linearRgb.r),
                    toSrgb(linearRgb.g),
                    toSrgb(linearRgb.b)
                );
                gl_FragColor = vec4(clamp(rgb, 0.0, 1.0), source.a);
            }
        """

    }
}

private class GPUImageRgbToHsvFilter : GPUImageFilter(
    NO_FILTER_VERTEX_SHADER,
    """
        varying highp vec2 textureCoordinate;
        uniform sampler2D inputImageTexture;

        void main() {
            highp vec4 source = texture2D(inputImageTexture, textureCoordinate);
            highp vec4 first = mix(
                vec4(source.bg, -1.0, 2.0 / 3.0),
                vec4(source.gb, 0.0, -1.0 / 3.0),
                step(source.b, source.g)
            );
            highp vec4 second = mix(
                vec4(first.xyw, source.r),
                vec4(source.r, first.yzx),
                step(first.x, source.r)
            );
            highp float delta = second.x - min(second.w, second.y);
            highp float epsilon = 0.0000001;
            highp vec3 hsv = vec3(
                abs(second.z + (second.w - second.y) / (6.0 * delta + epsilon)),
                delta / (second.x + epsilon),
                second.x
            );
            gl_FragColor = vec4(clamp(hsv, 0.0, 1.0), source.a);
        }
    """
)

private class GPUImageRelationCurveFilter(
    type: ImageCurvesEditorType,
    curve: CurvesValue
) : GPUImageFilter(
    NO_FILTER_VERTEX_SHADER,
    relationFragmentShader(type)
) {
    private val texture = intArrayOf(NoTexture)
    private val textureData = createTextureData(curve)
    private var curveTextureLocation = 0

    override fun onInit() {
        super.onInit()
        curveTextureLocation = GLES20.glGetUniformLocation(program, "relationCurveTexture")

        GLES20.glActiveTexture(CurveTextureUnit)
        GLES20.glGenTextures(1, texture, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES20.GL_TEXTURE_2D,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
    }

    override fun onInitialized() {
        super.onInitialized()
        runOnDraw {
            GLES20.glActiveTexture(CurveTextureUnit)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
            textureData.position(0)
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                LutSize,
                1,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                textureData
            )
        }
    }

    override fun onDrawArraysPre() {
        if (texture[0] == NoTexture) return
        GLES20.glActiveTexture(CurveTextureUnit)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0])
        GLES20.glUniform1i(curveTextureLocation, CurveTextureIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (texture[0] != NoTexture) {
            GLES20.glDeleteTextures(1, texture, 0)
            texture[0] = NoTexture
        }
    }

    private fun createTextureData(curve: CurvesValue): ByteBuffer {
        val values = curve.toLut(LutSize)
        return ByteBuffer.allocateDirect(LutSize * 4)
            .order(ByteOrder.nativeOrder())
            .apply {
                values.forEach { value ->
                    val byte = (value.coerceIn(0f, 1f) * 255f)
                        .roundToInt()
                        .toByte()
                    repeat(4) {
                        put(byte)
                    }
                }
                position(0)
            }
    }

    companion object {
        private fun relationFragmentShader(
            type: ImageCurvesEditorType
        ): String {
            val inputValue = when (type) {
                ImageCurvesEditorType.HueVsSat,
                ImageCurvesEditorType.HueVsHue,
                ImageCurvesEditorType.HueVsLuma -> "hsv.x"

                ImageCurvesEditorType.LumaVsSat,
                ImageCurvesEditorType.LumaVsHue -> "hsv.z"

                ImageCurvesEditorType.SatVsSat -> "hsv.y"
                else -> "hsv.x"
            }
            val applyCurve = when (type) {
                ImageCurvesEditorType.HueVsSat,
                ImageCurvesEditorType.LumaVsSat,
                ImageCurvesEditorType.SatVsSat -> """
                    highp float saturationDelta = mix(
                        delta * 2.0,
                        delta * (2.0 / 3.0),
                        step(0.0, delta)
                    );
                    hsv.y = clamp(hsv.y + saturationDelta, 0.0, 1.0);
                """.trimIndent()

                ImageCurvesEditorType.HueVsLuma -> {
                    """
                        highp vec3 rgb = hsvToRgb(hsv);
                        highp float luma = dot(
                            rgb,
                            vec3(0.2126, 0.7152, 0.0722)
                        );
                        highp float chroma = hsv.y * hsv.z;
                        highp float chromaWeight = smoothstep(0.04, 0.30, chroma);
                        highp float targetLuma = clamp(
                            luma + delta * 2.0 * chromaWeight,
                            0.0,
                            1.0
                        );
                        if (targetLuma < luma) {
                            rgb *= targetLuma / max(luma, 0.00001);
                        } else {
                            rgb = mix(
                                rgb,
                                vec3(1.0),
                                (targetLuma - luma) / max(1.0 - luma, 0.00001)
                            );
                        }
                        hsv = rgbToHsv(clamp(rgb, 0.0, 1.0));
                    """.trimIndent()
                }

                ImageCurvesEditorType.HueVsHue,
                ImageCurvesEditorType.LumaVsHue -> {
                    "hsv.x = mod(hsv.x + delta + 1.0, 1.0);"
                }

                else -> ""
            }
            return FRAGMENT_SHADER
                .replace("INPUT_VALUE", inputValue)
                .replace("APPLY_CURVE", applyCurve)
        }

        private const val LutSize = 256
        private const val NoTexture = -1
        private const val CurveTextureIndex = 3
        private const val CurveTextureUnit = GLES20.GL_TEXTURE3

        private const val FRAGMENT_SHADER = """
            varying highp vec2 textureCoordinate;
            uniform sampler2D inputImageTexture;
            uniform sampler2D relationCurveTexture;

            highp vec3 hsvToRgb(highp vec3 hsv) {
                highp vec3 hue = abs(
                    fract(hsv.xxx + vec3(0.0, 2.0 / 3.0, 1.0 / 3.0)) * 6.0 - 3.0
                );
                return hsv.z * mix(
                    vec3(1.0),
                    clamp(hue - 1.0, 0.0, 1.0),
                    hsv.y
                );
            }

            highp vec3 rgbToHsv(highp vec3 rgb) {
                highp vec4 first = mix(
                    vec4(rgb.bg, -1.0, 2.0 / 3.0),
                    vec4(rgb.gb, 0.0, -1.0 / 3.0),
                    step(rgb.b, rgb.g)
                );
                highp vec4 second = mix(
                    vec4(first.xyw, rgb.r),
                    vec4(rgb.r, first.yzx),
                    step(first.x, rgb.r)
                );
                highp float rgbDelta = second.x - min(second.w, second.y);
                highp float epsilon = 0.0000001;
                return vec3(
                    abs(
                        second.z +
                            (second.w - second.y) / (6.0 * rgbDelta + epsilon)
                    ),
                    rgbDelta / (second.x + epsilon),
                    second.x
                );
            }

            void main() {
                highp vec4 source = texture2D(inputImageTexture, textureCoordinate);
                highp vec3 hsv = source.rgb;
                highp float curve = texture2D(
                    relationCurveTexture,
                    vec2(clamp(INPUT_VALUE, 0.0, 1.0), 0.5)
                ).r;
                curve = clamp(curve, 0.0, 1.0);
                highp float delta = curve - 0.5;

                APPLY_CURVE

                gl_FragColor = vec4(clamp(hsv, 0.0, 1.0), source.a);
            }
        """
    }
}

private class GPUImageHsvToRgbFilter : GPUImageFilter(
    NO_FILTER_VERTEX_SHADER,
    """
        varying highp vec2 textureCoordinate;
        uniform sampler2D inputImageTexture;

        void main() {
            highp vec4 source = texture2D(inputImageTexture, textureCoordinate);
            highp vec3 hue = abs(
                fract(source.xxx + vec3(0.0, 2.0 / 3.0, 1.0 / 3.0)) * 6.0 - 3.0
            );
            highp vec3 rgb = source.z * mix(
                vec3(1.0),
                clamp(hue - 1.0, 0.0, 1.0),
                source.y
            );
            gl_FragColor = vec4(rgb, source.a);
        }
    """
)
