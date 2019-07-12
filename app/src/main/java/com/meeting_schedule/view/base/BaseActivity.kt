package com.meeting_schedule.view.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry


abstract class BaseActivity : AppCompatActivity(), BaseInterFace, LifecycleOwner {

    private var lifecycleRegistry = LifecycleRegistry(this)


    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutIdRes = layout
        if (layoutIdRes != 0) {
            val binding = DataBindingUtil.setContentView(this, layoutIdRes) as ViewDataBinding
            initUI(binding)
        }
    }
}
