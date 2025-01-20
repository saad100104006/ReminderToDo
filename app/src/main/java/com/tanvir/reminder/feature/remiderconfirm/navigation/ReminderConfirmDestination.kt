package com.tanvir.reminder.feature.remiderconfirm.navigation

import android.os.Bundle
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.tanvir.reminder.core.navigation.ReminderNavigationDestination
import com.tanvir.reminder.domain.model.Reminder
import com.tanvir.reminder.feature.remiderconfirm.ReminderConfirmRoute

const val REMINDER = "reminder"

object ReminderConfirmDestination : ReminderNavigationDestination {
    override val route = "reminder_confirm_route"
    override val destination = "reminder_confirm_destination"
}

fun NavGraphBuilder.reminderConfirmGraph(navController: NavController, bottomBarVisibility: MutableState<Boolean>, fabVisibility: MutableState<Boolean>, onBackClicked: () -> Unit, navigateToHome: () -> Unit) {

    composable(
        route = ReminderConfirmDestination.route,
    ) {
        LaunchedEffect(null) {
            bottomBarVisibility.value = false
            fabVisibility.value = false
        }
        val reminderBundle = navController.previousBackStackEntry?.savedStateHandle?.get<Bundle>(REMINDER)
        val reminderList = reminderBundle?.getParcelableArrayList<Reminder>(REMINDER)
        ReminderConfirmRoute(reminderList, onBackClicked, navigateToHome)
    }
}
