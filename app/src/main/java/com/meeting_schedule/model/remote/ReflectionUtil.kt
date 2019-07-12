package com.meeting_schedule.model.remote

import com.google.gson.Gson
import java.util.*


class ReflectionUtil(private val gson: Gson) {


    fun convertPojoToMap(model: Any): HashMap<String, String> {
        val json = gson.toJson(model)
        return gson.fromJson<HashMap<String, String>>(json, HashMap::class.java)
    }



}