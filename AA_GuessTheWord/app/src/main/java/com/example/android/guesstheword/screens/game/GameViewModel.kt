package com.example.android.guesstheword.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.util.concurrent.CountDownLatch

class GameViewModel : ViewModel() {
    companion object {
        private const val DONE = 0L
        private const val ONE_SECOND = 1000L
        private const val COUNTDOWN_TIME = 60L * ONE_SECOND
    }

    private lateinit var wordList: MutableList<String>
    private val timer: CountDownTimer

    // Current Time
    private var _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    val currentTimeString = Transformations.map(currentTime) {time ->
        DateUtils.formatElapsedTime(time)
    }

    // The current word
    private var _word = MutableLiveData<String>()
    val word: LiveData<String>
        get() = _word

    override fun onCleared() {
        super.onCleared()
        timer.cancel()
    }

    // The current score
    private var _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    // game finished event
    private var _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinish

    init{
        _word.value = ""
        _score.value = 0
        resetList()
        nextWord()
        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND) {
            override fun onFinish() {
                onGameFinish()
            }

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished / ONE_SECOND
            }
        }
        timer.start()

    }


    /**
     * Event processing for Game Finished
     */
    fun onGameFinish() {
        _eventGameFinish.value = true
    }

    fun gameFinishHandled() {
        _eventGameFinish.value = false
    }

    /** Methods for buttons presses **/
    fun onSkip() {
        _score.value = (_score.value)?.minus(1)
        nextWord()
    }

    fun onCorrect() {
        _score.value = (_score.value)?.plus(1)
        nextWord()
    }



    /**
     * Resets the list of words and randomizes the order
     */
    private fun resetList() {
        wordList = mutableListOf(
                "queen",
                "hospital",
                "basketball",
                "cat",
                "change",
                "snail",
                "soup",
                "calendar",
                "sad",
                "desk",
                "guitar",
                "home",
                "railway",
                "zebra",
                "jelly",
                "car",
                "crow",
                "trade",
                "bag",
                "roll",
                "bubble"
        )
        wordList.shuffle()
    }

    /**
     * Moves to the next word in the list
     * Triggers GameFinish event when no more words
     */
    private fun nextWord() {
        if (wordList.isEmpty()) {
            onGameFinish()
        } else {
            //Select and remove a word from the list
            _word.value = wordList.removeAt(0)
        }
    }

}