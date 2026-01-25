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

package com.t8rin.imagetoolbox.feature.recognize.text.domain


internal object TessConstants {

    const val LANGUAGE_CODE = "%s.traineddata"

    const val TESSERACT_DATA_DOWNLOAD_URL_BEST =
        "https://github.com/tesseract-ocr/tessdata_best/raw/refs/heads/main/%s.traineddata"
    const val TESSERACT_DATA_DOWNLOAD_URL_STANDARD =
        "https://github.com/tesseract-ocr/tessdata/raw/refs/heads/main/%s.traineddata"
    const val TESSERACT_DATA_DOWNLOAD_URL_FAST =
        "https://github.com/tesseract-ocr/tessdata_fast/raw/refs/heads/main/%s.traineddata"

    const val KEY_PRESERVE_INTERWORD_SPACES = "preserve_interword_spaces"
    const val KEY_CHOP_ENABLE = "chop_enable"
    const val KEY_USE_NEW_STATE_COST = "use_new_state_cost"
    const val KEY_SEGMENT_SEGCOST_RATING = "segment_segcost_rating"
    const val KEY_ENABLE_NEW_SEGSEARCH = "enable_new_segsearch"
    const val KEY_LANGUAGE_MODEL_NGRAM_ON = "language_model_ngram_on"
    const val KEY_TEXTORD_FORCE_MAKE_PROP_WORDS = "textord_force_make_prop_words"
    const val KEY_EDGES_MAX_CHILDREN_PER_OUTLINE = "edges_max_children_per_outline"
}