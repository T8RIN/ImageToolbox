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

package com.t8rin.imagetoolbox.texture_generation.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.GLES20
import androidx.core.graphics.createBitmap
import com.t8rin.imagetoolbox.core.utils.appContext
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureFilterType
import com.t8rin.imagetoolbox.texture_generation.domain.model.TextureParams
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.filter.GPUImageTwoInputFilter

internal fun generateRaymarchTexture(
    width: Int,
    height: Int,
    params: TextureParams,
    context: Context = appContext,
    environment: Bitmap? = null
): Bitmap {
    val input = createBitmap(width, height)
    val environmentBitmap = environment ?: createBitmap(1, 1)
    return try {
        GPUImage(context).apply {
            setImage(input)
            setFilter(
                RaymarchTextureFilter(params, environment != null).apply {
                    bitmap = environmentBitmap
                }
            )
        }.bitmapWithFilterApplied
    } finally {
        input.recycle()
        if (environment == null) environmentBitmap.recycle()
    }
}

private class RaymarchTextureFilter(
    private val params: TextureParams,
    private val hasEnvironment: Boolean
) : GPUImageTwoInputFilter(raymarchShader(params.textureFilterType)) {

    override fun onInit() {
        super.onInit()
        val p = params.raymarchParams
        fun uniform(
            name: String,
            value: Float,
            range: ClosedFloatingPointRange<Float>,
            fallback: Float
        ) {
            setFloat(
                GLES20.glGetUniformLocation(program, name),
                if (value.isFinite()) value.coerceIn(range) else fallback
            )
        }
        uniform("hasEnvironment", if (hasEnvironment) 1f else 0f, 0f..1f, 0f)
        uniform("innerRadius", p.innerRadius, 0.5f..1.2f, 0.9f)
        uniform("objectSize", p.size, 0.25f..2.5f, 1f)
        uniform("thickness", p.thickness, 0.03f..0.4f, 0.18f)
        uniform("roundness", p.roundness, 0f..0.2f, 0.06f)
        uniform("count", p.count.toFloat(), 1f..12f, 3f)
        uniform("rotationX", p.rotationX, -180f..180f, 20f)
        uniform("rotationY", p.rotationY, -180f..180f, 30f)
        uniform("rotationZ", p.rotationZ, -180f..180f, 0f)
        uniform("cameraDistance", p.cameraDistance, 1.5f..8f, 3.5f)
        uniform("cameraYaw", p.cameraYaw, -180f..180f, 0f)
        uniform("cameraPitch", p.cameraPitch, -80f..80f, 0f)
        uniform("fieldOfView", p.fieldOfView, 20f..90f, 45f)
        uniform("metallic", p.metallic, 0f..1f, 0.8f)
        uniform("roughness", p.roughness, 0.03f..1f, 0.25f)
        uniform("transmission", p.transmission, 0f..1f, 0f)
        uniform("lightAngle", p.lightAngle, -180f..180f, -35f)
        uniform("lightElevation", p.lightElevation, -80f..80f, 50f)
        fun color(name: String, value: Int) {
            setFloatVec4(
                GLES20.glGetUniformLocation(program, name), floatArrayOf(
                    Color.red(value) / 255f, Color.green(value) / 255f,
                    Color.blue(value) / 255f, Color.alpha(value) / 255f
                )
            )
        }
        color("objectColor", params.foregroundColor.colorInt)
        color("backgroundColor", params.backgroundColor.colorInt)
    }

    override fun onOutputSizeChanged(width: Int, height: Int) {
        super.onOutputSizeChanged(width, height)
        setFloat(GLES20.glGetUniformLocation(program, "aspect"), width.toFloat() / height)
    }
}

