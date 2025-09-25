package com.example.tldrapp.article

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ArticleScreenComposable(
    navController: NavController,
    articleText: String
) {
    val articleViewModel: ArticleViewModel = viewModel()
    val summary by articleViewModel.summary.collectAsState()
    val isLoading by articleViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Heading")
            Button(
                onClick = { articleViewModel.summarizeArticleUseFireabseAI(articleText) }
            ) {
                Text("Summarize use firebase AI")
            }

            Button(
                onClick = { /*TODO*/ }
            ) {
                Text("Summarize use firebase ML")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(articleText)
        }
    }

    // Loading dialog
    if (isLoading) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(24.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Summarizing...")
                }
            }
        }
    }

    // Summary dialog
    if (summary != null) {
        Dialog(onDismissRequest = { articleViewModel.dismissSummary() }) {
            Box(
                modifier = Modifier
                    .background(Color.White, shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { articleViewModel.dismissSummary() }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Text("Summary-")
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(){
                        val sentences = summary?.split(".")
                        sentences?.forEach { sentence ->
                            if (sentence.isNotBlank()) {
                                Text(".  "+sentence.trim() + ".")
                                Spacer(modifier = Modifier.height(6.dp))
                            }
                        }
                    }

                }
            }
        }
    }
}
