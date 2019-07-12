package com.meeting_schedule.model.remote

import com.meeting_schedule.model.bean.responses.MeetingResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface ApiServices {

    @GET(ApiConstant.GET_MEETINGS)
    fun getAllMeetings(@QueryMap params: HashMap<String, String>): Deferred<Response<ArrayList<MeetingResponse>>>
}