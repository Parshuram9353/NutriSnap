package com.aurafit.app.di

import android.content.Context
import com.aurafit.app.core.security.EncryptedPrefs
import com.aurafit.app.data.database.AuraDatabase
import com.aurafit.app.data.network.OpenAiService
import com.aurafit.app.data.repository.HealthRepositoryImpl
import com.aurafit.app.domain.repository.HealthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModules {

    @Provides
    @Singleton
    fun provideEncryptedPrefs(@ApplicationContext context: Context): EncryptedPrefs {
        return EncryptedPrefs(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AuraDatabase {
        // Enforce encrypted database initialization. In full production, the passphrase
        // can be derived from Android Keystore. Here we set up SQLCipher bindings.
        val passphrase = "AuraFitSecureLocalDatabaseSQLiteSeedKey".toByteArray()
        return AuraDatabase.getInstance(context, passphrase)
    }

    @Provides
    @Singleton
    fun provideHealthRepository(db: AuraDatabase): HealthRepository {
        return HealthRepositoryImpl(db)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenAiService(client: OkHttpClient): OpenAiService {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenAiService::class.java)
    }
}
