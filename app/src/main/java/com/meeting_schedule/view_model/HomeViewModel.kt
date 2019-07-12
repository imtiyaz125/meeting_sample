package com.meeting_schedule.view_model

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.meeting_schedule.MyApplication
import com.meeting_schedule.model.bean.requests.GetMeetingRequest
import com.meeting_schedule.model.bean.responses.MeetingResponse
import com.meeting_schedule.model.remote.ApiResponse
import com.meeting_schedule.model.repo.HomeRepository

class HomeViewModel constructor(app: MyApplication, var homeRepository: HomeRepository) : AndroidViewModel(app){
    private var meetingListLiveData = MutableLiveData<ApiResponse<ArrayList<MeetingResponse>>>()

    fun getMeetingList(getRequest: GetMeetingRequest) {
        homeRepository.getMeetingList(getRequest, meetingListLiveData)
    }
    fun getMeetingtResponse(): MutableLiveData<ApiResponse<ArrayList<MeetingResponse>>> {
        return meetingListLiveData
    }
}