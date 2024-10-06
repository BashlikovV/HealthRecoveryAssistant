package by.bashlikovvv.domain.base

sealed class BaseResult<out T> {
    data class Success<T>(
        val data: T
    ) : BaseResult<T>()

    data class Failure(
        val exception: Exception,
    ) : BaseResult<Nothing>()
}