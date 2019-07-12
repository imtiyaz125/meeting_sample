package com.meeting_schedule.model.repo

import androidx.lifecycle.MutableLiveData
import com.meeting_schedule.model.bean.requests.GetMeetingRequest
import com.meeting_schedule.model.bean.responses.MeetingResponse
import com.meeting_schedule.model.remote.ApiResponse
import com.meeting_schedule.model.remote.ApiServices
import com.meeting_schedule.model.remote.DataFetchCall
import com.meeting_schedule.model.remote.ReflectionUtil
import kotlinx.coroutines.Deferred
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Response


class HomeRepository constructor(
    private val apiServices: ApiServices
) : KoinComponent {

    /** ReflectionUtil get Object using koin DI
     * used to convert  request Model class to HashMap */
    private val reflectionUtil: ReflectionUtil by inject()

    /** function to get CommentList data Request
     * params* 1. GetContactListRequest is requestDataModel
     * params* 2.LiveData of Response DataModel
     * in which response/error is posted after dataFetch either from network or DB.
     */
    fun getMeetingList(
        getMeetingRequest: GetMeetingRequest,
        MeetingResponse: MutableLiveData<ApiResponse<ArrayList<MeetingResponse>>>
    ) {
        object : DataFetchCall<ArrayList<MeetingResponse>>(MeetingResponse) {

            /*** called when shouldFetchFromDB is false */
            override fun createCallAsync(): Deferred<Response<ArrayList<MeetingResponse>>> {
                return apiServices.getAllMeetings(reflectionUtil.convertPojoToMap(getMeetingRequest))
            }

            /***  called when  API Response is success and before post response to livedata */
            override fun saveResult(result: ArrayList<MeetingResponse>) {
            }
        }.execute()
        /*** execute function is used to call the above dataFetch Request from network/DB */
    }


}