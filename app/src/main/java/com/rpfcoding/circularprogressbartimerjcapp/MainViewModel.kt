package com.rpfcoding.circularprogressbartimerjcapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {


    private val _model = MutableStateFlow(Model())
    val model = _model.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    val isFinished: StateFlow<Boolean> = model.flatMapLatest {
        flow {
            val min = it.minutes * 60L * 1000L
            emit(min == it.elapsedTimeInMillis)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    val isActive: StateFlow<Boolean> = combine(isFinished, model) { isFinished, model ->
        !isFinished && model.listOfActiveDaysInWeek.any { it == LocalDate.now().dayOfWeek.value }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    val state = combine(
        model,
        isTimerRunning,
        isFinished,
        isActive
    ) { model, isTimerRunning, isFinished, isActive ->
        State(
            model = model,
            isFinished = isFinished,
            isActive = isActive,
            isTimerRunning = isTimerRunning
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), State())

    fun toggle(value: Boolean) {
        _isTimerRunning.update { value }
    }

    fun updateElapsedTime(elapsedTime: Long) {
        Log.d("updateElapsedTime()", elapsedTime.toString())

        _model.update {
            it.copy(elapsedTimeInMillis = elapsedTime)
        }

        if(isFinished.value) {
            toggle(false)
            onSave()
        }
    }

    private fun onSave() {
        Log.d("onSave()", "Saving record to database.")

        // TODO("Not yet implemented")
    }
}