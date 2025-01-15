package ro.pub.cs.systems.eim.practicaltest02v2

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: DictionaryApi by lazy {
        retrofit.create(DictionaryApi::class.java)
    }
}