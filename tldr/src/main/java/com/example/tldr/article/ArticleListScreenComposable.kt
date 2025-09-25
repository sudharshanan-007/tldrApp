package com.example.tldr.article

import android.net.Uri
import android.util.Log

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import com.example.tldrapp.R

// Data model for Article
data class Article(val article: String)

@Composable
fun ArticleListScreenComposable(navController: NavController) {
    val context = LocalContext.current
    var articles by remember { mutableStateOf(listOf<Article>()) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            // Make sure article.json is in src/main/assets/article.json
            val inputStream = context.resources.openRawResource(R.raw.article)
            val jsonString = InputStreamReader(inputStream).readText()
            val type = object : TypeToken<List<Article>>() {}.type
            articles = Gson().fromJson(jsonString, type)
        } catch (e: Exception) {
            error = "Failed to load articles: ${e.message}"
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
    ) {
        if (error != null) {
            Text(text = error!!, color = Color.Red)
        } else {
            LazyColumn {
                items(articles.size) { index ->
                    val article = articles[index]
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp)
                            .clickable {
                                // Handle article click if needed
                                val encodedText = Uri.encode(article.article) // Encodes safely for path
                                navController.navigate("article/$encodedText")
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = article.article.take(100) + if (article.article.length > 100) "..." else "",
                                color = Color.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}