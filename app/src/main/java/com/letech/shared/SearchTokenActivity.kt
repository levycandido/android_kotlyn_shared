package com.letech.shared

import android.app.Notification
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class SearchTokenActivity : AppCompatActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var tokenTextView: TextView
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val TAG = "SearchTokenActivity"
        private const val SERVER_KEY = "AIzaSyBJeaVHXp-3OCuHTy46TzUqoVxgtjIPfVw" // Substitua pela sua chave do servidor Firebase
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_token)

        nicknameEditText = findViewById(R.id.nicknameEditText)
        tokenTextView = findViewById(R.id.tokenTextView)
        db = FirebaseFirestore.getInstance()


        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener {
            onSearchButtonClick(it)
        }

        val sendButton = findViewById<Button>(R.id.sendButton)
        sendButton.setOnClickListener {
            onSendButtonClick(it)
        }
    }


    fun onSearchButtonClick(view: View) {
        val nickname = nicknameEditText.text.toString()

        if (nickname.isNotEmpty()) {
            searchTokenByNickname(nickname)
        } else {
            Toast.makeText(this, "Please enter a nickname", Toast.LENGTH_SHORT).show()
        }
    }

    fun searchTokenByNickname(nickname: String) {
        db.collection("users")
            .whereEqualTo("nickname", nickname)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    displayToken(documents)
                } else {
                    tokenTextView.text = "No user found with the nickname $nickname"
                }
            }
            .addOnFailureListener { e ->
                Log.e("SearchTokenActivity", "Error fetching documents", e)
                Toast.makeText(this, "Error fetching token: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun displayToken(documents: QuerySnapshot) {
        for (document in documents) {
            val token = document.getString("token")
            tokenTextView.text = "$token"
        }
    }

    fun onSendButtonClick(view: View) {

        var registrationToken = "YOUR_REGISTRATION_TOKEN";

        // Suponha que você tenha o token e a mensagem
        val token = tokenTextView.text.toString()

        // This registration token comes from the client FCM SDKs.

        // See documentation on defining a message payload.
        var message = Notification.MessagingStyle.Message.builder()
            .putData("score", "850")
            .putData("time", "2:45")
            .setToken(registrationToken)
            .build();

// Send a message to the device corresponding to the provided
// registration token.
        String response = FirebaseMessaging.getInstance().send(message);
// Response is a message ID string.
        System.out.println("Successfully sent message: " + response);

//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val payload = JSONObject().apply {
//                    put("to", token)
//                    put("data", JSONObject().apply {
//                        put("message", message)
//                    })
//                }
//
//                val url = URL("https://fcm.googleapis.com/fcm/send")
//                with(url.openConnection() as HttpURLConnection) {
//                    requestMethod = "POST"
//                    setRequestProperty("Authorization", "key=$SERVER_KEY")
//                    setRequestProperty("Content-Type", "application/json")
//                    doOutput = true
//
//                    outputStream.use { os: OutputStream ->
//                        os.write(payload.toString().toByteArray())
//                        os.flush()
//                    }
//
//                    val responseCode = responseCode
//                    Log.d(TAG, "Response Code: $responseCode")
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Error sending FCM message", e)
//            }
//        }
    }

}
