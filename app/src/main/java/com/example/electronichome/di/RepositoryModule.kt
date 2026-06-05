package com.example.electronichome.di

import com.example.electronichome.data.repository.AnnouncementRepositoryImpl
import com.example.electronichome.data.repository.ApartmentRepositoryImpl
import com.example.electronichome.data.repository.GuestPassRepositoryImpl
import com.example.electronichome.data.repository.ManagementRepositoryImpl
import com.example.electronichome.data.repository.MeterRepositoryImpl
import com.example.electronichome.data.repository.RequestRepositoryImpl
import com.example.electronichome.domain.repository.AnnouncementRepository
import com.example.electronichome.domain.repository.ApartmentRepository
import com.example.electronichome.domain.repository.GuestPassRepository
import com.example.electronichome.domain.repository.ManagementRepository
import com.example.electronichome.domain.repository.MeterRepository
import com.example.electronichome.domain.repository.RequestRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAnnouncementRepository(
        impl: AnnouncementRepositoryImpl
    ): AnnouncementRepository

    @Binds
    @Singleton
    abstract fun bindApartmentRepository(
        impl: ApartmentRepositoryImpl
    ): ApartmentRepository

    @Binds
    @Singleton
    abstract fun bindGuestPassRepository(
        impl: GuestPassRepositoryImpl
    ): GuestPassRepository

    @Binds
    @Singleton
    abstract fun bindManagementRepository(
        impl: ManagementRepositoryImpl
    ): ManagementRepository

    @Binds
    @Singleton
    abstract fun bindMeterRepository(
        impl: MeterRepositoryImpl
    ): MeterRepository

    @Binds
    @Singleton
    abstract fun bindRequestRepository(
        impl: RequestRepositoryImpl
    ): RequestRepository
}