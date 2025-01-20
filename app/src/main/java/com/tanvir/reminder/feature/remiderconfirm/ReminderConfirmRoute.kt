package com.tanvir.reminder.feature.remiderconfirm

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.extension.toFormattedDateString
import com.tanvir.reminder.feature.remiderconfirm.viewmodel.ReminderConfirmState
import com.tanvir.reminder.feature.remiderconfirm.viewmodel.ReminderConfirmViewModel
import com.tanvir.reminder.util.SnackbarUtil.Companion.showSnackbar
import com.tanvir.reminder.R

@Composable
fun ReminderConfirmRoute(
    reminder: List<Reminder>?,
    onBackClicked: () -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReminderConfirmViewModel = hiltViewModel()
) {
    reminder?.let {
        ReminderConfirmScreen(
            reminders = it,
            viewModel = viewModel,
            onBackClicked = onBackClicked,
            navigateToHome = navigateToHome,
        )
    } ?: {
        FirebaseCrashlytics.getInstance().log("Error: Cannot show ReminderConfirmScreen. Reminder is null.")
    }
}

@Composable
fun ReminderConfirmScreen(
    reminders: List<Reminder>,
    viewModel: ReminderConfirmViewModel,
    onBackClicked: () -> Unit,
    navigateToHome: () -> Unit,
) {

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel
            .isReminderSaved
            .collect {
                showSnackbar(
                    context.getString(
                        R.string.reminder_timely_reminders_setup_message,
                        reminders.first().title
                    )
                )
                navigateToHome()
            }
    }

    Column(
        modifier = Modifier.padding(0.dp, 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FloatingActionButton(
            onClick = {
                onBackClicked()
            },
            elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(id = R.string.back)
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.all_done),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall
        )

        val reminder = reminders.first()
        Text(
            text = pluralStringResource(
                id = R.plurals.all_set,
                count = reminders.size,
                reminder.title,
                reminders.size,
                reminder.recurrence.lowercase(),
                reminder.endDate.toFormattedDateString()
            ),
            style = MaterialTheme.typography.titleMedium
        )
    }

    Column(
        modifier = Modifier
            .padding(0.dp, 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .align(Alignment.CenterHorizontally),
            onClick = {
                viewModel.addReminder(ReminderConfirmState(reminders))
            },
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Text(
                text = stringResource(id = R.string.confirm),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
