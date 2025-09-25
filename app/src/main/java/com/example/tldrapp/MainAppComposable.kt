package com.example.tldrapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tldrapp.article.ArticleListScreenComposable
import com.example.tldrapp.article.ArticleScreenComposable

@Composable
fun MainApp() {

    Surface(modifier = Modifier.fillMaxSize()) {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "home"){

            composable("home"){
                HomeScreen(navController)
            }

            composable("articleList"){
                ArticleListScreenComposable(navController)
            }

            composable("article/{articleText}") { backStackEntry ->
                val articleText = backStackEntry.arguments?.getString("articleText") ?: ""
                ArticleScreenComposable(navController, articleText)
            }
        }

    }

}