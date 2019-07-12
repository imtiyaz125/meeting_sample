package com.meeting_schedule.view.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.meeting_schedule.databinding.RowMeetingBinding
import com.meeting_schedule.model.bean.responses.MeetingResponse

class MeetingAdapter(private val context: Context, private val arrList: ArrayList<MeetingResponse>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(RowMeetingBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return arrList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as MyViewHolder).bind(arrList[position])
    }


    /** View Holders */
    inner class MyViewHolder(private val binding: RowMeetingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MeetingResponse) {
            binding.meetingTimeTv.text=item.startTime+"-"+item.endTime
            binding.meetingDescTv.text=item.description
        }
    }


}