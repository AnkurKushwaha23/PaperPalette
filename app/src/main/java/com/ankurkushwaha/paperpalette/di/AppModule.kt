package com.ankurkushwaha.paperpalette.di

import android.content.Context
import androidx.room.Room
import com.ankurkushwaha.paperpalette.data.local.PaperPaletteDataBase
import com.ankurkushwaha.paperpalette.data.remote.UnsplashApiService
import com.ankurkushwaha.paperpalette.data.repository.AndroidImageDownloaderImpl
import com.ankurkushwaha.paperpalette.data.repository.ImageRepositoryImpl
import com.ankurkushwaha.paperpalette.data.repository.NetworkConnectivityObserverImpl
import com.ankurkushwaha.paperpalette.data.util.Constants.BASE_URL
import com.ankurkushwaha.paperpalette.domain.repository.Downloader
import com.ankurkushwaha.paperpalette.domain.repository.ImageRepository
import com.ankurkushwaha.paperpalette.domain.repository.NetworkConnectivityObserver
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val interceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val contentType = "application/json".toMediaType()
        val json = Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun providesApiService(retrofit: Retrofit): UnsplashApiService {
        return retrofit.create(UnsplashApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePaperPaletteDataBase(
        @ApplicationContext context: Context
    ):PaperPaletteDataBase{
        return Room.databaseBuilder(
            context = context,
            klass = PaperPaletteDataBase::class.java,
            name = "paper_palette_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageRepository(
        apiService: UnsplashApiService,
        dataBase: PaperPaletteDataBase
    ): ImageRepository {
        return ImageRepositoryImpl(apiService,dataBase)
    }

    @Provides
    @Singleton
    fun provideAndroidImageDownloader(
        @ApplicationContext context: Context
    ): Downloader {
        return AndroidImageDownloaderImpl(context)
    }

    @Provides
    @Singleton
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context,
        scope: CoroutineScope
    ): NetworkConnectivityObserver {
        return NetworkConnectivityObserverImpl(context, scope)
    }
}