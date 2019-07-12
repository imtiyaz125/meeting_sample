package com.meeting_schedule.model.bean.requests

import com.google.gson.annotations.SerializedName

class GetMeetingRequest {

    @SerializedName("date")
    var date: String = ""

}