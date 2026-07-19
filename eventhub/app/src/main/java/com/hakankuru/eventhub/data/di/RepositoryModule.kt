package com.hakankuru.eventhub.data.di

import com.hakankuru.eventhub.data.repository.AuthRepositoryImpl
import com.hakankuru.eventhub.data.repository.ClubMemberRepositoryImpl
import com.hakankuru.eventhub.data.repository.ClubRepositoryImpl
import com.hakankuru.eventhub.data.repository.EventRepositoryImpl
import com.hakankuru.eventhub.data.repository.SuperAdminRepositoryImpl
import com.hakankuru.eventhub.data.repository.UserRepositoryImpl
import com.hakankuru.eventhub.domain.repository.AuthRepository
import com.hakankuru.eventhub.domain.repository.ClubMemberRepository
import com.hakankuru.eventhub.domain.repository.ClubRepository
import com.hakankuru.eventhub.domain.repository.EventRepository
import com.hakankuru.eventhub.domain.repository.SuperAdminRepository
import com.hakankuru.eventhub.domain.repository.UserRepository
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
    abstract fun bindClubMemberRepository(
        clubMemberRepositoryImpl: ClubMemberRepositoryImpl
    ): ClubMemberRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindSuperAdminRepository(
        superAdminRepositoryImpl: SuperAdminRepositoryImpl
    ): SuperAdminRepository

    @Binds
    @Singleton
    abstract fun bindClubRepository(
        clubRepositoryImpl: ClubRepositoryImpl
    ): ClubRepository

    @Binds
    @Singleton
    abstract fun bindEventRepository(
        eventRepositoryImpl: EventRepositoryImpl
    ): EventRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}
