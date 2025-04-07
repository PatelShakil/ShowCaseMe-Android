package com.techsavvy.showcaseme.common

sealed class Resource<out R> {
    data class Success<out R>(val result: R):Resource<R>()
    data class Failure(
        val errorCode : Int = 0,
        val message:String
    ): Resource<Nothing>()
    object Loading: Resource<Nothing>()
}
