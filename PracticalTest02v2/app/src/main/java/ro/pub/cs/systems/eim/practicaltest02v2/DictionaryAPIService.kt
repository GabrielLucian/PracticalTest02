package ro.pub.cs.systems.eim.practicaltest02v2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface DictionaryApi {
    @GET("en/{word}")
    fun getWordDefinition(@Path("word") word: String): Call<List<WordDefinition>>
}