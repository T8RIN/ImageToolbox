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

package com.t8rin.imagetoolbox.core.filters.domain.model.shader

object ShaderSamplePresets {
    val Invert: String = """
        {
          "version": 1,
          "name": "Invert",
          "params": [
            {
              "name": "strength",
              "type": "float",
              "default": 1.0,
              "min": 0.0,
              "max": 1.0
            }
          ],
          "shader": "precision mediump float;\n\nvarying vec2 textureCoordinate;\nuniform sampler2D inputImageTexture;\nuniform float strength;\n\nvoid main() {\n    vec4 color = texture2D(inputImageTexture, textureCoordinate);\n    vec3 inverted = 1.0 - color.rgb;\n    gl_FragColor = vec4(mix(color.rgb, inverted, strength), color.a);\n}"
        }
    """.trimIndent()

    val Grayscale: String = """
        {
          "version": 1,
          "name": "Grayscale",
          "params": [
            {
              "name": "strength",
              "type": "float",
              "default": 1.0,
              "min": 0.0,
              "max": 1.0
            }
          ],
          "shader": "precision mediump float;\n\nvarying vec2 textureCoordinate;\nuniform sampler2D inputImageTexture;\nuniform float strength;\n\nvoid main() {\n    vec4 color = texture2D(inputImageTexture, textureCoordinate);\n    float gray = dot(color.rgb, vec3(0.299, 0.587, 0.114));\n    vec3 grayscale = vec3(gray);\n    gl_FragColor = vec4(mix(color.rgb, grayscale, strength), color.a);\n}"
        }
    """.trimIndent()

    val Vignette: String = """
        {
          "version": 1,
          "name": "Vignette",
          "params": [
            {
              "name": "strength",
              "type": "float",
              "default": 0.75,
              "min": 0.0,
              "max": 1.0
            },
            {
              "name": "center",
              "type": "vec2",
              "default": [0.5, 0.5],
              "min": [0.0, 0.0],
              "max": [1.0, 1.0]
            }
          ],
          "shader": "precision mediump float;\n\nvarying vec2 textureCoordinate;\nuniform sampler2D inputImageTexture;\nuniform float strength;\nuniform vec2 center;\n\nvoid main() {\n    vec4 color = texture2D(inputImageTexture, textureCoordinate);\n    float distanceFromCenter = distance(textureCoordinate, center);\n    float vignette = smoothstep(0.8, 0.2, distanceFromCenter);\n    vec3 result = mix(color.rgb, color.rgb * vignette, strength);\n    gl_FragColor = vec4(result, color.a);\n}"
        }
    """.trimIndent()

    val All: Set<String> = setOf(Invert, Grayscale, Vignette)
}
