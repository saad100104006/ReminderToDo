package com.tanvir.reminder.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanvir.reminder.R
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.extension.hasPassed
import com.tanvir.reminder.extension.toFormattedDateString
import com.tanvir.reminder.extension.toFormattedTimeString
import com.tanvir.reminder.ui.theme.Pink40
import com.tanvir.reminder.ui.theme.Purple80
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderCard(
    reminder: Reminder,
    navigateToReminderDetail: (Reminder) -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = { navigateToReminderDetail(reminder) },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier
                    .weight(2f),
                horizontalAlignment = Alignment.Start
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = MaterialTheme.typography.titleSmall,
                        text = reminder.endDate.toFormattedDateString().uppercase(),
                        color = MaterialTheme.colorScheme.primary
                    )

                    Box(
                        modifier = Modifier
                            .border(2.dp, if(reminder.isFromServer) Pink40 else Purple80 , shape = RoundedCornerShape(20.dp) )
                            .background(if(reminder.isFromServer) Pink40 else Purple80 , shape = RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            text = if(reminder.isFromServer) "From Server" else "You Created this" ,
                            color = Color.White,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }


                Text(
                    text = if (reminder.title.length > 30) reminder.title.take(30) + "..." else reminder.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = reminder.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleSmall
                )

                val medicationStatusText = when {
                    reminder.time.hasPassed() -> {
                        if (reminder.isCompleted) {
                            stringResource(
                                id = R.string.reminder_taken_at,
                                reminder.time.toFormattedTimeString()
                            )
                        } else {
                            stringResource(
                                id = R.string.reminder_skipped_at,
                                reminder.time.toFormattedTimeString()
                            )
                        }
                    }

                    else -> stringResource(
                        id = R.string.reminder_scheduled_at,
                        reminder.time.toFormattedTimeString()
                    )
                }

                Text(
                    text = medicationStatusText,
                    color = MaterialTheme.colorScheme.primary
                )

            }

            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Preview
@Composable
private fun MedicationCardTakeNowPreview() {
    ReminderCard(
        Reminder(
            id = 123L,
            title = "A big big name for a little medication I needs to take",
            description = "This is a sample description",
            recurrence = "2",
            endDate = Date(),
            time = Date(),
            isCompleted = false,
            isFromServer = false
        )
    ) { }
}

@Preview
@Composable
private fun MedicationCardTakenPreview() {
    ReminderCard(
        Reminder(
            id = 123L,
            title = "A big big name for a little medication I needs to take",
            description = "This is a sample description",
            recurrence = "2",
            endDate = Date(),
            time = Date(),
            isCompleted = true,
            isFromServer = false
        )
    ) { }
}
