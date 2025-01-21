package com.tanvir.reminder.feature.home.viewmodel

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.extension.toFormattedYearMonthDateString
import com.tanvir.reminder.feature.home.model.CalendarModel
import com.tanvir.reminder.feature.home.usecase.GetApiReminderUseCase
import com.tanvir.reminder.feature.home.usecase.GetRemindersUseCase
import com.tanvir.reminder.feature.home.usecase.UpdateReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val getApiReminderUseCase: GetApiReminderUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(Date())
    private val _dateFilter = savedStateHandle.getStateFlow(
        DATE_FILTER_KEY,
        Date().toFormattedYearMonthDateString()
    )
    private val _greeting = MutableStateFlow("")
    private val _userName = MutableStateFlow("")

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _reminders = _dateFilter.flatMapLatest { selectedDate ->
        getRemindersUseCase.getReminders(selectedDate)
    }

    private val _toDoList = MutableStateFlow<List<Reminder>>(emptyList())
    val toDoList: StateFlow<List<Reminder>> = _toDoList.asStateFlow()


    val homeUiState = combine(
        _selectedDate,
        _reminders,
        _dateFilter,
        _greeting,
        _userName
    ) { selectedDate, reminders, dateFilter, greeting, userName ->
        HomeState(
            lastSelectedDate = dateFilter,
            reminders = reminders.sortedBy { it.time },
            greeting = greeting,
            userName = userName
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeState(lastSelectedDate = Date().toFormattedYearMonthDateString())
    )

    fun updateSelectedDate(date: Date) {
        _selectedDate.value = date
    }

    init {
        getUserName()
        getGreeting()
    }

    private fun getUserName() {
        _userName.value = "Kathryn"
    }

    fun fetchToDos() {
        viewModelScope.launch {
            try {
                val response = getApiReminderUseCase.getReminders()
                _toDoList.value = response
            } catch (e: Exception) {
                _errorMessage.value = e.message?: "Unknown Error"
            }
        }
    }

    private fun getGreeting() {
        _greeting.value = "Greeting"
    }

    fun selectDate(selectedDate: CalendarModel.DateModel) {
        savedStateHandle[DATE_FILTER_KEY] = selectedDate.date.toFormattedYearMonthDateString()
    }

    companion object {
        const val DATE_FILTER_KEY = "reminder_date_filter"
    }

}
