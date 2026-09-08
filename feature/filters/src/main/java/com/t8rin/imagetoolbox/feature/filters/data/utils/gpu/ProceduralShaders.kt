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

import com.t8rin.imagetoolbox.core.filters.domain.model.enums.ProceduralEffect

internal fun ProceduralEffect.fragmentShader(): String = buildString {
    appendLine("precision highp float;")
    appendLine("varying highp vec2 textureCoordinate;")
    appendLine("uniform sampler2D inputImageTexture;")
    appendLine("uniform vec2 imageAspect;")
    parameters.forEach { appendLine("uniform float ${it.name};") }
    appendLine(BITMAP_SAMPLING_SHADER)
    colors.forEach { appendLine("uniform vec4 ${it.name};") }
    if (parameters.none { it.name == "offsetX" }) {
        appendLine("const float offsetX = 0.0, offsetY = 0.0, scale = 1.0, rotation = 0.0;")
    }
    appendLine(COMMON_SHADER)
    appendLine("void main() {")
    appendLine("vec2 p = toField(textureCoordinate);")
    appendLine("vec2 q = p;")
    appendLine("float shade = 1.0;")
    appendLine("{")
    appendLine(distortionShader())
    appendLine("}")
    appendLine("vec4 color = sampleField(q);")
    appendLine("gl_FragColor = vec4(color.rgb * shade, color.a);")
    appendLine("}")
}

