package eu.wewox.modalsheet

/**
 * Used for annotating experimental modal sheet API that is likely to change or be removed in the
 * future.
 */
@RequiresOptIn(
    "This API is experimental and is likely to change or to be removed in the future."
)
@Retention(AnnotationRetention.BINARY)
public annotation class ExperimentalSheetApi
