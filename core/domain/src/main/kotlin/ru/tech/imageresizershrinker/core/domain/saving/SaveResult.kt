package ru.tech.imageresizershrinker.core.domain.saving

sealed interface SaveResult {

    sealed class Success : SaveResult {
        data class WithData(
            val filename: String,
            val savingPath: String
        ) : Success()

        data object WithoutToast : Success()
    }

    sealed interface Error : SaveResult {
        data object MissingPermissions : Error
        class Exception(val throwable: Throwable) : Error
    }
}