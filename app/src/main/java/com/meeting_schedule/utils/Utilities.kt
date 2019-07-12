package com.meeting_schedule.utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*


object Utilities {

    fun parseDate(time: Long): String {
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            return sdf.format(time)
        } catch (e: java.lang.Exception) {
            return ""
        }
    }

    fun parseStringToDate(date: String, format: String): Date {
        try {
            val sdf = SimpleDateFormat(format, Locale.US)
            return sdf.parse(date)
        } catch (e: java.lang.Exception) {
            return Date();
        }
    }

    var toast: Toast? = null
    fun showToast(context: Context?, msg: String) {
        if (toast != null)
            toast!!.cancel()
        if (context != null) {
            toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
            toast!!.show()
        }

    }

}