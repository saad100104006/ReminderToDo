package com.tanvir.reminder.feature.history.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanvir.reminder.feature.home.usecase.GetRemindersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase
) : ViewModel() {

    var state by mutableStateOf(HistoryState())
        private set

    init {
        loadReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            getRemindersUseCase.getReminders().onEach { reminderList ->
                state = state.copy(
                    reminders = reminderList
                )
            }.launchIn(viewModelScope)
        }
    }
}
