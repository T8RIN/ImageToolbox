package io.github.alexzhirkevich.qrose.oned

/**
 * Single dimension barcode encoder.
 * */
interface BarcodeEncoder {

    /**
     * Encodes string [data] into barcode [BooleanArray] where 1 is a black bar and 0 is a white bar.
     *
     * Illegal contents can throw exceptions
     * */
    fun encode(data: String): BooleanArray
}