package com.example.readerapp.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.readerapp.screens.detail.DetailScreen
import com.example.readerapp.screens.detail.DetailViewModel
import com.example.readerapp.screens.home.HomeScreenViewModel
import com.example.readerapp.screens.home.ReaderHomeScreen
import com.example.readerapp.screens.login.LoginScreen
import com.example.readerapp.screens.search.SearchScreen
import com.example.readerapp.screens.search.SearchViewModel
import com.example.readerapp.screens.splash.ReaderSplashScreen
import com.example.readerapp.screens.stats.ReaderStatsScreen
import com.example.readerapp.screens.update.UpdateScreen

@Composable
fun ReaderNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ReaderScreens.ReaderSplashScreen.name){
        composable(ReaderScreens.ReaderSplashScreen.name){
            ReaderSplashScreen(navController = navController)
        }
        composable(ReaderScreens.LoginScreen.name){
            LoginScreen(navController = navController)
        }
        composable(ReaderScreens.HomeScreen.name){
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            ReaderHomeScreen(navController,viewModel)
        }
        composable(ReaderScreens.ReaderStatsScreen.name){
            val viewModel = hiltViewModel<HomeScreenViewModel>()
            ReaderStatsScreen(navController,viewModel)
        }
        composable(ReaderScreens.SearchScreen.name){
            val searchViewModel = hiltViewModel<SearchViewModel>()
            SearchScreen(navController,searchViewModel)
        }
        composable(ReaderScreens.DetailScreen.name + "/{bookId}",
        arguments = listOf(navArgument("bookId"){
            type = NavType.StringType
        })
        ){navBackStack->
            val bookId = navBackStack.arguments?.getString("bookId").let {
                val viewModel = hiltViewModel<DetailViewModel>()
                DetailScreen(navController = navController,it.toString(),viewModel)
            }

        }
        composable(ReaderScreens.UpdateScreen.name + "/{bookItemId}",
        arguments = listOf(navArgument("bookItemId"){
            type = NavType.StringType
        })
        ){navBackStack->
            val arg = navBackStack.arguments?.get("bookItemId")
            UpdateScreen(navController = navController,arg.toString())
        }
    }
}