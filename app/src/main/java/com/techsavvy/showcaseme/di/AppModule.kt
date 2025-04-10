package com.techsavvy.showcaseme.di

import android.content.Context
import com.techsavvy.showcaseme.data.repo.api.auth.AuthImpl
import com.techsavvy.showcaseme.data.repo.api.auth.AuthRepo
import com.techsavvy.showcaseme.data.repo.log.FcmLog
import com.techsavvy.showcaseme.data.repo.log.FcmLogImpl
import com.techsavvy.showcaseme.data.repo.user.UserImpl
import com.techsavvy.showcaseme.data.repo.user.UserRepo
import com.techsavvy.showcaseme.network.ShowCaseMeHttpClient
import com.techsavvy.showcaseme.utils.Helpers
import com.techsavvy.showcaseme.utils.js.JSBridge
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import javax.inject.Singleton

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

    @Provides
    @Singleton
    fun provideJSBridge(@ApplicationContext context: Context, helpers: Helpers): JSBridge {
        return JSBridge(helpers, context)
    }

    @Provides
    fun provideUserRepo(userRepo: UserImpl): UserRepo = userRepo

}