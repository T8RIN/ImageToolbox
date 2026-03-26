package io.github.alexzhirkevich.qrose.oned

enum class BarcodeType(val encoder: BarcodeEncoder) {

    Codabar(CodabarEncoder),
    Code39(Code39Encoder),
    Code93(Code93Encoder),
    Code128(Code128Encoder),
    EAN8(CodeEAN8Encoder),
    EAN13(CodeEAN13Encoder),
    ITF(CodeITFEncoder),
    UPCA(CodeUPCAEncoder),
    UPCE(CodeUPCEEncoder)
}