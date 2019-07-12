package com.meeting_schedule.view.schedule_meeting

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.view.View
import android.view.View.GONE
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.meeting_schedule.R
import com.meeting_schedule.databinding.ActivityScheduleMeetingBinding
import com.meeting_schedule.model.bean.requests.GetMeetingRequest
import com.meeting_schedule.model.bean.responses.MeetingResponse
import com.meeting_schedule.model.remote.ApiResponse
import com.meeting_schedule.utils.Connectivity
import com.meeting_schedule.utils.Utilities
import com.meeting_schedule.view.base.BaseActivity
import com.meeting_schedule.view_model.HomeViewModel
import kotlinx.android.synthetic.main.layout_button.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class ScheduleMeetingActivity : BaseActivity(), View.OnClickListener {

    private var selectedEndTime= Calendar.getInstance()!!
    private var selectedStartTime = Calendar.getInstance()!!
    private lateinit var mBinding: ActivityScheduleMeetingBinding
    private val mViewModel: HomeViewModel by viewModel()
    private var meetingList: ArrayList<MeetingResponse> = ArrayList()
    var date: DatePickerDialog.OnDateSetListener? = null
    val mCalendar = Calendar.getInstance()!!

    override fun getLayout(): Int {
        return R.layout.activity_schedule_meeting
    }

    override fun initUI(binding: ViewDataBinding?) {
        this.mBinding = binding as ActivityScheduleMeetingBinding
        setUpClickListener()
    }

    private fun setUpClickListener() {
        mBinding.meetingDateRL.setOnClickListener(this)
        mBinding.startTimeRL.setOnClickListener(this)
        mBinding.endTimeRL.setOnClickListener(this)
        mBinding.submitLyt.setOnClickListener(this)
        mBinding.toolbar.backTv.setOnClickListener(this)
        mBinding.toolbar.backIv.setOnClickListener(this)
        mBinding.toolbar.backTv.text = getString(R.string.back)
        mBinding.toolbar.nextIv.visibility = GONE
        mBinding.toolbar.nextTv.visibility = GONE
        mBinding.toolbar.meetingDateTv.text = getString(R.string.scedule_title)
        mBinding.submitLyt.submitTv.text = getString(R.string.submit_title)
    }


    private val meetingListObserver: Observer<ApiResponse<ArrayList<MeetingResponse>>> by lazy {
        Observer<ApiResponse<ArrayList<MeetingResponse>>> {
            handleMeetingResponse(it)
        }
    }

    private fun makeGetMeetingApiCall(selectedDate: String) {
        if (Connectivity.isConnected(this)) {

            val request = GetMeetingRequest()
            request.apply {
                date = selectedDate!!
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
                }
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

    override fun onClick(v: View?) {
        when (v!!.id) {
            mBinding.meetingDateRL.id -> {
                showDatePicker()

            }
            mBinding.startTimeRL.id -> {
                openTimePicker(TimePickerType.START_TIME)

            }
            mBinding.endTimeRL.id -> {
                openTimePicker(TimePickerType.END_TIME)
            }
            mBinding.submitLyt.id -> {
                scheduleMeeting()
            }
            mBinding.toolbar.backIv.id, mBinding.toolbar.backTv.id -> {
                finish()
            }
        }
    }

    var mStartTimeString: String = ""
    var mEndTimeString: String = ""
    private fun openTimePicker(type: TimePickerType) {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)


        val timePicker = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener(function = { view, h, m ->


            when (type) {
                TimePickerType.START_TIME -> {
                    mStartTimeString = h.toString() + ":" + m.toString()
                    mBinding.startTimeTv.text = mStartTimeString
                    var startDate = Utilities.parseStringToDate(
                        mBinding.meetingDateTv.text.toString() + " " + mStartTimeString,
                        "dd/MM/yyyy HH:mm"
                    );
                    selectedStartTime.time = startDate
                }
                TimePickerType.END_TIME -> {
                    mEndTimeString = h.toString() + ":" + m.toString()
                    mBinding.endTimeTv.text = mEndTimeString
                    var endDate = Utilities.parseStringToDate(
                        mBinding.meetingDateTv.text.toString() + " " + mEndTimeString,
                        "dd/MM/yyyy HH:mm"
                    )
                    selectedEndTime.time = endDate
                }

            }
        }), hour, minute, true)
        timePicker.show()


    }

    private fun scheduleMeeting() {
        if (mCalendar.timeInMillis == 0.toLong() || mStartTimeString.trim().isEmpty() || mEndTimeString.trim().isEmpty())
            Utilities.showToast(this, getString(R.string.date_time_cant_be_blank))
        else if (selectedStartTime.timeInMillis < Date().time )
            Utilities.showToast(this, getString(R.string.meeting_previous_date_msg))
        else if (selectedStartTime.timeInMillis == 0.toLong() || selectedEndTime.timeInMillis == 0.toLong())
            Utilities.showToast(this, getString(R.string.date_time_cant_be_blank))
        else if (selectedStartTime.timeInMillis >= selectedEndTime.timeInMillis)
            Utilities.showToast(this, getString(R.string.end_time_must_be_greater))
        else {
            var isValidSchedule = true
            meetingList.forEach {

                val startDate = Utilities.parseStringToDate(
                    mBinding.meetingDateTv.text.toString() + " " + it.startTime,
                    "dd/MM/yyyy HH:mm"
                )
                val startTimeMilli = startDate.time

                val endDate = Utilities.parseStringToDate(
                    mBinding.meetingDateTv.text.toString() + " " + it.endTime,
                    "dd/MM/yyyy HH:mm"
                )
                var endTimeMilli = endDate.time

                if (startTimeMilli>=selectedStartTime.timeInMillis && startTimeMilli<=selectedEndTime.timeInMillis) {
                    isValidSchedule = false
                }


            }
            if (!isValidSchedule)
                Utilities.showToast(this, resources.getString(R.string.already_booked_slot))
            else
                Utilities.showToast(this, getString(R.string.meeting_scheduled_successs))

        }
    }

    private fun showDatePicker() {
        date = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            mCalendar.set(Calendar.YEAR, year)
            mCalendar.set(Calendar.MONTH, monthOfYear)
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateText()
            makeGetMeetingApiCall(Utilities.parseDate(mCalendar.timeInMillis))

        }
        DatePickerDialog(
            this, date, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }


    private fun updateDateText() {
        mBinding.meetingDateTv.text = Utilities.parseDate(mCalendar.timeInMillis)
    }
}

enum class TimePickerType {
    START_TIME, END_TIME
}