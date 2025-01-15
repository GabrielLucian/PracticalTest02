package ro.pub.cs.systems.eim.practicaltest02v2

import android.util.Log
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.io.IOException
import java.net.Socket

class CommunicationThread(private val serverThread: ServerThread, private val socket: Socket) : Thread() {

    override fun run() {
        try {
            val bufferedReader = Utils.getReader(socket)
            val printWriter = Utils.getWriter(socket)

            Log.i(TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (word)")
            val word = bufferedReader.readLine()
            if (word.isNullOrEmpty()) {
                Log.e(TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (word)")
                return
            }

            val data = serverThread.getData()
            var wordDefinition: WordDefinition? = data[word]
            if (wordDefinition == null) {
                Log.i(TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...")

                // Call the Retrofit service inside a coroutine
                val response = runBlocking {
                    try {
                        RetrofitInstance.api.getWordDefinition(word).execute()
                    } catch (exception: Exception) {
                        Log.e(TAG, "[COMMUNICATION THREAD] Error during web service call: ${exception.message}")
                        null
                    }
                }

                response?.body()?.let {
                    if (it.isNotEmpty()) {
                        wordDefinition = it[0]
                        serverThread.setData(word, wordDefinition!!)
                    }
                }
            }

            val json = JSONObject()

            wordDefinition?.let {
                json.put("word", it.word)
                json.put("phonetic", it.phonetic)
                val meaningsArray = it.meanings.map { meaning ->
                    val meaningJson = JSONObject()
                    meaningJson.put("partOfSpeech", meaning.partOfSpeech)
                    val definitionsArray = meaning.definitions.map { definition ->
                        val definitionJson = JSONObject()
                        definitionJson.put("definition", definition.definition)
                        definitionJson.put("example", definition.example)
                        definitionJson
                    }
                    meaningJson.put("definitions", definitionsArray)
                    meaningJson
                }
                json.put("meanings", meaningsArray)
            }

            Log.i(TAG, "[COMMUNICATION THREAD] Sending the information to the client: $json")
            printWriter.println(json.toString())
            printWriter.flush()

        } catch (ioException: IOException) {
            Log.e(TAG, "[COMMUNICATION THREAD] An exception has occurred: ${ioException.message}")
        } finally {
            try {
                socket.close()
            } catch (ioException: IOException) {
                Log.e(TAG, "[COMMUNICATION THREAD] An exception has occurred: ${ioException.message}")
            }
        }
    }

    companion object {
        private const val TAG = "CommunicationThread"
    }
}