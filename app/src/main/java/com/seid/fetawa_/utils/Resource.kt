package com.seid.fetawa_.utils


data class Resource<out T>(
    val status: Status,
    val data: T?,
    val error: String?
) {
    enum class Status {
        SUCCESS,
        ERROR,
        LOADING,
        INITIAL
    }

    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(
            error: String?,
            data: T? = null,
        ): Resource<T> {
            return Resource(Status.ERROR, data, error)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }

        fun <T> initial(data: T? = null): Resource<T> {
            return Resource(Status.INITIAL, data, null)
        }
    }
}

