package com.tanvir.reminder.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.tanvir.reminder.feature.addreminder.navigation.addReminderGraph
import com.tanvir.reminder.feature.history.historyGraph
import com.tanvir.reminder.feature.home.navigation.HomeDestination
import com.tanvir.reminder.feature.home.navigation.homeGraph
import com.tanvir.reminder.feature.remiderconfirm.navigation.REMINDER
import com.tanvir.reminder.feature.remiderconfirm.navigation.ReminderConfirmDestination
import com.tanvir.reminder.feature.remiderconfirm.navigation.reminderConfirmGraph
import com.tanvir.reminder.feature.reminderdetail.ReminderDetailDestination
import com.tanvir.reminder.feature.reminderdetail.reminderDetailGraph
import com.tanvir.reminder.util.navigateSingleTop

@Composable
fun NavHost(
    bottomBarVisibility: MutableState<Boolean>,
    fabVisibility: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = HomeDestination.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        homeGraph(
            navController = navController,
            bottomBarVisibility = bottomBarVisibility,
            fabVisibility = fabVisibility,
            navigateToReminderDetail = { reminder ->
                navController.navigate(
                    ReminderDetailDestination.createNavigationRoute(reminder.id)
                )
            }
        )
        historyGraph(
            bottomBarVisibility = bottomBarVisibility,
            fabVisibility = fabVisibility,
            navigateToReminderDetail = { reminder ->
                navController.navigate(
                    ReminderDetailDestination.createNavigationRoute(reminder.id)
                )
            }
        )
        reminderDetailGraph(
            bottomBarVisibility = bottomBarVisibility,
            fabVisibility = fabVisibility,
            onBackClicked = { navController.navigateUp() }
        )
        addReminderGraph(
            navController = navController,
            bottomBarVisibility = bottomBarVisibility,
            fabVisibility = fabVisibility,
            onBackClicked = { navController.navigateUp() },
            navigateToReminderConfirm = {
                val bundle = Bundle()
                bundle.putParcelableArrayList(REMINDER, ArrayList(it))
                navController.currentBackStackEntry?.savedStateHandle.apply {
                    this?.set(REMINDER, bundle)
                }
                navController.navigate(ReminderConfirmDestination.route)
            }
        )
        reminderConfirmGraph(
            navController = navController,
            bottomBarVisibility = bottomBarVisibility,
            fabVisibility = fabVisibility,
            onBackClicked = { navController.navigateUp() },
            navigateToHome = {
                navController.navigateSingleTop(HomeDestination.route)
            }
        )
    }
}
