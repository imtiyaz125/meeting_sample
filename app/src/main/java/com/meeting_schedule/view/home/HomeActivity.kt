package com.meeting_schedule.view.home

import android.app.ProgressDialog
import android.content.Intent
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.meeting_schedule.R
import com.meeting_schedule.databinding.ActivityHomeBinding
import com.meeting_schedule.model.bean.requests.GetMeetingRequest
import com.meeting_schedule.model.bean.responses.MeetingResponse
import com.meeting_schedule.model.remote.ApiResponse
import com.meeting_schedule.utils.Connectivity
import com.meeting_schedule.utils.Utilities
import com.meeting_schedule.view.base.BaseActivity
import com.meeting_schedule.view.home.adapter.MeetingAdapter
import com.meeting_schedule.view.schedule_meeting.ScheduleMeetingActivity
import com.meeting_schedule.view_model.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList


class HomeActivity : BaseActivity(), View.OnClickListener {


    private var date: Date = Date()
    private var timeInMilli: Long = date.time
    private lateinit var mBinding: ActivityHomeBinding
    private lateinit var mAdapter: MeetingAdapter
    private val mViewModel: HomeViewModel by viewModel()
    private var meetingList: ArrayList<MeetingResponse> = ArrayList()


    companion object {
        var ONE_DAY_MILLI = 86400000
    }

    override fun getLayout(): Int {
        return R.layout.activity_home
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.mBinding = binding as ActivityHomeBinding
        initRecyclerView()
        makeGetMeetingApiCall()
        setUpClickListener()

        mBinding.submitLyt.setOnClickListener(this)


    }

    private fun setUpClickListener() {
        mBinding.toolbar.backIv.setOnClickListener(this)
        mBinding.toolbar.nextIv.setOnClickListener(this)
        mBinding.toolbar.backTv.setOnClickListener(this)
        mBinding.toolbar.nextTv.setOnClickListener(this)
        mBinding.submitLyt.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            mBinding.submitLyt.id -> {
                startActivity(Intent(this, ScheduleMeetingActivity::class.java))
            }
            mBinding.toolbar.backIv.id, mBinding.toolbar.backTv.id -> {
                timeInMilli -= ONE_DAY_MILLI
                makeGetMeetingApiCall()
            }
            mBinding.toolbar.nextIv.id, mBinding.toolbar.nextTv.id -> {
                timeInMilli += ONE_DAY_MILLI
                makeGetMeetingApiCall()
            }
        }
    }

    private fun initRecyclerView() {
        mBinding.meetingsRv.layoutManager = LinearLayoutManager(this)
        mAdapter = MeetingAdapter(this, meetingList)
        mBinding.meetingsRv.adapter = mAdapter

    }

    private val meetingListObserver: Observer<ApiResponse<ArrayList<MeetingResponse>>> by lazy {
        Observer<ApiResponse<ArrayList<MeetingResponse>>> {
            handleMeetingResponse(it)
        }
    }

    private fun makeGetMeetingApiCall() {

        if (Connectivity.isConnected(this)) {

            val dateOfMeeting = Utilities.parseDate(timeInMilli)
            mBinding.toolbar.meetingDateTv.text = dateOfMeeting
            val request = GetMeetingRequest()
            request.apply {
                date = dateOfMeeting!!
            }
            /*** request viewModel to get data ***/
            mViewModel.getMeetingList(request)
            /*** observe live data of viewModel*/
            mViewModel.getMeetingtResponse().observe(this, meetingListObserver)
        } else {
            Utilities.showToast(this, resources.getString(R.string.no_network_error))

        }
    }

    /* Response Handlers */
    private fun handleMeetingResponse(response: ApiResponse<ArrayList<MeetingResponse>>) {
        when (response.status) {
            ApiResponse.Status.LOADING -> {
                progressDialog.show()
            }
            ApiResponse.Status.SUCCESS -> {
                progressDialog.dismiss()
                if (!response.data?.isNullOrEmpty()!!) {
                    meetingList.addAll(response.data)
                } else {
                    meetingList.clear()
                    Utilities.showToast(this, getString(R.string.no_meeting))
                }
                mAdapter.notifyDataSetChanged()
            }

            ApiResponse.Status.ERROR -> {
                progressDialog.dismiss()
                Utilities.showToast(this, response.errorMessage.toString())
            }
        }
    }

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this).apply {
            setMessage("Loading Please wait...")
        }
    }

}