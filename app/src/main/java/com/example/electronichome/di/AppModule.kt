package com.example.electronichome.di


import android.content.Context
import com.example.electronichome.data.local.ApartmentPrefs
import com.example.electronichome.data.repository.ApartmentRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideApartmentRepository(): ApartmentRepository = ApartmentRepository()

    @Provides
    @Singleton
    fun provideApartmentPrefs(
        @ApplicationContext context: Context
    ): ApartmentPrefs = ApartmentPrefs(context)
}