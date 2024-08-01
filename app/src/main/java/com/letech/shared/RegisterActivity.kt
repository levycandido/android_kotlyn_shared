package com.letech.shared

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        FirebaseApp.initializeApp(this)

        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            onRegisterButtonClick(it)
        }
    }

    private fun onRegisterButtonClick(view: View) {
        val email = findViewById<EditText>(R.id.emailEditText).text.toString()
        val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
        val nickname = findViewById<EditText>(R.id.nicknameEditText).text.toString()

        if (email.isNotEmpty() && password.isNotEmpty() && nickname.isNotEmpty()) {
            registerUser(email, password, nickname)
        } else {
            Toast.makeText(this, "Please enter email, password, and nickname", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerUser(email: String, password: String, nickname: String) {
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()

        Log.d("RegisterActivity", "Attempting to register user with email: $email")

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro bem-sucedido
                    val user = auth.currentUser
                    Log.d("RegisterActivity", "User registered successfully with uid: ${user?.uid}")

                    // Obter o token do dispositivo
                    FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                        if (tokenTask.isSuccessful) {
                            val token = tokenTask.result
                            Log.d("RegisterActivity", "FCM token obtained: $token")

                            // Armazenar os dados do usuário no Firestore
                            val userMap = hashMapOf(
                                "email" to email,
                                "nickname" to nickname,
                                "token" to token
                            )

                            user?.let {
                                db.collection("users").document(it.uid).set(userMap)
                                    .addOnSuccessListener {
                                        Log.d("RegisterActivity", "User data saved successfully in Firestore")
                                        Toast.makeText(this, "User registered and token saved successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e("RegisterActivity", "Failed to save user data in Firestore", e)
                                        Toast.makeText(this, "Failed to save token: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Log.e("RegisterActivity", "Failed to obtain FCM token", tokenTask.exception)
                            Toast.makeText(this, "Failed to get FCM token: ${tokenTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Falha no registro
                    Log.e("RegisterActivity", "User registration failed", task.exception)
                    if (task.exception != null) {
                        Log.e("RegisterActivity", "Exception: ", task.exception)
                    }
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
