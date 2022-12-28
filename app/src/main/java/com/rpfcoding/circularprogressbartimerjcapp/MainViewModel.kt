package com.rpfcoding.circularprogressbartimerjcapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {


    private val _model = MutableStateFlow(Model(
        id = 1,
        minutes = 10,
        elapsedTimeInMillis = 0,
        listOfActiveDaysInWeek = Model.listOfWeekdays()
    ))

    private val _isTimerRunning = MutableStateFlow(false)

    private val _isFinished: StateFlow<Boolean> = _model.flatMapLatest {
        flow {
            emit(it.minutes.convertMinutesToMillis() == it.elapsedTimeInMillis)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    private val _isActive: StateFlow<Boolean> = combine(_isFinished, _model) { isFinished, model ->
        !isFinished && model.listOfActiveDaysInWeek.any { it == LocalDate.now().dayOfWeek.value }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), false)

    val state = combine(
        _model,
        _isTimerRunning,
        _isFinished,
        _isActive
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

        if(_isFinished.value) {
            toggle(false)
            onSave()
        }
    }

    private fun onSave() {
        Log.d("onSave()", "Saving record to database.")

        viewModelScope.launch {
            // TODO("Not yet implemented")
        }
    }

    fun setNewMinutes(newMinutes: Int) {
        if(newMinutes.convertMinutesToMillis() > _model.value.elapsedTimeInMillis) {
            viewModelScope.launch {
                _model.update {
                    it.copy(
                        minutes = newMinutes
                    )
                }
            }
        }
    }
}