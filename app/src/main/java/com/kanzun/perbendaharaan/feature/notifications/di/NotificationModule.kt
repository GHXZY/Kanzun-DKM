package com.kanzun.perbendaharaan.feature.notifications.di

import com.kanzun.perbendaharaan.feature.notifications.data.repository.NotificationRepositoryImpl
import com.kanzun.perbendaharaan.feature.notifications.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl,
    ): NotificationRepository
}
