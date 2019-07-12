package com.meeting_schedule.model.remote

import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response


abstract class DataFetchCall<ResultType>(private val responseLiveData: MutableLiveData<ApiResponse<ResultType>>) {


    abstract fun createCallAsync(): Deferred<Response<ResultType>>
    abstract fun saveResult(result: ResultType)

    fun execute() {
        responseLiveData.postValue(ApiResponse.loading())

        GlobalScope.launch {
            try {
                val request = createCallAsync()
                val response = request.await()
                if (response?.body() != null) {
                    saveResult(response.body()!!)
                    responseLiveData.postValue(
                        ApiResponse.success(
                            response.body()!!
                        )
                    )
                } else {
                    responseLiveData.postValue(
                        ApiResponse.error(
                            Throwable(response.message())
                        )
                    )
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                responseLiveData.postValue(ApiResponse.error(exception))
            }
        }
    }

}