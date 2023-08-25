package com.smarttoolfactory.colorpicker.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class HexVisualTransformation(private val useAlpha: Boolean) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {

        val limit = if (useAlpha) 8 else 6

        val trimmed =
            if (text.text.length >= limit) text.text.substring(0 until limit) else text.text

        val output = if (trimmed.isEmpty()) {
            trimmed
        } else {
            if (!useAlpha || trimmed.length < 7) {
                "#${trimmed.uppercase()}"
            } else {
                "#${
                    trimmed.substring(0, 2).lowercase() +
                            trimmed.substring(2, trimmed.length).uppercase()
                }"
            }
        }

        return TransformedText(
            AnnotatedString(output),
            if (useAlpha) hexAlphaOffsetMapping else hexOffsetMapping
        )
    }

    private val hexOffsetMapping = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {

            // when empty return only 1 char for #
            if (offset == 0) return offset
            if (offset <= 5) return offset + 1
            return 7
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset == 0) return offset
            // #ABCABC
            if (offset <= 6) return offset - 1
            return 6
        }
    }

    private val hexAlphaOffsetMapping = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {

            // when empty return only 1 char for #
            if (offset == 0) return offset
            if (offset <= 7) return offset + 1
            return 9
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset == 0) return offset
            // #ffABCABC
            if (offset <= 8) return offset - 1
            return 8
        }
    }
}
