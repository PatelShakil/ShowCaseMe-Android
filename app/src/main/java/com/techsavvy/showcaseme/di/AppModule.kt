package com.techsavvy.showcaseme.di

import android.content.Context
import com.techsavvy.showcaseme.data.repo.api.auth.AuthImpl
import com.techsavvy.showcaseme.data.repo.api.auth.AuthRepo
import com.techsavvy.showcaseme.data.repo.log.FcmLog
import com.techsavvy.showcaseme.data.repo.log.FcmLogImpl
import com.techsavvy.showcaseme.network.ShowCaseMeHttpClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    fun provideHttpClient(httpClient: ShowCaseMeHttpClient): HttpClient = httpClient.getHttpClient()

    @Provides
    fun provideAuthRepo(authRepo: AuthImpl): AuthRepo = authRepo

    @Provides
    fun provideFcmLog(): FcmLog = FcmLogImpl()

}