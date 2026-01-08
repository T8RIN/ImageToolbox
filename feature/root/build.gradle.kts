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

plugins {
    alias(libs.plugins.image.toolbox.library)
    alias(libs.plugins.image.toolbox.feature)
    alias(libs.plugins.image.toolbox.hilt)
    alias(libs.plugins.image.toolbox.compose)
}

android.namespace = "com.t8rin.imagetoolbox.feature.root"

dependencies {
    implementation(projects.feature.main)
    implementation(projects.feature.loadNetImage)
    implementation(projects.feature.crop)
    implementation(projects.feature.limitsResize)
    implementation(projects.feature.cipher)
    implementation(projects.feature.imagePreview)
    implementation(projects.feature.weightResize)
    implementation(projects.feature.compare)
    implementation(projects.feature.deleteExif)
    implementation(projects.feature.paletteTools)
    implementation(projects.feature.resizeConvert)
    implementation(projects.feature.pdfTools)
    implementation(projects.feature.singleEdit)
    implementation(projects.feature.eraseBackground)
    implementation(projects.feature.draw)
    implementation(projects.feature.filters)
    implementation(projects.feature.imageStitch)
    implementation(projects.feature.pickColor)
    implementation(projects.feature.recognizeText)
    implementation(projects.feature.gradientMaker)
    implementation(projects.feature.watermarking)
    implementation(projects.feature.gifTools)
    implementation(projects.feature.apngTools)
    implementation(projects.feature.zip)
    implementation(projects.feature.jxlTools)
    implementation(projects.feature.settings)
    implementation(projects.feature.easterEgg)
    implementation(projects.feature.svgMaker)
    implementation(projects.feature.formatConversion)
    implementation(projects.feature.documentScanner)
    implementation(projects.feature.scanQrCode)
    implementation(projects.feature.imageStacking)
    implementation(projects.feature.imageSplitting)
    implementation(projects.feature.colorTools)
    implementation(projects.feature.webpTools)
    implementation(projects.feature.noiseGeneration)
    implementation(projects.feature.collageMaker)
    implementation(projects.feature.librariesInfo)
    implementation(projects.feature.markupLayers)
    implementation(projects.feature.base64Tools)
    implementation(projects.feature.checksumTools)
    implementation(projects.feature.meshGradients)
    implementation(projects.feature.editExif)
    implementation(projects.feature.imageCutting)
    implementation(projects.feature.audioCoverExtractor)
    implementation(projects.feature.libraryDetails)
    implementation(projects.feature.wallpapersExport)
    implementation(projects.feature.asciiArt)
    implementation(projects.feature.aiTools)
}