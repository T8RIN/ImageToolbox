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

package com.t8rin.imagetoolbox.feature.filters.data.utils.gpu

internal fun String.toFragmentShader(uniforms: List<ShaderUniform>): String = buildString {
    appendLine("precision highp float;")
    appendLine("varying highp vec2 textureCoordinate;")
    appendLine("uniform sampler2D inputImageTexture;")
    appendLine("uniform vec2 imageAspect;")
    uniforms.forEach {
        val type = when (it) {
            is ShaderUniform.Number -> "float"
            is ShaderUniform.Color -> "vec4"
        }
        appendLine("uniform $type ${it.name};")
    }
    if (uniforms.none { it.name == "offsetX" }) appendLine("const float offsetX = 0.0;")
    if (uniforms.none { it.name == "offsetY" }) appendLine("const float offsetY = 0.0;")
    if (uniforms.none { it.name == "scale" }) appendLine("const float scale = 1.0;")
    if (uniforms.none { it.name == "rotation" }) appendLine("const float rotation = 0.0;")
    appendLine(BITMAP_SAMPLING_SHADER)
    appendLine(COMMON_SHADER)
    appendLine("void main() {")
    appendLine("vec2 p = toField(textureCoordinate);")
    appendLine("vec2 q = p;")
    appendLine("float shade = 1.0;")
    appendLine("{")
    appendLine(this@toFragmentShader)
    appendLine("}")
    appendLine("vec4 color = sampleField(q);")
    appendLine("gl_FragColor = vec4(color.rgb * shade, color.a);")
    appendLine("}")
}

private val COMMON_SHADER = """
    const float TAU = 6.28318530718;

    float angleOf(vec2 p) {
        return dot(p, p) < 0.00000001 ? 0.0 : atan(p.y, p.x);
    }

    vec2 rotatePoint(vec2 p, float angle) {
        float c = cos(angle);
        float s = sin(angle);
        return vec2(c * p.x - s * p.y, s * p.x + c * p.y);
    }

    vec2 toField(vec2 uv) {
        return rotatePoint((uv - 0.5 - vec2(offsetX, offsetY)) * imageAspect,
            -radians(rotation)) / scale;
    }

    vec4 sampleField(vec2 p) {
        vec2 uv = rotatePoint(p * scale, radians(rotation)) / imageAspect
            + 0.5 + vec2(offsetX, offsetY);
        uv = 1.0 - abs(mod(uv, 2.0) - 1.0);
        return sampleInput(uv);
    }

    float hash(vec2 p) {
        vec3 h = fract(vec3(p.xyx) * vec3(0.1031, 0.1030, 0.0973));
        h += dot(h, h.yzx + 33.33);
        return fract((h.x + h.y) * h.z);
    }

    vec2 hash2(vec2 p) {
        return vec2(hash(p), hash(p + vec2(17.17, 47.23)));
    }

    float noise(vec2 p) {
        vec2 i = floor(p);
        vec2 f = fract(p);
        f = f * f * (3.0 - 2.0 * f);
        return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),
            mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0)), f.x), f.y);
    }

    vec3 rgbToHsv(vec3 color) {
        float high = max(max(color.r, color.g), color.b);
        float low = min(min(color.r, color.g), color.b);
        float delta = high - low;
        float hue = 0.0;
        if (delta > 0.00001) {
            if (high == color.r) hue = mod((color.g - color.b) / delta, 6.0);
            else if (high == color.g) hue = (color.b - color.r) / delta + 2.0;
            else hue = (color.r - color.g) / delta + 4.0;
        }
        return vec3(fract(hue / 6.0), high > 0.00001 ? delta / high : 0.0, high);
    }

    vec3 hsvToRgb(vec3 hsv) {
        vec3 wave = clamp(abs(fract(hsv.x + vec3(0.0, 0.6666667, 0.3333333)) * 6.0 - 3.0) - 1.0, 0.0, 1.0);
        return hsv.z * mix(vec3(1.0), wave, hsv.y);
    }

    float luminance(vec3 color) {
        return dot(color, vec3(0.2126, 0.7152, 0.0722));
    }

    vec2 luminanceGradient(vec2 p, float delta) {
        float dx = luminance(sampleField(p + vec2(delta, 0.0)).rgb)
            - luminance(sampleField(p - vec2(delta, 0.0)).rgb);
        float dy = luminance(sampleField(p + vec2(0.0, delta)).rgb)
            - luminance(sampleField(p - vec2(0.0, delta)).rgb);
        return vec2(dx, dy) / (2.0 * delta);
    }
""".trimIndent()