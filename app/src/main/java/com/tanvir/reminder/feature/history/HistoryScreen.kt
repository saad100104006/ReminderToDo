package com.tanvir.reminder.feature.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.extension.hasPassed
import com.tanvir.reminder.feature.history.viewmodel.HistoryState
import com.tanvir.reminder.feature.history.viewmodel.HistoryViewModel
import com.tanvir.reminder.feature.home.ReminderCard
import com.tanvir.reminder.feature.home.ReminderListItem
import com.tanvir.reminder.R

@Composable
fun HistoryRoute(
    navigateToReminderDetail: (Reminder) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state = viewModel.state
    HistoryScreen(
        state = state,
        navigateToReminderDetail = navigateToReminderDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    state: HistoryState,
    navigateToReminderDetail: (Reminder) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .padding(top = 16.dp),
                title = {
                    Text(
                        text = stringResource(id = R.string.history),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.displaySmall,
                    )
                }
            )
        },
        bottomBar = { },
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReminderList(
                state = state,
                navigateToReminderDetail = navigateToReminderDetail
            )
        }
    }
}

@Composable
fun ReminderList(
    state: HistoryState,
    navigateToReminderDetail: (Reminder) -> Unit
) {

    val filteredReminderList = state.reminders.filter { it.time.hasPassed() }
    val sortedReminderList: List<ReminderListItem> = filteredReminderList.sortedBy { it.time }.map { ReminderListItem.ReminderItem(it) }

    when (sortedReminderList.isEmpty()) {
        true -> EmptyView()
        false -> ReminderLazyColumn(sortedReminderList, navigateToReminderDetail)
    }
}

@Composable
fun ReminderLazyColumn(sortedReminderList: List<ReminderListItem>, navigateToReminderDetail: (Reminder) -> Unit) {
    LazyColumn(
        modifier = Modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(
            items = sortedReminderList,
            itemContent = {
                when (it) {
                    is ReminderListItem.OverviewItem -> { }
                    is ReminderListItem.HeaderItem -> {
                        Text(
                            modifier = Modifier
                                .padding(4.dp, 12.dp, 8.dp, 0.dp)
                                .fillMaxWidth(),
                            text = it.headerText.uppercase(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                    is ReminderListItem.ReminderItem -> {
                        ReminderCard(
                            reminder = it.reminder,
                            navigateToReminderDetail = { reminder ->
                                navigateToReminderDetail(reminder)
                            }
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun EmptyView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.headlineMedium,
            text = stringResource(id = R.string.no_history_yet),
            color = MaterialTheme.colorScheme.tertiary
        )
    }
}
