package ro.pub.cs.systems.eim.practicaltest02v2

import android.util.Log
import java.io.IOException
import java.net.Socket
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class ClientThread(
    private val address: String,
    private val port: Int,
    private val word: String,
    private val onWordDefinitionReceived: (String?) -> Unit
) : Thread() {

    private var socket: Socket? = null

    override fun run() {
        try {
            socket = Socket(address, port)
            val bufferedReader = Utils.getReader(socket!!)
            val printWriter = Utils.getWriter(socket!!)

            printWriter.println(word)
            printWriter.flush()

            val wordDefinitionString = bufferedReader.readLine()
            Log.i(TAG, "[CLIENT THREAD] Received word definition string: $wordDefinitionString")
            val definition = wordDefinitionString?.let {
                val jsonObject = JSONObject(it)
                val meaningsString = jsonObject.getString("meanings")
                val meaningsArray = JSONArray(meaningsString)

                val firstMeaning = meaningsArray.getJSONObject(0)
                val definitionsString = firstMeaning.getString("definitions")
                val definitionsArray = JSONArray(definitionsString)

                Log.e(TAG, "[CLIENT THREAD] definitionsArray FOUND: $definitionsArray")
                val firstDefinition = definitionsArray.getJSONObject(0)
                firstDefinition.optString("definition")
            }

            Log.i(TAG, "[CLIENT THREAD] Parsed definition: $definition")
            onWordDefinitionReceived(definition)

        } catch (ioException: IOException) {
            Log.e(TAG, "[CLIENT THREAD] An exception has occurred: ${ioException.message}")
            onWordDefinitionReceived(null)
        } catch (jsonException: JSONException) {
            Log.e(TAG, "[CLIENT THREAD] JSON exception has occurred: ${jsonException.message}")
            onWordDefinitionReceived(null)
        } finally {
            try {
                socket?.close()
            } catch (ioException: IOException) {
                Log.e(TAG, "[CLIENT THREAD] An exception has occurred: ${ioException.message}")
            }
        }
    }

    companion object {
        private const val TAG = "ClientThread"
    }
}