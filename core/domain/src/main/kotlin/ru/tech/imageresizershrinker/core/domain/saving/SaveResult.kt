package ru.tech.imageresizershrinker.core.domain.saving

sealed interface SaveResult {
    class Success(
        val filename: String,
        val savingPath: String
    ) : SaveResult

    sealed interface Error : SaveResult {
        data object MissingPermissions : Error
        class Exception(val throwable: Throwable) : Error
    }
}