package ru.tech.imageresizershrinker.domain.saving

sealed interface SaveResult {
    class Success(
        filename: String,
        savingPath: String
    ) : SaveResult

    sealed interface Error : SaveResult {
        data object MissingPermissions : Error
        class Exception(throwable: Throwable) : Error
    }
}