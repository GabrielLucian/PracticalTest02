package ro.pub.cs.systems.eim.practicaltest02v2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val serverButton = findViewById<Button>(R.id.serverbutton)
        val clientButton = findViewById<Button>(R.id.clientbutton)
        val serverPortEditText = findViewById<EditText>(R.id.serverport)
        val clientAddressEditText = findViewById<EditText>(R.id.address)
        val clientPortEditText = findViewById<EditText>(R.id.clientport)
        val wordEditText = findViewById<EditText>(R.id.word)
        val resultTextView = findViewById<TextView>(R.id.data)

        serverButton.setOnClickListener {
            val port = serverPortEditText.text.toString().toInt()
            serverThread = ServerThread(port)
            serverThread?.start()
        }

        clientButton.setOnClickListener {
            val address = clientAddressEditText.text.toString()
            val port = clientPortEditText.text.toString().toInt()
            val word = wordEditText.text.toString()

            val clientThread = ClientThread(address, port, word) { definition ->
                runOnUiThread {
                    Log.i("MainActivity", "Received definition: $definition")
                    resultTextView.text = definition ?: "No definition found"
                }
            }
            clientThread.start()
        }
        val timeButton = findViewById<Button>(R.id.timeButton)
        timeButton.setOnClickListener {
            val intent = Intent(this, TimeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serverThread?.stopThread()
    }
}