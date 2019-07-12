package com.meeting_schedule.di.koin
import com.meeting_schedule.model.repo.HomeRepository
import org.koin.dsl.module



val repoModule = module {

    /**Provide ContactRepository class Singleton object
     * you can use it any KoinComponent class  below is declaration
     *  private val globalRepository: ContactRepository by inject() */

    single { HomeRepository(get()) }

}