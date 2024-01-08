package ru.tech.imageresizershrinker.core.domain.saving

sealed interface SaveResult {

    data class Success(val message: String? = null) : SaveResult

    sealed interface Error : SaveResult {
        data object MissingPermissions : Error
        class Exception(val throwable: Throwable) : Error
    }
}