package com.example.movieexplorerapp

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "🔐 Login",
            fontSize = 30.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {

                auth.signInWithEmailAndPassword(
                    email,
                    password
                )
                    .addOnSuccessListener {
                        onLoginSuccess()
                    }
                    .addOnFailureListener {
                        error = it.message ?: "Login Failed"
                    }
            }
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {

                auth.createUserWithEmailAndPassword(
                    email,
                    password
                )
                    .addOnSuccessListener {
                        onLoginSuccess()
                    }
                    .addOnFailureListener {
                        error = it.message ?: "Registration Failed"
                    }
            }
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(error)
    }
}