private fun raymarchShader(type: TextureFilterType): String {
    val shape = when (type) {
        TextureFilterType.Sphere3D -> "length(p) - 0.75"
        TextureFilterType.Cube3D -> "box(p, vec3(0.65)) - roundness"
        TextureFilterType.CubeStair3D -> "stairs(p) - roundness"
        TextureFilterType.CubeSpheres3D -> "length(abs(p) - vec3(0.43)) - thickness"
        TextureFilterType.EmptiedCube3D -> "max(box(p, vec3(0.7)) - roundness, -(length(p) - innerRadius))"
        TextureFilterType.Torus3D -> "torus(p, 0.62, thickness)"
        TextureFilterType.MobiusTorus3D -> "mobius(p) - roundness * 0.25"
        TextureFilterType.GyroRings3D -> "min(torus(p, 0.68, thickness * 0.5), min(torus(p.yxz, 0.68, thickness * 0.5), torus(p.xzy, 0.68, thickness * 0.5)))"
        TextureFilterType.TorusGem3D -> "(abs(length(p.xz) - 0.62) + abs(p.y) - thickness) * 0.7071 - roundness"
        TextureFilterType.CubeOctahedron3D -> "max(box(p, vec3(0.65)), (dot(abs(p), vec3(1.0)) - 1.2) * 0.57735) - roundness"
        TextureFilterType.InfiniteSpheroids3D -> "max(length(repeatCell(p)) - thickness * 1.5, box(p, vec3(2.4)))"
        TextureFilterType.InfiniteStructure3D -> "max(min(box(repeatCell(p), vec3(0.5, thickness, thickness)), min(box(repeatCell(p), vec3(thickness, 0.5, thickness)), box(repeatCell(p), vec3(thickness, thickness, 0.5)))) - roundness, box(p, vec3(2.4)))"
        TextureFilterType.InfiniteCylinders3D -> "max(length(repeatCell(p).xz) - thickness, box(p, vec3(2.4)))"
        TextureFilterType.TruchetRings3D -> "max(truchet(p, false), box(p, vec3(2.4)))"
        TextureFilterType.TruchetPipes3D -> "max(truchet(p, true), box(p, vec3(2.4)))"
        else -> error("Unsupported 3D texture: $type")
    }
    return """
        precision highp float;
        varying vec2 textureCoordinate;
        uniform float aspect, objectSize, thickness, roundness, count, innerRadius;
        uniform sampler2D inputImageTexture2;
        uniform float hasEnvironment;
        uniform float rotationX, rotationY, rotationZ;
        uniform float cameraDistance, cameraYaw, cameraPitch, fieldOfView;
        uniform float metallic, roughness, transmission, lightAngle, lightElevation;
        uniform vec4 objectColor, backgroundColor;
        const float PI = 3.14159265359;
        mat2 rotate(float a) { float c = cos(a), s = sin(a); return mat2(c,-s,s,c); }
        float box(vec3 p, vec3 b) {
            vec3 q = abs(p) - b;
            return length(max(q, 0.0)) + min(max(q.x, max(q.y, q.z)), 0.0);
        }
        float torus(vec3 p, float radius, float width) {
            return length(vec2(length(p.xz) - radius, p.y)) - width;
        }
        vec3 repeatCell(vec3 p) { return mod(p + 0.5, 1.0) - 0.5; }
        float stairs(vec3 p) {
            float d = 10.0;
            for (int i = 0; i < 6; i++) {
                float step = float(i) / 6.0;
                d = min(d, box(p - vec3(0.0, step - 0.5, step - 0.5),
                    vec3(0.65, 0.09, 0.65 - step * 0.5)));
            }
            return d;
        }
        float mobius(vec3 p) {
            float angle = atan(p.z, p.x);
            vec2 q = rotate(angle * count * 0.5) * vec2(length(p.xz) - 0.65, p.y);
            return box(vec3(q, 0.0), vec3(thickness, 0.035, 1.0));
        }
        float truchet(vec3 p, bool pipes) {
            vec3 cell = floor(p + 0.5);
            vec3 q = repeatCell(p);
            float hash = fract(sin(dot(cell, vec3(127.1, 311.7, 74.7))) * 43758.5453);
            if (hash > 0.5) q.x = -q.x;
            if (hash > 0.75 || hash < 0.25) q = q.yzx;
            if (pipes) {
                float a = length(vec2(length(q.xy - 0.5) - 0.5, q.z));
                float b = length(vec2(length(q.xy + 0.5) - 0.5, q.z));
                return min(a, b) - thickness * 0.45;
            }
            return torus(q, 0.34, thickness * 0.45);
        }
        float scene(vec3 p) {
            p.yz = rotate(radians(rotationX)) * p.yz;
            p.xz = rotate(radians(rotationY)) * p.xz;
            p.xy = rotate(radians(rotationZ)) * p.xy;
            p /= objectSize;
            return ($shape) * objectSize;
        }
        vec3 normalAt(vec3 p) {
            const vec2 e = vec2(0.001, 0.0);
            return normalize(vec3(scene(p+e.xyy)-scene(p-e.xyy),
                scene(p+e.yxy)-scene(p-e.yxy), scene(p+e.yyx)-scene(p-e.yyx)));
        }
        vec3 environment(vec3 direction) {
            if (hasEnvironment > 0.5) {
                vec2 uv = vec2(atan(direction.z, direction.x) / (2.0 * PI) + 0.5,
                    acos(clamp(direction.y, -1.0, 1.0)) / PI);
                vec4 sample = texture2D(inputImageTexture2, uv);
                return sample.rgb / max(sample.a, 0.00001);
            }
            vec3 sky = mix(vec3(0.06,0.09,0.16), vec3(0.75,0.87,1.0), direction.y * 0.5 + 0.5);
            float strip = pow(max(0.0, 1.0 - abs(direction.y - 0.3)), mix(70.0, 3.0, roughness));
            return sky + vec3(1.0,0.88,0.67) * strip * 0.7;
        }
        void main() {
            vec2 uv = (textureCoordinate * 2.0 - 1.0) * vec2(aspect, -1.0);
            float yaw = radians(cameraYaw), pitch = radians(cameraPitch);
            vec3 origin = cameraDistance * vec3(sin(yaw)*cos(pitch), sin(pitch), cos(yaw)*cos(pitch));
            vec3 forward = normalize(-origin);
            vec3 right = normalize(cross(forward, vec3(0.0,1.0,0.0)));
            vec3 up = cross(right, forward);
            vec3 ray = normalize(forward + tan(radians(fieldOfView)*0.5) * (uv.x*right + uv.y*up));
            float distance = 0.0;
            bool hit = false;
            for (int i = 0; i < 128; i++) {
                float step = scene(origin + ray * distance);
                if (abs(step) < 0.001 * max(1.0, distance)) { hit = true; break; }
                distance += max(abs(step) * 0.6, 0.0005);
                if (distance > 30.0) break;
            }
            if (!hit) { gl_FragColor = vec4(backgroundColor.rgb * backgroundColor.a, backgroundColor.a); return; }
            vec3 point = origin + ray * distance;
            vec3 normal = normalAt(point);
            if (dot(normal, ray) > 0.0) normal = -normal;
            float az = radians(lightAngle), el = radians(lightElevation);
            vec3 light = vec3(sin(az)*cos(el), sin(el), cos(az)*cos(el));
            float diffuse = max(dot(normal, light), 0.0);
            vec3 halfVector = normalize(light - ray);
            float specular = pow(max(dot(normal, halfVector), 0.0), mix(160.0, 4.0, roughness));
            float fresnel = pow(1.0 - max(dot(normal, -ray), 0.0), 5.0);
            vec3 tint = objectColor.rgb;
            vec3 matte = tint * (0.18 + diffuse * 0.82);
            vec3 reflected = environment(reflect(ray, normal)) * mix(vec3(1.0), tint, metallic);
            vec3 color = mix(matte, reflected, metallic * 0.75 + fresnel * 0.2);
            color += specular * mix(vec3(0.25), tint, metallic) * (1.0 - roughness * 0.6);
            vec3 glass = environment(refract(ray, normal, 0.67)) * tint;
            color = mix(color, mix(glass, reflected, 0.08 + fresnel * 0.92), transmission);
            gl_FragColor = vec4(clamp(color, 0.0, 1.0) * objectColor.a, objectColor.a);
        }
    """.trimIndent()
}