package com.tanvir.reminder.di

import android.content.Context
import com.tanvir.reminder.ReminderNotificationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    @Singleton
    fun provideReminderNotificationService(
        @ApplicationContext context: Context,
    ): ReminderNotificationService = ReminderNotificationService(context)
}
