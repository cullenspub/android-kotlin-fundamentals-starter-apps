/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        val app: Application) : AndroidViewModel(app) {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private var tonight = MutableLiveData<SleepNight>()
    val nights = database.getAllNights()
    val nightsString = Transformations.map(nights) {nights ->
        formatNights(nights, app.resources)
    }

    init{
        initializeTonight()
    }

    // Navigation Events - LiveData
    // Usage: View should observe this event
    //        If Observer handles the event, call the
    //        navigateToSleepQualityCompleted function
    private val _navigateToSleepQualityEvent = MutableLiveData<SleepNight>()
    val navigateToSleepQualityEvent: LiveData<SleepNight>
        get() = _navigateToSleepQualityEvent


    // View Model Overrides
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    // ViewModel API
    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()
            addNightToDatabase(newNight)
            tonight.value = getTonightFromDatabase()
        }
    }

    fun onClearTracking() {
        uiScope.launch {
            deleteAllSleepData()
        }
    }

    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            updateNight(oldNight)
            _navigateToSleepQualityEvent.value = oldNight
        }
    }

    fun onClickSleepNight(id: Long) {
        val toast = Toast.makeText(app,"Testing - selected $id", Toast.LENGTH_LONG)
        toast.show()
    }

    fun navigateToSleepQualityCompleted() {
        _navigateToSleepQualityEvent.value = null
    }

    /**
     * Transformations to support control enablement
     */
    val startButtonEnabled = Transformations.map(tonight){
        it == null
    }

    val stopButtonEnabled = Transformations.map(tonight){
        it != null
    }

    val clearButtonEnabled = Transformations.map(nights) {
        it.size > 0
    }

    /**
     * ViewModel Private Methods
     **/
    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun deleteAllSleepData() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    private suspend fun updateNight(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }

    private suspend fun addNightToDatabase(night: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(night)
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if(night?.startTimeMilli != night?.endTimeMilli) {
                night = null
            }
            night
        }
    }



}

