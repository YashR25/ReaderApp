package com.example.readerapp.screens.login

data class LoadingState(val status: Status, val message: String? = null) {

    companion object{
        val IDLE = Status.IDLE
        val SUCCESS = Status.SUCCESS
        val FAILED = Status.FAILED
        val LOADING = Status.LOADING
    }
    enum class Status{
        SUCCESS,
        FAILED,
        LOADING,
        IDLE
    }
}