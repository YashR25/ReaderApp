package com.example.readerapp.navigation

enum class ReaderScreens {
    ReaderSplashScreen,
    HomeScreen,
    LoginScreen,
    ReaderStatsScreen,
    SearchScreen,
    DetailScreen;
    companion object{
        fun getScreens(route: String?): ReaderScreens{
            return when(route){
                ReaderSplashScreen.name -> ReaderSplashScreen
                HomeScreen.name -> HomeScreen
                LoginScreen.name -> LoginScreen
                ReaderStatsScreen.name -> ReaderStatsScreen
                SearchScreen.name -> SearchScreen
                DetailScreen.name -> DetailScreen
                else -> HomeScreen
            }
        }
    }
}