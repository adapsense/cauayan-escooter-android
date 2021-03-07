package com.adapsense.escooter.api

import com.adapsense.escooter.BuildConfig
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiConnector private constructor() {

	private var retrofit: Retrofit

	companion object {
		var instance: ApiConnector? = null
			private set

		fun init() {
			instance = ApiConnector()
		}

	}

	init {

		val logging = HttpLoggingInterceptor()
		logging.level = HttpLoggingInterceptor.Level.BODY

		retrofit = Retrofit.Builder()
			.baseUrl(BuildConfig.BASE_URL)
			.addConverterFactory(GsonConverterFactory.create())
			.client(OkHttpClient.Builder()
				.connectTimeout(5, TimeUnit.MINUTES)
				.writeTimeout(5, TimeUnit.MINUTES)
				.readTimeout(5, TimeUnit.MINUTES)
				.addInterceptor { chain ->
					val newRequest = chain.request().newBuilder().headers(
						ApiHeaders().getHeaders().toHeaders()
					).build()
					chain.proceed(newRequest)
				}
				.addInterceptor(logging)
				.build())
			.build()
	}

	@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
	fun <S> createService(serviceClass: Class<S>?): S {
		return retrofit.create(serviceClass)
	}

}
