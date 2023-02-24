package org.otunjargych.tamtam.di.module

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.otunjargych.tamtam.api.ApiService
import org.otunjargych.tamtam.api.AuthInterceptor
import org.otunjargych.tamtam.di.data.AppData
import org.otunjargych.tamtam.util.isConnectedToNetwork
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.jvm.Throws

val remoteDataSourceModule = module {

    fun provideGson(): Gson = GsonBuilder().serializeNulls().setLenient().create()
    fun provideConverterFactory(gson: Gson): Converter.Factory = GsonConverterFactory.create(gson)
    fun provideCallFactory(): CallAdapter.Factory =
        RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io())

    fun provideAuthInterceptor(appData: AppData) = AuthInterceptor(appData)

    fun provideHttpClient(context: Context, authInterceptor: AuthInterceptor): OkHttpClient {
//        val clientBuilder = OkHttpClient.Builder()
//            //.authenticator(authenticator)
//            .addNetworkInterceptor(
//                HttpLoggingInterceptor()
//                    .apply { level = HttpLoggingInterceptor.Level.BODY })
//            .readTimeout(1, TimeUnit.MINUTES)
//            .writeTimeout(1, TimeUnit.MINUTES)
//            .connectTimeout(5, TimeUnit.SECONDS)
//            .cache(Cache(File(context.cacheDir, "http-cache"), 10 * 1024 * 1024))
//
//        clientBuilder.addInterceptor(authInterceptor)
//
//        val logInterceptor = HttpLoggingInterceptor { message -> Log.e("REQUEST INFO", message) }
//        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        clientBuilder.addInterceptor(logInterceptor)
//
//        return clientBuilder.build()

        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .cache(Cache(File(context.cacheDir, "http-cache"), 10 * 1024 * 1024))

        clientBuilder.addInterceptor(authInterceptor)

        val logInterceptor = HttpLoggingInterceptor { message ->
            Log.e("REQUEST INFO", message)
        }
        logInterceptor.level = HttpLoggingInterceptor.Level.BODY
        clientBuilder.addInterceptor(logInterceptor)

        clientBuilder.addNetworkInterceptor {
            val response = it.proceed(it.request())
            val cacheControl: CacheControl = if (context.isConnectedToNetwork()) {
                CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build()
            } else {
                CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()
            }

            return@addNetworkInterceptor response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                .header("Cache-Control", cacheControl.toString())
                .build()
        }

        clientBuilder.addInterceptor {
            var request = it.request()

            if (!context.isConnectedToNetwork()) {
                val cacheControl = CacheControl.Builder()
                    .maxStale(7, TimeUnit.DAYS)
                    .build()

                request = request.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .cacheControl(cacheControl)
                    .build()
            }

            return@addInterceptor it.proceed(request)
        }

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
    single { provideAuthInterceptor(get()) }
    single { provideHttpClient(androidContext(), get()) }
    single { provideApiService(get(), get(), get()) }
}
