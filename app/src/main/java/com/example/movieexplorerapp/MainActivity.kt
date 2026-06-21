package com.example.movieexplorerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            var loggedIn by remember {
                mutableStateOf(
                    FirebaseAuth.getInstance().currentUser != null
                )
            }

            MaterialTheme {

                if (loggedIn) {
                    MovieScreen(
                        onLogout = {
                            loggedIn = false
                        }
                    )
                } else {
                    LoginScreen {
                        loggedIn = true
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieScreen(
    onLogout: () -> Unit
) {

    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var filteredMovies by remember { mutableStateOf<List<Movie>>(emptyList()) }

    var searchText by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var favorites by remember {
        mutableStateOf(setOf<String>())
    }

    LaunchedEffect(Unit) {
        try {
            movies = RetrofitInstance.api.getMovies()
            filteredMovies = movies
            isLoading = false
        } catch (_: Exception) {
            error = "Failed to load movies"
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF8F5FF),
                        Color(0xFFEDE7F6)
                    )
                )
            )
            .padding(12.dp)
    ) {

        Text(
            text = "🎬 Movie Explorer",
            fontSize = 30.sp,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Browse Studio Ghibli Movies",
            fontSize = 14.sp
        )
        Text(
            text = "❤️ Favorites: ${favorites.size}",
            fontSize = 14.sp,
            color = Color.Red
        )


        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                onLogout()
            }
        ) {
            Text("Logout")
        }

        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it

                filteredMovies = movies.filter { movie ->
                    movie.title.contains(
                        searchText,
                        ignoreCase = true
                    )
                }
            },
            label = {
                Text("🔍 Search Movies")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        when {

            isLoading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        CircularProgressIndicator()

                        Spacer(
                            modifier = Modifier.height(12.dp)
                        )

                        Text(
                            text = "Loading Movies..."
                        )
                    }
                }
            }

            error.isNotEmpty() -> {

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text(error)

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Button(
                        onClick = {
                            error = ""
                            isLoading = true
                        }
                    ) {
                        Text("Retry")
                    }
                }
            }

            else -> {

                LazyColumn {

                    items(filteredMovies) { movie ->

                        var expanded by remember {
                            mutableStateOf(false)
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            onClick = {
                                expanded = !expanded
                            }
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Text(
                                        text = movie.title,
                                        fontSize = 24.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )

                                    TextButton(
                                        onClick = {
                                            favorites =
                                                if (favorites.contains(movie.title))
                                                    favorites - movie.title
                                                else
                                                    favorites + movie.title
                                        }
                                    ) {
                                        Text(
                                            if (favorites.contains(movie.title))
                                                "❤️"
                                            else
                                                "🤍"
                                        )
                                    }
                                }
                                Spacer(
                                    modifier = Modifier.height(6.dp)
                                )

                                Text(
                                    text = "🎬 Director: ${movie.director}"
                                )

                                Text(
                                    text = "📅 Release: ${movie.releaseDate}"
                                )

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                AnimatedVisibility(
                                    visible = expanded
                                ) {

                                    Text(
                                        text = movie.description,
                                        fontSize = 14.sp
                                    )
                                }

                                Spacer(
                                    modifier = Modifier.height(8.dp)
                                )

                                Text(
                                    text =
                                        if (expanded)
                                            "▲ Tap to Collapse"
                                        else
                                            "▼ Tap to Read More",
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}