package com.meeting_schedule.model.bean.responses

class MeetingResponse(val startTime: String = "",
                           val endTime: String = "",
                           val description: String = "",
                           val participants: List<String>?)