private fun ProceduralEffect.distortionShader(): String = when (this) {
    ProceduralEffect.StreakExpand -> """

            float mapped = sign(p.y) * max(abs(p.y) - size * 0.5, 0.0);
            q.y = mix(p.y, mapped, intensity);
        """.trimIndent()

    ProceduralEffect.RadialInterpolate -> """

            float r = length(p);
            float inside = 1.0 - step(width * 0.5, abs(r - radius));
            float level = floor((r - radius) / width * count) / count;
            float mapped = radius + level * width;
            q = p * mix(1.0, mapped / max(r, 0.00001), inside * intensity);
        """.trimIndent()

    ProceduralEffect.RadialStreak -> """

            float r = length(p);
            float segment = floor(angleOf(p) / TAU * count);
            float target = radius * (0.7 + 0.3 * hash(vec2(segment, 3.0)));
            q = p * mix(1.0, target / max(r, 0.00001), intensity);
        """.trimIndent()

    ProceduralEffect.ColumnStreak -> """

            float column = floor(p.x * count);
            float levels = mix(4.0, 64.0, hash(vec2(column, 1.0)) * variability);
            q.y = mix(p.y, (floor(p.y * levels) + 0.5) / levels, intensity);
        """.trimIndent()

    ProceduralEffect.StreakCircles -> """

            vec4 result = sampleInput(textureCoordinate);
            for (int i = 0; i < 64; i++) {
                if (float(i) >= count) break;
                vec2 center = (hash2(vec2(float(i), seed)) - 0.5) * imageAspect;
                float r = length(p - center);
                float limit = radius * (0.3 + hash(vec2(float(i), seed + 7.0)));
                if (r < limit) {
                    float ring = floor(r / limit * layers) / layers;
                    vec4 color = sampleField(center + vec2(ring * limit, 0.0));
                    result = mix(result, color, intensity);
                }
            }
            gl_FragColor = result;
            return;
        """.trimIndent()

    ProceduralEffect.TiledStreak -> """

            vec2 cell = floor(p * frequency);
            vec2 local = fract(p * frequency) - 0.5;
            float angle = angleOf(local);
            float radius = length(local);
            vec2 center = (cell + 0.5) / frequency;
            vec2 mapped = center + vec2(radius, sin(angle) * (1.0 - balance) * 0.2) / frequency;
            mapped += (hash2(cell + seed) - 0.5) * variability / frequency;
            q = mix(p, mapped, intensity);
        """.trimIndent()

    ProceduralEffect.BrokenCells -> """

            vec2 grid = p * frequency, cell = floor(grid), chosen = cell, local = vec2(0.0);
            float nearest = 100.0;
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    vec2 candidate = cell + vec2(float(x), float(y));
                    vec2 delta = grid - candidate - 0.5 - (hash2(candidate + seed) - 0.5) * variability;
                    float d = dot(delta, delta);
                    if (d < nearest) { nearest = d; chosen = candidate; local = delta; }
                }
            }
            vec2 random = hash2(chosen + seed) - 0.5;
            q += (random + local * distortion) * intensity / frequency;
        """.trimIndent()

    ProceduralEffect.SpiralBreaks -> """

            float r = length(p), angle = angleOf(p);
            float segment = floor((angle + r * 8.0) / TAU * count);
            vec2 offset = hash2(vec2(segment, 3.0)) - 0.5;
            q = rotatePoint(p, offset.x * intensity * distortion) + offset * intensity * 0.15;
        """.trimIndent()

    ProceduralEffect.ConcentricCircleBreaks -> """

            float r = length(p);
            float band = floor(r * frequency);
            vec2 offset = hash2(vec2(band, 17.0)) - 0.5;
            float strength = intensity * exp(-r * dampening);
            q = rotatePoint(p, offset.x * distortion * strength) + offset * strength / frequency;
        """.trimIndent()

    ProceduralEffect.ConcentricSquareBreaks -> """

            float r = max(abs(p.x), abs(p.y));
            float band = floor(r * frequency);
            vec2 offset = hash2(vec2(band, 17.0)) - 0.5;
            float strength = intensity * exp(-r * dampening);
            q = rotatePoint(p, offset.x * distortion * strength) + offset * strength / frequency;
        """.trimIndent()

    ProceduralEffect.CircularMirror -> """

            float r = length(p);
            float target = mode < 0.5 ? radius + abs(r - radius) : max(0.0, radius - abs(r - radius));
            q = p * mix(1.0, target / max(r, 0.00001), intensity);
        """.trimIndent()

    ProceduralEffect.ProgressiveScaling -> """

            float r = max(length(p), 0.00001);
            float level = floor(log(r / radius) / log(ratio));
            q = mix(p, p * pow(ratio, -level), intensity);
        """.trimIndent()

    ProceduralEffect.BrokenGlass -> """

            vec2 grid = p * frequency;
            vec2 cell = floor(grid);
            float nearest = 100.0;
            vec2 local = vec2(0.0);
            vec2 chosen = cell;
            for (int y = -1; y <= 1; y++) {
                for (int x = -1; x <= 1; x++) {
                    vec2 candidate = cell + vec2(float(x), float(y));
                    vec2 delta = grid - candidate - 0.5 - (hash2(candidate + seed) - 0.5) * 0.8;
                    float distance = dot(delta, delta);
                    if (distance < nearest) {
                        nearest = distance;
                        local = delta;
                        chosen = candidate;
                    }
                }
            }
            vec2 random = hash2(chosen + seed + 19.3) - 0.5;
            q += (rotatePoint(local, random.x * intensity * 3.0) - local + random * intensity) / frequency;
        """.trimIndent()

    ProceduralEffect.InlineBreaks -> """

            float line = floor(p.y * frequency);
            float shift = hash(vec2(line, 13.0)) - 0.5;
            q.x += shift * intensity * 0.4;
            q.y += sin(p.x * 12.0 + line) * intensity * distortion / frequency;
        """.trimIndent()

    ProceduralEffect.BokehLights -> """

            vec3 light = vec3(0.0);
            for (int i = 0; i < 64; i++) {
                if (float(i) >= count) break;
                vec2 random = hash2(vec2(float(i), seed));
                vec2 center = (random - 0.5) * imageAspect * 1.5;
                vec2 delta = p - center;
                float r = radius * mix(1.0, 0.25 + random.x, variability);
                float sector = TAU / blades;
                float angle = mod(angleOf(delta) + sector * 0.5, sector) - sector * 0.5;
                float edge = r * cos(sector * 0.5) / max(cos(angle), 0.001);
                float disc = 1.0 - smoothstep(edge * 0.8, edge, length(delta));
                vec3 hsv = rgbToHsv(lightColor.rgb);
                hsv.x = fract(hsv.x + (random.y - 0.5) * colorVariation);
                light += hsvToRgb(hsv) * disc * lightColor.a;
            }
            vec4 source = sampleInput(textureCoordinate);
            gl_FragColor = vec4(min(vec3(source.a), source.rgb + light * intensity * source.a), source.a);
            return;
        """.trimIndent()

    ProceduralEffect.Fireworks -> """

            vec3 light = vec3(0.0);
            for (int i = 0; i < 12; i++) {
                if (float(i) >= explosions) break;
                vec2 random = hash2(vec2(float(i), seed));
                vec2 delta = p - (random - 0.5) * imageAspect;
                float radius = length(delta);
                float angle = angleOf(delta) + random.x * TAU;
                float spoke = pow(max(0.0, cos(angle * particles)), 24.0);
                float distance = abs(radius - time * (0.1 + random.y * 0.4));
                float trail = exp(-distance * (mode < 0.5 ? 80.0 : 30.0));
                if (mode > 1.5) trail *= 0.5 + 0.5 * sin(radius * 150.0);
                light += hsvToRgb(vec3(random.x, 0.8, 1.0)) * spoke * trail;
            }
            vec4 source = sampleInput(textureCoordinate);
            gl_FragColor = vec4(min(vec3(source.a), source.rgb + light * intensity * source.a), source.a);
            return;
        """.trimIndent()


    ProceduralEffect.HueShiftBands -> """
            vec4 source = sampleInput(textureCoordinate);
            vec3 hsv = rgbToHsv(source.rgb / max(source.a, 0.00001));
            hsv.x = fract(hsv.x + p.y * intensity + hueOffset);
            hsv.y = clamp(hsv.y * saturation, 0.0, 1.0);
            gl_FragColor = vec4(hsvToRgb(hsv) * source.a, source.a);
            return;
        """.trimIndent()

    ProceduralEffect.HueShiftTunnel -> """
            vec4 source = sampleInput(textureCoordinate);
            vec3 hsv = rgbToHsv(source.rgb / max(source.a, 0.00001));
            hsv.x = fract(hsv.x + length(p) * intensity + hueOffset);
            hsv.y = clamp(hsv.y * saturation, 0.0, 1.0);
            gl_FragColor = vec4(hsvToRgb(hsv) * source.a, source.a);
            return;
        """.trimIndent()

    ProceduralEffect.HueShiftFan -> """
            vec4 source = sampleInput(textureCoordinate);
            vec3 hsv = rgbToHsv(source.rgb / max(source.a, 0.00001));
            hsv.x = fract(hsv.x + angleOf(p) / TAU * intensity + hueOffset);
            hsv.y = clamp(hsv.y * saturation, 0.0, 1.0);
            gl_FragColor = vec4(hsvToRgb(hsv) * source.a, source.a);
            return;
        """.trimIndent()


    ProceduralEffect.SpiralArms -> """
            float r = length(p);
            float angle = angleOf(p) / TAU;
            vec2 uv = vec2(angle * count + r * intensity, r);
            gl_FragColor = sampleInput(fract(uv));
            return;
        """.trimIndent()

    ProceduralEffect.LogarithmicSpiral -> """
            float r = max(length(p), 0.00001);
            float angle = angleOf(p) / TAU;
            vec2 uv = vec2(angle * count + log(r) * intensity, angle + log(r));
            gl_FragColor = sampleInput(fract(uv));
            return;
        """.trimIndent()

    ProceduralEffect.SpiralDroste -> """
            float r = max(length(p), 0.00001);
            float angle = angleOf(p);
            float period = log(intensity);
            float logRadius = log(r / 0.5) - distortion * angle;
            float layer = floor(logRadius / period);
            float radius = exp(mod(logRadius, period)) * 0.5 / intensity;
            float turn = angle + distortion * layer * TAU;
            q = radius * vec2(cos(turn), sin(turn));
        """.trimIndent()

    ProceduralEffect.SquareSpiralDroste -> """
            vec2 point = p * vec2(1.0, shapeAspect);
            float r = max(max(abs(point.x), abs(point.y)), 0.00001);
            float angle = angleOf(point);
            float period = log(intensity);
            float logRadius = log(r / 0.5) - distortion * angle;
            float layer = floor(logRadius / period);
            float radius = exp(mod(logRadius, period)) * 0.5 / intensity;
            q = rotatePoint(point / r * radius, distortion * layer * TAU) / vec2(1.0, shapeAspect);
        """.trimIndent()

    ProceduralEffect.HexKaleidoscope -> """
            vec2 grid = p * frequency;
            vec2 stepSize = vec2(1.7320508, 3.0);
            vec2 a = mod(grid, stepSize) - stepSize * 0.5;
            vec2 b = mod(grid - stepSize * 0.5, stepSize) - stepSize * 0.5;
            vec2 local = dot(a, a) < dot(b, b) ? a : b;
            float sector = TAU / count;
            float angle = mod(angleOf(local) + sector * 0.5, sector);
            if (mode < 0.5) angle = abs(angle - sector * 0.5);
            else if (mode < 1.5) angle -= sector * 0.5;
            else angle = sector * 0.5 - abs(angle - sector * 0.5);
            q = vec2(cos(angle), sin(angle)) * length(local) * 0.5 + vec2(offset, 0.0);
        """.trimIndent()

    ProceduralEffect.SmoothKaleidoscope -> """
            vec2 grid = p * frequency;
            vec2 cell = floor(grid);
            vec2 f = fract(grid);
            vec4 sum = vec4(0.0);
            float weightSum = 0.0;
            for (int y = 0; y < 2; y++) {
                for (int x = 0; x < 2; x++) {
                    vec2 corner = vec2(float(x), float(y));
                    vec2 local = f - corner;
                    float angle = mod(cell.x + corner.x + cell.y + corner.y, 4.0) * TAU * 0.25;
                    vec2 uv = rotatePoint(local, angle) * 0.5 + 0.5 + offset;
                    vec2 weight = max(vec2(0.0), vec2(1.0) - abs(local));
                    float w = pow(weight.x * weight.y, mix(16.0, 1.0, blend));
                    sum += sampleInput(1.0 - abs(mod(uv, 2.0) - 1.0)) * w;
                    weightSum += w;
                }
            }
            gl_FragColor = sum / max(weightSum, 0.00000000000000000001);
            return;
        """.trimIndent()

    ProceduralEffect.RandomKaleidoscopeGrid -> """
            vec2 grid = p * frequency;
            vec2 cell = floor(grid);
            vec2 local = fract(grid) - 0.5;
            vec2 random = hash2(cell + seed);
            float spikes = max(1.0, floor(count + (random.x - 0.5) * variability * count));
            float sector = TAU / spikes;
            float angle = abs(mod(angleOf(local) + random.y * TAU, sector) - sector * 0.5);
            vec2 mapped = vec2(cos(angle), sin(angle)) * length(local);
            vec4 kaleidoscope = sampleField(mapped);
            vec4 original = sampleField(local);
            float edge = max(abs(local.x), abs(local.y)) * 2.0;
            float weight = smoothstep(1.0 - max(blend, 0.001), 1.0, edge);
            gl_FragColor = mix(kaleidoscope, original, weight);
            return;
        """.trimIndent()

    ProceduralEffect.SquareFresnel -> """
            float r = max(max(abs(p.x), abs(p.y)), 0.00001);
            float ring = floor(r * intensity * 2.0);
            q = p / (1.0 + ring);
        """.trimIndent()

    ProceduralEffect.InversionFractal -> """
            for (int i = 0; i < 16; i++) {
                if (float(i) >= iterations) break;
                if (mode < 0.5) q = abs(q) - vec2(0.4);
                else if (mode < 1.5) q = abs(q.yx) - vec2(0.35, 0.5);
                else q = mod(q + 0.5, 1.0) - 0.5;
                q *= intensity / max(dot(q, q), 0.01);
                q = clamp(q, vec2(-100.0), vec2(100.0));
            }
        """.trimIndent()


    ProceduralEffect.RandomColorDispersion -> """
            vec2 delta = (hash2(floor(p * 24.0) + seed) - 0.5) * intensity * 0.15;
            vec4 red = sampleField(p + delta);
            vec4 green = sampleField(p);
            vec4 blue = sampleField(p - delta);
            gl_FragColor = vec4(red.r, green.g, blue.b, max(red.a, max(green.a, blue.a)));
            return;
        """.trimIndent()

    ProceduralEffect.RadialShimmer -> """
            float r = length(p);
            q = rotatePoint(p, intensity * 0.3 * sin(pow(r, spacing) * count * TAU));
        """.trimIndent()

    ProceduralEffect.HelixWaves -> """
            float r = length(p);
            float a = angleOf(p);
            float phase = a * max(1.0, floor(frequency * 0.25)) + r * frequency * TAU;
            float wave = sin(phase);
            if (mode < 0.5) q *= 1.0 + wave * intensity * 0.2;
            else if (mode < 1.5) q = rotatePoint(p, wave * intensity * 0.3);
            else q += vec2(cos(phase), sin(phase)) * intensity * 0.06;
            shade = 1.0 + lighting * cos(phase) * 0.3;
        """.trimIndent()

    ProceduralEffect.GridSineDistortion2 -> """
            vec2 wave = sin(p * frequency * TAU);
            q += vec2(wave.x * cos(p.y * frequency * TAU), wave.y * cos(p.x * frequency * TAU)) * intensity * 0.1;
        """.trimIndent()

    ProceduralEffect.Quicksilver -> """
            vec2 gradient = luminanceGradient(p, 0.005);
            float wave = sin(luminance(sampleField(p).rgb) * frequency * TAU);
            q += gradient * displacement * 0.1 + vec2(wave, cos(wave * 3.0)) * intensity * 0.04;
        """.trimIndent()

    ProceduralEffect.Gallium -> """
            for (int i = 0; i < 16; i++) {
                if (float(i) >= iterations) break;
                vec2 gradient = luminanceGradient(q, 0.007);
                q += gradient / (1.0 + length(gradient)) * intensity * 0.08;
            }
        """.trimIndent()

    ProceduralEffect.Rubidium -> """
            vec2 gradient = luminanceGradient(p, 0.008);
            vec2 tangent = vec2(-gradient.y, gradient.x);
            q += mix(gradient, tangent, balance) / (1.0 + length(gradient)) * displacement;
            vec3 color = sampleField(p).rgb;
            q += vec2(color.r - color.g, color.b - color.g) * intensity * 0.2;
        """.trimIndent()

    ProceduralEffect.GlassRectTiles -> """
            vec2 local = fract(p * frequency + 0.5) - 0.5;
            q += local * (intensity + distortion * dot(local, local) * 4.0) / frequency;
        """.trimIndent()

    ProceduralEffect.GlassHexTiles -> """
            vec2 grid = p * frequency;
            vec2 stepSize = vec2(1.7320508, 3.0);
            vec2 a = mod(grid, stepSize) - stepSize * 0.5;
            vec2 b = mod(grid - stepSize * 0.5, stepSize) - stepSize * 0.5;
            vec2 local = dot(a, a) < dot(b, b) ? a : b;
            q += local * (intensity + distortion * dot(local, local)) / frequency;
        """.trimIndent()

    ProceduralEffect.GlassTriangleTiles -> """
            vec2 grid = vec2(p.x - p.y / 1.7320508, p.y * 1.1547005) * frequency;
            vec2 cell = floor(grid);
            vec2 f = fract(grid);
            vec2 center = cell + (f.x + f.y < 1.0 ? vec2(0.3333333) : vec2(0.6666667));
            vec2 local = grid - center;
            local = vec2(local.x + local.y * 0.5, local.y * 0.8660254);
            q += local * (intensity + distortion * dot(local, local) * 3.0) / frequency;
        """.trimIndent()

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