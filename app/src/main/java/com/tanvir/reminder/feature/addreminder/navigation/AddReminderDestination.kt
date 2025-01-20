package com.tanvir.reminder.feature.addreminder.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tanvir.reminder.core.navigation.ReminderNavigationDestination
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.feature.addreminder.AddReminderRoute
import com.tanvir.reminder.feature.home.navigation.ASK_ALARM_PERMISSION
import com.tanvir.reminder.feature.home.navigation.ASK_NOTIFICATION_PERMISSION

object AddReminderDestination : ReminderNavigationDestination {
    override val route = "add_reminder_route"
    override val destination = "add_reminder_destination"
}

fun NavGraphBuilder.addReminderGraph(navController: NavController, bottomBarVisibility: MutableState<Boolean>, fabVisibility: MutableState<Boolean>, onBackClicked: () -> Unit, navigateToReminderConfirm: (List<Reminder>) -> Unit) {
    composable(route = AddReminderDestination.route) {
        LaunchedEffect(null) {
            bottomBarVisibility.value = false
            fabVisibility.value = false
        }

        navController.previousBackStackEntry?.savedStateHandle.apply {
            this?.set(ASK_NOTIFICATION_PERMISSION, true)
        }
        navController.previousBackStackEntry?.savedStateHandle.apply {
            this?.set(ASK_ALARM_PERMISSION, true)
        }
        AddReminderRoute(onBackClicked, navigateToReminderConfirm)
    }
}
