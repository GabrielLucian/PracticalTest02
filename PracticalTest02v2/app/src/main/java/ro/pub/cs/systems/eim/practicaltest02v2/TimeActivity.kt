package ro.pub.cs.systems.eim.practicaltest02v2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

class TimeActivity : AppCompatActivity() {

    private lateinit var timeTextView: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time)

        timeTextView = findViewById(R.id.timeTextView)

        val address = "10.0.2.2"  // Emulator's loopback address
        val port = 12345

        Thread {
            try {
                val socket = Socket(address, port)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                while (isRunning) {
                    val time = reader.readLine()
                    handler.post {
                        timeTextView.text = time
                    }
                    Thread.sleep(1000)  // Update every second
                }
                socket.close()
            } catch (e: Exception) {
                Log.e("TimeActivity", "Error: ${e.message}")
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
    }
}