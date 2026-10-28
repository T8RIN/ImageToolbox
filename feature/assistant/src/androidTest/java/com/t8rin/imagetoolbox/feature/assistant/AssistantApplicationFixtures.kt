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

package com.t8rin.imagetoolbox.feature.assistant

import com.t8rin.imagetoolbox.feature.assistant.domain.AssistantRequest

internal object AssistantApplicationFixtures {
    val requests = listOf(
        "ru_open_background" to AssistantRequest("Открой удаление фона."),
        "en_open_background" to AssistantRequest("Open background removal."),
        "ru_compress_500kb" to AssistantRequest("Сожми фотографии до 500 КБ."),
        "en_searchable_pdf" to AssistantRequest("How do I create a searchable PDF with OCR? Open the correct mode."),
        "en_remove_exif" to AssistantRequest("Remove EXIF metadata."),
        "ru_background_then_compress" to AssistantRequest("Удали фон, затем сожми фотографии до 500 КБ."),
        "ru_compress_question" to AssistantRequest("как сжать фото"),
        "en_compress_question" to AssistantRequest("How can I compress a photo?")
    )

    fun prompt(grounding: String, question: String): String =
        "<|im_start|>system\n$grounding<|im_end|>\n<|im_start|>user\n$question<|im_end|>\n<|im_start|>assistant\n<think>\n\n</think>\n\n"
}
