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

package com.example.android.trackmysleepquality.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import kotlinx.coroutines.*

class SleepQualityViewModel(
        private val sleepNightKey: Long = 0,
        val database: SleepDatabaseDao): ViewModel() {

    /**
     *  Setup for coroutine calls
     */
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     *     Setup for navigation back to the main tracker screen
     *     Observer will listen for a true value on navigate to sleep tracker
     *     perform the navigation, then call the navigationToSleepTrackerCompeted
     *     funcition
     */
    private val _navigateToSleepTracker = MutableLiveData<Boolean>()
    val navigateToSleepTracker: LiveData<Boolean>
        get() = _navigateToSleepTracker

    fun navigatationToSleepTrackerCompelete() {
        _navigateToSleepTracker.value = null
    }

    /**
     * Click handler set the sleep quality and raise the navigate event
     */
    fun setSleepQuality(value: Int) {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                val tonight = database.getTonight() ?: return@withContext
                tonight.sleepQuality = value
                database.update(tonight)
            }
            _navigateToSleepTracker.value = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}