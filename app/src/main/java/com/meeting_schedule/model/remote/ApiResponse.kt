package com.meeting_schedule.model.remote

import com.meeting_schedule.MyApplication
import com.meeting_schedule.R
import java.net.SocketTimeoutException


class ApiResponse<T>(val status: Status, val data: T?, val error: Throwable?) {

    var errorMessage: String? = null
    var errorType: Error? = null

    init {
        if (error == null && status == Status.ERROR) {
            errorType = Error.API_ERROR
        } else if (error is SocketTimeoutException) {
            errorType = Error.TIMEOUT_ERROR
            errorMessage = MyApplication.context.getString(R.string.network_error)
        } else {
            errorType = Error.SERVER_ERROR
            errorMessage = MyApplication.context.getString(R.string.internal_server_error)
        }
    }

    companion object {
        fun <T> loading(): ApiResponse<T> {
            return ApiResponse(
                Status.LOADING,
                null,
                null
            )
        }

        fun <T> success(data: T?): ApiResponse<T> {
            return ApiResponse(
                Status.SUCCESS,
                data,
                null
            )
        }

        fun <T> error(error: Throwable?): ApiResponse<T> {
            return ApiResponse(
                Status.ERROR,
                null,
                error
            )
        }
    }

    enum class Status {
        LOADING,
        SUCCESS,
        ERROR,
    }

    enum class Error {
        SERVER_ERROR,
        TIMEOUT_ERROR,
        API_ERROR

    }
}