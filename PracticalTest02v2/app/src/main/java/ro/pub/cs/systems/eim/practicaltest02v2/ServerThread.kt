package ro.pub.cs.systems.eim.practicaltest02v2

import android.util.Log
import java.io.IOException
import java.net.ServerSocket

class ServerThread(port: Int) : Thread() {

    private var serverSocket: ServerSocket? = null
    private val data: HashMap<String, WordDefinition> = HashMap()

    init {
        try {
            serverSocket = ServerSocket(port)
        } catch (ioException: IOException) {
            Log.e(TAG, "An exception has occurred: ${ioException.message}")
        }
    }

    @Synchronized
    fun setData(word: String, wordDefinition: WordDefinition) {
        data[word] = wordDefinition
    }

    @Synchronized
    fun getData(): HashMap<String, WordDefinition> {
        return data
    }

    override fun run() {
        try {
            while (!currentThread().isInterrupted) {
                Log.i(TAG, "[SERVER THREAD] Waiting for a client invocation...")
                val socket = serverSocket?.accept()
                Log.i(TAG, "[SERVER THREAD] A connection request was received from ${socket?.inetAddress}:${socket?.localPort}")
                val communicationThread = CommunicationThread(this, socket!!)
                communicationThread.start()
            }
        } catch (ioException: IOException) {
            Log.e(TAG, "[SERVER THREAD] An exception has occurred: ${ioException.message}")
        }
    }

    fun stopThread() {
        interrupt()
        serverSocket?.close()
    }

    companion object {
        private const val TAG = "ServerThread"
    }
}