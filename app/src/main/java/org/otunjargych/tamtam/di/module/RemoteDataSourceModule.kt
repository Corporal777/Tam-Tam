package org.otunjargych.tamtam.di.module

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.otunjargych.tamtam.api.ApiService
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

val remoteDataSourceModule = module {

    fun provideGson(): Gson = GsonBuilder().serializeNulls().setLenient().create()
    fun provideConverterFactory(gson: Gson): Converter.Factory = GsonConverterFactory.create(gson)
    fun provideCallFactory(): CallAdapter.Factory =
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

    fun provideHttpClient(context: Context): OkHttpClient {
        val clientBuilder = OkHttpClient.Builder()
            //.authenticator(authenticator)
            .addNetworkInterceptor(
                HttpLoggingInterceptor()
                    .apply { level = HttpLoggingInterceptor.Level.BODY })
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(5, TimeUnit.SECONDS)
            .cache(Cache(File(context.cacheDir, "http-cache"), 10 * 1024 * 1024))

        val logInterceptor = HttpLoggingInterceptor { message -> Log.e("REQUEST INFO", message) }
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(logInterceptor)

        return clientBuilder.build()
    }

    fun provideApiService(
        httpClient: OkHttpClient,
        callAdapterFactory: CallAdapter.Factory,
        converterFactory: Converter.Factory
    ): ApiService {

        return Retrofit.Builder()
            .baseUrl("http://192.168.1.3:8080")
            .client(httpClient)
            .addCallAdapterFactory(callAdapterFactory)
            .addConverterFactory(converterFactory)
            .build()
            .create(ApiService::class.java)
    }

    single { provideGson() }
    single { provideConverterFactory(get()) }
    single { provideCallFactory() }
    single { provideHttpClient(androidContext()) }
    single { provideApiService(get(), get(), get()) }
}