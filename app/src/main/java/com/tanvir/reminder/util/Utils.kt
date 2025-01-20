package com.tanvir.reminder.util

import androidx.navigation.NavHostController

fun NavHostController.navigateSingleTop(route: String) {
    this.navigate(route) {
        popUpTo(route)
        launchSingleTop = true
    }
}
