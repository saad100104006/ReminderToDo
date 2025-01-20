package com.tanvir.reminder.feature.reminderdetail

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.tanvir.reminder.core.navigation.ReminderNavigationDestination
import com.tanvir.reminder.core.navigation.NavigationConstants.DEEP_LINK_URI_PATTERN
import com.tanvir.reminder.core.navigation.NavigationConstants.REMINDER_ID

object ReminderDetailDestination : ReminderNavigationDestination {
    override val route = "reminder_detail_route/{$REMINDER_ID}"
    override val destination = "reminder_detail_destination"

    fun createNavigationRoute(reminderId: Long) = "reminder_detail_route/$reminderId"
}

fun NavGraphBuilder.reminderDetailGraph(
    bottomBarVisibility: MutableState<Boolean>,
    fabVisibility: MutableState<Boolean>,
    onBackClicked: () -> Unit
) {
    composable(
        route = ReminderDetailDestination.route,
        arguments = listOf(
            navArgument(REMINDER_ID) { type = NavType.LongType }
        ),
        deepLinks = listOf(
            navDeepLink {
                uriPattern = DEEP_LINK_URI_PATTERN
            }
        )
    ) { backStackEntry ->
        LaunchedEffect(null) {
            bottomBarVisibility.value = false
            fabVisibility.value = false
        }

        val reminderId = backStackEntry.arguments?.getLong(REMINDER_ID)

        ReminderDetailRoute(
            reminderId = reminderId,
            onBackClicked = onBackClicked
        )
    }
}
